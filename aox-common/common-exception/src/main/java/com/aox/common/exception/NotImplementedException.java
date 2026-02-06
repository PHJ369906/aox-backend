package com.aox.common.exception;

/**
 * 功能未实现异常
 * 用于标记尚未实现的功能，避免误导调用方
 *
 * @author Aox Team
 */
public class NotImplementedException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public NotImplementedException(String feature) {
        super(501, "功能未实现: " + feature);
    }

    public NotImplementedException(String feature, String suggestion) {
        super(501, "功能未实现: " + feature + "。建议: " + suggestion);
    }
}
