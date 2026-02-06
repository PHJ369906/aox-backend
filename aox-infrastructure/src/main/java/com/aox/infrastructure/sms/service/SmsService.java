package com.aox.infrastructure.sms.service;

import cn.hutool.core.util.RandomUtil;
import com.aox.common.exception.BusinessException;
import com.aox.infrastructure.sms.client.SmsClient;
import com.aox.infrastructure.sms.client.SmsClientFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 短信服务
 *
 * @author Aox Team
 * @since 2026-01-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    private final SmsClientFactory smsClientFactory;
    private final StringRedisTemplate redisTemplate;

    /**
     * 验证码缓存前缀
     */
    private static final String SMS_CODE_KEY = "sms:code:";

    /**
     * 验证码有效期（分钟）
     */
    private static final int CODE_EXPIRE_MINUTES = 5;

    /**
     * 发送频率限制（秒）
     */
    private static final int SEND_INTERVAL_SECONDS = 60;

    /**
     * 发送验证码
     *
     * @param phone 手机号
     * @return 是否发送成功
     */
    public boolean sendCode(String phone) {
        // 检查发送频率
        String intervalKey = "sms:interval:" + phone;
        Boolean hasKey = redisTemplate.hasKey(intervalKey);
        if (Boolean.TRUE.equals(hasKey)) {
            throw new BusinessException("发送过于频繁，请稍后再试");
        }

        // 生成6位验证码
        String code = RandomUtil.randomNumbers(6);

        // 发送短信
        SmsClient smsClient = smsClientFactory.getSmsClient();
        boolean success = smsClient.sendCode(phone, code);

        if (success) {
            // 保存验证码到 Redis
            String codeKey = SMS_CODE_KEY + phone;
            redisTemplate.opsForValue().set(codeKey, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);

            // 设置发送频率限制
            redisTemplate.opsForValue().set(intervalKey, "1", SEND_INTERVAL_SECONDS, TimeUnit.SECONDS);

            log.info("验证码发送成功: phone={}, code={}", phone, code);
        }

        return success;
    }

    /**
     * 验证验证码
     *
     * @param phone 手机号
     * @param code  验证码
     * @return 是否验证通过
     */
    public boolean verifyCode(String phone, String code) {
        String codeKey = SMS_CODE_KEY + phone;
        String cachedCode = redisTemplate.opsForValue().get(codeKey);

        if (cachedCode == null) {
            throw new BusinessException("验证码已过期或不存在");
        }

        if (!cachedCode.equals(code)) {
            throw new BusinessException("验证码错误");
        }

        // 验证成功后删除验证码
        redisTemplate.delete(codeKey);

        log.info("验证码验证成功: phone={}", phone);
        return true;
    }

    /**
     * 发送通知短信
     *
     * @param phone   手机号
     * @param content 通知内容
     * @return 是否发送成功
     */
    public boolean sendNotice(String phone, String content) {
        SmsClient smsClient = smsClientFactory.getSmsClient();
        boolean success = smsClient.sendNotice(phone, content);

        log.info("通知短信发送{}: phone={}, content={}", success ? "成功" : "失败", phone, content);
        return success;
    }
}
