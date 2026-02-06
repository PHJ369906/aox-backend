package com.aox.system.service.impl;

import com.aox.common.core.domain.PageResult;
import com.aox.system.domain.SysRole;
import com.aox.system.domain.request.RoleCreateRequest;
import com.aox.system.domain.request.RoleQueryRequest;
import com.aox.system.domain.request.RoleUpdateRequest;
import com.aox.system.mapper.SysRoleMapper;
import com.aox.system.mapper.SysRolePermissionMapper;
import com.aox.system.service.RoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色业务层实现
 *
 * @author Aox Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final SysRoleMapper roleMapper;
    private final SysRolePermissionMapper rolePermissionMapper;

    @Override
    public PageResult<SysRole> listRoles(RoleQueryRequest request) {
        // 构造查询条件
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(request.getRoleName() != null, SysRole::getRoleName, request.getRoleName())
                .like(request.getRoleCode() != null, SysRole::getRoleCode, request.getRoleCode())
                .eq(request.getStatus() != null, SysRole::getStatus, request.getStatus())
                .orderByAsc(SysRole::getRoleSort)
                .orderByDesc(SysRole::getCreateTime);

        // 分页查询
        Page<SysRole> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<SysRole> result = roleMapper.selectPage(page, queryWrapper);

        // 返回分页结果
        return PageResult.of(result.getTotal(), result.getRecords(),
                request.getPageNum(), request.getPageSize());
    }

    @Override
    public SysRole getRoleById(Long roleId) {
        return roleMapper.selectById(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRole(RoleCreateRequest request) {
        // 创建角色实体
        SysRole role = new SysRole();
        role.setRoleCode(request.getRoleCode());
        role.setRoleName(request.getRoleName());
        role.setRoleSort(request.getRoleSort());
        role.setDataScope(request.getDataScope() != null ? request.getDataScope() : 1);
        role.setStatus(request.getStatus() != null ? request.getStatus() : 0);
        role.setRemark(request.getRemark());

        // 保存角色
        roleMapper.insert(role);

        // 保存角色权限关联
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            rolePermissionMapper.batchInsert(role.getRoleId(), request.getPermissionIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Long roleId, RoleUpdateRequest request) {
        // 更新角色信息
        SysRole role = new SysRole();
        role.setRoleId(roleId);
        role.setRoleName(request.getRoleName());
        role.setRoleSort(request.getRoleSort());
        role.setDataScope(request.getDataScope());
        role.setStatus(request.getStatus());
        role.setRemark(request.getRemark());

        roleMapper.updateById(role);

        // 更新角色权限关联
        if (request.getPermissionIds() != null) {
            // 先删除旧的权限关联
            rolePermissionMapper.deleteByRoleId(roleId);
            // 再插入新的权限关联
            if (!request.getPermissionIds().isEmpty()) {
                rolePermissionMapper.batchInsert(roleId, request.getPermissionIds());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long roleId) {
        // 删除角色
        roleMapper.deleteById(roleId);
        // 删除角色权限关联
        rolePermissionMapper.deleteByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        // 先删除旧的权限关联
        rolePermissionMapper.deleteByRoleId(roleId);

        // 再插入新的权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            rolePermissionMapper.batchInsert(roleId, permissionIds);
        }
    }

    @Override
    public List<Long> getRolePermissions(Long roleId) {
        return rolePermissionMapper.selectPermissionIdsByRoleId(roleId);
    }
}
