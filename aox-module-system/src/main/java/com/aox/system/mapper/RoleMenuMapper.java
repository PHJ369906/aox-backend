package com.aox.system.mapper;

import com.aox.system.domain.SysMenu;
import com.aox.system.domain.SysRoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色菜单关联 Mapper
 *
 * @author Aox Team
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<SysRoleMenu> {

    /**
     * 根据角色ID查询菜单列表
     */
    List<SysMenu> selectMenusByRoleId(@Param("roleId") Long roleId);
}
