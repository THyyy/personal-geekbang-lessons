package com.yuancome.spring.security.loadbalance;

import com.yuancome.spring.security.loadbalance.Node;

import java.util.List;

/**
 * Cluster 接口
 * 用于管理节点
 *
 * @author yuancome
 * @date 2021/5/19
 */

public interface Cluster {

    void addNode(Node node);

    void removeNode(String nodeName);

    default void removeNode(Node node) {
        this.removeNode(node.getName());
    }

    Node get(String key);

    List<Node> getNodes();
}
