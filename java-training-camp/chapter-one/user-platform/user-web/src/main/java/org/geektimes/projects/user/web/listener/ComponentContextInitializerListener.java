package org.geektimes.projects.user.web.listener;


import org.geektimes.Context.ComponentContext;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.jmx.MBeanManager;
import org.geektimes.projects.user.management.UserManager;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.lang.management.ManagementFactory;
import java.util.logging.Logger;

/**
 * {@link ComponentContext} 初始化器
 * ContextLoaderListener
 */
public class ComponentContextInitializerListener implements ServletContextListener {

    private Logger logger = Logger.getLogger("org.geektimes...ComponentContextInitializerListener");

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        ComponentContext context = new ComponentContext();
        context.init(servletContext);
        try {
            MBeanManager.registerMBean("org.geektimes.projects.user.management:type=User", new User());
        } catch (Exception e) {
            logger.severe("注册 UserMBean 失败，异常信息：" + e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ComponentContext context = ComponentContext.getInstance();
        context.destroy();
    }

}
