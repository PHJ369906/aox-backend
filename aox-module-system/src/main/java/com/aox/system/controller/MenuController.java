package com.aox.system.controller;

import com.aox.common.core.domain.R;
import com.aox.common.log.annotation.Log;
import com.aox.system.domain.SysMenu;
import com.aox.system.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 *
 * @author Aox Team
 */
@RestController
@RequestMapping("/api/v1/system/menu")
@RequiredArgsConstructor
@Tag(name = "菜单管理", description = "菜单信息管理")
public class MenuController {

    private final MenuService menuService;

    /**
     * 查询菜单列表（树形）
     */
    @GetMapping("/tree")
    @Operation(summary = "查询菜单树")
    public R<List<SysMenu>> getMenuTree(@RequestParam(value = "menuName", required = false) String menuName) {
        List<SysMenu> tree = menuService.getMenuTree(menuName);
        return R.ok(tree);
    }

    /**
     * 获取所有菜单
     */
    @GetMapping("/all")
    @Operation(summary = "获取所有菜单")
    public R<List<SysMenu>> getAllMenus() {
        List<SysMenu> list = menuService.getAllMenus();
        return R.ok(list);
    }

    /**
     * 获取菜单详情
     */
    @GetMapping("/{menuId}")
    @Operation(summary = "获取菜单详情")
    public R<SysMenu> getMenuDetail(@PathVariable Long menuId) {
        SysMenu menu = menuService.getMenuById(menuId);
        return R.ok(menu);
    }

    /**
     * 创建菜单
     */
    @PostMapping
    @Operation(summary = "创建菜单")
    @Log(module = "菜单管理", operation = "创建菜单")
    public R<Void> createMenu(@RequestBody SysMenu menu) {
        menuService.createMenu(menu);
        return R.ok();
    }

    /**
     * 更新菜单
     */
    @PutMapping("/{menuId}")
    @Operation(summary = "更新菜单")
    @Log(module = "菜单管理", operation = "更新菜单")
    public R<Void> updateMenu(@PathVariable Long menuId, @RequestBody SysMenu menu) {
        menu.setMenuId(menuId);
        menuService.updateMenu(menu);
        return R.ok();
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{menuId}")
    @Operation(summary = "删除菜单")
    @Log(module = "菜单管理", operation = "删除菜单")
    public R<Void> deleteMenu(@PathVariable Long menuId) {
        menuService.deleteMenu(menuId);
        return R.ok();
    }

    /**
     * 批量删除菜单
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除菜单")
    @Log(module = "菜单管理", operation = "批量删除菜单")
    public R<Void> batchDeleteMenus(@RequestBody List<Long> menuIds) {
        menuService.batchDeleteMenus(menuIds);
        return R.ok();
    }
}
