SET NAMES utf8mb4;
-- P1-8: 报价单管理
CREATE TABLE IF NOT EXISTS `crm_quotation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '编号',
  `no` VARCHAR(50) NOT NULL COMMENT '报价单号',
  `business_id` BIGINT NOT NULL COMMENT '商机编号',
  `business_name` VARCHAR(100) DEFAULT NULL COMMENT '商机名称',
  `customer_id` BIGINT NOT NULL COMMENT '客户编号',
  `customer_name` VARCHAR(100) DEFAULT NULL COMMENT '客户名称',
  `total_price` DECIMAL(15,2) DEFAULT 0 COMMENT '报价总金额',
  `discount_percent` DECIMAL(5,2) DEFAULT 100 COMMENT '折扣(%)',
  `discount_price` DECIMAL(15,2) DEFAULT 0 COMMENT '优惠金额',
  `final_price` DECIMAL(15,2) DEFAULT 0 COMMENT '最终报价',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `audit_status` TINYINT NOT NULL DEFAULT 0 COMMENT '审批状态：0-草稿 1-审批中 2-审批通过 3-审批拒绝',
  `process_instance_id` VARCHAR(64) DEFAULT NULL COMMENT '工作流实例编号',
  `owner_user_id` BIGINT NOT NULL COMMENT '负责人编号',
  `tenant_id` BIGINT DEFAULT NULL,
  `creator` VARCHAR(64) DEFAULT NULL, `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` VARCHAR(64) DEFAULT NULL, `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` BIT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`), INDEX `idx_business_id` (`business_id`), INDEX `idx_no` (`no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CRM 报价单表';

CREATE TABLE IF NOT EXISTS `crm_quotation_product` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '编号',
  `quotation_id` BIGINT NOT NULL COMMENT '报价单编号',
  `product_id` BIGINT NOT NULL COMMENT '产品编号',
  `product_name` VARCHAR(100) DEFAULT NULL COMMENT '产品名称',
  `product_no` VARCHAR(50) DEFAULT NULL COMMENT '产品编号',
  `product_unit` VARCHAR(10) DEFAULT NULL COMMENT '单位',
  `product_price` DECIMAL(15,2) DEFAULT 0 COMMENT '产品单价',
  `count` INT DEFAULT 1 COMMENT '数量',
  `total_price` DECIMAL(15,2) DEFAULT 0 COMMENT '小计',
  PRIMARY KEY (`id`), INDEX `idx_quotation_id` (`quotation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CRM 报价单产品关联表';

INSERT IGNORE INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6050, '报价单管理', '', 2, 105, 2397, 'quotation', 'ep:document-copy', 'crm/quotation/index', 'CrmQuotation', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);
INSERT IGNORE INTO `system_role_menu` (`role_id`, `menu_id`) VALUES (1, 6050);
INSERT IGNORE INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6050, 'zh-CN', '报价单管理', '1', NOW(), '1', NOW(), 0);
INSERT IGNORE INTO `system_menu_i18n` (`menu_id`, `language`, `name`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6050, 'en', 'Quotation Management', '1', NOW(), '1', NOW(), 0);
