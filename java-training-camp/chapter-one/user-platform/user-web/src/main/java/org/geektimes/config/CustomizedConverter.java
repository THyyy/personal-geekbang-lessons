package org.geektimes.config;

import org.eclipse.microprofile.config.spi.Converter;

/**
 * @author tanheyuan
 * @version 1.0
 * @since 2021/3/16
 */
public interface CustomizedConverter<T> extends Converter<T> {

    /**
     * 类型匹配
     * 如果匹配则使用该自定义类型转换器
     *
     * @param type 转换的目标类型
     * @return 是否匹配，匹配为 true，不匹配为 false
     */
    boolean matching(Class<T> type);
}
