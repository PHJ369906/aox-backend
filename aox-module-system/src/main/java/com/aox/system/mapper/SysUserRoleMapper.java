package com.aox.system.mapper;

import com.aox.system.domain.SysUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户角色关联 Mapper
 *
 * @author Aox Team
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 根据用户ID查询角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    @Select("SELECT role_id FROM sys_user_role WHERE user_id = #{userId}")
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID删除用户角色关系
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    @Delete("DELETE FROM sys_user_role WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID删除用户角色关系
     *
     * @param roleId 角色ID
     * @return 影响行数
     */
    @Delete("DELETE FROM sys_user_role WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入用户角色关系
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     * @return 影响行数
     */
    default int batchInsert(Long userId, List<Long> roleIds) {
        int count = 0;
        for (Long roleId : roleIds) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            count += insert(userRole);
        }
        return count;
    }
}
