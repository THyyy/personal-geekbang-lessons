package org.geektimes.component.initialization;


import org.geektimes.component.context.ComponentContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * {@link ComponentContext} 初始化器
 * ContextLoaderListener
 */
public class ComponentContextInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        ComponentContext context = new ComponentContext();
        context.init(servletContext);
        //try {
        //    MBeanManager.registerMBean("org.geektimes.projects.user.management:type=User", new UserManager(new User()));
        //    // 注册自定义 MBean —— ComponentContextManager
        //    MBeanManager.registerMBean("org.geektimes.projects.user.management:type=ComponentContext", new ComponentContextManager());
        //} catch (Exception e) {
        //    logger.severe("注册 UserMBean 失败，异常信息：" + e);
        //}
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ComponentContext context = ComponentContext.getInstance();
        context.destroy();
    }

}
