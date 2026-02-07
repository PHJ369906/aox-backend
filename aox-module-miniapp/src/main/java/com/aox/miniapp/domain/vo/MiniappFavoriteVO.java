package com.aox.miniapp.domain.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 小程序收藏列表项
 */
@Data
@Builder
public class MiniappFavoriteVO {

    private Long id;

    private String title;

    private String desc;

    private String time;

    private String image;
}
