package com.aox.system.controller;

import com.aox.common.core.domain.R;
import com.aox.common.log.annotation.Log;
import com.aox.system.domain.SysDept;
import com.aox.system.service.DeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 *
 * @author Aox Team
 */
@RestController
@RequestMapping("/api/v1/system/dept")
@RequiredArgsConstructor
@Tag(name = "部门管理", description = "部门信息管理")
public class DeptController {

    private final DeptService deptService;

    /**
     * 查询部门列表（树形）
     */
    @GetMapping("/tree")
    @Operation(summary = "查询部门树")
    public R<List<SysDept>> getDeptTree(@RequestParam(value = "deptName", required = false) String deptName) {
        List<SysDept> tree = deptService.getDeptTree(deptName);
        return R.ok(tree);
    }

    /**
     * 获取所有部门
     */
    @GetMapping("/all")
    @Operation(summary = "获取所有部门")
    public R<List<SysDept>> getAllDepts() {
        List<SysDept> list = deptService.getAllDepts();
        return R.ok(list);
    }

    /**
     * 获取部门详情
     */
    @GetMapping("/{deptId}")
    @Operation(summary = "获取部门详情")
    public R<SysDept> getDeptDetail(@PathVariable("deptId") Long deptId) {
        SysDept dept = deptService.getDeptById(deptId);
        return R.ok(dept);
    }

    /**
     * 创建部门
     */
    @PostMapping
    @Operation(summary = "创建部门")
    @Log(module = "部门管理", operation = "创建部门")
    public R<Void> createDept(@RequestBody SysDept dept) {
        deptService.createDept(dept);
        return R.ok();
    }

    /**
     * 更新部门
     */
    @PutMapping("/{deptId}")
    @Operation(summary = "更新部门")
    @Log(module = "部门管理", operation = "更新部门")
    public R<Void> updateDept(@PathVariable("deptId") Long deptId, @RequestBody SysDept dept) {
        dept.setDeptId(deptId);
        deptService.updateDept(dept);
        return R.ok();
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{deptId}")
    @Operation(summary = "删除部门")
    @Log(module = "部门管理", operation = "删除部门")
    public R<Void> deleteDept(@PathVariable("deptId") Long deptId) {
        deptService.deleteDept(deptId);
        return R.ok();
    }

    /**
     * 批量删除部门
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除部门")
    @Log(module = "部门管理", operation = "批量删除部门")
    public R<Void> batchDeleteDepts(@RequestBody List<Long> deptIds) {
        deptService.batchDeleteDepts(deptIds);
        return R.ok();
    }
}
