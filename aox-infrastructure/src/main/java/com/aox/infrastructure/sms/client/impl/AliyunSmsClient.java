package com.aox.infrastructure.sms.client.impl;

import cn.hutool.json.JSONUtil;
import com.aox.infrastructure.sms.client.SmsClient;
import com.aox.infrastructure.sms.config.SmsProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云短信客户端实现
 *
 * @author Aox Team
 * @since 2026-01-31
 */
@Slf4j
public class AliyunSmsClient implements SmsClient {

    private final SmsProperties.AliyunSmsConfig config;

    public AliyunSmsClient(SmsProperties.AliyunSmsConfig config) {
        this.config = config;
    }

    @Override
    public boolean sendCode(String phone, String code) {
        log.info("阿里云短信 - 发送验证码: phone={}, code={}", phone, code);

        try {
            // 构建参数
            Map<String, String> params = new HashMap<>();
            params.put("code", code);

            return sendTemplate(phone, config.getCodeTemplateCode(), code);
        } catch (Exception e) {
            log.error("阿里云短信发送失败: phone={}", phone, e);
            return false;
        }
    }

    @Override
    public boolean sendNotice(String phone, String content) {
        log.info("阿里云短信 - 发送通知: phone={}, content={}", phone, content);

        try {
            return sendTemplate(phone, config.getNoticeTemplateCode(), content);
        } catch (Exception e) {
            log.error("阿里云短信发送失败: phone={}", phone, e);
            return false;
        }
    }

    @Override
    public boolean sendTemplate(String phone, String templateCode, String... params) {
        log.info("阿里云短信 - 发送模板短信: phone={}, templateCode={}, params={}",
                phone, templateCode, JSONUtil.toJsonStr(params));

        try {
            // 注意：以下为开发环境模拟实现，生产环境需集成阿里云短信 SDK
            // 集成步骤：添加 aliyun-java-sdk-dysmsapi 依赖后，参考以下示例代码
            /*
            DefaultProfile profile = DefaultProfile.getProfile(
                config.getRegionId(),
                config.getAccessKeyId(),
                config.getAccessKeySecret()
            );
            IAcsClient client = new DefaultAcsClient(profile);

            SendSmsRequest request = new SendSmsRequest();
            request.setPhoneNumbers(phone);
            request.setSignName(config.getSignName());
            request.setTemplateCode(templateCode);

            Map<String, String> paramMap = new HashMap<>();
            for (int i = 0; i < params.length; i++) {
                paramMap.put("param" + (i + 1), params[i]);
            }
            request.setTemplateParam(JSONUtil.toJsonStr(paramMap));

            SendSmsResponse response = client.getAcsResponse(request);
            return "OK".equals(response.getCode());
            */

            // 开发环境模拟发送成功
            log.warn("【开发模式】短信未实际发送,请配置阿里云短信SDK");
            return true;

        } catch (Exception e) {
            log.error("阿里云短信发送失败: phone={}, templateCode={}", phone, templateCode, e);
            return false;
        }
    }
}