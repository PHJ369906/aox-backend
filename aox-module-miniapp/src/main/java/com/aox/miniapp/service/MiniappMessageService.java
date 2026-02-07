package com.aox.miniapp.service;

import com.aox.common.exception.BusinessException;
import com.aox.common.security.context.SecurityContextHolder;
import com.aox.miniapp.domain.entity.BizUserMessage;
import com.aox.miniapp.domain.vo.MiniappMessageVO;
import com.aox.miniapp.mapper.BizUserMessageMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * 小程序消息服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MiniappMessageService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final BizUserMessageMapper messageMapper;

    /**
     * 分页查询消息列表
     */
    public Page<MiniappMessageVO> getMessagePage(Integer pageNum, Integer pageSize) {
        Long userId = getCurrentUserId();

        LambdaQueryWrapper<BizUserMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizUserMessage::getUserId, userId)
                .orderByAsc(BizUserMessage::getIsRead)
                .orderByDesc(BizUserMessage::getCreateTime);

        Page<BizUserMessage> page = new Page<>(pageNum, pageSize);
        Page<BizUserMessage> result = messageMapper.selectPage(page, wrapper);

        Page<MiniappMessageVO> voPage = new Page<>();
        voPage.setCurrent(result.getCurrent());
        voPage.setSize(result.getSize());
        voPage.setTotal(result.getTotal());
        voPage.setPages(result.getPages());
        voPage.setRecords(result.getRecords().stream().map(this::toMessageVO).collect(Collectors.toList()));

        return voPage;
    }

    /**
     * 标记单条消息已读
     */
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long messageId) {
        Long userId = getCurrentUserId();

        BizUserMessage message = messageMapper.selectById(messageId);
        if (message == null) {
            throw new BusinessException("消息不存在");
        }
        if (!userId.equals(message.getUserId())) {
            throw new BusinessException("无权操作该消息");
        }
        if (Integer.valueOf(1).equals(message.getIsRead())) {
            return;
        }

        BizUserMessage update = new BizUserMessage();
        update.setMessageId(messageId);
        update.setIsRead(1);
        update.setReadTime(LocalDateTime.now());
        messageMapper.updateById(update);
    }

    /**
     * 全部标记已读
     */
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead() {
        Long userId = getCurrentUserId();

        LambdaUpdateWrapper<BizUserMessage> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(BizUserMessage::getUserId, userId)
                .eq(BizUserMessage::getIsRead, 0)
                .set(BizUserMessage::getIsRead, 1)
                .set(BizUserMessage::getReadTime, LocalDateTime.now());

        messageMapper.update(null, wrapper);
    }

    /**
     * 查询未读数量
     */
    public Long getUnreadCount() {
        Long userId = getCurrentUserId();

        LambdaQueryWrapper<BizUserMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizUserMessage::getUserId, userId)
                .eq(BizUserMessage::getIsRead, 0);
        return messageMapper.selectCount(wrapper);
    }

    private MiniappMessageVO toMessageVO(BizUserMessage message) {
        LocalDateTime time = message.getCreateTime();

        return MiniappMessageVO.builder()
                .id(message.getMessageId())
                .title(message.getMessageTitle())
                .content(message.getMessageContent())
                .time(time == null ? "" : time.format(TIME_FORMATTER))
                .read(Integer.valueOf(1).equals(message.getIsRead()))
                .build();
    }

    private Long getCurrentUserId() {
        Long userId = SecurityContextHolder.getUserId();
        if (userId == null) {
            log.warn("获取消息失败，用户未登录");
            throw new BusinessException(401, "未登录或token已过期");
        }
        return userId;
    }
}
