package org.geektimes.config;


import org.apache.commons.collections.CollectionUtils;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;
import org.geektimes.Context.ComponentContext;
import org.geektimes.config.converter.CustomizedConverter;
import org.geektimes.config.source.SystemPropertiesConfigSource;
import org.geektimes.utils.TypeTransFormUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * 通用参数配置类
 *
 * @author tanheyuan
 * @version 1.0
 * @since 2021/3/16
 */
public class CommonConfig implements Config {
    private final Logger logger = Logger.getLogger("org.geektimes.config.JavaConfig");

    /**
     * 内部可变的集合，不要直接暴露在外面
     */
    private List<ConfigSource> configSources = new LinkedList<>();

    private static Comparator<ConfigSource> configSourceComparator = (o1, o2) -> Integer.compare(o2.getOrdinal(), o1.getOrdinal());

    public CommonConfig() {
        // SPI 方式加载配置源
        ClassLoader classLoader = getClass().getClassLoader();
        ServiceLoader<ConfigSource> serviceLoader = ServiceLoader.load(ConfigSource.class, classLoader);
        serviceLoader.forEach(configSources::add);
        // 排序
        configSources.sort(configSourceComparator);
    }

    @Override
    public <T> T getValue(String propertyName, Class<T> propertyType) {
        String propertyValue = getPropertyValue(propertyName);
        // String 转换成目标类型
        return propertyValue != null ? getUsableConverter(propertyType).convert(propertyValue) : null;
    }

    @Override
    public ConfigValue getConfigValue(String propertyName) {
        ConfigSource configSource = getConfigSource(propertyName);
        return configSource != null ? new CommonConfigValue(configSource, propertyName) : null;
    }

    @Override
    public <T> List<T> getValues(String propertyName, Class<T> propertyType) {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        try {
            return type.newInstance();
        } catch (InstantiationException e) {
            logger.severe("实例化异常：" + e);
        } catch (IllegalAccessException e) {
            logger.severe("访问控制权限限制，访问异常：" + e);
        }
        return null;
    }

    protected String getPropertyValue(String propertyName) {
        String propertyValue = null;
        for (ConfigSource configSource : configSources) {
            propertyValue = configSource.getValue(propertyName);
            if (propertyValue != null) {
                break;
            }
        }
        return propertyValue;
    }

    @Override
    public <T> Optional<T> getOptionalValue(String propertyName, Class<T> propertyType) {
        T value = getValue(propertyName, propertyType);
        return Optional.ofNullable(value);
    }

    @Override
    public Iterable<String> getPropertyNames() {
        Iterable<ConfigSource> configSources = getConfigSources();
        Set<String> propertyNames = new LinkedHashSet<>();
        configSources.forEach(item -> propertyNames.addAll(item.getPropertyNames()));
        return propertyNames;
    }

    /**
     * JNDI 方式加载配置源
     *
     * @return
     */
    @Override
    public Iterable<ConfigSource> getConfigSources() {
        List<Object> config = ComponentContext.getInstance().getComponentByPrefix("config");
        if (config == null) {
            return null;
        }
        List<ConfigSource> configSourceList = config.stream().filter(item -> item instanceof ConfigSource).map(item -> (ConfigSource) item).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(configSourceList)) {
            return Arrays.asList(new SystemPropertiesConfigSource());
        }
        configSourceList.add(new SystemPropertiesConfigSource());
        //排序
        configSourceList.sort(configSourceComparator);
        configSources = configSourceList;
        return Collections.unmodifiableList(configSources);
    }

    /**
     * 获取配置源
     *
     * @param propertyName 配置属性名
     * @return
     */
    private ConfigSource getConfigSource(String propertyName) {
        Iterable<ConfigSource> configSources = getConfigSources();
        for (ConfigSource configSource : configSources) {
            if (configSource.getPropertyNames().contains(propertyName)) {
                return configSource;
            }
        }
        return null;
    }

    @Override
    public <T> Optional<Converter<T>> getConverter(Class<T> tClass) {
        return Optional.of(getUsableConverter(tClass));
    }

    /**
     * 获取一个可用的转换器
     *
     * @param type 转换的目标类型
     * @param <T>  可用的转换器实例
     * @return
     */
    private <T> Converter<T> getUsableConverter(Class<T> type) {
        // 自定义加载器通过 JNDI 加载到应用上下文
        List<Object> customizedConverterList = ComponentContext.getInstance().getComponentByPrefix("converter");
        Converter<T> converter = null;
        if (!CollectionUtils.isEmpty(customizedConverterList)) {
            Optional<CustomizedConverter> optional = customizedConverterList.stream()
                    .filter(item -> item instanceof CustomizedConverter)
                    .map(item -> (CustomizedConverter) item).findFirst();
            if (optional.isPresent()) {
                converter = optional.get();
            }
        }
        return converter == null ? value -> TypeTransFormUtils.transform(value, type) : converter;
    }
}
