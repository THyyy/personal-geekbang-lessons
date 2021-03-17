package org.geektimes.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * properties文件配置源
 *
 * @author : KangNing Hu
 */
public class PropertiesConfigSource implements ConfigSource {

    private static final Map<String, String> PROPERTIES = new HashMap<>();

    static {
        String localPath = "META-INF/application.properties";
        InputStream resourceAsStream;
        try {
            resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(localPath);
            if (resourceAsStream != null) {
                Properties properties = new Properties();
                properties.load(resourceAsStream);
                Enumeration<String> enumeration = (Enumeration<String>) properties.propertyNames();
                while (enumeration.hasMoreElements()) {
                    String name = enumeration.nextElement();
                    PROPERTIES.put(name, (String) properties.get(name));
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Set<String> getPropertyNames() {
        return PROPERTIES.keySet();
    }

    @Override
    public String getValue(String propertyName) {
        return PROPERTIES.get(propertyName);
    }

    @Override
    public String getName() {
        return "properties-config";
    }

    @Override
    public int getOrdinal() {
        return 999;
    }
}
