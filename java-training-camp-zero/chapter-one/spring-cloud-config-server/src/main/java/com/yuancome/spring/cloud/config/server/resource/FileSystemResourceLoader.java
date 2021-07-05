package com.yuancome.spring.cloud.config.server.resource;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.File;

/**
 * 文件系统资源加载器
 *
 * @author yuancome
 * @date 2021/6/2
 */
public class FileSystemResourceLoader extends DefaultResourceLoader {

    @Override
    protected Resource getResourceByPath(String path) {
        File file = new File(path);
        return new FileSystemContextResource(file);
    }

}
