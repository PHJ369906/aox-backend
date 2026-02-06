package com.aox.system.service.impl;

import com.aox.common.exception.BusinessException;
import com.aox.system.domain.SysMenu;
import com.aox.system.mapper.MenuMapper;
import com.aox.system.service.MenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单信息服务实现
 *
 * @author Aox Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;

    @Override
    public List<SysMenu> getMenuTree(String menuName) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();

        if (menuName != null && !menuName.isEmpty()) {
            wrapper.like(SysMenu::getMenuName, menuName);
        }

        wrapper.eq(SysMenu::getDeleted, 0)
                .orderByAsc(SysMenu::getSortOrder);

        List<SysMenu> allMenus = menuMapper.selectList(wrapper);
        return buildMenuTree(allMenus, 0L);
    }

    @Override
    public List<SysMenu> getAllMenus() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getStatus, 0)
                .eq(SysMenu::getDeleted, 0)
                .orderByAsc(SysMenu::getSortOrder);
        return menuMapper.selectList(wrapper);
    }

    @Override
    public SysMenu getMenuById(Long menuId) {
        return menuMapper.selectById(menuId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createMenu(SysMenu menu) {
        // 验证父菜单是否存在
        if (menu.getParentId() != null && menu.getParentId() != 0) {
            SysMenu parentMenu = menuMapper.selectById(menu.getParentId());
            if (parentMenu == null) {
                throw new BusinessException("父菜单不存在");
            }
        } else {
            menu.setParentId(0L);
        }

        menuMapper.insert(menu);
        log.info("创建菜单成功: {}", menu.getMenuName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenu(SysMenu menu) {
        // 不能将菜单设置为自己的子菜单
        if (menu.getParentId() != null && menu.getParentId().equals(menu.getMenuId())) {
            throw new BusinessException("不能将菜单设置为自己的子菜单");
        }

        // 验证父菜单是否存在
        if (menu.getParentId() != null && menu.getParentId() != 0) {
            SysMenu parentMenu = menuMapper.selectById(menu.getParentId());
            if (parentMenu == null) {
                throw new BusinessException("父菜单不存在");
            }
        }

        menuMapper.updateById(menu);
        log.info("更新菜单成功: {}", menu.getMenuId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenu(Long menuId) {
        // 检查是否有子菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, menuId)
                .eq(SysMenu::getDeleted, 0);
        Long count = menuMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("存在子菜单，不允许删除");
        }

        SysMenu menu = new SysMenu();
        menu.setMenuId(menuId);
        menu.setDeleted(1);
        menuMapper.updateById(menu);
        log.info("删除菜单成功: {}", menuId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteMenus(List<Long> menuIds) {
        menuIds.forEach(this::deleteMenu);
    }

    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> allMenus, Long parentId) {
        List<SysMenu> tree = new ArrayList<>();

        for (SysMenu menu : allMenus) {
            if (menu.getParentId().equals(parentId)) {
                List<SysMenu> children = buildMenuTree(allMenus, menu.getMenuId());
                menu.setChildren(children);
                tree.add(menu);
            }
        }

        return tree;
    }
}
