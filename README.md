# personal-geekbang-lessons——个人极客时间课程代码
## 第一模块：Java EE 单体应用

### 第一周

- 完善自研 MVC 框架 

1. 修复了 `org.geektimes.web.mvc.FrontControllerServlet#initHandleMethods ` 方法**路径重复拼接**问题
2. 通过反射修复了 `org.geektimes.web.mvc.FrontControllerServlet#service ` 方法一个**请求路径对应一个控制器类**问题

- 完成 JNDI 获取数据库源

1. 修复 `src/main/webapp/META-INF/context.xml` 存放路径问题

- 完成用户注册功能

1. 把数据库操作相关方法抽出到 `org.geektimes.projects.user.sql.JdbcTemplate` 类

### 第二周

- 完善简易版的依赖注入框架，并通过该框架完成用户注册功能

1. 根据第一周的 `org.geektimes.projects.user.sql.JdbcTemplate` 类进行相应的依赖注入，实现依赖注入链：`UserController -> UserServiceImpl -> DatabaseUserRepository -> JdbcTemplate -> DBConnectionManager`

- 实现用户注册数据校验（id > 0，6 < password 长度 < 32）

### 第三周

- 实现自定义 JMX MBean，通过 Jolokia 做 Servlet 代理

具体实现查看：`org.geektimes.projects.user.web.listener.ComponentContextInitializerListener#contextInitialized` 方法

自定义  JMX MBean 为：`org.geektimes.projects.user.management.ComponentContextManager`

- 扩展 `org.eclipse.microprofile.config.spi.ConfigSource` 中的实现

本地配置环境实现：`org.geektimes.config.source.PropertiesConfigSource` 

系统 OS 环境变量实现： `org.geektimes.config.source.SystemPropertiesConfigSource`

- 扩展 `org.eclipse.microprofile.config.spi.Converter` 

主要使用了 `org.geektimes.utils.TypeTransFormUtils` 进行转换，留下了复杂类型的扩展接口类：`org.geektimes.config.converter.CustomizedConverter`

- 本地配置文件：`META-INF/application.properties`

### 第四周

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

`FrontControllerServlet#init` 方法中获取 `servletContext` 上下文，获取对应配置：

```java
public class FrontControllerServlet extends HttpServlet {
     @Override
    public void init(ServletConfig servletConfig) {
        ServletContext servletContext = servletConfig.getServletContext();
        Object config = servletContext.getAttribute("config");
        System.out.println("first out：" + ((Config)config).getConfigValue("application.name").getValue());
        initHandleMethods();
    }
}    
```

### 第五周

- 修复本程序 `org.geektimes.reactive.streams` 包下的程序逻辑

1. 主要是因为接受消息之后应该先处理再判断下一次是否超出设定的最大请求数即可

```java
public class BusinessSubscriber<T> implements Subscriber<T> {
	@Override
    public void onNext(Object o) {
        System.out.println("收到数据：" + o);
        if (++count > 2) { // 当到达数据阈值时，取消 Publisher 给当前 Subscriber 发送数据
            subscription.cancel();
            return;
        }
    }   
}    
```

2. 数据请求处理流程：`Publisher` -> `publisher.subscribe(Subscriber s)` -> `publisher.publish(T data);`
3. 需要注意的是，当队列正常处理完成时需要调用 `org.reactivestreams.Subscriber#onComplete` 方法，异常时需要调用 `org.reactivestreams.Subscriber#onError` 方法

- 继续完善 `my-rest-client POST` 方法

  1. 参考 `org.geektimes.rest.client.HttpGetInvocation` 类创建 `org.geektimes.rest.client.HttpPostInvocation` 类，多加了 `Entity<?> entity` 成员变量作为 `Post` 请求的请求体对象

  - 需要注意，`Post` 请求需要设置请求头内容类型 `Content-type`

  2. `org.geektimes.rest.client.DefaultInvocationBuilder#buildPost` 方法返回值为新增的 `HttpPostInvocation` 类的实例对象
  3. 新增`org.geektimes.projects.user.web.controller.HelloWorldController#testPost` 方法作为测试 `Post` 请求的接口，请求路径为：`127.0.0.1:8080/hello/test`，可以使用 `org.geektimes.rest.demo.RestClientDemo#main` 方法直接测试

  - 目前响应时需要用 `javax.servlet.http.HttpServletResponse` 设置响应数据，但是参考其他小伙伴的代码是不需要设置的，还没找到原因

