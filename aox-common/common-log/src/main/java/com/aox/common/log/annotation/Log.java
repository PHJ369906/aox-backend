package com.aox.common.log.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 * @author Aox Team
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     * 模块名称
     */
    String module() default "";

    /**
     * 操作描述
     */
    String operation() default "";

    /**
     * 是否保存请求参数
     */
    boolean saveRequestData() default true;

    /**
     * 是否保存响应结果
     */
    boolean saveResponseData() default true;
}
