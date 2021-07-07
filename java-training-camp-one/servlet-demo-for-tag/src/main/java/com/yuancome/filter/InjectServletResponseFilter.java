package com.yuancome.filter;

import com.yuancome.servlet.GlobalHttpServletResponseHolder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 将当前请求的 servlet response 注册到 holder 中
 *
 * @author yuancome
 * @date 2021/7/7
 */

public class InjectServletResponseFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletResponse instanceof HttpServletResponse) {
            //注入全局响应
            System.out.println("inject global servlet response");
            GlobalHttpServletResponseHolder.setHttpServletResponse((HttpServletResponse) servletResponse);
        }


        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
