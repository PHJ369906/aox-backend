package com.aox.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 站内消息实体
 *
 * @author Aox Team
 */
@Data
@TableName("sys_message")
public class SysMessage {

    /**
     * 消息ID
     */
    @TableId(type = IdType.AUTO)
    private Long messageId;

    /**
     * 消息类型 1系统消息 2业务消息 3提醒消息
     */
    private Integer messageType;

    /**
     * 消息标题
     */
    private String messageTitle;

    /**
     * 消息内容
     */
    private String messageContent;

    /**
     * 消息级别 1普通 2重要 3紧急
     */
    private Integer messageLevel;

    /**
     * 发送人ID
     */
    private Long fromUserId;

    /**
     * 发送人姓名
     */
    private String fromUserName;

    /**
     * 接收人ID
     */
    private Long toUserId;

    /**
     * 接收人姓名
     */
    private String toUserName;

    /**
     * 是否已读 0未读 1已读
     */
    private Integer isRead;

    /**
     * 阅读时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime readTime;

    /**
     * 关联类型（订单、工单等）
     */
    private String relatedType;

    /**
     * 关联ID
     */
    private Long relatedId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 删除标记 0正常 1删除
     */
    private Integer deleted;
}
