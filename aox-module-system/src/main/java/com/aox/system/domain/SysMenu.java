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
 * 菜单实体
 *
 * @author Aox Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

    /**
     * 菜单ID
     */
    @TableId(type = IdType.AUTO)
    private Long menuId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 菜单类型 1目录 2菜单 3按钮
     */
    private Integer menuType;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 是否可见 0隐藏 1显示
     */
    private Integer visible;

    /**
     * 状态 0正常 1禁用
     */
    private Integer status;

    /**
     * 是否外链 0否 1是
     */
    private Integer isFrame;

    /**
     * 是否缓存 0否 1是
     */
    private Integer isCache;

    /**
     * 备注
     */
    private String remark;

    /**
     * 子菜单列表（非数据库字段，用于树形结构）
     */
    @TableField(exist = false)
    private List<SysMenu> children;
}
