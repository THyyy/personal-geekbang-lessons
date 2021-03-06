package org.geektimes.web.mvc;

import org.apache.commons.lang.StringUtils;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.geektimes.web.mvc.controller.Controller;
import org.geektimes.web.mvc.controller.PageController;
import org.geektimes.web.mvc.controller.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.substringAfter;

public class FrontControllerServlet extends HttpServlet {

    /**
     * 请求路径和 {@link HandlerMethodInfo} 映射关系缓存
     */
    private final Map<String, HandlerMethodInfo> handleMethodInfoMapping = new HashMap<>();

    /**
     * 初始化 Servlet
     *
     * @param servletConfig servlet 配置
     */
    @Override
    public void init(ServletConfig servletConfig) {
        ServletContext servletContext = servletConfig.getServletContext();
        Object config = servletContext.getAttribute("config");
        System.out.println("first out：" + ((Config)config).getConfigValue("application.name").getValue());
        initHandleMethods();
    }

    /**
     * 读取所有的 RestController 的注解元信息 @Path
     * 利用 ServiceLoader 技术（Java SPI）
     */
    private void initHandleMethods() {
        Config config = ConfigProviderResolver.instance().getConfig(Thread.currentThread().getContextClassLoader());
        System.out.println(config.getConfigValue("application.name").getValue());
        for (Controller controller : ServiceLoader.load(Controller.class)) {
            Class<?> controllerClass = controller.getClass();
            Path pathFromClass = controllerClass.getAnnotation(Path.class);
            // 不直接赋值 pathFromClass.value() 是因为下面的循环会不断 append 导致路径不正确
            String requestPath;
            Method[] publicMethods = controllerClass.getMethods();
            // 处理方法支持的 HTTP 方法集合
            for (Method method : publicMethods) {
                Set<String> supportedHttpMethods = findSupportedHttpMethods(method);
                Path pathFromMethod = method.getAnnotation(Path.class);
                if (pathFromMethod != null) {
                    // 此时重新赋值不会导致路径拼接错误
                    requestPath = pathFromClass.value() + pathFromMethod.value();
                    handleMethodInfoMapping.put(requestPath,
                            new HandlerMethodInfo(requestPath, method, supportedHttpMethods, controller));
                }
            }
        }
    }

    /**
     * 获取处理方法中标注的 HTTP方法集合
     *
     * @param method 处理方法
     * @return 支持的方法集合
     */
    private Set<String> findSupportedHttpMethods(Method method) {
        Set<String> supportedHttpMethods = new LinkedHashSet<>();
        for (Annotation annotationFromMethod : method.getAnnotations()) {
            HttpMethod httpMethod = annotationFromMethod.annotationType().getAnnotation(HttpMethod.class);
            if (httpMethod != null) {
                supportedHttpMethods.add(httpMethod.value());
            }
        }

        // 如果不注释，Controller 的方法可以通过任何的 HTTP 请求方式访问
        //if (supportedHttpMethods.isEmpty()) {
        //    supportedHttpMethods.addAll(asList(HttpMethod.GET, HttpMethod.POST,
        //            HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.HEAD, HttpMethod.OPTIONS));
        //}

        return supportedHttpMethods;
    }

    /**
     * SCWCD
     *
     * @param request HTTP 请求
     * @param response HTTP 相应
     * @throws ServletException Servlet 异常
     * @throws IOException IO 异常
     */
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 建立映射关系
        // requestURI = /a/hello/world
        String requestURI = request.getRequestURI();
        // contextPath  = /a or "/" or ""
        String prefixPath = request.getContextPath();
        // 映射路径（子路径）
        String requestMappingPath = substringAfter(requestURI,
                StringUtils.replace(prefixPath, "//", "/"));
        // 映射到 Controller
        HandlerMethodInfo handlerMethodInfo = handleMethodInfoMapping.get(requestMappingPath);
        try {
            if (handlerMethodInfo != null) {
                Controller controller = handlerMethodInfo.getController();
                String httpMethod = request.getMethod();
                if (!handlerMethodInfo.getSupportedHttpMethods().contains(httpMethod)) {
                    // HTTP 方法不支持
                    response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                    return;
                }

                Method handlerMethod = handlerMethodInfo.getHandlerMethod();
                if (controller instanceof PageController) {
                    PageController pageController = (PageController) controller;
                    //String viewPath = pageController.execute(request, response);
                    // 修改成反射的形式，支持多个方法
                    String viewPath = (String) handlerMethod.invoke(pageController, request, response);
                    // 页面请求 forward
                    // request -> RequestDispatcher forward
                    // RequestDispatcher requestDispatcher = request.getRequestDispatcher(viewPath);
                    // ServletContext -> RequestDispatcher forward
                    // ServletContext -> RequestDispatcher 必须以 "/" 开头
                    ServletContext servletContext = request.getServletContext();
                    if (!viewPath.startsWith("/")) {
                        viewPath = "/" + viewPath;
                    }
                    RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(viewPath);
                    requestDispatcher.forward(request, response);
                } else if (controller instanceof RestController) {
                    // TODO
                    System.out.println("REST 控制器请求待实现");
                }

            }
        } catch (Throwable throwable) {
            if (throwable.getCause() instanceof IOException) {
                throw (IOException) throwable.getCause();
            } else {
                throw new ServletException(throwable.getCause());
            }
        }
    }

//    private void beforeInvoke(Method handleMethod, HttpServletRequest request, HttpServletResponse response) {
//
//        CacheControl cacheControl = handleMethod.getAnnotation(CacheControl.class);
//
//        Map<String, List<String>> headers = new LinkedHashMap<>();
//
//        if (cacheControl != null) {
//            CacheControlHeaderWriter writer = new CacheControlHeaderWriter();
//            writer.write(headers, cacheControl.value());
//        }
//    }
}
