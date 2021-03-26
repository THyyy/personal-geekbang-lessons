package org.geektimes.configuration.microprofile.config.listener;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

/**
 * ServletRequestListener是用户响应监听器
 * 用于对Request请求进行监听（创建、销毁）
 *
 * @author tanheyuan
 * @version 1.0
 * @since 2021/3/26
 */
public class ConfigServletRequestListener implements ServletRequestListener {

    private static final ThreadLocal<Config> configThreadLocal = new ThreadLocal<>();

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        ServletRequest request = sre.getServletRequest();
        ServletContext servletContext = request.getServletContext();
        ClassLoader classLoader = servletContext.getClassLoader();
        ConfigProviderResolver configProviderResolver = ConfigProviderResolver.instance();
        Config config = configProviderResolver.getConfig(classLoader);
        configThreadLocal.set(config);
    }

    public static Config getConfig() {
        return configThreadLocal.get();
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        // 防止 OOM
        configThreadLocal.remove();
    }

}
