package com.aox.common.core.constant;

/**
 * Redis Key 常量定义
 *
 * @author Aox Team
 */
public final class RedisConstants {

    private RedisConstants() {
        // 防止实例化
    }

    /**
     * 登录 Token 前缀
     */
    public static final String LOGIN_TOKEN_KEY = "login:token:";

    /**
     * 用户权限缓存前缀
     */
    public static final String USER_PERMISSIONS_KEY = "user:permissions:";

    /**
     * 用户角色缓存前缀
     */
    public static final String USER_ROLES_KEY = "user:roles:";

    /**
     * 验证码前缀
     */
    public static final String CAPTCHA_KEY = "captcha:";

    /**
     * 用户信息缓存前缀
     */
    public static final String USER_INFO_KEY = "user:info:";

    /**
     * 系统配置缓存前缀
     */
    public static final String SYS_CONFIG_KEY = "sys:config:";

    /**
     * 字典数据缓存前缀
     */
    public static final String SYS_DICT_KEY = "sys:dict:";

    /**
     * 限流前缀
     */
    public static final String RATE_LIMIT_KEY = "rate:limit:";

    /**
     * Token 默认过期时间（秒）- 24小时
     */
    public static final long TOKEN_EXPIRE_TIME = 24 * 60 * 60;

    /**
     * 验证码过期时间（秒）- 5分钟
     */
    public static final long CAPTCHA_EXPIRE_TIME = 5 * 60;

    /**
     * 用户权限缓存过期时间（秒）- 30分钟
     */
    public static final long USER_PERMISSIONS_EXPIRE_TIME = 30 * 60;
}
