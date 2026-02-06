package com.aox.system.domain;

import com.aox.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统公告实体
 *
 * @author Aox Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_notice")
public class SysNotice extends BaseEntity {

    /**
     * 公告ID
     */
    @TableId(type = IdType.AUTO)
    private Long noticeId;

    /**
     * 公告标题
     */
    private String noticeTitle;

    /**
     * 公告类型 1通知 2公告
     */
    private Integer noticeType;

    /**
     * 公告内容
     */
    private String noticeContent;

    /**
     * 公告级别 1普通 2重要 3紧急
     */
    private Integer noticeLevel;

    /**
     * 状态 0草稿 1已发布 2已撤回
     */
    private Integer status;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    /**
     * 发布人ID
     */
    private Long publishUserId;

    /**
     * 发布人姓名
     */
    private String publishUserName;

    /**
     * 目标类型 0全部 1指定用户 2指定角色 3指定部门
     */
    private Integer targetType;

    /**
     * 目标ID列表（非持久化，来自关联表）
     */
    @TableField(exist = false)
    private List<Long> targetIds;

    /**
     * 是否置顶 0否 1是
     */
    private Integer isTop;

    /**
     * 置顶排序
     */
    private Integer topOrder;

    /**
     * 附件URL
     */
    private String attachmentUrl;

    /**
     * 阅读次数
     */
    private Integer readCount;

    /**
     * 租户ID
     */
    private Long tenantId;
}
