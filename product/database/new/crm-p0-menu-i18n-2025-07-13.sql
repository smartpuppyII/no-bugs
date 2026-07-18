SET NAMES utf8mb4;

-- ============================================
-- CRM P0 菜单国际化补丁
-- 补充 public_clue、followup_task、order 菜单的 system_menu_i18n 记录
-- ============================================

-- 公共线索 6010
INSERT IGNORE INTO `system_menu_i18n` (`id`, `menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (NULL, 6010, 'zh-CN', '公共线索', '1', NOW(), '1', NOW(), b'0');
INSERT IGNORE INTO `system_menu_i18n` (`id`, `menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (NULL, 6010, 'en', 'Public Clues', '1', NOW(), '1', NOW(), b'0');

-- 公共线索查询 6011
INSERT IGNORE INTO `system_menu_i18n` (`id`, `menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (NULL, 6011, 'zh-CN', '公共线索查询', '1', NOW(), '1', NOW(), b'0');
INSERT IGNORE INTO `system_menu_i18n` (`id`, `menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (NULL, 6011, 'en', 'Public Clue Query', '1', NOW(), '1', NOW(), b'0');

-- 公共线索领取 6012
INSERT IGNORE INTO `system_menu_i18n` (`id`, `menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (NULL, 6012, 'zh-CN', '公共线索领取', '1', NOW(), '1', NOW(), b'0');
INSERT IGNORE INTO `system_menu_i18n` (`id`, `menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (NULL, 6012, 'en', 'Claim Public Clue', '1', NOW(), '1', NOW(), b'0');

-- 跟进任务 6013
INSERT IGNORE INTO `system_menu_i18n` (`id`, `menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (NULL, 6013, 'zh-CN', '跟进任务', '1', NOW(), '1', NOW(), b'0');
INSERT IGNORE INTO `system_menu_i18n` (`id`, `menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (NULL, 6013, 'en', 'Follow-up Tasks', '1', NOW(), '1', NOW(), b'0');

-- 任务查询 6014
INSERT IGNORE INTO `system_menu_i18n` (`id`, `menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (NULL, 6014, 'zh-CN', '任务查询', '1', NOW(), '1', NOW(), b'0');
INSERT IGNORE INTO `system_menu_i18n` (`id`, `menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (NULL, 6014, 'en', 'Task Query', '1', NOW(), '1', NOW(), b'0');

-- 订单管理 6004
INSERT IGNORE INTO `system_menu_i18n` (`id`, `menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (NULL, 6004, 'zh-CN', '订单管理', '1', NOW(), '1', NOW(), b'0');
INSERT IGNORE INTO `system_menu_i18n` (`id`, `menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (NULL, 6004, 'en', 'Order Management', '1', NOW(), '1', NOW(), b'0');

-- 订单查询 6005
INSERT IGNORE INTO `system_menu_i18n` (`id`, `menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (NULL, 6005, 'zh-CN', '订单查询', '1', NOW(), '1', NOW(), b'0');
INSERT IGNORE INTO `system_menu_i18n` (`id`, `menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (NULL, 6005, 'en', 'Order Query', '1', NOW(), '1', NOW(), b'0');

-- 订单创建 6006
INSERT IGNORE INTO `system_menu_i18n` (`id`, `menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (NULL, 6006, 'zh-CN', '订单创建', '1', NOW(), '1', NOW(), b'0');
INSERT IGNORE INTO `system_menu_i18n` (`id`, `menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (NULL, 6006, 'en', 'Create Order', '1', NOW(), '1', NOW(), b'0');

-- 订单更新 6007
INSERT IGNORE INTO `system_menu_i18n` (`id`, `menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (NULL, 6007, 'zh-CN', '订单更新', '1', NOW(), '1', NOW(), b'0');
INSERT IGNORE INTO `system_menu_i18n` (`id`, `menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (NULL, 6007, 'en', 'Update Order', '1', NOW(), '1', NOW(), b'0');

-- 订单删除 6008
INSERT IGNORE INTO `system_menu_i18n` (`id`, `menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (NULL, 6008, 'zh-CN', '订单删除', '1', NOW(), '1', NOW(), b'0');
INSERT IGNORE INTO `system_menu_i18n` (`id`, `menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (NULL, 6008, 'en', 'Delete Order', '1', NOW(), '1', NOW(), b'0');

-- 订单导出 6009
INSERT IGNORE INTO `system_menu_i18n` (`id`, `menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (NULL, 6009, 'zh-CN', '订单导出', '1', NOW(), '1', NOW(), b'0');
INSERT IGNORE INTO `system_menu_i18n` (`id`, `menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (NULL, 6009, 'en', 'Export Order', '1', NOW(), '1', NOW(), b'0');
