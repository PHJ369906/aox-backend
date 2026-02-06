package com.aox.miniapp.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 小程序用户VO（后台管理）
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Data
@Builder
public class MiniappUserVO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 个性签名
     */
    private String signature;

    /**
     * 用户状态：0-正常，1-禁用
     */
    private Integer status;

    /**
     * 注册来源：wechat-微信，password-账号密码，sms-短信
     */
    private String registerSource;

    /**
     * 微信OpenID
     */
    private String wxOpenid;

    /**
     * 注册时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 最后登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;
}
