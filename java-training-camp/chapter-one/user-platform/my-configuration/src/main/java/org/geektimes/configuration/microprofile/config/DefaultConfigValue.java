package org.geektimes.configuration.microprofile.config;

import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;

/**
 * 通用配置值
 *
 * @author yuancome
 * @date 2021/3/16
 */
public class DefaultConfigValue implements ConfigValue {

    private ConfigSource configSource;

    private String name;

    public DefaultConfigValue(ConfigSource  configSource , String name){
        this.configSource = configSource;
        this.name = name;
    }


    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.configSource.getValue(this.name);
    }

    @Override
    public String getRawValue() {
        return this.configSource.getValue(this.name);
    }

    @Override
    public String getSourceName() {
        return this.configSource.getName();
    }

    @Override
    public int getSourceOrdinal() {
        return this.configSource.getOrdinal();
    }
}
