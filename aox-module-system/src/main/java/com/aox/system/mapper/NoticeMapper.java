package com.aox.system.mapper;

import com.aox.system.domain.SysNotice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统公告 Mapper
 *
 * @author Aox Team
 */
@Mapper
public interface NoticeMapper extends BaseMapper<SysNotice> {

    /**
     * 获取用户未读公告列表
     *
     * @param userId 用户ID
     * @return 未读公告列表
     */
    List<SysNotice> getUnreadNoticesByUserId(@Param("userId") Long userId);

    /**
     * 增加阅读次数
     *
     * @param noticeId 公告ID
     */
    void increaseReadCount(@Param("noticeId") Long noticeId);
}
