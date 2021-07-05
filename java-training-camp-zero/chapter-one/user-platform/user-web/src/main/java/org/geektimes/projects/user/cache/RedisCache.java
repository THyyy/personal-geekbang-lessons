/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geektimes.projects.user.cache;

import org.springframework.cache.Cache;
import redis.clients.jedis.Jedis;

import javax.cache.CacheException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * Redis Cache 实现
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 * Date : 2021-04-29
 */
public class RedisCache implements Cache {

    private final String name;

    private final Jedis jedis;

    /**
     * 存储的前缀的字节数组
     */
    private final byte[] prefixBytes;

    /**
     * 使用list存储该{@link RedisCache}缓存的所有key
     */
    private final byte[] namespaceBytes;

    public RedisCache(String name, Jedis jedis) {
        Objects.requireNonNull(name, "The 'name' argument must not be null.");
        Objects.requireNonNull(jedis, "The 'jedis' argument must not be null.");
        this.name = name;
        this.jedis = jedis;
        prefixBytes = (this.name + ":").getBytes(StandardCharsets.UTF_8);
        namespaceBytes = ("namespace:" + this.name).getBytes(StandardCharsets.UTF_8);
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getNativeCache() {
        return jedis;
    }

    @Override
    public ValueWrapper get(Object key) {
        byte[] keyBytes = serialize(key);
        byte[] valueBytes = jedis.get(keyBytes);
        return () -> deserialize(valueBytes);
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return null;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return null;
    }

    @Override
    public void put(Object key, Object value) {
        byte[] keyBytes = getActualKeyBytes(key);
        byte[] valueBytes = serialize(value);
        jedis.set(keyBytes, valueBytes);
        jedis.lpush(namespaceBytes, keyBytes);
    }

    @Override
    public void evict(Object key) {
        byte[] keyBytes = serialize(key);
        jedis.del(keyBytes);
    }

    @Override
    public void clear() {
        // Redis 是否支持 namespace
        // name:key
        // String 类型的 key :
        List<byte[]> list = jedis.lrange(namespaceBytes, 0L, jedis.llen(namespaceBytes));
        if (list != null && !list.isEmpty()) {
            list.forEach(jedis::del);
        }
        jedis.del(namespaceBytes);
    }

    // 是否可以抽象出一套序列化和反序列化的 API
    private byte[] serialize(Object value) throws CacheException {
        byte[] bytes = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)
        ) {
            // Key -> byte[]
            objectOutputStream.writeObject(value);
            bytes = outputStream.toByteArray();
        } catch (IOException e) {
            throw new CacheException(e);
        }
        return bytes;
    }

    private <T> T deserialize(byte[] bytes) throws CacheException {
        if (bytes == null) {
            return null;
        }
        T value = null;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)
        ) {
            // byte[] -> Value
            value = (T) objectInputStream.readObject();
        } catch (Exception e) {
            throw new CacheException(e);
        }
        return value;
    }

    /**
     * 获取实际 key 对应的 Byte 数组
     *
     * @param key
     * @return
     */
    private byte[] getActualKeyBytes(Object key) {
        byte[] keyBytes = serialize(key);
        return getMergeBytes(prefixBytes, keyBytes);
    }

    /**
     * 拼接 Byte 数组用于 redis 分组
     *
     * @param prefixBytes
     * @param sourceBytes
     * @return
     */
    protected byte[] getMergeBytes(byte[] prefixBytes, byte[] sourceBytes) {
        byte[] result = new byte[prefixBytes.length + sourceBytes.length];
        System.arraycopy(prefixBytes, 0, result, 0, prefixBytes.length);
        System.arraycopy(sourceBytes, 0, result, prefixBytes.length, sourceBytes.length);
        return result;
    }
}
