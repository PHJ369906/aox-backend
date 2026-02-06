package com.aox.system.mapper;

import com.aox.system.domain.SysPost;
import com.aox.system.domain.SysUserPost;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户岗位关联 Mapper
 *
 * @author Aox Team
 */
@Mapper
public interface UserPostMapper extends BaseMapper<SysUserPost> {

    /**
     * 根据用户ID查询岗位列表
     */
    List<SysPost> selectPostsByUserId(@Param("userId") Long userId);
}
