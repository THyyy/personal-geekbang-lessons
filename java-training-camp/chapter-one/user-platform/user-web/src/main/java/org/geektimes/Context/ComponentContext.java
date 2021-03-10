package org.geektimes.Context;

import org.geektimes.function.ThrowableAction;
import org.geektimes.function.ThrowableFunction;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Component 上下文（IoC 容器）
 *
 * @author tanheyuan
 * @version 1.0
 * @since 2021/3/5
 */
public class ComponentContext {
    public static final String CONTEXT_NAME = ComponentContext.class.getName();

    private static final String COMPONENT_ENV_CONTEXT_NAME = "java:comp/env";

    private static final Logger logger = Logger.getLogger(CONTEXT_NAME);

    private static ServletContext servletContext; // 请注意
    // 假设一个 Tomcat JVM 进程，三个 Web Apps，会不会相互冲突？（不会冲突）
    // static 字段是 JVM 缓存吗？（是 ClassLoader 缓存）

//    private static ApplicationContext applicationContext;

//    public void setApplicationContext(ApplicationContext applicationContext){
//        ComponentContext.applicationContext = applicationContext;
//        WebApplicationContextUtils.getRootWebApplicationContext()
//    }

    private Context envContext; // Component Env Context

    private ClassLoader classLoader;

    private final Map<String, Object> componentsMap = new LinkedHashMap<>();
    // 需要进行销毁前处理的 Bean 集合，key 为 Bean 对象，value 为对应的处理方法
    private final Map<Object, Method> preDestroyMap = new LinkedHashMap<>();

    /**
     * 获取 ComponentContext
     *
     * @return 组件上下文对象实例
     */
    public static ComponentContext getInstance() {
        return (ComponentContext) servletContext.getAttribute(CONTEXT_NAME);
    }

    private static void close(Context context) {
        if (context != null) {
            ThrowableAction.execute(context::close);
        }
    }

    public void init(ServletContext servletContext) throws RuntimeException {
        ComponentContext.servletContext = servletContext;
        servletContext.setAttribute(CONTEXT_NAME, this);
        // 获取当前 ServletContext（WebApp）ClassLoader
        this.classLoader = servletContext.getClassLoader();
        initEnvContext();
        instantiateComponents();
        initializeComponents();
    }

    /**
     * 实例化组件
     */
    protected void instantiateComponents() {
        // 遍历获取所有的组件名称
        List<String> componentNames = listAllComponentNames();
        // 通过依赖查找，实例化对象（ Tomcat BeanFactory setter 方法的执行，仅支持简单类型）
        componentNames.forEach(name -> componentsMap.put(name, lookupComponent(name)));
    }

    /**
     * 初始化组件（支持 Java 标准 Commons Annotation 生命周期）
     * <ol>
     *  <li>注入阶段 - {@link Resource}</li>
     *  <li>初始阶段 - {@link PostConstruct}</li>
     *  <li>销毁阶段 - {@link PreDestroy}</li>
     * </ol>
     */
    protected void initializeComponents() {
        componentsMap.values().forEach(component -> {
            Class<?> componentClass = component.getClass();
            // 注入阶段 - {@link Resource}
            injectComponents(component, componentClass);
            // 初始阶段 - {@link PostConstruct}
            processPostConstruct(component, componentClass);
            // TODO 实现销毁阶段 - {@link PreDestroy}
            // 这部分处理逻辑应该是先使用一个 Map 进行管理需要处理的 Bean，
            // 到了真正需要 destroy 的时候再调用其 Bean 的处理逻辑
            processPreDestroy(component, componentClass);
        });
    }

    private void injectComponents(Object component, Class<?> componentClass) {
        Stream.of(componentClass.getDeclaredFields())
                .filter(field -> {
                    int mods = field.getModifiers();
                    // 过滤掉非 static 标记和有 @Resource 注解的属性
                    return !Modifier.isStatic(mods) &&
                            field.isAnnotationPresent(Resource.class);
                }).forEach(field -> {
            Resource resource = field.getAnnotation(Resource.class);
            String resourceName = resource.name();
            Object injectedObject = lookupComponent(resourceName);
            field.setAccessible(true);
            try {
                // 注入目标对象
                field.set(component, injectedObject);
            } catch (IllegalAccessException e) {
                logger.log(Level.SEVERE, "属性注入失败！该属性无法访问！！！");
            }
        });
    }

    private void processPostConstruct(Object component, Class<?> componentClass) {
        Stream.of(componentClass.getMethods())
                .filter(method ->
                        !Modifier.isStatic(method.getModifiers()) &&      // 非 static
                                method.getParameterCount() == 0 &&        // 没有参数
                                method.isAnnotationPresent(PostConstruct.class) // 标注 @PostConstruct
                ).forEach(method -> {
            // 执行目标方法
            try {
                method.invoke(component);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void processPreDestroy(Object component, Class<?> componentClass) {
        Stream.of(componentClass.getMethods())
                .filter(method ->
                        !Modifier.isStatic(method.getModifiers()) &&      // 非 static
                                method.getParameterCount() == 0 &&        // 没有参数
                                method.isAnnotationPresent(PreDestroy.class) // 标注 @PostConstruct
                ).forEach(method -> {
            // 将销毁前需要进行处理的类加到 Map 中
            preDestroyMap.put(component, method);
        });
    }

    /**
     * 在 Context 中执行，通过指定 ThrowableFunction 返回计算结果
     *
     * @param function ThrowableFunction
     * @param <R>      返回结果类型
     * @return 返回
     * @see ThrowableFunction#apply(Object)
     */
    protected <R> R executeInContext(ThrowableFunction<Context, R> function) {
        return executeInContext(function, false);
    }

    /**
     * 在 Context 中执行，通过指定 ThrowableFunction 返回计算结果
     *
     * @param function         ThrowableFunction
     * @param ignoredException 是否忽略异常
     * @param <R>              返回结果类型
     * @return 返回
     * @see ThrowableFunction#apply(Object)
     */
    protected <R> R executeInContext(ThrowableFunction<Context, R> function, boolean ignoredException) {
        return executeInContext(this.envContext, function, ignoredException);
    }

    private <R> R executeInContext(Context context, ThrowableFunction<Context, R> function,
                                   boolean ignoredException) {
        R result = null;
        try {
            result = ThrowableFunction.execute(context, function);
        } catch (Throwable e) {
            if (ignoredException) {
                logger.warning(e.getMessage());
            } else {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    protected <C> C lookupComponent(String name) {
        return executeInContext(context -> (C) context.lookup(name));
    }

    /**
     * 通过名称进行依赖查找
     *
     * @param name 组件名称
     * @param <C> 组件对象
     * @return 组件对象
     */
    public <C> C getComponent(String name) {
        return (C) componentsMap.get(name);
    }

    /**
     * 获取所有的组件名称
     *
     * @return 组件名称列表
     */
    public List<String> getComponentNames() {
        return new ArrayList<>(componentsMap.keySet());
    }

    private List<String> listAllComponentNames() {
        return listComponentNames("/");
    }

    protected List<String> listComponentNames(String name) {
        return executeInContext(context -> {
            NamingEnumeration<NameClassPair> e = executeInContext(context, ctx -> ctx.list(name), true);

            // 目录 - Context
            // 节点 -
            if (e == null) { // 当前 JNDI 名称下没有子节点
                return Collections.emptyList();
            }

            List<String> fullNames = new LinkedList<>();
            while (e.hasMoreElements()) {
                NameClassPair element = e.nextElement();
                String className = element.getClassName();
                Class<?> targetClass = classLoader.loadClass(className);
                if (Context.class.isAssignableFrom(targetClass)) {
                    // 如果当前名称是目录（Context 实现类）的话，递归查找
                    fullNames.addAll(listComponentNames(element.getName()));
                } else {
                    // 否则，当前名称绑定目标类型的话话，添加该名称到集合中
                    String fullName = name.startsWith("/") ?
                            element.getName() : name + "/" + element.getName();
                    fullNames.add(fullName);
                }
            }
            return fullNames;
        });
    }

    public void destroy() throws RuntimeException {
        preDestroyMap.forEach((bean,method) -> {
            try {
                method.invoke(bean);
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.log(Level.SEVERE, String.format("%s 类销毁前处理失败，失败原因：%s", bean.getClass(), e));
            }
        });
        logger.log(Level.INFO, "所有 Bean 销毁结束");
        // 因为 Context#close() 方法幂等性问题，会抛出下列异常
        // javax.naming.OperationNotSupportedException: Context is read only
        //close(this.envContext);
    }

    private void initEnvContext() throws RuntimeException {
        if (this.envContext != null) {
            return;
        }
        Context context = null;
        try {
            context = new InitialContext();
            this.envContext = (Context) context.lookup(COMPONENT_ENV_CONTEXT_NAME);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } finally {
            close(context);
        }
    }
}
