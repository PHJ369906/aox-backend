package com.aox.system.service.impl;

import com.aox.common.security.context.SecurityContextHolder;
import com.aox.system.domain.SysMessage;
import com.aox.system.mapper.MessageMapper;
import com.aox.system.service.MessageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 站内消息服务实现
 *
 * @author Aox Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;

    @Override
    public IPage<SysMessage> getMessageList(Integer current, Integer size, Integer messageType, Integer isRead) {
        Long userId = SecurityContextHolder.getUserId();
        if (userId == null) {
            return new Page<>(current, size);
        }

        Page<SysMessage> page = new Page<>(current, size);
        LambdaQueryWrapper<SysMessage> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(SysMessage::getToUserId, userId)
                .eq(SysMessage::getDeleted, 0);

        if (messageType != null) {
            wrapper.eq(SysMessage::getMessageType, messageType);
        }
        if (isRead != null) {
            wrapper.eq(SysMessage::getIsRead, isRead);
        }

        wrapper.orderByDesc(SysMessage::getCreateTime);

        return messageMapper.selectPage(page, wrapper);
    }

    @Override
    public SysMessage getMessageById(Long messageId) {
        return messageMapper.selectById(messageId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendMessage(SysMessage message) {
        message.setFromUserId(SecurityContextHolder.getUserId());
        message.setFromUserName(SecurityContextHolder.getUsername());
        message.setIsRead(0);
        message.setCreateTime(LocalDateTime.now());
        messageMapper.insert(message);
        log.info("发送消息成功: {} -> {}", message.getFromUserId(), message.getToUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendSystemMessage(String title, String content, List<Long> userIds) {
        for (Long userId : userIds) {
            SysMessage message = new SysMessage();
            message.setMessageType(1); // 系统消息
            message.setMessageTitle(title);
            message.setMessageContent(content);
            message.setToUserId(userId);
            message.setIsRead(0);
            message.setCreateTime(LocalDateTime.now());
            messageMapper.insert(message);
        }
        log.info("发送系统消息成功，接收人数: {}", userIds.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long messageId) {
        SysMessage message = new SysMessage();
        message.setMessageId(messageId);
        message.setIsRead(1);
        message.setReadTime(LocalDateTime.now());
        messageMapper.updateById(message);
        log.info("标记消息为已读: {}", messageId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchMarkAsRead(List<Long> messageIds) {
        messageIds.forEach(this::markAsRead);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead() {
        Long userId = SecurityContextHolder.getUserId();
        if (userId != null) {
            messageMapper.markAllAsRead(userId);
            log.info("用户 {} 标记所有消息为已读", userId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMessage(Long messageId) {
        SysMessage message = new SysMessage();
        message.setMessageId(messageId);
        message.setDeleted(1);
        messageMapper.updateById(message);
        log.info("删除消息成功: {}", messageId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteMessages(List<Long> messageIds) {
        messageIds.forEach(this::deleteMessage);
    }

    @Override
    public Integer getUnreadCount() {
        Long userId = SecurityContextHolder.getUserId();
        if (userId == null) {
            return 0;
        }
        return messageMapper.getUnreadCountByUserId(userId);
    }
}
