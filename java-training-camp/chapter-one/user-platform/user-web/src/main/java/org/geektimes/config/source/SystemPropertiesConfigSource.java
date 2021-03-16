package org.geektimes.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author tanheyuan
 * @version 1.0
 * @since 2021/3/16
 */
public class SystemPropertiesConfigSource implements ConfigSource {

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> propertiesMap = new HashMap<>();
        Properties properties = System.getProperties();
        for (Object o : properties.keySet()) {
            String key = (String) o;
            propertiesMap.put(key, properties.getProperty(key));
        }
        return propertiesMap;
    }

    @Override
    public int getOrdinal() {
        return 0;
    }

    @Override
    public Set<String> getPropertyNames() {
        return System.getProperties().stringPropertyNames();
    }

    @Override
    public String getValue(String propertyName) {
        String value = System.getProperty(propertyName);
        if (value == null){
            value = System.getenv(propertyName);
        }
        return value;
    }

    @Override
    public String getName() {
        return "system-properties";
    }
}
