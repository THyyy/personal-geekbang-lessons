package com.yuancome.spring.security.loadbalance.random;

import com.yuancome.spring.security.loadbalance.AbstractCluster;
import com.yuancome.spring.security.loadbalance.Node;

import java.util.List;
import java.util.Random;

/**
 * 随机算法
 *
 * @author yuancome
 * @date 2021/5/19
 */

public class RandomCluster extends AbstractCluster {

    private static final Random random = new Random();

    @Override
    public List<Node> getNodes() {
        return super.getNodes();
    }

    @Override
    public void addNode(Node node) {
        getNodes().add(node);
    }

    @Override
    public void removeNode(String nodeName) {
        getNodes().removeIf(e -> nodeName.equals(e.getName()));
    }

    @Override
    public Node get(String key) {
        return getNode();
    }

    public Node getNode() {
        int number = random.nextInt(getNodes().size());
        return getNodes().get(number);
    }
}
