package com.aox.system.domain.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 新增用户请求
 *
 * @author Aox Team
 */
@Data
public class UserCreateRequest {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 30, message = "用户名长度必须在2-30个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;

    /**
     * 昵称
     */
    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 性别 0女 1男 2未知
     */
    private Integer gender;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 状态 0正常 1禁用
     */
    private Integer status;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 角色ID列表
     */
    private List<Long> roleIds;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
