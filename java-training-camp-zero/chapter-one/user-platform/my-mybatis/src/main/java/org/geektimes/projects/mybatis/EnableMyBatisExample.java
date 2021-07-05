package org.geektimes.projects.mybatis;

import org.geektimes.projects.mybatis.annotation.EnableMyBatis;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * my-mybatis example
 *
 * @author yuancome
 * @date 2021/5/26
 */

@EnableMyBatis(dataSource = "dataSource",
        configLocation = "classpath:/META-INF/mybatis/mybatis-config.xml",
        mapperLocations = {"classpath:/META-INF/mybatis/mapper/*.xml"},
        environment = "development",
        typeAliasesPackage = "com.study.mybatis.domain")
@PropertySource("classpath:/META-INF/mybatis/jdbc.properties")
@MapperScan(basePackages = "org.geektimes.projects.mybatis.mapper")
public class EnableMyBatisExample {

    @Bean
    public DataSource dataSource(@Value("${jdbc.mysql.driver}") String driverClass, @Value("${jdbc.mysql.url}") String url, @Value("${jdbc.mysql.username}") String username, @Value("${jdbc.mysql.password}") String password) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}