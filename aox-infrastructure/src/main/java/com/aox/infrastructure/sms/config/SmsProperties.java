package com.aox.infrastructure.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 短信配置属性
 *
 * @author Aox Team
 * @since 2026-01-31
 */
@Data
@Component
@ConfigurationProperties(prefix = "sms")
public class SmsProperties {

    /**
     * 短信服务类型: aliyun / tencent
     */
    private String type = "aliyun";

    /**
     * 阿里云短信配置
     */
    private AliyunSmsConfig aliyun = new AliyunSmsConfig();

    /**
     * 腾讯云短信配置
     */
    private TencentSmsConfig tencent = new TencentSmsConfig();

    /**
     * 阿里云短信配置
     */
    @Data
    public static class AliyunSmsConfig {
        /**
         * AccessKey ID
         */
        private String accessKeyId;

        /**
         * AccessKey Secret
         */
        private String accessKeySecret;

        /**
         * 短信签名
         */
        private String signName;

        /**
         * 验证码模板 CODE
         */
        private String codeTemplateCode;

        /**
         * 通知短信模板 CODE
         */
        private String noticeTemplateCode;

        /**
         * 区域节点
         */
        private String regionId = "cn-hangzhou";
    }

    /**
     * 腾讯云短信配置
     */
    @Data
    public static class TencentSmsConfig {
        /**
         * SecretId
         */
        private String secretId;

        /**
         * SecretKey
         */
        private String secretKey;

        /**
         * 短信应用 ID
         */
        private String appId;

        /**
         * 短信签名
         */
        private String signName;

        /**
         * 验证码模板 ID
         */
        private String codeTemplateId;

        /**
         * 通知短信模板 ID
         */
        private String noticeTemplateId;

        /**
         * 区域
         */
        private String region = "ap-guangzhou";
    }
}