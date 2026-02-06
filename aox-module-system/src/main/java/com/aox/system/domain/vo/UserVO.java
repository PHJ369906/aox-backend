package com.aox.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户VO
 *
 * @author Aox Team
 */
@Data
public class UserVO {

    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String email;
    private String phone;
    private Integer gender;
    private Integer status;
    private Long deptId;
    private String deptName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    private String lastLoginIp;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 角色列表
     */
    private List<RoleVO> roles;
}
