package com.aox.infrastructure.sms.client;

import com.aox.common.exception.BusinessException;
import com.aox.infrastructure.sms.client.impl.AliyunSmsClient;
import com.aox.infrastructure.sms.client.impl.TencentSmsClient;
import com.aox.infrastructure.sms.config.SmsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 短信客户端工厂
 *
 * @author Aox Team
 * @since 2026-01-31
 */
@Component
@RequiredArgsConstructor
public class SmsClientFactory {

    private final SmsProperties smsProperties;

    /**
     * 获取短信客户端
     *
     * @return 短信客户端
     */
    public SmsClient getSmsClient() {
        String type = smsProperties.getType();

        return switch (type.toLowerCase()) {
            case "aliyun" -> new AliyunSmsClient(smsProperties.getAliyun());
            case "tencent" -> new TencentSmsClient(smsProperties.getTencent());
            default -> throw new BusinessException("不支持的短信服务类型: " + type);
        };
    }
}
