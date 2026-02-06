package com.aox.system.domain;

import com.aox.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Banner轮播图实体
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_banner")
public class Banner extends BaseEntity {

    /**
     * Banner ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * Banner标题
     */
    private String title;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 跳转链接
     */
    private String linkUrl;

    /**
     * 链接类型：0-无，1-内部页面，2-外部链接
     */
    private Integer linkType;

    /**
     * 排序（数字越小越靠前）
     */
    private Integer sortOrder;

    /**
     * 状态：0-下架，1-上架
     */
    private Integer status;

    /**
     * 创建人
     */
    private Long createdBy;
}
