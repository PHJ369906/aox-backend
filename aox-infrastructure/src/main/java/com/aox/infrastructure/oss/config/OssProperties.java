package com.aox.infrastructure.oss.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * OSS 配置属性
 *
 * @author Aox Team
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "oss")
public class OssProperties {

    /**
     * OSS 类型: minio / aliyun / tencent
     */
    private String type = "minio";

    /**
     * MinIO 配置
     */
    private MinioProperties minio = new MinioProperties();

    /**
     * 阿里云 OSS 配置
     */
    private AliyunProperties aliyun = new AliyunProperties();

    /**
     * 腾讯云 COS 配置
     */
    private TencentProperties tencent = new TencentProperties();

    /**
     * 七牛云 Kodo 配置
     */
    private QiniuProperties qiniu = new QiniuProperties();

    @Data
    public static class MinioProperties {
        private String endpoint = "http://localhost:9000";
        private String accessKey = "minioadmin";
        private String secretKey = "minioadmin";
        private String bucketName = "aox-files";
    }

    @Data
    public static class AliyunProperties {
        private String endpoint = "oss-cn-hangzhou.aliyuncs.com";
        private String accessKeyId;
        private String accessKeySecret;
        private String bucketName;
    }

    @Data
    public static class TencentProperties {
        private String region = "ap-guangzhou";
        private String secretId;
        private String secretKey;
        private String bucketName;
    }

    @Data
    public static class QiniuProperties {
        private String accessKey;
        private String secretKey;
        private String bucketName;
        private String domain;
    }
}
