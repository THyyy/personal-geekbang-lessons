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



### 第六周

- 提供一套抽象 API 实现对象的序列化和反序列化

抽出一个序列化和反序列化抽象类 `org.geektimes.cache.serialize.CacheSerializer`

```java
// 序列化方法
public final byte[] serialize(Serializable source) {
        if (source == null) {
            return new byte[0];
        }
  			// 抽象方法 doSerialize 给子类实现
        return doSerialize(source);
}

// 反序列化方法
public final <T> T deserialize(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
  			// 抽象方法 doDeserialize 给子类实现
        return doDeserialize(data);
}
```

默认实现类 `org.geektimes.cache.serialize.DefaultCacheSerializer`

```java
public class DefaultCacheSerializer extends CacheSerializer {

    @Override
    public byte[] doSerialize(Serializable source) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);) {
            objectOutputStream.writeObject(source);
            return out.toByteArray();
        } catch (IOException e) {
            throw new CacheException(e.getMessage(), e);
        }
    }

    @Override
    public <T> T doDeserialize(byte[] data) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);) {
            return (T) objectInputStream.readObject();
        } catch (Exception e) {
            throw new CacheException(e.getMessage(), e);
        }
    }
}
```

关于 `Redis` 的具体序列化和反序列化实现类 `org.geektimes.cache.redis.DefaultRedisSerializer`

```java
public K decodeKey(ByteBuffer byteBuffer) {
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes, 0, bytes.length);
        return serializer.deserialize(bytes);
}


public V decodeValue(ByteBuffer byteBuffer) {
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes, 0, bytes.length);
        return serializer.deserialize(bytes);
}

public ByteBuffer encodeKey(K key) {
        return ByteBuffer.wrap(serializer.serialize(key));
}


public ByteBuffer encodeValue(V value) {
        return ByteBuffer.wrap(serializer.serialize(value));
}
```

- 通过 Lettuce 实现一套 Redis CacheManager 以及 Cache

具体参考：`org.geektimes.cache.redis.LettuceCache` 和 `org.geektimes.cache.redis.LettuceCacheManager`，详细代码不列出

## 第二模块：Java 开源混合架构

### 第七周

**从本周开始使用 `open-source` 分支开发**

- 使用 `Spring Boot` 来实现一个整合 `Gitee` 或者 `Github` OAuth2 认证

本次作业相对比较简单，主要是 `Spring Security` 的一些配置，基本上就完成了，但是目的还是为了了解 OAuth 2 认证

本次主要是参考 [Spring官网 OAuth2 教程](https://spring.io/guides/tutorials/spring-boot-oauth2/) 进行实现，下面是 OAuth 2 的认证流程：

![OAuth 2 认证流程](http://processon.com/chart_image/5c63c941e4b0641c83f44c0c.png)



`Spring Security` 的核心配置 `GithubConfigurerAdapter` 配置类：

```java
@Configuration
public class GithubConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(a -> a
                 // 匹配请求放行               
                .antMatchers("/", "/error", "/webjars/**").permitAll()
        		// 对其他请求均进行验证，但是没有对验证用户角色做权限管理                       
                .anyRequest().authenticated() 
         //退出页面放行                      
        ).logout(l -> l.logoutSuccessUrl("/").permitAll() 
         // 配置跨域请求        
        ).csrf(c -> c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
         // 异常拦截处理，均返回 403 状态码      
        ).exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
         // 设置 OAuth2.0 登录，采用默认的 OAuth2LoginConfigurer 配置                   
        ).oauth2Login(); 
    }
}    
```

因为是通过默认的 `OAuth2LoginConfigurer` 进行实现，配置文件中需要如下配置：

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          github:
            # 客户端id
            clientId: github-client-id
            # 密钥
            clientSecret: github-secret-key
```

随即访问在 GitHub 上配置的主页 `http://localhost:8080` 点击 `click here` 即可进行 OAuth 2 认证授权

### 第八周

- 如何解决多个 `WebSecurityConfigurerAdapter` **Bean** 相同配置相互冲突的问题？

问题原因在于：`WebSecurityConfigurerAdapter#getHttp` 方法

```java
protected final HttpSecurity getHttp() throws Exception {
		if (this.http != null) {
			return this.http;
		}
		...
        // 此处通过 new 的方式构建 HttpSecurity 对象
		this.http = new HttpSecurity(this.objectPostProcessor, this.authenticationBuilder, sharedObjects);
		...
		return this.http;
	}
```

因为 `WebSecurityConfigurerAdapter` 在实例化的时候是 new 一个 HttpSecurity 对象，然后将其添加到 `List<SecurityBuilder<? extends SecurityFilterChain>> securityFilterChainBuilders` 集合中，在 `WebSecurity` 构建方法 `performBuild` 中遍历并创建 `FilterChainProxy` 对象：

```java
@Override
protected Filter performBuild() throws Exception {
    	...
    	// 遍历 securityFilterChainBuilders 列表
        // 里面的 SecurityBuilder 对象是WebSecurityConfigurerAdapter#init方法添加的
		for (SecurityBuilder<? extends SecurityFilterChain> securityFilterChainBuilder : this.securityFilterChainBuilders) {
			securityFilterChains.add(securityFilterChainBuilder.build());
		}
    	// 创建 FilterChainProxy 对象
    	// 并将securityFilterChains设置为FilterChainProxy的filterChains属性
		FilterChainProxy filterChainProxy = new FilterChainProxy(securityFilterChains);
		...
		return result;
}
```

在真正的过滤器逻辑中，`FilterChainProxy#doFilter` 方法通过调用 `doFilterInternal` 方法间接调用 `getFilters` 方法获取实际的 `SecurityFilterChain` 过滤器

```java
private List<Filter> getFilters(HttpServletRequest request) {
		int count = 0;
		for (SecurityFilterChain chain : this.filterChains) {
			if (logger.isTraceEnabled()) {
				logger.trace(LogMessage.format("Trying to match request against %s (%d/%d)", chain, ++count,
						this.filterChains.size()));
			}
			if (chain.matches(request)) {
				return chain.getFilters();
			}
		}
		return null;
	}
```

从上面代码来看，实际在具体匹配中只会返回第一个匹配的 `SecurityFilterChain` 的过滤器。到此，实际的 `HttpSecurity` 匹配逻辑完成，而作业中的问题是因为实际开发中，开发人员对 `Spring Boot Security` 使用不当导致配置覆盖问题。

具体多个 `HttpSecurity` 配置使用方式可以参考[官方文档](https://docs.spring.io/spring-security/site/docs/current/reference/html5/#multiple-httpsecurity)。

解决方法：所有的配置定义在一个配置类 `com.yuancome.spring.security.oauth2.adapter.GlobalHttpSecurityConfig` 中，根据配置定义顺序从上到下进行顺序限定

```java
@Configuration
public class GlobalHttpSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public SecurityFilterChain filter1(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.authorizeRequests(a -> a
                .antMatchers("/", "/error", "/webjars/**").permitAll()
                .anyRequest().authenticated() // 对这些请求均进行验证，但是没有对验证用户角色做权限管理
        ).build();
    }

    @Bean
    public SecurityFilterChain filter2(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.authorizeRequests(a -> a
                .antMatchers("/", "/error", "/webjars/**").permitAll()
                .anyRequest().authenticated() // 对这些请求均进行验证，但是没有对验证用户角色做权限管理
        ).csrf().disable().build();
    }
}
```

### 第九周

- 如何清除某个 `Spring Cache` 所有的 Keys 关联的对象(如果 `Redis` 中心化方案 - `Redis + Sentinel`, 如果 `Redis` 去中心化方案 - `Redis Cluster`)

`RedisCache` 在每个缓存值 `push` 的时候保存到 `Reids List` 中，然后该 `Redis Cache` 存储的所有缓存的 `key` ，清除 `RedisCache` 的所有缓存只需要遍历 `Redis List`，获取该 `RedisCache` 关联的所有 `key`，然后依次删除即可。

`RedisCache` 构造函数初始化 `key` 的前缀字节数组和 `Redis List` 的 `key` 的名称。

```
public RedisCache(String name, Jedis jedis) {
    Objects.requireNonNull(name, "The 'name' argument must not be null.");
    Objects.requireNonNull(jedis, "The 'jedis' argument must not be null.");
    this.name = name;
    this.jedis = jedis;
    prefixBytes = (this.name + ":").getBytes(StandardCharsets.UTF_8);
    namespaceBytes = ("namespace:" + this.name).getBytes(StandardCharsets.UTF_8);
}
```

往 `RedisCache` 存数据时，先将 `key` 序列化，然后再加上前缀的字节数组 `prefixBytes`，新的字节数组 `actualKeyBytes` 作为真正的`Redis `的`key`，并将 `actualKeyBytes` 存到 `Redis List` 中。

```java
public void put(Object key, Object value) {
    byte[] actualKeyBytes = getActualKeyBytes(key);
    byte[] valueBytes = serialize(value);
    jedis.set(actualKeyBytes, valueBytes);
    jedis.lpush(namespaceBytes, actualKeyBytes);
}

private byte[] getActualKeyBytes(Object key) {
    byte[] keyBytes = serialize(key);
    return getMergeBytes(prefixBytes, keyBytes);
}

protected byte[] getMergeBytes(byte[] prefixBytes, byte[] sourceBytes) {
    byte[] result = new byte[prefixBytes.length + sourceBytes.length];
    System.arraycopy(prefixBytes, 0, result, 0, prefixBytes.length);
    System.arraycopy(sourceBytes, 0, result, prefixBytes.length, sourceBytes.length);
    return result;
}
```

`RedisCache#clear` 方法先根据 `Redis List` 的缓存的 `key` 列表依次执行删除操作，然后再删除 `Redis List` 自身即可。

```java
public void clear() {
    List<byte[]> list = jedis.lrange(namespaceBytes, 0L, jedis.llen(namespaceBytes));
    if (list != null && !list.isEmpty()) {
        list.forEach(jedis::del);
    }
    jedis.del(namespaceBytes);
}
```

- 如何将 `RedisCacheManager` 与 `@Cacheable` 注解打通

将`RedisCacheManager` 与 `@Cacheable` 注解打通主要是通过 `CacheManager` 接口进行实现，可以看出我们的 `RedisCacheManager` 已经实现了 `CacheManager` 接口，只需要将其注入到 `Spring` 容器中即可：

```java
@Configuration
public class RedisCacheConfig {

    @Bean
    public CacheManager redisCacheManager() {
        String uri = "127.0.0.1";
        return new RedisCacheManager(uri);
    }
}
```

具体配置肯定不止这些，目前只是作为一个简单的示例进行演示。

### 第十周

- 完善 `@org.geektimes.projects.user.mybatis.annotation.EnableMyBatis` 实现，尽可能多地注入 `org.mybatis.spring.SqlSessionFactoryBean` 中依赖的组件

根据 `org.mybatis.spring.SqlSessionFactoryBean` 类来看，主要有以下的组件：

```java
		private static final ResourcePatternResolver RESOURCE_PATTERN_RESOLVER = new PathMatchingResourcePatternResolver();
    private static final MetadataReaderFactory METADATA_READER_FACTORY = new CachingMetadataReaderFactory();
    private Resource configLocation;
    private Configuration configuration;
    private Resource[] mapperLocations;
    private DataSource dataSource;
    private TransactionFactory transactionFactory;
    private Properties configurationProperties;
    private SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
    private SqlSessionFactory sqlSessionFactory;
    private String environment = SqlSessionFactoryBean.class.getSimpleName();
    private boolean failFast;
    private Interceptor[] plugins;
    private TypeHandler<?>[] typeHandlers;
    private String typeHandlersPackage;
    private Class<? extends TypeHandler> defaultEnumTypeHandler;
    private Class<?>[] typeAliases;
    private String typeAliasesPackage;
    private Class<?> typeAliasesSuperType;
    private LanguageDriver[] scriptingLanguageDrivers;
    private Class<? extends LanguageDriver> defaultScriptingLanguageDriver;
    private DatabaseIdProvider databaseIdProvider;
    private Class<? extends VFS> vfs;
    private Cache cache;
    private ObjectFactory objectFactory;
    private ObjectWrapperFactory objectWrapperFactory;
```

使用时一般不会全部用到上面的所有组件参数，比较常用的就是 `configLocation`，`mapperLocations`，`dataSource` 等，由于时间关系就先添加简单的 `typeHandlersPackage`，`typeAliasesPackage`， 这两个分别指定了`类型处理包` 和 `类型别名包`。

```java
public @interface EnableMyBatis {
    String typeHandlersPackage() default "";

    String typeAliasesPackage() default "";
}
```

然后在 `MyBatisBeanDefinitionRegistrar#registerBeanDefinitions` 方法中补充：

```java
beanDefinitionBuilder.addPropertyValue("typeHandlersPackage", attributes.get("typeHandlersPackage"));
beanDefinitionBuilder.addPropertyValue("typeAliasesPackage", attributes.get("typeAliasesPackage"));
```

此外还完善了 `configurationProperties` ：

```java
// 获取配置文件路径
String configLocation = (String) attributes.get("configLocation");
beanDefinitionBuilder.addPropertyValue("configLocation", configLocation);
	// 根据配置文件路径获取配置文件源
	if (StringUtils.isNotEmpty(configLocation)) {
		Properties properties = resolveConfigurationProperties(configLocation);
    beanDefinitionBuilder.addPropertyValue("configurationProperties", properties);
  }
```

至此，简单地完成了作业内容。