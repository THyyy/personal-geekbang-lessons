package org.geektimes.utils;

/**
 * 类型转换工具类
 *
 * @author tanheyuan
 * @version 1.0
 * @since 2021/3/16
 */
public class TypeTransFormUtils {

    /**
     * 类型转换：8 大基本类型 + String
     *
     * @param value 转换值
     * @param targetClass 转换目标类 Class 对象
     * @param <T> 转换目标类
     * @return 转换目标类实例
     */
    public static <T> T transform(String value, Class<T> targetClass) {
        if (value == null) {
            return null;
        }
        Object result;
        if (targetClass == String.class) {
            result = value;
        } else if (targetClass == Integer.class || targetClass == int.class) {
            result = Integer.valueOf(value);
        } else if (targetClass == Long.class || targetClass == long.class) {
            result = Long.valueOf(value);
        } else if (targetClass == Short.class || targetClass == short.class) {
            result = Short.valueOf(value);
        } else if (targetClass == Byte.class || targetClass == byte.class) {
            result = Byte.valueOf(value);
        } else if (targetClass == Float.class || targetClass == float.class) {
            result = Float.valueOf(value);
        } else if (targetClass == Double.class || targetClass == double.class) {
            result = Double.valueOf(value);
        } else if (targetClass == Character.class || targetClass == char.class) {
            result = value.toCharArray()[0];
        } else if (targetClass == Boolean.class || targetClass == boolean.class) {
            result = Boolean.valueOf(value);
        } else {
            throw new IllegalArgumentException("不支持的类型" + targetClass.getName());
        }
        return (T) result;
    }
}
