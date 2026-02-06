package com.aox.miniapp.domain.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 用户信息VO
 *
 * @author Aox Team
 */
@Data
@Builder
public class UserInfoVO {

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
     * 性别 0未知 1男 2女
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
}
