package com.aox.infrastructure.oss.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 客户端配置
 *
 * @author Aox Team
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "oss.type", havingValue = "minio", matchIfMissing = true)
public class MinioClientConfig {

    private final OssProperties ossProperties;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(ossProperties.getMinio().getEndpoint())
                .credentials(ossProperties.getMinio().getAccessKey(),
                           ossProperties.getMinio().getSecretKey())
                .build();
    }
}
