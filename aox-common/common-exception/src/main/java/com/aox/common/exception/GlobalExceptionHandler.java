package com.aox.common.exception;

import com.aox.common.core.domain.R;
import com.aox.common.core.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.access.AccessDeniedException;

/**
 * 全局异常处理器
 *
 * @author Aox Team
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public R<?> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("业务异常: URI={}, 错误信息={}", request.getRequestURI(), e.getMessage());
        return R.fail(e.getCode(), e.getMessage());
    }

    /**
     * 权限校验异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public R<?> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.error("权限校验异常: URI={}, 错误信息={}", request.getRequestURI(), e.getMessage());
        return R.fail(ErrorCode.FORBIDDEN.getCode(), ErrorCode.FORBIDDEN.getMessage());
    }

    /**
     * 参数校验异常 - BindException
     */
    @ExceptionHandler(BindException.class)
    public R<?> handleBindException(BindException e, HttpServletRequest request) {
        log.error("参数校验异常: URI={}", request.getRequestURI());
        FieldError fieldError = e.getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        return R.fail(ErrorCode.BAD_REQUEST.getCode(), message);
    }

    /**
     * 参数校验异常 - MethodArgumentNotValidException
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("参数校验异常: URI={}", request.getRequestURI());
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        return R.fail(ErrorCode.BAD_REQUEST.getCode(), message);
    }

    /**
     * 缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public R<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {
        log.error("缺少请求参数: URI={}, 参数名={}", request.getRequestURI(), e.getParameterName());
        return R.fail(ErrorCode.BAD_REQUEST.getCode(), "缺少必要参数: " + e.getParameterName());
    }

    /**
     * 运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public R<?> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("运行时异常: URI={}", request.getRequestURI(), e);
        return R.fail(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), "系统异常，请稍后重试");
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public R<?> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常: URI={}", request.getRequestURI(), e);
        return R.fail(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), "系统异常，请稍后重试");
    }
}
