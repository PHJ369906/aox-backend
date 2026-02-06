package com.aox.infrastructure.email.service;

import com.aox.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

/**
 * 邮件服务
 *
 * @author Aox Team
 */
@Slf4j
@Service
@ConditionalOnBean(JavaMailSender.class)
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * 发送简单文本邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param content 内容
     */
    public void sendSimpleMail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@aox.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);
            log.info("简单邮件发送成功: to={}, subject={}", to, subject);

        } catch (Exception e) {
            log.error("邮件发送失败", e);
            throw new BusinessException("邮件发送失败: " + e.getMessage());
        }
    }

    /**
     * 发送 HTML 邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param content HTML内容
     */
    public void sendHtmlMail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("noreply@aox.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
            log.info("HTML邮件发送成功: to={}, subject={}", to, subject);

        } catch (Exception e) {
            log.error("HTML邮件发送失败", e);
            throw new RuntimeException("邮件发送失败: " + e.getMessage());
        }
    }
}
