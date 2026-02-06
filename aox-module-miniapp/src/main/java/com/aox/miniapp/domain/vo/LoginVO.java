package com.aox.miniapp.domain.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 登录响应VO
 *
 * @author Aox Team
 */
@Data
@Builder
public class LoginVO {

    /**
     * 访问令牌
     */
    private String token;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 用户信息
     */
    private UserInfoVO user;

    @Data
    @Builder
    public static class UserInfoVO {
        private Long userId;
        private String nickname;
        private String avatar;
        private String phone;
    }
}
