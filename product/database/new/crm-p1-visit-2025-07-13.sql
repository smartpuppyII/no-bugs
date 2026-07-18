SET NAMES utf8mb4;

-- ============================================
-- P1-6: 日程与拜访管理
-- ============================================

CREATE TABLE IF NOT EXISTS `crm_visit_plan` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '编号',
  `customer_id` BIGINT NOT NULL COMMENT '客户编号',
  `customer_name` VARCHAR(100) DEFAULT NULL COMMENT '客户名称',
  `visit_time` DATETIME NOT NULL COMMENT '计划拜访时间',
  `purpose` VARCHAR(200) DEFAULT NULL COMMENT '拜访目的',
  `address` VARCHAR(200) DEFAULT NULL COMMENT '拜访地址',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待拜访 1-已签到 2-已完成 3-已取消',
  `owner_user_id` BIGINT NOT NULL COMMENT '负责人编号',
  `tenant_id` BIGINT DEFAULT NULL COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  INDEX `idx_owner_user_id` (`owner_user_id`),
  INDEX `idx_visit_time` (`visit_time`),
  INDEX `idx_customer_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CRM 拜访计划表';

CREATE TABLE IF NOT EXISTS `crm_visit_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '编号',
  `plan_id` BIGINT NOT NULL COMMENT '拜访计划编号',
  `customer_id` BIGINT NOT NULL COMMENT '客户编号',
  `customer_name` VARCHAR(100) DEFAULT NULL COMMENT '客户名称',
  `check_in_time` DATETIME DEFAULT NULL COMMENT '签到时间',
  `check_in_location` VARCHAR(200) DEFAULT NULL COMMENT '签到地点',
  `latitude` VARCHAR(50) DEFAULT NULL COMMENT '纬度',
  `longitude` VARCHAR(50) DEFAULT NULL COMMENT '经度',
  `content` VARCHAR(1000) DEFAULT NULL COMMENT '拜访内容/报告',
  `customer_feedback` VARCHAR(500) DEFAULT NULL COMMENT '客户反馈',
  `next_step` VARCHAR(500) DEFAULT NULL COMMENT '下一步计划',
  `owner_user_id` BIGINT NOT NULL COMMENT '负责人编号',
  `tenant_id` BIGINT DEFAULT NULL COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  INDEX `idx_plan_id` (`plan_id`),
  INDEX `idx_customer_id` (`customer_id`),
  INDEX `idx_check_in_time` (`check_in_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CRM 拜访记录表';

-- 菜单
INSERT IGNORE INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6030, '拜访管理', '', 1, 95, 2397, 'visit', 'ep:map-location', 'crm/visit/plan/index', 'CrmVisitPlan', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

INSERT IGNORE INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6031, '拜访计划查询', 'crm:visit:query', 3, 1, 6030, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);
INSERT IGNORE INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6032, '拜访计划创建', 'crm:visit:create', 3, 2, 6030, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);
INSERT IGNORE INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6033, '拜访计划更新', 'crm:visit:update', 3, 3, 6030, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);
INSERT IGNORE INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6034, '拜访计划删除', 'crm:visit:delete', 3, 4, 6030, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

INSERT IGNORE INTO `system_role_menu` (`role_id`, `menu_id`) VALUES (1, 6030), (1, 6031), (1, 6032), (1, 6033), (1, 6034);

INSERT IGNORE INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6030, 'zh-CN', '拜访管理', '1', NOW(), '1', NOW(), 0);
INSERT IGNORE INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6030, 'en', 'Visit Management', '1', NOW(), '1', NOW(), 0);
