package com.yuancome.servlet;

import javax.servlet.http.HttpServletResponse;

/**
 * Http Servlet Response 全局响应
 *
 * @author yuancome
 * @date 2021/7/7
 */

public class GlobalHttpServletResponseHolder {
    private static final ThreadLocal<HttpServletResponse> responseHolder = new ThreadLocal<>();

    public static void setHttpServletResponse(HttpServletResponse response) {
        responseHolder.set(response);
    }

    public static HttpServletResponse getHttpServletResponse() {
        return responseHolder.get();
    }

    public static void reset() {
        responseHolder.remove();
    }
}
