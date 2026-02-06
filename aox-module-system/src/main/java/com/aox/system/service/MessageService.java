package com.aox.system.service;

import com.aox.system.domain.SysMessage;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 站内消息服务接口
 *
 * @author Aox Team
 */
public interface MessageService {

    /**
     * 分页查询用户消息列表
     */
    IPage<SysMessage> getMessageList(Integer current, Integer size, Integer messageType, Integer isRead);

    /**
     * 获取消息详情
     */
    SysMessage getMessageById(Long messageId);

    /**
     * 发送消息
     */
    void sendMessage(SysMessage message);

    /**
     * 发送系统消息（批量）
     */
    void sendSystemMessage(String title, String content, List<Long> userIds);

    /**
     * 标记消息为已读
     */
    void markAsRead(Long messageId);

    /**
     * 批量标记为已读
     */
    void batchMarkAsRead(List<Long> messageIds);

    /**
     * 标记所有消息为已读
     */
    void markAllAsRead();

    /**
     * 删除消息
     */
    void deleteMessage(Long messageId);

    /**
     * 批量删除消息
     */
    void batchDeleteMessages(List<Long> messageIds);

    /**
     * 获取未读消息数量
     */
    Integer getUnreadCount();
}
