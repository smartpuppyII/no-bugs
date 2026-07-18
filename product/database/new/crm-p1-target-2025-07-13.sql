SET NAMES utf8mb4;
-- P1-7: 销售目标管理
CREATE TABLE IF NOT EXISTS `crm_sales_target` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '编号',
  `target_type` VARCHAR(20) NOT NULL COMMENT '类型：user/dept',
  `target_id` BIGINT NOT NULL COMMENT '目标对象ID（用户ID或部门ID）',
  `target_name` VARCHAR(100) DEFAULT NULL COMMENT '目标对象名称',
  `period_type` VARCHAR(10) NOT NULL COMMENT '周期：month/quarter/year',
  `period_value` VARCHAR(10) NOT NULL COMMENT '周期值：2025-07/2025-Q3/2025',
  `contract_amount` DECIMAL(15,2) DEFAULT 0 COMMENT '合同金额目标',
  `receivable_amount` DECIMAL(15,2) DEFAULT 0 COMMENT '回款金额目标',
  `new_customer_count` INT DEFAULT 0 COMMENT '新客数目标',
  `tenant_id` BIGINT DEFAULT NULL,
  `creator` VARCHAR(64) DEFAULT NULL, `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` VARCHAR(64) DEFAULT NULL, `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` BIT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`), INDEX `idx_target` (`target_type`,`target_id`,`period_type`,`period_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CRM 销售目标表';

INSERT IGNORE INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6040, '销售目标', '', 2, 100, 2397, 'target', 'ep:aim', 'crm/target/index', 'CrmTarget', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);
INSERT IGNORE INTO `system_role_menu` (`role_id`, `menu_id`) VALUES (1, 6040);
INSERT IGNORE INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6040, 'zh-CN', '销售目标', '1', NOW(), '1', NOW(), 0);
INSERT IGNORE INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6040, 'en', 'Sales Target', '1', NOW(), '1', NOW(), 0);
