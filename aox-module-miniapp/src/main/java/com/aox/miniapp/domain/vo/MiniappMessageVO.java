package com.aox.miniapp.domain.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 小程序消息列表项
 */
@Data
@Builder
public class MiniappMessageVO {

    private Long id;

    private String title;

    private String content;

    private String time;

    private Boolean read;
}
