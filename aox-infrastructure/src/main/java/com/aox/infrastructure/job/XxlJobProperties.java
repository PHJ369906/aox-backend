package com.aox.infrastructure.job;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * XXL-Job 配置属性
 *
 * @author Aox Team
 * @since 2026-01-31
 */
@Data
@ConfigurationProperties(prefix = "xxl.job")
public class XxlJobProperties {

    /**
     * 是否启用
     */
    private Boolean enabled = false;

    /**
     * 调度中心地址
     */
    private String adminAddresses = "http://localhost:8081/xxl-job-admin";

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 管理后台用户名（可选）
     */
    private String adminUsername;

    /**
     * 管理后台密码（可选）
     */
    private String adminPassword;

    /**
     * 执行器配置
     */
    private ExecutorConfig executor = new ExecutorConfig();

    @Data
    public static class ExecutorConfig {
        /**
         * 执行器AppName
         */
        private String appName = "aox-executor";

        /**
         * 执行器注册地址
         */
        private String address;

        /**
         * 执行器IP
         */
        private String ip;

        /**
         * 执行器端口
         */
        private Integer port = 9999;

        /**
         * 执行器日志路径
         */
        private String logPath = "./logs/xxl-job";

        /**
         * 执行器日志保留天数
         */
        private Integer logRetentionDays = 30;
    }
}
