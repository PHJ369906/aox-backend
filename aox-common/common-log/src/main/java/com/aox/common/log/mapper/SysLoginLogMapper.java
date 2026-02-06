package com.aox.common.log.mapper;

import com.aox.common.log.domain.SysLoginLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录日志 Mapper
 *
 * @author Aox Team
 */
@Mapper
public interface SysLoginLogMapper extends BaseMapper<SysLoginLog> {
}
