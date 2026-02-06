package com.aox.system.service;

import com.aox.system.domain.SysNotice;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 系统公告服务接口
 *
 * @author Aox Team
 */
public interface NoticeService {

    /**
     * 分页查询公告列表
     */
    IPage<SysNotice> getNoticeList(Integer current, Integer size, String noticeTitle,
                                    Integer noticeType, Integer status);

    /**
     * 获取公告详情
     */
    SysNotice getNoticeById(Long noticeId);

    /**
     * 创建公告
     */
    void createNotice(SysNotice notice);

    /**
     * 更新公告
     */
    void updateNotice(SysNotice notice);

    /**
     * 删除公告
     */
    void deleteNotice(Long noticeId);

    /**
     * 批量删除公告
     */
    void batchDeleteNotices(List<Long> noticeIds);

    /**
     * 发布公告
     */
    void publishNotice(Long noticeId);

    /**
     * 撤回公告
     */
    void revokeNotice(Long noticeId);

    /**
     * 获取用户未读公告列表
     */
    List<SysNotice> getUnreadNotices();

    /**
     * 标记公告为已读
     */
    void markAsRead(Long noticeId);

    /**
     * 获取未读公告数量
     */
    Integer getUnreadCount();

    /**
     * 置顶公告
     */
    void topNotice(Long noticeId, Integer topOrder);

    /**
     * 取消置顶
     */
    void cancelTop(Long noticeId);
}
