SET NAMES utf8mb4;

-- ============================================
-- 1. 修复 CRM P0 菜单名称（防止因编码问题导致的乱码）
-- ============================================
UPDATE `system_menu` SET `name` = 'CRM工作台' WHERE `id` = 6014;
UPDATE `system_menu` SET `name` = '标签管理' WHERE `id` = 6015;
UPDATE `system_menu` SET `name` = '离职交接' WHERE `id` = 6016;
UPDATE `system_menu` SET `name` = '重复客户管理' WHERE `id` = 6017;
UPDATE `system_menu` SET `name` = '查重规则配置' WHERE `id` = 6018;

-- ============================================
-- 2. 补充 system_menu_i18n 记录
-- ============================================
-- CRM工作台 6014
INSERT IGNORE INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6014, 'zh-CN', 'CRM工作台', '1', NOW(), '1', NOW(), 0);
INSERT IGNORE INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6014, 'en', 'CRM Dashboard', '1', NOW(), '1', NOW(), 0);

-- 标签管理 6015
INSERT IGNORE INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6015, 'zh-CN', '标签管理', '1', NOW(), '1', NOW(), 0);
INSERT IGNORE INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6015, 'en', 'Tag Management', '1', NOW(), '1', NOW(), 0);

-- 离职交接 6016
INSERT IGNORE INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6016, 'zh-CN', '离职交接', '1', NOW(), '1', NOW(), 0);
INSERT IGNORE INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6016, 'en', 'Handover', '1', NOW(), '1', NOW(), 0);

-- 重复客户管理 6017
INSERT IGNORE INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6017, 'zh-CN', '重复客户管理', '1', NOW(), '1', NOW(), 0);
INSERT IGNORE INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6017, 'en', 'Duplicate Customers', '1', NOW(), '1', NOW(), 0);

-- 查重规则配置 6018
INSERT IGNORE INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6018, 'zh-CN', '查重规则配置', '1', NOW(), '1', NOW(), 0);
INSERT IGNORE INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6018, 'en', 'Duplicate Rules', '1', NOW(), '1', NOW(), 0);

-- 公共线索 6010 (之前的补丁)
INSERT IGNORE INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6010, 'zh-CN', '公共线索', '1', NOW(), '1', NOW(), 0);
INSERT IGNORE INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6010, 'en', 'Public Clues', '1', NOW(), '1', NOW(), 0);

-- 跟进任务 6013 (之前的补丁)
INSERT IGNORE INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6013, 'zh-CN', '跟进任务', '1', NOW(), '1', NOW(), 0);
INSERT IGNORE INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6013, 'en', 'Follow-up Tasks', '1', NOW(), '1', NOW(), 0);

-- 订单管理 6004 (之前的补丁)
INSERT IGNORE INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6004, 'zh-CN', '订单管理', '1', NOW(), '1', NOW(), 0);
INSERT IGNORE INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6004, 'en', 'Order Management', '1', NOW(), '1', NOW(), 0);
