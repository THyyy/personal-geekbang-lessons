package org.geektimes.projects.user.jmx;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * MBean 管理器
 *
 * @author tanheyuan
 * @version 1.0
 * @since 2021/3/16
 */
public class MBeanManager {

    private static final MBeanServer MBEAN_SERVER;

    static {
        MBEAN_SERVER =  ManagementFactory.getPlatformMBeanServer();
    }

    /**
     * 注册 MBean
     *
     * @param mBeanName
     * @param mBean
     */
    public static void registerMBean(String mBeanName, Object mBean) throws Exception{
        ObjectName objectName = new ObjectName(mBeanName);
        MBEAN_SERVER.registerMBean(mBean, objectName);
    }
}
