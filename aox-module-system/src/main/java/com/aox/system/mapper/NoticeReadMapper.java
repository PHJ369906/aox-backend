package com.aox.system.mapper;

import com.aox.system.domain.SysNoticeRead;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公告阅读记录 Mapper
 *
 * @author Aox Team
 */
@Mapper
public interface NoticeReadMapper extends BaseMapper<SysNoticeRead> {
}
