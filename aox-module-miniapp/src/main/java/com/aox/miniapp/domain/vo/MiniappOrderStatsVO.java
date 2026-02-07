package com.aox.miniapp.domain.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 小程序订单统计
 */
@Data
@Builder
public class MiniappOrderStatsVO {

    private Long all;

    private Long pay;

    private Long ship;

    private Long receive;

    private Long refund;
}
