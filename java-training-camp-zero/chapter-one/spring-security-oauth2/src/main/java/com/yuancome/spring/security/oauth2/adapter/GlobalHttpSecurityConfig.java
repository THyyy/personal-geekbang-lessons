package com.yuancome.spring.security.oauth2.adapter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author tanheyuan
 * @version 1.0
 * @since 2021/4/28
 */
@Configuration
public class GlobalHttpSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public SecurityFilterChain filter1(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.authorizeRequests(a -> a
                .antMatchers("/", "/error", "/webjars/**").permitAll()
                .anyRequest().authenticated() // 对这些请求均进行验证，但是没有对验证用户角色做权限管理
        ).build();
    }

    @Bean
    public SecurityFilterChain filter2(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.authorizeRequests(a -> a
                .antMatchers("/", "/error", "/webjars/**").permitAll()
                .anyRequest().authenticated() // 对这些请求均进行验证，但是没有对验证用户角色做权限管理
        ).csrf().disable().build();
    }
}
