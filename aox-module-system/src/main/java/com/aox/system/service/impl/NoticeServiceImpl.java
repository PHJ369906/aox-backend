package com.aox.system.service.impl;

import com.aox.common.security.context.SecurityContextHolder;
import com.aox.system.domain.SysNotice;
import com.aox.system.domain.SysNoticeRead;
import com.aox.system.domain.SysNoticeTarget;
import com.aox.system.mapper.NoticeMapper;
import com.aox.system.mapper.NoticeReadMapper;
import com.aox.system.mapper.NoticeTargetMapper;
import com.aox.system.service.NoticeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 系统公告服务实现
 *
 * @author Aox Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper noticeMapper;
    private final NoticeReadMapper noticeReadMapper;
    private final NoticeTargetMapper noticeTargetMapper;

    @Override
    public IPage<SysNotice> getNoticeList(Integer current, Integer size, String noticeTitle,
                                          Integer noticeType, Integer status) {
        Page<SysNotice> page = new Page<>(current, size);
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();

        if (noticeTitle != null && !noticeTitle.isEmpty()) {
            wrapper.like(SysNotice::getNoticeTitle, noticeTitle);
        }
        if (noticeType != null) {
            wrapper.eq(SysNotice::getNoticeType, noticeType);
        }
        if (status != null) {
            wrapper.eq(SysNotice::getStatus, status);
        }

        wrapper.eq(SysNotice::getDeleted, 0)
                .orderByDesc(SysNotice::getIsTop)
                .orderByDesc(SysNotice::getTopOrder)
                .orderByDesc(SysNotice::getPublishTime);

        IPage<SysNotice> result = noticeMapper.selectPage(page, wrapper);
        fillNoticeTargets(result.getRecords());
        return result;
    }

    @Override
    public SysNotice getNoticeById(Long noticeId) {
        SysNotice notice = noticeMapper.selectById(noticeId);
        if (notice == null) {
            return null;
        }
        fillNoticeTargets(List.of(notice));
        return notice;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createNotice(SysNotice notice) {
        Integer targetType = notice.getTargetType() == null ? 0 : notice.getTargetType();
        notice.setTargetType(targetType);
        notice.setTargetIds(normalizeTargetIds(targetType, notice.getTargetIds()));
        notice.setPublishUserId(SecurityContextHolder.getUserId());
        notice.setPublishUserName(SecurityContextHolder.getUsername());
        notice.setReadCount(0);
        noticeMapper.insert(notice);
        saveNoticeTargets(notice.getNoticeId(), targetType, notice.getTargetIds());
        log.info("创建公告成功: {}", notice.getNoticeTitle());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNotice(SysNotice notice) {
        Integer targetType = notice.getTargetType() == null ? 0 : notice.getTargetType();
        notice.setTargetType(targetType);
        notice.setTargetIds(normalizeTargetIds(targetType, notice.getTargetIds()));
        noticeMapper.updateById(notice);
        saveNoticeTargets(notice.getNoticeId(), targetType, notice.getTargetIds());
        log.info("更新公告成功: {}", notice.getNoticeId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotice(Long noticeId) {
        SysNotice notice = new SysNotice();
        notice.setNoticeId(noticeId);
        notice.setDeleted(1);
        noticeMapper.updateById(notice);
        clearNoticeTargets(noticeId);
        log.info("删除公告成功: {}", noticeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteNotices(List<Long> noticeIds) {
        noticeIds.forEach(this::deleteNotice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishNotice(Long noticeId) {
        SysNotice notice = new SysNotice();
        notice.setNoticeId(noticeId);
        notice.setStatus(1);
        notice.setPublishTime(LocalDateTime.now());
        noticeMapper.updateById(notice);
        log.info("发布公告成功: {}", noticeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revokeNotice(Long noticeId) {
        SysNotice notice = new SysNotice();
        notice.setNoticeId(noticeId);
        notice.setStatus(2);
        noticeMapper.updateById(notice);
        log.info("撤回公告成功: {}", noticeId);
    }

    @Override
    public List<SysNotice> getUnreadNotices() {
        Long userId = SecurityContextHolder.getUserId();
        if (userId == null) {
            return List.of();
        }
        List<SysNotice> notices = noticeMapper.getUnreadNoticesByUserId(userId);
        fillNoticeTargets(notices);
        return notices;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long noticeId) {
        Long userId = SecurityContextHolder.getUserId();
        if (userId == null) {
            return;
        }

        // 检查是否已读
        LambdaQueryWrapper<SysNoticeRead> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysNoticeRead::getNoticeId, noticeId)
                .eq(SysNoticeRead::getUserId, userId);
        SysNoticeRead existRead = noticeReadMapper.selectOne(wrapper);

        if (existRead == null) {
            // 新增阅读记录
            SysNoticeRead read = new SysNoticeRead();
            read.setNoticeId(noticeId);
            read.setUserId(userId);
            read.setUsername(SecurityContextHolder.getUsername());
            read.setReadTime(LocalDateTime.now());
            noticeReadMapper.insert(read);

            // 增加阅读次数
            noticeMapper.increaseReadCount(noticeId);

            log.info("用户 {} 阅读公告: {}", userId, noticeId);
        }
    }

    @Override
    public Integer getUnreadCount() {
        return getUnreadNotices().size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void topNotice(Long noticeId, Integer topOrder) {
        SysNotice notice = new SysNotice();
        notice.setNoticeId(noticeId);
        notice.setIsTop(1);
        notice.setTopOrder(topOrder);
        noticeMapper.updateById(notice);
        log.info("置顶公告成功: {}", noticeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelTop(Long noticeId) {
        SysNotice notice = new SysNotice();
        notice.setNoticeId(noticeId);
        notice.setIsTop(0);
        notice.setTopOrder(0);
        noticeMapper.updateById(notice);
        log.info("取消置顶公告: {}", noticeId);
    }

    private void saveNoticeTargets(Long noticeId, Integer targetType, List<Long> targetIds) {
        if (noticeId == null) {
            return;
        }
        clearNoticeTargets(noticeId);

        if (targetType == null || targetType == 0) {
            return;
        }

        List<Long> targetIdList = normalizeTargetIds(targetType, targetIds);
        if (targetIdList.isEmpty()) {
            return;
        }

        Long tenantId = SecurityContextHolder.getTenantId();
        if (tenantId == null) {
            tenantId = 0L;
        }

        for (Long targetId : targetIdList) {
            SysNoticeTarget target = new SysNoticeTarget();
            target.setNoticeId(noticeId);
            target.setTargetType(targetType);
            target.setTargetId(targetId);
            target.setTenantId(tenantId);
            noticeTargetMapper.insert(target);
        }
    }

    private void clearNoticeTargets(Long noticeId) {
        noticeTargetMapper.delete(new LambdaQueryWrapper<SysNoticeTarget>()
                .eq(SysNoticeTarget::getNoticeId, noticeId));
    }

    private List<Long> normalizeTargetIds(Integer targetType, List<Long> targetIds) {
        if (targetType == null || targetType == 0 || targetIds == null) {
            return new ArrayList<>();
        }
        return targetIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    private void fillNoticeTargets(List<SysNotice> notices) {
        if (notices == null || notices.isEmpty()) {
            return;
        }
        List<Long> noticeIds = notices.stream()
                .map(SysNotice::getNoticeId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (noticeIds.isEmpty()) {
            return;
        }

        List<SysNoticeTarget> targets = noticeTargetMapper.selectList(
                new LambdaQueryWrapper<SysNoticeTarget>()
                        .in(SysNoticeTarget::getNoticeId, noticeIds)
        );

        Map<Long, List<Long>> targetMap = new HashMap<>();
        for (SysNoticeTarget target : targets) {
            targetMap.computeIfAbsent(target.getNoticeId(), key -> new ArrayList<>())
                    .add(target.getTargetId());
        }

        for (SysNotice notice : notices) {
            if (notice.getTargetType() == null || notice.getTargetType() == 0) {
                notice.setTargetIds(new ArrayList<>());
            } else {
                notice.setTargetIds(targetMap.getOrDefault(notice.getNoticeId(), new ArrayList<>()));
            }
        }
    }
}
