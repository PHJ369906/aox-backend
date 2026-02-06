package com.aox.infrastructure.oss.client;

import org.springframework.web.multipart.MultipartFile;

/**
 * 对象存储统一接口
 * 支持多种云存储商：MinIO、阿里云OSS、腾讯云COS、七牛云等
 *
 * @author Aox Team
 */
public interface OssClient {

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
     * 获取存储类型
     *
     * @return 存储类型标识
     */
    String getStorageType();
}
