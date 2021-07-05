package com.yuancome.spring.cloud.config.server.resource;

import java.io.File;

import org.springframework.core.io.ContextResource;
import org.springframework.core.io.FileSystemResource;

/**
 * 文件系统上下文资源实现
 *
 * @author yuancome
 * @date 2021/6/2
 */
public class FileSystemContextResource extends FileSystemResource implements ContextResource {


    public FileSystemContextResource(File file) {
        super(file);
    }

    @Override
    public String getPathWithinContext() {
        return getPath();
    }

}
