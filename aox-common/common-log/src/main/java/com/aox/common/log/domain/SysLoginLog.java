package com.aox.common.log.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志实体
 *
 * @author Aox Team
 */
@Data
@TableName("sys_login_log")
public class SysLoginLog {

    /**
     * 登录日志ID
     */
    @TableId(type = IdType.AUTO)
    private Long loginLogId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 登录类型 1后台 2小程序
     */
    private Integer loginType;

    /**
     * 登录状态 0成功 1失败
     */
    private Integer status;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 登录地点
     */
    private String location;

    /**
     * 设备信息
     */
    private String device;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 失败原因
     */
    private String errorMsg;

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;
}
