package com.aox.system.domain;

import com.aox.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 部门实体
 *
 * @author Aox Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class SysDept extends BaseEntity {

    /**
     * 部门ID
     */
    @TableId(type = IdType.AUTO)
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 祖级列表
     */
    private String ancestors;

    /**
     * 负责人
     */
    private String leader;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态 0正常 1禁用
     */
    private Integer status;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 子部门列表（非数据库字段，用于树形结构）
     */
    @TableField(exist = false)
    private List<SysDept> children;
}
