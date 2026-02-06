-- ==========================================
-- 完整菜单数据初始化脚本
-- ==========================================

-- 清空现有菜单数据（可选，谨慎使用）
-- DELETE FROM sys_menu WHERE deleted = 0;
-- DELETE FROM sys_role_menu WHERE role_id = 1;

-- 1. 系统管理目录（一级菜单）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, sort_order, path, component, menu_type, visible, status, permission, icon, remark, deleted) 
VALUES (1, '系统管理', 0, 1, '/system', null, 1, 0, 0, null, 'SettingOutlined', '系统管理目录', 0)
ON CONFLICT (menu_id) DO UPDATE SET menu_name = EXCLUDED.menu_name, path = EXCLUDED.path;

-- 2. 用户管理（二级菜单）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, sort_order, path, component, menu_type, visible, status, permission, icon, remark, deleted) 
VALUES (2, '用户管理', 1, 1, '/system/user', 'system/UserManagement', 2, 0, 0, 'system:user:list', 'UserOutlined', '用户管理菜单', 0)
ON CONFLICT (menu_id) DO UPDATE SET menu_name = EXCLUDED.menu_name, path = EXCLUDED.path, component = EXCLUDED.component;

-- 3. 角色管理（二级菜单）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, sort_order, path, component, menu_type, visible, status, permission, icon, remark, deleted) 
VALUES (3, '角色管理', 1, 2, '/system/role', 'system/RoleManagement', 2, 0, 0, 'system:role:list', 'TeamOutlined', '角色管理菜单', 0)
ON CONFLICT (menu_id) DO UPDATE SET menu_name = EXCLUDED.menu_name, path = EXCLUDED.path, component = EXCLUDED.component;

-- 4. 菜单管理（二级菜单）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, sort_order, path, component, menu_type, visible, status, permission, icon, remark, deleted) 
VALUES (4, '菜单管理', 1, 3, '/system/menu', 'system/Menu', 2, 0, 0, 'system:menu:list', 'MenuOutlined', '菜单管理菜单', 0)
ON CONFLICT (menu_id) DO UPDATE SET menu_name = EXCLUDED.menu_name, path = EXCLUDED.path, component = EXCLUDED.component;

-- 5. 部门管理（二级菜单）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, sort_order, path, component, menu_type, visible, status, permission, icon, remark, deleted) 
VALUES (5, '部门管理', 1, 4, '/system/dept', 'system/Dept', 2, 0, 0, 'system:dept:list', 'ApartmentOutlined', '部门管理菜单', 0)
ON CONFLICT (menu_id) DO UPDATE SET menu_name = EXCLUDED.menu_name, path = EXCLUDED.path, component = EXCLUDED.component;

-- 6. 岗位管理（二级菜单）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, sort_order, path, component, menu_type, visible, status, permission, icon, remark, deleted) 
VALUES (6, '岗位管理', 1, 5, '/system/post', 'system/Post', 2, 0, 0, 'system:post:list', 'SolutionOutlined', '岗位管理菜单', 0)
ON CONFLICT (menu_id) DO UPDATE SET menu_name = EXCLUDED.menu_name, path = EXCLUDED.path, component = EXCLUDED.component;

-- 7. 字典管理（二级菜单）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, sort_order, path, component, menu_type, visible, status, permission, icon, remark, deleted) 
VALUES (7, '字典管理', 1, 6, '/system/dict', null, 1, 0, 0, null, 'BookOutlined', '字典管理目录', 0)
ON CONFLICT (menu_id) DO UPDATE SET menu_name = EXCLUDED.menu_name, path = EXCLUDED.path;

-- 7-1. 字典类型（三级菜单）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, sort_order, path, component, menu_type, visible, status, permission, icon, remark, deleted) 
VALUES (71, '字典类型', 7, 1, '/system/dict/type', 'system/DictType', 2, 0, 0, 'system:dict:type:list', null, '字典类型菜单', 0)
ON CONFLICT (menu_id) DO UPDATE SET menu_name = EXCLUDED.menu_name, path = EXCLUDED.path, component = EXCLUDED.component;

-- 7-2. 字典数据（三级菜单）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, sort_order, path, component, menu_type, visible, status, permission, icon, remark, deleted) 
VALUES (72, '字典数据', 7, 2, '/system/dict/data', 'system/DictData', 2, 0, 0, 'system:dict:data:list', null, '字典数据菜单', 0)
ON CONFLICT (menu_id) DO UPDATE SET menu_name = EXCLUDED.menu_name, path = EXCLUDED.path, component = EXCLUDED.component;

-- 8. 系统配置（二级菜单）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, sort_order, path, component, menu_type, visible, status, permission, icon, remark, deleted) 
VALUES (8, '系统配置', 1, 7, '/system/config', 'system/Config', 2, 0, 0, 'system:config:list', 'ControlOutlined', '系统配置菜单', 0)
ON CONFLICT (menu_id) DO UPDATE SET menu_name = EXCLUDED.menu_name, path = EXCLUDED.path, component = EXCLUDED.component;

-- 9. 日志管理（二级菜单）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, sort_order, path, component, menu_type, visible, status, permission, icon, remark, deleted) 
VALUES (9, '日志管理', 1, 8, '/system/logs', null, 1, 0, 0, null, 'FileTextOutlined', '日志管理目录', 0)
ON CONFLICT (menu_id) DO UPDATE SET menu_name = EXCLUDED.menu_name, path = EXCLUDED.path;

-- 9-1. 操作日志（三级菜单）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, sort_order, path, component, menu_type, visible, status, permission, icon, remark, deleted) 
VALUES (91, '操作日志', 9, 1, '/system/logs/operation', 'system/OperationLog', 2, 0, 0, 'system:log:operation:list', null, '操作日志菜单', 0)
ON CONFLICT (menu_id) DO UPDATE SET menu_name = EXCLUDED.menu_name, path = EXCLUDED.path, component = EXCLUDED.component;

-- 9-2. 登录日志（三级菜单）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, sort_order, path, component, menu_type, visible, status, permission, icon, remark, deleted) 
VALUES (92, '登录日志', 9, 2, '/system/logs/login', 'system/LoginLog', 2, 0, 0, 'system:log:login:list', null, '登录日志菜单', 0)
ON CONFLICT (menu_id) DO UPDATE SET menu_name = EXCLUDED.menu_name, path = EXCLUDED.path, component = EXCLUDED.component;

-- 10. 消息管理（一级菜单）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, sort_order, path, component, menu_type, visible, status, permission, icon, remark, deleted) 
VALUES (10, '消息管理', 0, 2, '/message', null, 1, 0, 0, null, 'MessageOutlined', '消息管理目录', 0)
ON CONFLICT (menu_id) DO UPDATE SET menu_name = EXCLUDED.menu_name, path = EXCLUDED.path;

-- 10-1. 系统公告（二级菜单）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, sort_order, path, component, menu_type, visible, status, permission, icon, remark, deleted) 
VALUES (101, '系统公告', 10, 1, '/system/notice', 'system/Notice', 2, 0, 0, 'system:notice:list', 'NotificationOutlined', '系统公告菜单', 0)
ON CONFLICT (menu_id) DO UPDATE SET menu_name = EXCLUDED.menu_name, path = EXCLUDED.path, component = EXCLUDED.component;

-- 10-2. 消息中心（二级菜单）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, sort_order, path, component, menu_type, visible, status, permission, icon, remark, deleted) 
VALUES (102, '消息中心', 10, 2, '/system/message', 'system/Message', 2, 0, 0, 'system:message:list', 'MailOutlined', '消息中心菜单', 0)
ON CONFLICT (menu_id) DO UPDATE SET menu_name = EXCLUDED.menu_name, path = EXCLUDED.path, component = EXCLUDED.component;

-- 11. 文件管理（一级菜单）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, sort_order, path, component, menu_type, visible, status, permission, icon, remark, deleted) 
VALUES (11, '文件管理', 0, 3, '/file', null, 1, 0, 0, null, 'FolderOutlined', '文件管理目录', 0)
ON CONFLICT (menu_id) DO UPDATE SET menu_name = EXCLUDED.menu_name, path = EXCLUDED.path;

-- 11-1. 文件列表（二级菜单）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, sort_order, path, component, menu_type, visible, status, permission, icon, remark, deleted) 
VALUES (111, '文件列表', 11, 1, '/system/file', 'system/File', 2, 0, 0, 'system:file:list', 'FileOutlined', '文件列表菜单', 0)
ON CONFLICT (menu_id) DO UPDATE SET menu_name = EXCLUDED.menu_name, path = EXCLUDED.path, component = EXCLUDED.component;

-- 11-2. 存储配置（二级菜单）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, sort_order, path, component, menu_type, visible, status, permission, icon, remark, deleted) 
VALUES (112, '存储配置', 11, 2, '/system/oss-config', 'system/OssConfig', 2, 0, 0, 'system:oss:config', 'CloudServerOutlined', '云存储配置菜单', 0)
ON CONFLICT (menu_id) DO UPDATE SET menu_name = EXCLUDED.menu_name, path = EXCLUDED.path, component = EXCLUDED.component;

-- 为超级管理员角色（roleId=1）分配所有菜单权限
INSERT INTO sys_role_menu (role_id, menu_id, tenant_id)
SELECT 1, menu_id, 0 FROM sys_menu WHERE deleted = 0
ON CONFLICT (role_id, menu_id) DO NOTHING;

-- 更新序列（如果使用PostgreSQL）
SELECT setval('sys_menu_menu_id_seq', (SELECT MAX(menu_id) FROM sys_menu) + 1, false);

COMMIT;
