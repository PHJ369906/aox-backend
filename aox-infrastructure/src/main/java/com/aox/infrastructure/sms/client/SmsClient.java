package com.aox.infrastructure.sms.client;

/**
 * 短信客户端抽象接口
 *
 * @author Aox Team
 * @since 2026-01-31
 */
public interface SmsClient {

    /**
     * 发送验证码短信
     *
     * @param phone 手机号
     * @param code  验证码
     * @return 是否发送成功
     */
    boolean sendCode(String phone, String code);

    /**
     * 发送通知短信
     *
     * @param phone   手机号
     * @param content 通知内容
     * @return 是否发送成功
     */
    boolean sendNotice(String phone, String content);

    /**
     * 发送模板短信
     *
     * @param phone        手机号
     * @param templateCode 模板CODE
     * @param params       模板参数
     * @return 是否发送成功
     */
    boolean sendTemplate(String phone, String templateCode, String... params);
}