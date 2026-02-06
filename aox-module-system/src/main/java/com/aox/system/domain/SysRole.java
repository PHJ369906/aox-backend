package com.aox.system.domain;

import com.aox.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体
 *
 * @author Aox Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    /**
     * 角色ID
     */
    @TableId(type = IdType.AUTO)
    private Long roleId;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 排序
     */
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
    private String remark;

    /**
     * 租户ID
     */
    private Long tenantId;
}
