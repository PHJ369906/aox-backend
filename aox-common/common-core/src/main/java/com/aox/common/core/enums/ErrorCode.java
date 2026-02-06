package com.aox.common.core.enums;

import lombok.Getter;

/**
 * 错误码枚举
 *
 * @author Aox Team
 */
@Getter
public enum ErrorCode {

    // 成功
    SUCCESS(0, "操作成功"),

    // 客户端错误 (400-499)
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或token已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    CONFLICT(409, "资源冲突"),
    TOO_MANY_REQUESTS(429, "请求过于频繁"),

    // 业务错误 - 用户相关 (1000-1099)
    USER_NOT_EXIST(1001, "用户不存在"),
    USER_ALREADY_EXIST(1002, "用户已存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    USER_DISABLED(1004, "用户已被禁用"),
    USERNAME_OR_PASSWORD_ERROR(1005, "用户名或密码错误"),
    OLD_PASSWORD_ERROR(1006, "原密码错误"),
    CAPTCHA_ERROR(1007, "验证码错误"),
    CAPTCHA_EXPIRED(1008, "验证码已过期"),

    // 业务错误 - 角色相关 (1100-1199)
    ROLE_NOT_EXIST(1101, "角色不存在"),
    ROLE_ALREADY_EXIST(1102, "角色已存在"),
    ROLE_IN_USE(1103, "角色正在使用中"),

    // 业务错误 - 权限相关 (1200-1299)
    PERMISSION_NOT_EXIST(1201, "权限不存在"),
    PERMISSION_ALREADY_EXIST(1202, "权限已存在"),

    // 业务错误 - 文件相关 (1300-1399)
    FILE_UPLOAD_FAIL(1301, "文件上传失败"),
    FILE_SIZE_EXCEED(1302, "文件大小超出限制"),
    FILE_TYPE_NOT_ALLOW(1303, "文件类型不允许"),
    FILE_NOT_EXIST(1304, "文件不存在"),

    // 业务错误 - 短信相关 (1400-1499)
    SMS_SEND_FAIL(1401, "短信发送失败"),
    SMS_CODE_ERROR(1402, "短信验证码错误"),
    SMS_CODE_EXPIRED(1403, "短信验证码已过期"),

    // 服务器错误 (500-599)
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    DATABASE_ERROR(501, "数据库异常"),
    REDIS_ERROR(502, "Redis异常"),
    SERVICE_UNAVAILABLE(503, "服务暂时不可用");

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误信息
     */
    private final String message;

    /**
     * 构造函数
     *
     * @param code 错误码
     * @param message 错误信息
     */
    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
