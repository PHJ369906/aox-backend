package com.aox.common.core.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一返回体
 *
 * @author Aox Team
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 返回消息
     */
    private String msg;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 全参构造函数
     */
    public R(Integer code, String msg, T data, Long timestamp) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.timestamp = timestamp;
    }

    /**
     * 成功返回
     */
    public static <T> R<T> ok() {
        return ok(null);
    }

    /**
     * 成功返回（带数据）
     */
    public static <T> R<T> ok(T data) {
        return new R<T>(0, "success", data, System.currentTimeMillis());
    }

    /**
     * 成功返回（带消息和数据）
     */
    public static <T> R<T> ok(String msg, T data) {
        return new R<T>(0, msg, data, System.currentTimeMillis());
    }

    /**
     * 失败返回
     */
    public static <T> R<T> fail(String msg) {
        return new R<T>(500, msg, null, System.currentTimeMillis());
    }

    /**
     * 失败返回（带状态码）
     */
    public static <T> R<T> fail(Integer code, String msg) {
        return new R<T>(code, msg, null, System.currentTimeMillis());
    }

    /**
     * 自定义返回
     */
    public static <T> R<T> build(Integer code, String msg, T data) {
        return new R<T>(code, msg, data, System.currentTimeMillis());
    }
}
