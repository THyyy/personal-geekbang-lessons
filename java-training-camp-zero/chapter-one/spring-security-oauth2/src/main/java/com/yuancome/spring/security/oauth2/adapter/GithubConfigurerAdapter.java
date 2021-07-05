package com.yuancome.spring.security.oauth2.adapter;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * GitHub配置适配器
 *
 * @author tanheyuan
 * @version 1.0
 * @since 2021/4/20
 */
@Configuration
public class GithubConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(a -> a
                .antMatchers("/", "/error", "/webjars/**").permitAll() // 匹配请求放行
                .anyRequest().authenticated() // 对其他请求均进行验证，但是没有对验证用户角色做权限管理
        ).logout(l -> l.logoutSuccessUrl("/").permitAll() //退出页面放行
        ).csrf(c -> c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // 配置跨域请求
        ).exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)) // 异常拦截处理，均返回 403 状态码
        ).oauth2Login(); // 设置 OAuth2.0 登录，采用默认的 OAuth2LoginConfigurer 配置
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
        //auth.inMemoryAuthentication()
        //        .withUser("user").password("password").roles("USER").authorities("ROLE_USER");
    }
}
