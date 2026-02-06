package com.aox.common.core.constant;

/**
 * 通用常量
 *
 * @author Aox Team
 */
public class Constants {

    /**
     * 成功标记
     */
    public static final Integer SUCCESS = 0;

    /**
     * 失败标记
     */
    public static final Integer FAIL = 500;

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "登录成功";

    /**
     * 退出成功
     */
    public static final String LOGOUT_SUCCESS = "退出成功";

    /**
     * 注册成功
     */
    public static final String REGISTER_SUCCESS = "注册成功";

    /**
     * 操作成功
     */
    public static final String OPERATION_SUCCESS = "操作成功";

    /**
     * 操作失败
     */
    public static final String OPERATION_FAIL = "操作失败";

    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    public static final String GBK = "GBK";

    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";

    /**
     * 通用成功标识
     */
    public static final String SUCCESS_MSG = "success";

    /**
     * 通用失败标识
     */
    public static final String FAIL_MSG = "fail";

    /**
     * 验证码 Redis Key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha:code:";

    /**
     * 登录用户 Redis Key
     */
    public static final String LOGIN_TOKEN_KEY = "login:token:";

    /**
     * 验证码有效期（分钟）
     */
    public static final Integer CAPTCHA_EXPIRATION = 2;

    /**
     * Token有效期（秒）
     */
    public static final Long TOKEN_EXPIRATION = 7200L;

    /**
     * 管理员角色编码
     */
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    /**
     * 普通用户角色编码
     */
    public static final String ROLE_USER = "ROLE_USER";

    /**
     * 正常状态
     */
    public static final Integer STATUS_NORMAL = 0;

    /**
     * 禁用状态
     */
    public static final Integer STATUS_DISABLE = 1;

    /**
     * 删除标记 - 正常
     */
    public static final Integer DELETED_NO = 0;

    /**
     * 删除标记 - 删除
     */
    public static final Integer DELETED_YES = 1;
}
