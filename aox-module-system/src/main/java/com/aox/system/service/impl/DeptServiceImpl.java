package com.aox.system.service.impl;

import com.aox.common.exception.BusinessException;
import com.aox.system.domain.SysDept;
import com.aox.system.mapper.DeptMapper;
import com.aox.system.service.DeptService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 部门信息服务实现
 *
 * @author Aox Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeptServiceImpl implements DeptService {

    private final DeptMapper deptMapper;

    @Override
    public List<SysDept> getDeptTree(String deptName) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();

        if (deptName != null && !deptName.isEmpty()) {
            wrapper.like(SysDept::getDeptName, deptName);
        }

        wrapper.eq(SysDept::getDeleted, 0)
                .orderByAsc(SysDept::getSortOrder);

        List<SysDept> allDepts = deptMapper.selectList(wrapper);
        return buildDeptTree(allDepts, 0L);
    }

    @Override
    public List<SysDept> getAllDepts() {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getStatus, 0)
                .eq(SysDept::getDeleted, 0)
                .orderByAsc(SysDept::getSortOrder);
        return deptMapper.selectList(wrapper);
    }

    @Override
    public SysDept getDeptById(Long deptId) {
        return deptMapper.selectById(deptId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDept(SysDept dept) {
        // 设置祖级列表
        if (dept.getParentId() == null || dept.getParentId() == 0) {
            dept.setParentId(0L);
            dept.setAncestors("0");
        } else {
            SysDept parentDept = deptMapper.selectById(dept.getParentId());
            if (parentDept == null) {
                throw new BusinessException("父部门不存在");
            }
            dept.setAncestors(parentDept.getAncestors() + "," + parentDept.getDeptId());
        }

        deptMapper.insert(dept);
        log.info("创建部门成功: {}", dept.getDeptName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDept(SysDept dept) {
        // 如果修改了父部门，需要更新祖级列表
        SysDept oldDept = deptMapper.selectById(dept.getDeptId());
        if (oldDept == null) {
            throw new BusinessException("部门不存在");
        }

        // 不能将部门设置为自己的子部门
        if (dept.getParentId() != null && dept.getParentId().equals(dept.getDeptId())) {
            throw new BusinessException("不能将部门设置为自己的子部门");
        }

        if (dept.getParentId() != null && !dept.getParentId().equals(oldDept.getParentId())) {
            // 父部门发生变化，更新祖级列表
            if (dept.getParentId() == 0) {
                dept.setAncestors("0");
            } else {
                SysDept parentDept = deptMapper.selectById(dept.getParentId());
                if (parentDept == null) {
                    throw new BusinessException("父部门不存在");
                }
                dept.setAncestors(parentDept.getAncestors() + "," + parentDept.getDeptId());
            }

            // 更新所有子部门的祖级列表
            updateChildrenAncestors(dept, oldDept.getAncestors());
        }

        deptMapper.updateById(dept);
        log.info("更新部门成功: {}", dept.getDeptId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDept(Long deptId) {
        // 检查是否有子部门
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getParentId, deptId)
                .eq(SysDept::getDeleted, 0);
        Long count = deptMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("存在子部门，不允许删除");
        }

        SysDept dept = new SysDept();
        dept.setDeptId(deptId);
        dept.setDeleted(1);
        deptMapper.updateById(dept);
        log.info("删除部门成功: {}", deptId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteDepts(List<Long> deptIds) {
        deptIds.forEach(this::deleteDept);
    }

    /**
     * 构建部门树
     */
    private List<SysDept> buildDeptTree(List<SysDept> allDepts, Long parentId) {
        List<SysDept> tree = new ArrayList<>();

        for (SysDept dept : allDepts) {
            if (dept.getParentId().equals(parentId)) {
                List<SysDept> children = buildDeptTree(allDepts, dept.getDeptId());
                dept.setChildren(children);
                tree.add(dept);
            }
        }

        return tree;
    }

    /**
     * 更新子部门的祖级列表
     */
    private void updateChildrenAncestors(SysDept dept, String oldAncestors) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(SysDept::getAncestors, oldAncestors + "," + dept.getDeptId())
                .eq(SysDept::getDeleted, 0);

        List<SysDept> children = deptMapper.selectList(wrapper);

        for (SysDept child : children) {
            String newAncestors = child.getAncestors().replace(
                    oldAncestors + "," + dept.getDeptId(),
                    dept.getAncestors() + "," + dept.getDeptId()
            );
            child.setAncestors(newAncestors);
            deptMapper.updateById(child);
        }
    }
}
