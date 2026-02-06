package com.aox.system.mapper;

import com.aox.system.domain.SysRolePermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色权限关联 Mapper
 *
 * @author Aox Team
 */
@Mapper
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {

    /**
     * 根据角色ID查询权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    @Select("SELECT permission_id FROM sys_role_permission WHERE role_id = #{roleId}")
    List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据角色ID删除角色权限关系
     *
     * @param roleId 角色ID
     * @return 影响行数
     */
    @Delete("DELETE FROM sys_role_permission WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入角色权限关系
     *
     * @param roleId        角色ID
     * @param permissionIds 权限ID列表
     * @return 影响行数
     */
    default int batchInsert(Long roleId, List<Long> permissionIds) {
        int count = 0;
        for (Long permissionId : permissionIds) {
            SysRolePermission rolePermission = new SysRolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            count += insert(rolePermission);
        }
        return count;
    }
}
