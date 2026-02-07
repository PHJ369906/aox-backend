package com.aox.miniapp.domain.entity;

import com.aox.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 小程序用户消息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_user_message")
public class BizUserMessage extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long messageId;

    private Long userId;

    private Integer messageType;

    private String messageTitle;

    private String messageContent;

    private Integer isRead;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime readTime;

    private Long tenantId;
}
