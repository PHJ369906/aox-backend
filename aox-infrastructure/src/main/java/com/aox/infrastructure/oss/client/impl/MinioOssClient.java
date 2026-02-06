package com.aox.infrastructure.oss.client.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.aox.common.core.enums.ErrorCode;
import com.aox.common.exception.BusinessException;
import com.aox.infrastructure.oss.client.OssClient;
import com.aox.infrastructure.oss.config.OssProperties;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;

/**
 * MinIO 对象存储客户端实现
 *
 * @author Aox Team
 */
@Slf4j
public class MinioOssClient implements OssClient {

    private final MinioClient minioClient;
    private final OssProperties.MinioProperties config;

    public MinioOssClient(MinioClient minioClient, OssProperties.MinioProperties config) {
        this.minioClient = minioClient;
        this.config = config;
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
            InputStream inputStream = file.getInputStream();
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(config.getBucketName())
                    .object(filePath)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();

            minioClient.putObject(args);

            String fileUrl = getFileUrl(filePath);
            log.info("MinIO 文件上传成功: {}", fileUrl);
            return fileUrl;

        } catch (Exception e) {
            log.error("MinIO 文件上传失败", e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAIL);
        }
    }

    @Override
    public void delete(String fileName) {
        try {
            RemoveObjectArgs args = RemoveObjectArgs.builder()
                    .bucket(config.getBucketName())
                    .object(fileName)
                    .build();

            minioClient.removeObject(args);
            log.info("MinIO 文件删除成功: {}", fileName);

        } catch (Exception e) {
            log.error("MinIO 文件删除失败", e);
            throw new BusinessException("文件删除失败");
        }
    }

    @Override
    public String getFileUrl(String fileName) {
        return config.getEndpoint() + "/" + config.getBucketName() + "/" + fileName;
    }

    @Override
    public String getStorageType() {
        return "minio";
    }
}
