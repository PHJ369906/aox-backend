package com.aox.system.mapper;

import com.aox.system.domain.Banner;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Banner Mapper
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Mapper
public interface BannerMapper extends BaseMapper<Banner> {

    /**
     * 批量更新排序
     *
     * @param banners Banner列表
     * @return 影响行数
     */
    int batchUpdateSort(@Param("list") List<Banner> banners);
}
