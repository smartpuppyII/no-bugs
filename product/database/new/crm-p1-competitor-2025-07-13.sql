SET NAMES utf8mb4;

-- ============================================
-- P1-9: 竞争对手管理
-- ============================================

CREATE TABLE IF NOT EXISTS `crm_competitor` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` VARCHAR(100) NOT NULL COMMENT '竞争对手名称',
  `advantage` VARCHAR(500) DEFAULT NULL COMMENT '优势',
  `disadvantage` VARCHAR(500) DEFAULT NULL COMMENT '劣势',
  `product_compare` VARCHAR(500) DEFAULT NULL COMMENT '产品对比',
  `website` VARCHAR(200) DEFAULT NULL COMMENT '官网',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-正常 1-禁用',
  `tenant_id` BIGINT DEFAULT NULL COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  INDEX `idx_tenant_id` (`tenant_id`),
  INDEX `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CRM 竞争对手表';

-- 菜单
INSERT IGNORE INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6020, '竞争对手管理', '', 2, 90, 2397, 'competitor', 'ep:connection', 'crm/competitor/index', 'CrmCompetitor', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

INSERT IGNORE INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6021, '竞争对手查询', 'crm:competitor:query', 3, 1, 6020, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);
INSERT IGNORE INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6022, '竞争对手创建', 'crm:competitor:create', 3, 2, 6020, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);
INSERT IGNORE INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6023, '竞争对手更新', 'crm:competitor:update', 3, 3, 6020, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);
INSERT IGNORE INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6024, '竞争对手删除', 'crm:competitor:delete', 3, 4, 6020, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- 角色授权
INSERT IGNORE INTO `system_role_menu` (`role_id`, `menu_id`) VALUES (1, 6020), (1, 6021), (1, 6022), (1, 6023), (1, 6024);

-- i18n
INSERT IGNORE INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6020, 'zh-CN', '竞争对手管理', '1', NOW(), '1', NOW(), 0);
INSERT IGNORE INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6020, 'en', 'Competitor Management', '1', NOW(), '1', NOW(), 0);

-- 商机表新增输单竞品字段
ALTER TABLE `crm_business` ADD COLUMN `lose_competitor_id` BIGINT DEFAULT NULL COMMENT '输单关联竞品编号' AFTER `end_status`;
