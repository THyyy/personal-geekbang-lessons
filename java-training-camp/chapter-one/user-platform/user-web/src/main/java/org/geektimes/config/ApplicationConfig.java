package org.geektimes.config;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;

import java.util.Optional;

/**
 * 应用程序配置类
 *
 * @author yuancome
 * @date 2021/3/16
 */
public class ApplicationConfig implements Config {


	public final static ApplicationConfig INSTANCE = new ApplicationConfig();

	/**
	 * 获取配置
	 *
	 * @return 配置类
	 */
	protected Config getConfig(){
		return ConfigProvider.getConfig(Thread.currentThread().getContextClassLoader());
	}


	@Override
	public <T> T getValue(String name, Class<T> propertyType) {
		return getConfig().getValue(name, propertyType);
	}

	@Override
	public ConfigValue getConfigValue(String name) {
		return getConfig().getConfigValue(name);
	}

	@Override
	public <T> Optional<T> getOptionalValue(String name, Class<T> propertyType) {
		return getConfig().getOptionalValue(name , propertyType);
	}

	@Override
	public Iterable<String> getPropertyNames() {
		return getConfig().getPropertyNames();
	}

	@Override
	public Iterable<ConfigSource> getConfigSources() {
		return getConfig().getConfigSources();
	}

	@Override
	public <T> Optional<Converter<T>> getConverter(Class<T> propertyType) {
		return getConfig().getConverter(propertyType);
	}

	@Override
	public <T> T unwrap(Class<T> propertyType) {
		return getConfig().unwrap(propertyType);
	}
}
