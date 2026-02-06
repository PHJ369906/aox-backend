package com.aox.system.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 新增角色请求
 *
 * @author Aox Team
 */
@Data
public class RoleCreateRequest {

    /**
     * 角色编码
     */
    @NotBlank(message = "角色编码不能为空")
    @Size(min = 2, max = 50, message = "角色编码长度必须在2-50个字符之间")
    private String roleCode;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称长度不能超过50个字符")
    private String roleName;

    /**
     * 排序
     */
    @NotNull(message = "排序不能为空")
    private Integer roleSort;

    /**
     * 数据权限 1全部 2本部门 3本部门及以下 4仅本人
     */
    private Integer dataScope;

    /**
     * 状态 0正常 1禁用
     */
    private Integer status;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;

    /**
     * 权限ID列表
     */
    private List<Long> permissionIds;
}
