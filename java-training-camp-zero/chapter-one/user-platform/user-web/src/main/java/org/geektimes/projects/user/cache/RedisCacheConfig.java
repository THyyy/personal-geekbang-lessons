package org.geektimes.projects.user.cache;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RedisCache 配置类
 *
 * @author tanheyuan
 * @version 1.0
 * @since 2021/5/5
 */
@Configuration
public class RedisCacheConfig {

    @Bean
    public CacheManager redisCacheManager() {
        String uri = "127.0.0.1";
        return new RedisCacheManager(uri);
    }
}
