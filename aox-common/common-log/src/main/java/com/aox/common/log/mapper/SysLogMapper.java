package com.aox.common.log.mapper;

import com.aox.common.log.domain.SysLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志 Mapper
 *
 * @author Aox Team
 */
@Mapper
public interface SysLogMapper extends BaseMapper<SysLog> {
}
