package com.aox.common.core.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储服务接口
 * 定义文件存储的通用操作，具体实现由基础设施层提供
 *
 * @author Aox Team
 */
public interface FileStorageService {

    /**
     * 文件上传
     *
     * @param file 文件
     * @return 文件访问URL
     */
    String upload(MultipartFile file);

    /**
     * 文件上传（指定路径）
     *
     * @param file     文件
     * @param filePath 文件路径
     * @return 文件访问URL
     */
    String upload(MultipartFile file, String filePath);

    /**
     * 文件删除
     *
     * @param fileName 文件名
     */
    void delete(String fileName);

    /**
     * 获取文件访问URL
     *
     * @param fileName 文件名
     * @return 文件访问URL
     */
    String getFileUrl(String fileName);

    /**
     * 获取当前使用的存储类型
     *
     * @return 存储类型（如: minio, aliyun, tencent）
     */
    String getCurrentStorageType();
}
