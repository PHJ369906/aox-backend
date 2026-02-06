package com.aox.admin.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户趋势数据VO
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTrendVO {

    /**
     * 日期（格式：MM-DD）
     */
    private String date;

    /**
     * 新增用户数
     */
    private Long newUsers;

    /**
     * 活跃用户数
     */
    private Long activeUsers;
}
