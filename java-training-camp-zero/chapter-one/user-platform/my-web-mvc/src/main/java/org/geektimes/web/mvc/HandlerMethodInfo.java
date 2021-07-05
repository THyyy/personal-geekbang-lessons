package org.geektimes.web.mvc;

import org.geektimes.web.mvc.controller.Controller;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * 处理方法信息类
 *
 * @since 1.0
 */
public class HandlerMethodInfo {

    private final String requestPath;

    private final Method handlerMethod;

    private final Set<String> supportedHttpMethods;

    private final Controller controller;

    public HandlerMethodInfo(String requestPath, Method handlerMethod, Set<String> supportedHttpMethods, Controller controller) {
        this.requestPath = requestPath;
        this.handlerMethod = handlerMethod;
        this.supportedHttpMethods = supportedHttpMethods;
        this.controller = controller;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public Method getHandlerMethod() {
        return handlerMethod;
    }

    public Set<String> getSupportedHttpMethods() {
        return supportedHttpMethods;
    }

    public Controller getController() {
        return controller;
    }
}
