package com.aox.system.mapper;

import com.aox.system.domain.SysPermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限 Mapper
 *
 * @author Aox Team
 */
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    /**
     * 根据用户ID查询权限编码列表
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    @Select("SELECT DISTINCT p.permission_code " +
            "FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.permission_id = rp.permission_id " +
            "INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND p.status = 0 AND p.deleted = 0")
    List<String> selectPermissionCodesByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Select("SELECT p.* " +
            "FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.permission_id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId} AND p.status = 0 AND p.deleted = 0 " +
            "ORDER BY p.sort_order")
    List<SysPermission> selectPermissionsByRoleId(@Param("roleId") Long roleId);

    /**
     * 查询所有权限（树形结构用）
     *
     * @return 权限列表
     */
    @Select("SELECT * FROM sys_permission WHERE status = 0 AND deleted = 0 ORDER BY sort_order")
    List<SysPermission> selectAllPermissions();
}
