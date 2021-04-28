package com.yuancome.spring.security.oauth2.adapter;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * Security配置类
 *
 * @author tanheyuan
 * @version 1.0
 * @since 2021/4/26
 */
@Configuration
@Order(value = 200)
public class SecurityConfig2 extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // 关闭
        httpSecurity.authorizeRequests(a -> a
                .antMatchers("/", "/error", "/webjars/**")
                .authenticated() // 对这些请求均进行验证，但是没有对验证用户角色做权限管理
        ).csrf().disable();
        System.out.println("SecurityConfig2"+httpSecurity);
    }
}
