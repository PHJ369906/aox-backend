-- 迁移 sys_banner 字段到 BaseEntity 规范
-- 目标字段: create_by / create_time / update_by / update_time

USE aox;

SET @db := DATABASE();

-- create_by
SET @has_create_by := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='sys_banner' AND COLUMN_NAME='create_by'
);
SET @has_created_by := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='sys_banner' AND COLUMN_NAME='created_by'
);
SET @sql := IF(@has_create_by = 0,
  'ALTER TABLE sys_banner ADD COLUMN create_by VARCHAR(50) NULL COMMENT ''创建者'' AFTER status',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @sql := IF(@has_created_by = 1,
  'UPDATE sys_banner SET create_by = COALESCE(create_by, CAST(created_by AS CHAR)) WHERE created_by IS NOT NULL',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- create_time
SET @has_create_time := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='sys_banner' AND COLUMN_NAME='create_time'
);
SET @has_created_at := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='sys_banner' AND COLUMN_NAME='created_at'
);
SET @sql := IF(@has_create_time = 0,
  'ALTER TABLE sys_banner ADD COLUMN create_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间'' AFTER create_by',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @sql := IF(@has_created_at = 1,
  'UPDATE sys_banner SET create_time = COALESCE(create_time, created_at) WHERE created_at IS NOT NULL',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- update_by
SET @has_update_by := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='sys_banner' AND COLUMN_NAME='update_by'
);
SET @sql := IF(@has_update_by = 0,
  'ALTER TABLE sys_banner ADD COLUMN update_by VARCHAR(50) NULL COMMENT ''更新者'' AFTER create_time',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- update_time
SET @has_update_time := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='sys_banner' AND COLUMN_NAME='update_time'
);
SET @has_updated_at := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@db AND TABLE_NAME='sys_banner' AND COLUMN_NAME='updated_at'
);
SET @sql := IF(@has_update_time = 0,
  'ALTER TABLE sys_banner ADD COLUMN update_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'' AFTER update_by',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @sql := IF(@has_updated_at = 1,
  'UPDATE sys_banner SET update_time = COALESCE(update_time, updated_at) WHERE updated_at IS NOT NULL',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 删除旧字段
SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='sys_banner' AND COLUMN_NAME='created_by') = 1,
  'ALTER TABLE sys_banner DROP COLUMN created_by',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='sys_banner' AND COLUMN_NAME='created_at') = 1,
  'ALTER TABLE sys_banner DROP COLUMN created_at',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='sys_banner' AND COLUMN_NAME='updated_at') = 1,
  'ALTER TABLE sys_banner DROP COLUMN updated_at',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE sys_banner
  MODIFY COLUMN create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  MODIFY COLUMN update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';
