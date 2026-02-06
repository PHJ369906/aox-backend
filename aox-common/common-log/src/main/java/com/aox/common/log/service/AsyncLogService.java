package com.aox.common.log.service;

import com.aox.common.log.domain.SysLog;
import com.aox.common.log.domain.SysLoginLog;

/**
 * 异步日志服务接口
 *
 * @author Aox Team
 */
public interface AsyncLogService {

    /**
     * 保存操作日志
     *
     * @param module         模块名称
     * @param operation      操作描述
     * @param method         方法名
     * @param requestUri     请求URI
     * @param requestMethod  请求方式
     * @param requestParams  请求参数
     * @param responseResult 响应结果
     * @param status         状态 0成功 1失败
     * @param errorMsg       错误信息
     * @param ip             IP地址
     * @param userAgent      用户代理
     * @param userId         用户ID
     * @param username       用户名
     * @param executionTime  执行耗时(ms)
     */
    void saveLog(String module, String operation, String method, String requestUri,
                 String requestMethod, String requestParams, String responseResult,
                 Integer status, String errorMsg, String ip, String userAgent,
                 Long userId, String username, Long executionTime);

    /**
     * 保存操作日志
     *
     * @param sysLog 操作日志实体
     */
    void saveLog(SysLog sysLog);

    /**
     * 保存登录日志
     *
     * @param username  用户名
     * @param loginType 登录类型 1后台 2小程序
     * @param status    登录状态 0成功 1失败
     * @param ip        IP地址
     * @param userAgent 用户代理
     * @param errorMsg  失败原因
     */
    void saveLoginLog(String username, Integer loginType, Integer status,
                      String ip, String userAgent, String errorMsg);

    /**
     * 保存登录日志
     *
     * @param loginLog 登录日志实体
     */
    void saveLoginLog(SysLoginLog loginLog);
}
