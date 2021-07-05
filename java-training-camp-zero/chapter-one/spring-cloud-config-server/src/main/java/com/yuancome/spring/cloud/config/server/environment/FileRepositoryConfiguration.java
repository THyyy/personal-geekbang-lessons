package com.yuancome.spring.cloud.config.server.environment;

import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 文件存储配置类
 *
 * @author yuancome
 * @date 2021/6/2
 */
@Configuration
public class FileRepositoryConfiguration {

    @Bean
    public EnvironmentRepository fileEnvironmentRepository(
            ConfigurableEnvironment configurableEnvironment,
            FileEnvironmentRepositoryProperties fileEnvironmentRepositoryProperties) {
        return new FileEnvironmentRepository(configurableEnvironment,
                fileEnvironmentRepositoryProperties);
    }

}
