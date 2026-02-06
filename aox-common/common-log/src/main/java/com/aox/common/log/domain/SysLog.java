package com.aox.common.log.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体
 *
 * @author Aox Team
 */
@Data
@TableName("sys_log")
public class SysLog {

    /**
     * 日志ID
     */
    @TableId(type = IdType.AUTO)
    private Long logId;

    /**
     * 模块名称
     */
    private String module;

    /**
     * 操作描述
     */
    private String operation;

    /**
     * 方法名
     */
    private String method;

    /**
     * 请求URI
     */
    private String requestUri;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 响应结果
     */
    private String responseResult;

    /**
     * 状态 0成功 1失败
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 执行耗时(ms)
     */
    private Long executionTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
