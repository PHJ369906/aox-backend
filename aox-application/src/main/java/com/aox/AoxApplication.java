package com.aox;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.aox.infrastructure.job.XxlJobProperties;

/**
 * Aox 应用启动类
 *
 * @author Aox Team
 */
@Slf4j
@SpringBootApplication
@EnableConfigurationProperties(XxlJobProperties.class)
public class AoxApplication {

    public static void main(String[] args) {
        SpringApplication.run(AoxApplication.class, args);
        log.info("""

            ╔═══════════════════════════════════════════════════════╗
            ║                                                       ║
            ║   █████╗  ██████╗ ██╗  ██╗                          ║
            ║  ██╔══██╗██╔═══██╗╚██╗██╔╝                          ║
            ║  ███████║██║   ██║ ╚███╔╝                           ║
            ║  ██╔══██║██║   ██║ ██╔██╗                           ║
            ║  ██║  ██║╚██████╔╝██╔╝ ██╗                          ║
            ║  ╚═╝  ╚═╝ ╚═════╝ ╚═╝  ╚═╝                          ║
            ║                                                       ║
            ║  Aox 三端脚手架 - 后端服务启动成功!                    ║
            ║  接口文档: http://localhost:8080/doc.html            ║
            ║                                                       ║
            ╚═══════════════════════════════════════════════════════╝
            """);
    }
}
