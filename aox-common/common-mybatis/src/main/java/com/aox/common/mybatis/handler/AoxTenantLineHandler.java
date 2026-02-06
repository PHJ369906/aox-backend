package com.aox.common.mybatis.handler;

import com.aox.common.security.context.SecurityContextHolder;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 多租户处理器
 * 自动为 SQL 添加 tenant_id 条件，实现数据隔离
 *
 * @author Aox Team
 */
@Component
public class AoxTenantLineHandler implements com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AoxTenantLineHandler.class);

    /**
     * 不需要租户隔离的表
     * 系统配置表、字典表等公共数据不需要租户隔离
     */
    private static final List<String> IGNORE_TABLES = Arrays.asList(
            "sys_config",           // 系统配置表
            "sys_dict_type",        // 字典类型表
            "sys_dict_data"         // 字典数据表
    );

    /**
     * 获取租户字段名
     *
     * @return 租户字段名
     */
    @Override
    public String getTenantIdColumn() {
        return "tenant_id";
    }

    /**
     * 获取当前租户ID
     * 从 SecurityContextHolder 中获取当前登录用户的租户ID
     *
     * @return 租户ID表达式
     */
    @Override
    public Expression getTenantId() {
        Long tenantId = SecurityContextHolder.getTenantId();

        if (tenantId == null) {
            log.debug("当前上下文中未找到租户ID，使用默认租户ID: 0");
            tenantId = 0L;
        }

        log.debug("多租户拦截 - 租户ID: {}", tenantId);
        return new LongValue(tenantId);
    }

    /**
     * 判断表是否需要忽略租户隔离
     *
     * @param tableName 表名
     * @return true-忽略租户隔离，false-需要租户隔离
     */
    @Override
    public boolean ignoreTable(String tableName) {
        boolean ignore = IGNORE_TABLES.contains(tableName.toLowerCase());

        if (ignore) {
            log.debug("表 {} 忽略租户隔离", tableName);
        }

        return ignore;
    }
}
