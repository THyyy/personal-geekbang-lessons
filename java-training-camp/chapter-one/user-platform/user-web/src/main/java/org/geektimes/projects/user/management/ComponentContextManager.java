package org.geektimes.projects.user.management;

import org.geektimes.Context.ComponentContext;

import java.util.List;

/**
 * ComponentContext 管理类
 *
 * @author tanheyuan
 * @version 1.0
 * @since 2021/3/17
 */
public class ComponentContextManager implements ComponentContextManagerMBean{

    private ComponentContext componentContext = ComponentContext.getInstance();

    @Override
    public List<String> getComponentNames() {
        return componentContext.getComponentNames();
    }

    @Override
    public <C> C getComponent(String name) {
        return componentContext.getComponent(name);
    }

    @Override
    public List<Object> getComponentByPrefix(String prefix) {
        return componentContext.getComponentByPrefix(prefix);
    }
}
