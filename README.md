# personal-geekbang-lessons——个人极客时间课程代码
## 第一模块：Java EE 单体应用

**第一周内容：**

- 完善自研 MVC 框架 

1. 修复了 `org.geektimes.web.mvc.FrontControllerServlet#initHandleMethods ` 方法**路径重复拼接**问题
2.  通过反射修复了 `org.geektimes.web.mvc.FrontControllerServlet#service ` 方法一个**请求路径对应一个控制器类**问题

- 完成 JNDI 获取数据库源

1. 修复 `src/main/webapp/META-INF/context.xml` 存放路径问题

- 完成用户注册功能

1. 把数据库操作相关方法抽出到 `org.geektimes.projects.user.sql.JdbcTemplate` 类

**第二周内容：**

- 完善简易版的依赖注入框架，并通过该框架完成用户注册功能

1. 根据第一周的 `org.geektimes.projects.user.sql.JdbcTemplate` 类进行相应的依赖注入，实现依赖注入链：`UserController -> UserServiceImpl -> DatabaseUserRepository -> JdbcTemplate -> DBConnectionManager`

- 实现用户注册数据校验（id > 0，6 < password 长度 < 32）

**第三周内容：**

- 实现自定义 JMX MBean，通过 Jolokia 做 Servlet 代理

具体实现查看：`org.geektimes.projects.user.web.listener.ComponentContextInitializerListener#contextInitialized` 方法

自定义  JMX MBean 为：`org.geektimes.projects.user.management.ComponentContextManager`

- 扩展 `org.eclipse.microprofile.config.spi.ConfigSource` 中的实现

本地配置环境实现：`org.geektimes.config.source.PropertiesConfigSource` 

系统 OS 环境变量实现： `org.geektimes.config.source.SystemPropertiesConfigSource`

- 扩展 `org.eclipse.microprofile.config.spi.Converter` 

主要使用了 `org.geektimes.utils.TypeTransFormUtils` 进行转换，留下了复杂类型的扩展接口类：`org.geektimes.config.converter.CustomizedConverter`

- 本地配置文件：`META-INF/application.properties`

**第四周内容**

- 完善 `my-dependency-injection` 模块，提供给 `user-web` 模块使用

抽离 `user-web` 模块中的 `ComponentContext` 类到 `my-dependency-injection` 模块，添加 `ServletContainerInitializer` 实现类和 `ComponentContextInitializer` 监听器：

```java
public class WebAppServletComponentInitializer implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> c, ServletContext servletContext) throws ServletException {
        // 增加 ComponentContextInitializer 进行 ComponentContext 初始化
        servletContext.addListener(ComponentContextInitializer.class);
    }
}

public class ComponentContextInitializer implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        ComponentContext context = new ComponentContext();
        context.init(servletContext);
    }
}
```

- 完善 `my-configuration` 模块，使其能在 `my-web-mvc` 模块中使用

`MapBasedConfigSource` 类构造器添加 `lazy` 懒加载判断，并把 `source` 私有常量改成保护权限的变量：

```java
// private final Map<String, String> source;
protected Map<String, String> source;
```

