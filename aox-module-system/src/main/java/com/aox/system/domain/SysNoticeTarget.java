package com.aox.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告目标实体
 *
 * @author Aox Team
 */
@Data
@TableName("sys_notice_target")
public class SysNoticeTarget {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 公告ID
     */
    private Long noticeId;

    /**
     * 目标类型 1指定用户 2指定角色 3指定部门
     */
    private Integer targetType;

    /**
     * 目标ID
     */
    private Long targetId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
