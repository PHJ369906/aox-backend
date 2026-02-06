package com.aox.infrastructure.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * XXL-Job 配置
 *
 * @author Aox Team
 * @since 2026-01-31
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "xxl.job", name = "enabled", havingValue = "true")
public class XxlJobConfig {

    private final XxlJobProperties xxlJobProperties;

    /**
     * XXL-Job 执行器配置
     *
     * 使用说明：
     * 1. 添加 XXL-Job 依赖到 pom.xml ✅
     * 2. 在 application.yml 中配置 xxl.job.enabled=true ✅
     * 3. 配置调度中心地址和执行器信息 ✅
     * 4. 启动项目后，在 XXL-Job 管理后台添加任务
     *
     * 注意：如果不需要XXL-Job功能，可以在配置文件中设置 xxl.job.enabled=false
     */
    // 取消下面注释以启用XXL-Job（需要先部署XXL-Job调度中心）
    /*
    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        log.info("初始化 XXL-Job 执行器配置");

        XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
        executor.setAdminAddresses(xxlJobProperties.getAdminAddresses());
        executor.setAccessToken(xxlJobProperties.getAccessToken());
        executor.setAppname(xxlJobProperties.getExecutor().getAppName());
        executor.setAddress(xxlJobProperties.getExecutor().getAddress());
        executor.setIp(xxlJobProperties.getExecutor().getIp());
        executor.setPort(xxlJobProperties.getExecutor().getPort());
        executor.setLogPath(xxlJobProperties.getExecutor().getLogPath());
        executor.setLogRetentionDays(xxlJobProperties.getExecutor().getLogRetentionDays());

        log.info("XXL-Job 执行器配置完成: appName={}, port={}",
                xxlJobProperties.getExecutor().getAppName(),
                xxlJobProperties.getExecutor().getPort());

        return executor;
    }
    */
}
