package com.aox.infrastructure.oss.client.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.aox.common.core.enums.ErrorCode;
import com.aox.common.exception.BusinessException;
import com.aox.infrastructure.oss.client.OssClient;
import com.aox.infrastructure.oss.config.OssProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * 阿里云 OSS 客户端实现
 *
 * @author Aox Team
 */
@Slf4j
public class AliyunOssClient implements OssClient {

    private final OssProperties.AliyunProperties config;
    // 注意：需要引入阿里云 OSS SDK 依赖才能使用
    // 依赖: com.aliyun.oss:aliyun-sdk-oss
    // private final OSS ossClient;

    public AliyunOssClient(OssProperties.AliyunProperties config) {
        this.config = config;
        // 初始化阿里云 OSS 客户端（需要先添加SDK依赖）
        // this.ossClient = new OSSClientBuilder().build(
        //     config.getEndpoint(),
        //     config.getAccessKeyId(),
        //     config.getAccessKeySecret()
        // );
    }

    @Override
    public String upload(MultipartFile file) {
        // 生成文件路径: yyyyMMdd/uuid.ext
        String originalFilename = file.getOriginalFilename();
        String extension = FileUtil.extName(originalFilename);
        String datePath = DateUtil.format(new Date(), "yyyyMMdd");
        String fileName = datePath + "/" + IdUtil.simpleUUID() + "." + extension;

        return upload(file, fileName);
    }

    @Override
    public String upload(MultipartFile file, String filePath) {
        try {
            // 阿里云 OSS 上传实现（需要先添加aliyun-sdk-oss依赖）
            // ossClient.putObject(config.getBucketName(), filePath, file.getInputStream());

            // 当前为模拟实现，生产环境请启用上述代码
            log.warn("【开发模式】阿里云 OSS 上传功能未实现，返回模拟URL");
            String fileUrl = getFileUrl(filePath);
            log.info("阿里云 OSS 文件上传成功（模拟）: {}", fileUrl);
            return fileUrl;

        } catch (Exception e) {
            log.error("阿里云 OSS 文件上传失败", e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAIL);
        }
    }

    @Override
    public void delete(String fileName) {
        try {
            // 阿里云 OSS 删除实现（需要先添加aliyun-sdk-oss依赖）
            // ossClient.deleteObject(config.getBucketName(), fileName);

            // 当前为模拟实现，生产环境请启用上述代码
            log.warn("【开发模式】阿里云 OSS 删除功能未实现");
            log.info("阿里云 OSS 文件删除成功（模拟）: {}", fileName);

        } catch (Exception e) {
            log.error("阿里云 OSS 文件删除失败", e);
            throw new BusinessException("文件删除失败");
        }
    }

    @Override
    public String getFileUrl(String fileName) {
        return "https://" + config.getBucketName() + "." + config.getEndpoint() + "/" + fileName;
    }

    @Override
    public String getStorageType() {
        return "aliyun";
    }
}
