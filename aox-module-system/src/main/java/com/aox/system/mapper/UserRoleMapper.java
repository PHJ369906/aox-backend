package com.aox.system.mapper;

import com.aox.system.domain.SysRole;
import com.aox.system.domain.SysUserRole;
import com.aox.system.domain.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户角色关联 Mapper
 *
 * @author Aox Team
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 根据用户ID查询角色列表
     */
    List<SysRole> selectRolesByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询菜单列表（通过角色关联）
     */
    List<SysMenu> selectMenusByUserId(@Param("userId") Long userId);
}
