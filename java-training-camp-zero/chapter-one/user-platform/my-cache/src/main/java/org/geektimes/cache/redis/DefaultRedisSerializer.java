package org.geektimes.cache.redis;

import io.lettuce.core.codec.RedisCodec;
import org.geektimes.cache.serialize.CacheSerializer;


import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * Redis 序列化实现类
 *
 * @author yuancome
 * @date 2021/4/14
 */
public class DefaultRedisSerializer<K extends Serializable, V extends Serializable> implements RedisCodec<K, V> {

    final CacheSerializer serializer;

    public DefaultRedisSerializer() {
        this.serializer = CacheSerializer.getInstance();
    }

    public DefaultRedisSerializer(CacheSerializer cacheSerializer) {
        this.serializer = cacheSerializer;
    }

    @Override
    public K decodeKey(ByteBuffer byteBuffer) {
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes, 0, bytes.length);
        return serializer.deserialize(bytes);
    }

    @Override
    public V decodeValue(ByteBuffer byteBuffer) {
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes, 0, bytes.length);
        return serializer.deserialize(bytes);
    }

    @Override
    public ByteBuffer encodeKey(K key) {
        return ByteBuffer.wrap(serializer.serialize(key));
    }

    @Override
    public ByteBuffer encodeValue(V value) {
        return ByteBuffer.wrap(serializer.serialize(value));
    }

}