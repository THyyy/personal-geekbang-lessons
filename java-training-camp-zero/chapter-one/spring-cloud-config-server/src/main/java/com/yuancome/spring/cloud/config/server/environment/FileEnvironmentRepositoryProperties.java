package com.yuancome.spring.cloud.config.server.environment;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * 文件环境存储配置类
 *
 * @author yuancome
 * @date 2021/6/2
 */
@Configuration
@ConfigurationProperties("spring.cloud.config.server.file")
public class FileEnvironmentRepositoryProperties {

    private int order;

    private String filePath;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileEnvironmentRepositoryProperties that = (FileEnvironmentRepositoryProperties) o;
        return order == that.order &&
                Objects.equals(filePath, that.filePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, filePath);
    }

    @Override
    public String toString() {
        return "FileEnvironmentRepositoryProperties{" +
                "order=" + order +
                ", filePath='" + filePath + '\'' +
                '}';
    }

}
