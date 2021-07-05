package org.geektimes.cache.serialize;

import javax.cache.CacheException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 默认缓存序列化器实现类
 *
 * @author yuancome
 * @date 2021/4/14
 */
public class DefaultCacheSerializer extends CacheSerializer {

    @Override
    public byte[] doSerialize(Serializable source) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);) {
            objectOutputStream.writeObject(source);
            return out.toByteArray();
        } catch (IOException e) {
            throw new CacheException(e.getMessage(), e);
        }
    }

    @Override
    public <T> T doDeserialize(byte[] data) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);) {
            return (T) objectInputStream.readObject();
        } catch (Exception e) {
            throw new CacheException(e.getMessage(), e);
        }
    }
}