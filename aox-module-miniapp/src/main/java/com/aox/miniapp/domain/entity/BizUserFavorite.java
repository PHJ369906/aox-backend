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
 * 小程序用户收藏
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_user_favorite")
public class BizUserFavorite extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long favoriteId;

    private Long userId;

    private String favoriteTitle;

    private String favoriteDesc;

    private String imageUrl;

    private String relatedType;

    private Long relatedId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime favoriteTime;

    private Long tenantId;
}
