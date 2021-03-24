package org.geektimes.configuration.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 基于 Map 数据结构 {@link ConfigSource} 实现
 */
public abstract class MapBasedConfigSource implements ConfigSource {

    private final String name;

    private final int ordinal;

    protected Map<String, String> source;

    protected MapBasedConfigSource(String name, int ordinal) {
        this(name, ordinal, false);
    }

    /**
     * 配置源父类构造器
     *
     * @param name 配置源名称
     * @param ordinal 配置源优先级，数值高的优先
     * @param lazy 是否懒加载，若为 true，需要子类添加 this.source = getProperties();
     */
    protected MapBasedConfigSource(String name, int ordinal, boolean lazy) {
        this.name = name;
        this.ordinal = ordinal;
        if (!lazy) {
            this.source = getProperties();
        }
    }

    /**
     * 获取配置数据 Map
     *
     * @return 不可变 Map 类型的配置数据
     */
    public final Map<String, String> getProperties() {
        Map<String,String> configData = new HashMap<>();
        try {
            prepareConfigData(configData);
        } catch (Throwable cause) {
            throw new IllegalStateException("准备配置数据发生错误",cause);
        }
        return Collections.unmodifiableMap(configData);
    }

    /**
     * 准备配置数据
     * @param configData
     * @throws Throwable
     */
    protected abstract void prepareConfigData(Map configData) throws Throwable;

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final int getOrdinal() {
        return ordinal;
    }

    @Override
    public Set<String> getPropertyNames() {
        return source.keySet();
    }

    @Override
    public String getValue(String propertyName) {
        return source.get(propertyName);
    }

}
