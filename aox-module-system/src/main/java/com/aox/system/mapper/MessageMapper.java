package com.aox.system.mapper;

import com.aox.system.domain.SysMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 站内消息 Mapper
 *
 * @author Aox Team
 */
@Mapper
public interface MessageMapper extends BaseMapper<SysMessage> {

    /**
     * 获取用户未读消息数量
     *
     * @param userId 用户ID
     * @return 未读消息数量
     */
    Integer getUnreadCountByUserId(@Param("userId") Long userId);

    /**
     * 标记所有消息为已读
     *
     * @param userId 用户ID
     */
    void markAllAsRead(@Param("userId") Long userId);
}
