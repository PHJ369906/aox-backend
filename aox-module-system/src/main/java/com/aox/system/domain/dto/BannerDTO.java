package com.aox.system.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Banner创建/更新DTO
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Data
public class BannerDTO {

    /**
     * Banner标题
     */
    @NotBlank(message = "Banner标题不能为空")
    private String title;

    /**
     * 图片URL
     */
    @NotBlank(message = "图片URL不能为空")
    private String imageUrl;

    /**
     * 跳转链接
     */
    private String linkUrl;

    /**
     * 链接类型：0-无，1-内部页面，2-外部链接
     */
    @NotNull(message = "链接类型不能为空")
    private Integer linkType;

    /**
     * 排序（数字越小越靠前）
     */
    @NotNull(message = "排序不能为空")
    private Integer sortOrder;

    /**
     * 状态：0-下架，1-上架
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}
