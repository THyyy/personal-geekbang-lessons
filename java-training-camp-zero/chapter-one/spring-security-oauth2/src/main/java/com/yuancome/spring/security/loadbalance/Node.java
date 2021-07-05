package com.yuancome.spring.security.loadbalance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务器节点
 *
 * @author yuancome
 * @date 2021/5/19
 */

public class Node {
    /**
     * 服务器 ip
     */
    private String ip;
    /**
     * 服务器端口
     */
    private int port;
    /**
     * 服务器名称
     */
    private String name;
    /**
     * 数据存储 map
     */
    private Map<String, Object> data = new ConcurrentHashMap<>();

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public Node(String ip, int port, String name) {
        this.ip = ip;
        this.port = port;
        this.name = name;
    }

    public Map<String, Object> getData() {
        return data;
    }

    /**
     * 获取值
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        return data.get(key);
    }

    /**
     * 加入新值，如果key存在旧值则返回旧值
     *
     * @param key
     * @param value
     * @return
     */
    public Object put(String key, Object value) {
        return data.put(key, value);
    }

}
