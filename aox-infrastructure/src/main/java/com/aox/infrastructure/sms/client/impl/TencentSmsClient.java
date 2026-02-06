package com.aox.infrastructure.sms.client.impl;

import cn.hutool.json.JSONUtil;
import com.aox.infrastructure.sms.client.SmsClient;
import com.aox.infrastructure.sms.config.SmsProperties;
import lombok.extern.slf4j.Slf4j;

/**
 * 腾讯云短信客户端实现
 *
 * @author Aox Team
 * @since 2026-01-31
 */
@Slf4j
public class TencentSmsClient implements SmsClient {

    private final SmsProperties.TencentSmsConfig config;

    public TencentSmsClient(SmsProperties.TencentSmsConfig config) {
        this.config = config;
    }

    @Override
    public boolean sendCode(String phone, String code) {
        log.info("腾讯云短信 - 发送验证码: phone={}, code={}", phone, code);

        try {
            return sendTemplate(phone, config.getCodeTemplateId(), code);
        } catch (Exception e) {
            log.error("腾讯云短信发送失败: phone={}", phone, e);
            return false;
        }
    }

    @Override
    public boolean sendNotice(String phone, String content) {
        log.info("腾讯云短信 - 发送通知: phone={}, content={}", phone, content);

        try {
            return sendTemplate(phone, config.getNoticeTemplateId(), content);
        } catch (Exception e) {
            log.error("腾讯云短信发送失败: phone={}", phone, e);
            return false;
        }
    }

    @Override
    public boolean sendTemplate(String phone, String templateCode, String... params) {
        log.info("腾讯云短信 - 发送模板短信: phone={}, templateCode={}, params={}",
                phone, templateCode, JSONUtil.toJsonStr(params));

        try {
            // 注意：以下为开发环境模拟实现，生产环境需集成腾讯云短信 SDK
            // 集成步骤：添加 tencentcloud-sdk-java-sms 依赖后，参考以下示例代码
            /*
            Credential cred = new Credential(config.getSecretId(), config.getSecretKey());
            HttpProfile httpProfile = new HttpProfile();
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            SmsClient client = new SmsClient(cred, config.getRegion(), clientProfile);
            SendSmsRequest req = new SendSmsRequest();

            req.setSmsSdkAppId(config.getAppId());
            req.setSignName(config.getSignName());
            req.setTemplateId(templateCode);
            req.setPhoneNumberSet(new String[]{"+86" + phone});
            req.setTemplateParamSet(params);

            SendSmsResponse resp = client.SendSms(req);
            return "Ok".equals(resp.getSendStatusSet()[0].getCode());
            */

            // 开发环境模拟发送成功
            log.warn("【开发模式】短信未实际发送,请配置腾讯云短信SDK");
            return true;

        } catch (Exception e) {
            log.error("腾讯云短信发送失败: phone={}, templateCode={}", phone, templateCode, e);
            return false;
        }
    }
}