package com.aox.infrastructure.oss.service;

import com.aox.common.core.service.FileStorageService;
import com.aox.infrastructure.oss.client.OssClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * OSS 服务实现
 * 实现 FileStorageService 接口，提供统一文件上传服务
 * 支持动态切换云存储商
 *
 * @author Aox Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OssService implements FileStorageService {

    private final OssConfigService ossConfigService;

    /**
     * 文件上传
     * 自动根据数据库配置使用对应的云存储服务
     *
     * @param file 文件
     * @return 文件访问URL
     */
    @Override
    public String upload(MultipartFile file) {
        OssClient ossClient = ossConfigService.getCurrentOssClient();
        String fileUrl = ossClient.upload(file);

        log.info("文件上传成功，存储类型: {}, URL: {}", ossClient.getStorageType(), fileUrl);
        return fileUrl;
    }

    /**
     * 文件上传（指定路径）
     *
     * @param file     文件
     * @param filePath 文件路径
     * @return 文件访问URL
     */
    @Override
    public String upload(MultipartFile file, String filePath) {
        OssClient ossClient = ossConfigService.getCurrentOssClient();
        return ossClient.upload(file, filePath);
    }

    /**
     * 文件删除
     *
     * @param fileName 文件名
     */
    @Override
    public void delete(String fileName) {
        OssClient ossClient = ossConfigService.getCurrentOssClient();
        ossClient.delete(fileName);

        log.info("文件删除成功，存储类型: {}, 文件: {}", ossClient.getStorageType(), fileName);
    }

    /**
     * 获取文件访问URL
     *
     * @param fileName 文件名
     * @return 文件访问URL
     */
    @Override
    public String getFileUrl(String fileName) {
        OssClient ossClient = ossConfigService.getCurrentOssClient();
        return ossClient.getFileUrl(fileName);
    }

    /**
     * 获取当前使用的存储类型
     *
     * @return 存储类型
     */
    @Override
    public String getCurrentStorageType() {
        OssClient ossClient = ossConfigService.getCurrentOssClient();
        return ossClient.getStorageType();
    }
}
