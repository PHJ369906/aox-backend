package com.aox.common.security.aspect;

import com.aox.common.security.annotation.RequirePermission;
import com.aox.common.security.context.SecurityContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 权限校验切面
 */
@Aspect
@Component
public class RequirePermissionAspect {

    @Pointcut("@annotation(com.aox.common.security.annotation.RequirePermission) || " +
            "@within(com.aox.common.security.annotation.RequirePermission)")
    public void requirePermissionPointcut() {
    }

    @Around("requirePermissionPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        RequirePermission annotation = method.getAnnotation(RequirePermission.class);
        if (annotation == null) {
            annotation = point.getTarget().getClass().getAnnotation(RequirePermission.class);
        }

        if (annotation != null) {
            String permission = annotation.value();
            if (permission != null && !permission.trim().isEmpty()) {
                if (!SecurityContextHolder.hasPermission(permission)) {
                    throw new AccessDeniedException("无权限访问");
                }
            }
        }

        return point.proceed();
    }
}
