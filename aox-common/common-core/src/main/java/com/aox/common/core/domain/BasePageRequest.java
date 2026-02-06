package com.aox.common.core.domain;

import lombok.Data;

/**
 * 基础分页请求
 *
 * @author Aox Team
 */
@Data
public class BasePageRequest {

    /**
     * 页码（从1开始）
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序方式 asc/desc
     */
    private String sortOrder;
}
