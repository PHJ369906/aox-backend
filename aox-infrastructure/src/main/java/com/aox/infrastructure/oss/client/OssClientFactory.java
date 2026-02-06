package com.aox.infrastructure.oss.client;

import com.aox.infrastructure.oss.client.impl.AliyunOssClient;
import com.aox.infrastructure.oss.client.impl.MinioOssClient;
import com.aox.infrastructure.oss.config.OssProperties;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * OSS 客户端工厂
 * 根据配置类型动态创建对应的 OssClient 实现
 *
 * @author Aox Team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OssClientFactory {

    private final OssProperties ossProperties;
    private final MinioClient minioClient;

    /**
     * 创建 OSS 客户端
     *
     * @param storageType 存储类型 (minio/aliyun/tencent)
     * @return OssClient 实例
     */
    public OssClient createClient(String storageType) {
        log.info("创建 OSS 客户端，存储类型: {}", storageType);

        switch (storageType.toLowerCase()) {
            case "minio":
                return new MinioOssClient(minioClient, ossProperties.getMinio());

            case "aliyun":
                return new AliyunOssClient(ossProperties.getAliyun());

            case "tencent":
                // 腾讯云 COS 暂未实现，需要添加 cos-java-sdk 依赖后实现
                throw new UnsupportedOperationException("腾讯云 COS 暂未实现，请使用 MinIO 或阿里云 OSS");

            case "qiniu":
                // 七牛云 Kodo 暂未实现，需要添加 qiniu-java-sdk 依赖后实现
                throw new UnsupportedOperationException("七牛云 Kodo 暂未实现，请使用 MinIO 或阿里云 OSS");

            default:
                throw new IllegalArgumentException("不支持的存储类型: " + storageType);
        }
    }

    /**
     * 根据配置文件创建默认客户端
     *
     * @return OssClient 实例
     */
    public OssClient createDefaultClient() {
        return createClient(ossProperties.getType());
    }
}
