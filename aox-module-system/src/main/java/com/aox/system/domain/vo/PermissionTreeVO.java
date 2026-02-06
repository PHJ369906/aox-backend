package com.aox.system.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 权限树 VO
 *
 * @author Aox Team
 */
@Data
public class PermissionTreeVO {

    /**
     * 权限ID
     */
    private Long permissionId;

    /**
     * 权限编码
     */
    private String permissionCode;

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 权限类型 1菜单 2按钮 3接口
     */
    private Integer permissionType;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 父级ID
     */
    private Long parentId;

    /**
     * 子权限列表
     */
    private List<PermissionTreeVO> children;
}
