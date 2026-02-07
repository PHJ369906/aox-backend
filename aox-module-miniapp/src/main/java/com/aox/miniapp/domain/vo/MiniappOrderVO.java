package com.aox.miniapp.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 小程序订单列表项
 */
@Data
@Builder
public class MiniappOrderVO {

    private String id;

    private String status;

    private String title;

    private BigDecimal amount;

    private String time;
}
