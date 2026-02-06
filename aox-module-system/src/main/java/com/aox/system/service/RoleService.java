package com.aox.system.service;

import com.aox.common.core.domain.PageResult;
import com.aox.system.domain.SysRole;
import com.aox.system.domain.request.RoleCreateRequest;
import com.aox.system.domain.request.RoleQueryRequest;
import com.aox.system.domain.request.RoleUpdateRequest;

import java.util.List;

/**
 * 角色业务层接口
 *
 * @author Aox Team
 */
public interface RoleService {

    /**
     * 分页查询角色列表
     *
     * @param request 查询参数
     * @return 分页结果
     */
    PageResult<SysRole> listRoles(RoleQueryRequest request);

    /**
     * 根据ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色信息
     */
    SysRole getRoleById(Long roleId);

    /**
     * 新增角色
     *
     * @param request 角色信息
     */
    void createRole(RoleCreateRequest request);

    /**
     * 更新角色
     *
     * @param roleId  角色ID
     * @param request 角色信息
     */
    void updateRole(Long roleId, RoleUpdateRequest request);

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     */
    void deleteRole(Long roleId);

    /**
     * 分配权限
     *
     * @param roleId        角色ID
     * @param permissionIds 权限ID列表
     */
    void assignPermissions(Long roleId, List<Long> permissionIds);

    /**
     * 获取角色已分配的权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> getRolePermissions(Long roleId);
}
