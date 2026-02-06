package com.aox.system.mapper;

import com.aox.system.domain.SysOssConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 云存储配置 Mapper
 *
 * @author Aox Team
 */
@Mapper
public interface OssConfigMapper extends BaseMapper<SysOssConfig> {
}
