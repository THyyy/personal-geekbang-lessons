package org.geektimes.cache.serialize;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * 基于 Jackson 的 JSON 缓存序列化实现类
 *
 * @author tanheyuan
 * @version 1.0
 * @since 2021/4/21
 */
public class JSONCacheSerializer<T> extends CacheSerializer {
    private final Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public byte[] doSerialize(Serializable source) {
        if (source == null) {
            return new byte[0];
        }
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(source);
        } catch (JsonProcessingException e) {
            logger.severe("JSON 序列化处理失败：" + e);
            bytes = new byte[0];
        }
        return bytes;
    }

    @Override
    public <T> T doDeserialize(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        T result = null;
        String json = new String(data, StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            result = (T) objectMapper.readValue(json, result.getClass());
        }catch (JsonParseException e) {
            logger.severe("JSON 序列化转换异常：" + e);
        } catch (JsonMappingException e) {
            logger.severe("JSON 序列化映射异常：" + e);
        } catch (IOException e) {
            logger.severe("JSON 序列化 IO 异常：" + e);
        }
        return result;
    }
}
