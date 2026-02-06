package com.aox.common.security.handler;

import com.aox.common.security.context.SecurityContextHolder;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 元数据自动填充处理器
 * 自动填充 BaseEntity 中的创建时间、更新时间、创建者、更新者、删除标记等字段
 *
 * @author Aox Team
 */
@Component
public class MyBatisMetaObjectHandler implements MetaObjectHandler {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MyBatisMetaObjectHandler.class);

    /**
     * 插入时自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("开始插入填充...");

        // 填充创建时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());

        // 填充更新时间
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        // 填充创建者
        String username = getCurrentUsername();
        this.strictInsertFill(metaObject, "createBy", String.class, username);

        // 填充更新者
        this.strictInsertFill(metaObject, "updateBy", String.class, username);

        // 填充删除标记（默认未删除）
        this.strictInsertFill(metaObject, "deleted", Integer.class, 0);

        // 填充租户ID
        Long tenantId = getCurrentTenantId();
        if (tenantId != null) {
            this.strictInsertFill(metaObject, "tenantId", Long.class, tenantId);
            log.debug("自动填充租户ID: {}", tenantId);
        }
    }

    /**
     * 更新时自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("开始更新填充...");

        // 填充更新时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        // 填充更新者
        String username = getCurrentUsername();
        this.strictUpdateFill(metaObject, "updateBy", String.class, username);
    }

    /**
     * 获取当前操作用户名
     * 优先从 SecurityContextHolder 获取，获取不到则返回 "system"
     */
    private String getCurrentUsername() {
        try {
            // 尝试从 Spring Security 上下文获取用户名
            String username = SecurityContextHolder.getUsername();
            if (username != null) {
                return username;
            }
        } catch (Exception e) {
            log.debug("无法从 SecurityContext 获取用户名: {}", e.getMessage());
        }

        // 如果获取不到，返回默认值
        return "system";
    }

    /**
     * 获取当前租户ID
     * 从 SecurityContextHolder 获取当前登录用户的租户ID
     */
    private Long getCurrentTenantId() {
        try {
            // 从 Spring Security 上下文获取租户ID
            Long tenantId = SecurityContextHolder.getTenantId();
            if (tenantId != null) {
                return tenantId;
            }
        } catch (Exception e) {
            log.debug("无法从 SecurityContext 获取租户ID: {}", e.getMessage());
        }

        // 如果获取不到，返回默认租户ID (0表示系统租户)
        return 0L;
    }
}
