package com.aox.system.service;

import com.aox.system.domain.SysDept;

import java.util.List;

/**
 * 部门信息服务接口
 *
 * @author Aox Team
 */
public interface DeptService {

    /**
     * 查询部门列表（树形）
     */
    List<SysDept> getDeptTree(String deptName);

    /**
     * 获取所有部门（不含树形结构）
     */
    List<SysDept> getAllDepts();

    /**
     * 根据ID查询部门
     */
    SysDept getDeptById(Long deptId);

    /**
     * 创建部门
     */
    void createDept(SysDept dept);

    /**
     * 更新部门
     */
    void updateDept(SysDept dept);

    /**
     * 删除部门
     */
    void deleteDept(Long deptId);

    /**
     * 批量删除部门
     */
    void batchDeleteDepts(List<Long> deptIds);
}
