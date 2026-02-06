package com.aox.miniapp.domain.dto;

import lombok.Data;

/**
 * 更新用户信息DTO
 *
 * @author Aox Team
 */
@Data
public class UpdateUserInfoDTO {

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
     * 个性签名
     */
    private String signature;
}
