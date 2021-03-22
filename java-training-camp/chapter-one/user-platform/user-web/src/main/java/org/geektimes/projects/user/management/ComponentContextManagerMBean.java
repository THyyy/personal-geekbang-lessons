package org.geektimes.projects.user.management;

import java.util.List;

/**
 * {@link org.geektimes.component.context.ComponentContext} MBean 接口描述
 *
 * @author tanheyuan
 * @version 1.0
 * @since 2021/3/17
 */
public interface ComponentContextManagerMBean {

    List<String> getComponentNames();

    <C> C getComponent(String name);

    List<Object> getComponentByPrefix(String prefix);


}
