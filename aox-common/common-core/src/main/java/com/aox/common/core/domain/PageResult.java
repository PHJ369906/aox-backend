package com.aox.common.core.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果
 *
 * @author Aox Team
 * @param <T> 数据类型
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 数据列表
     */
    private List<T> list;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 总页数
     */
    private Integer totalPages;

    public PageResult() {
    }

    public PageResult(Long total, List<T> list, Integer pageNum, Integer pageSize) {
        this.total = total;
        this.list = list;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
    }

    public static <T> PageResult<T> of(Long total, List<T> list, Integer pageNum, Integer pageSize) {
        return new PageResult<>(total, list, pageNum, pageSize);
    }
}
