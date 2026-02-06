-- ==========================================
-- 权限关联表初始化脚本
-- ==========================================

-- 用户-角色关联表
CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` VARCHAR(64) COMMENT '创建人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色-菜单关联表
CREATE TABLE IF NOT EXISTS `sys_role_menu` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `menu_id` BIGINT NOT NULL COMMENT '菜单ID',
    `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` VARCHAR(64) COMMENT '创建人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_menu_id` (`menu_id`),
    KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 用户-岗位关联表
CREATE TABLE IF NOT EXISTS `sys_user_post` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `post_id` BIGINT NOT NULL COMMENT '岗位ID',
    `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` VARCHAR(64) COMMENT '创建人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_post` (`user_id`, `post_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_post_id` (`post_id`),
    KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户岗位关联表';

-- 初始化超级管理员角色的菜单权限（所有菜单）
-- 假设角色ID=1是超级管理员
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`, `tenant_id`)
SELECT 1, menu_id, 0 FROM sys_menu WHERE deleted = 0;

-- 为管理员用户分配超级管理员角色
-- 假设用户ID=1是管理员
INSERT IGNORE INTO `sys_user_role` (`user_id`, `role_id`, `tenant_id`)
VALUES (1, 1, 0);
