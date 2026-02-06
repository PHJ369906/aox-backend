package com.aox.system.service;

import com.aox.system.domain.SysMenu;

import java.util.List;

/**
 * 菜单信息服务接口
 *
 * @author Aox Team
 */
public interface MenuService {

    /**
     * 查询菜单列表（树形）
     */
    List<SysMenu> getMenuTree(String menuName);

    /**
     * 获取所有菜单
     */
    List<SysMenu> getAllMenus();

    /**
     * 根据ID查询菜单
     */
    SysMenu getMenuById(Long menuId);

    /**
     * 创建菜单
     */
    void createMenu(SysMenu menu);

    /**
     * 更新菜单
     */
    void updateMenu(SysMenu menu);

    /**
     * 删除菜单
     */
    void deleteMenu(Long menuId);

    /**
     * 批量删除菜单
     */
    void batchDeleteMenus(List<Long> menuIds);

    /**
     * 构建菜单树
     * 公共方法，供其他Service调用（如PermissionService）
     */
    List<SysMenu> buildMenuTree(List<SysMenu> allMenus, Long parentId);
}
