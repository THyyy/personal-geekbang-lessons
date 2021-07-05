package com.yuancome.spring.security.loadbalance;

import com.yuancome.spring.security.loadbalance.Cluster;
import com.yuancome.spring.security.loadbalance.Node;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * 抽象 cluster
 * 目前并没有抽象方法，定义为抽象是为了作为钩子，后续扩展使用
 *
 * @author yuancome
 * @date 2021/5/19
 */

public abstract class AbstractCluster implements Cluster {

    private static MessageDigest md5 = null;

    protected List<Node> nodes = new ArrayList<>();

    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm has not found");
        }
    }

    @Override
    public List<Node> getNodes() {
        return this.nodes;
    }


    /**
     * 实现一致性哈希算法中使用的哈希函数,使用MD5算法来保证一致性哈希的平衡性
     */
    public long hash(String key) {
        md5.reset();
        md5.update(key.getBytes());
        byte[] byteOfKey = md5.digest();
        //具体的哈希函数实现细节--每个字节 & 0xFF 再移位
        long result = ((long) (byteOfKey[3] & 0xFF) << 24)
                | ((long) (byteOfKey[2] & 0xFF) << 16
                | ((long) (byteOfKey[1] & 0xFF) << 8) | (long) (byteOfKey[0] & 0xFF));
        return result & 0xffffffffL;
    }
}
