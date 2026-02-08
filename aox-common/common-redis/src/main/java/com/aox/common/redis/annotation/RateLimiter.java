package com.aox.common.redis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口限流注解
 *
 * @author Aox Team
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    /**
     * 限流key前缀
     */
    String key() default "rate_limit:";

    /**
     * 限流时间窗口，单位秒
     */
    int time() default 60;

    /**
     * 限流次数
     */
    int count() default 10;

    /**
     * 限流类型
     */
    LimitType limitType() default LimitType.DEFAULT;

    /**
     * 提示消息
     */
    String message() default "请求过于频繁，请稍后再试";

    /**
     * 限流类型枚举
     */
    enum LimitType {
        /**
         * 默认策略：全局限流
         */
        DEFAULT,
        /**
         * IP限流
         */
        IP,
        /**
         * 用户限流
         */
        USER
    }
}
