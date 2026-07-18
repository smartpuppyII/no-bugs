SET NAMES utf8mb4;

-- ============================================
-- CRM 公海机制完善 - 数据库层扩展
-- 日期: 2026-07-18
-- 说明: 扩展 crm_customer_pool_config 表，新建4张公海相关表
-- ============================================

-- ============================================
-- 1. 扩展 crm_customer_pool_config 表
-- ============================================
ALTER TABLE `crm_customer_pool_config`
    ADD COLUMN `level_expire_days` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '客户等级回收时效JSON，格式: {"A":30,"B":15,"C":7}',
    ADD COLUMN `pause_contract_enabled` tinyint(1) NULL DEFAULT 0 COMMENT '是否暂停有合同客户的回收 0-否 1-是',
    ADD COLUMN `pause_receivable_enabled` tinyint(1) NULL DEFAULT 0 COMMENT '是否暂停有应收款客户的回收 0-否 1-是',
    ADD COLUMN `extend_max_count` int NULL DEFAULT 0 COMMENT '最大延期次数';

-- ============================================
-- 2. 新建 crm_clue_pool_config 线索公海配置表
-- ============================================
DROP TABLE IF EXISTS `crm_clue_pool_config`;
CREATE TABLE `crm_clue_pool_config` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
    `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用线索公海',
    `clue_expire_days` int NULL DEFAULT NULL COMMENT '线索回收时效天数',
    `follow_up_reset_enabled` tinyint(1) NULL DEFAULT 1 COMMENT '跟进是否重置计时 0-否 1-是',
    `a_level_exempt_enabled` tinyint(1) NULL DEFAULT 0 COMMENT 'A级线索是否免回收 0-否 1-是',
    `notify_days` int NULL DEFAULT NULL COMMENT '提前提醒天数',
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'CRM 线索公海配置表';

-- ============================================
-- 3. 新建 crm_sea_pool_limit_config 公海领取限制配置表
-- ============================================
DROP TABLE IF EXISTS `crm_sea_pool_limit_config`;
CREATE TABLE `crm_sea_pool_limit_config` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
    `daily_clue_limit` int NULL DEFAULT NULL COMMENT '每日线索领取上限',
    `daily_customer_limit` int NULL DEFAULT NULL COMMENT '每日客户领取上限',
    `cooling_days` int NULL DEFAULT NULL COMMENT '冷却天数（退回公海后多少天内不可再次领取）',
    `protect_hours` int NULL DEFAULT NULL COMMENT '保护小时数（新领取后保护期内不可被回收）',
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'CRM 公海领取限制配置表';

-- ============================================
-- 4. 新建 crm_clue_pool_record 线索公海流转记录表
-- ============================================
DROP TABLE IF EXISTS `crm_clue_pool_record`;
CREATE TABLE `crm_clue_pool_record` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
    `clue_id` bigint NOT NULL COMMENT '线索编号',
    `from_user_id` bigint NULL DEFAULT NULL COMMENT '来源用户编号（回收前归属人/退回人）',
    `to_user_id` bigint NULL DEFAULT NULL COMMENT '目标用户编号（领取人/回收后归属）',
    `operate_type` int NOT NULL COMMENT '操作类型: 1-自动回收 2-手动退回 3-主动领取 4-管理员分配',
    `reason` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '操作原因/备注',
    `operate_time` datetime NOT NULL COMMENT '操作时间',
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_clue_id` (`clue_id`) USING BTREE,
    INDEX `idx_operate_time` (`operate_time`) USING BTREE,
    INDEX `idx_tenant_id` (`tenant_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'CRM 线索公海流转记录表';

-- ============================================
-- 5. 新建 crm_customer_pool_record 客户公海流转记录表
-- ============================================
DROP TABLE IF EXISTS `crm_customer_pool_record`;
CREATE TABLE `crm_customer_pool_record` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
    `customer_id` bigint NOT NULL COMMENT '客户编号',
    `from_user_id` bigint NULL DEFAULT NULL COMMENT '来源用户编号（回收前归属人/退回人）',
    `to_user_id` bigint NULL DEFAULT NULL COMMENT '目标用户编号（领取人/回收后归属）',
    `operate_type` int NOT NULL COMMENT '操作类型: 1-自动回收 2-手动退回 3-主动领取 4-管理员分配',
    `reason` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '操作原因/备注',
    `operate_time` datetime NOT NULL COMMENT '操作时间',
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_customer_id` (`customer_id`) USING BTREE,
    INDEX `idx_operate_time` (`operate_time`) USING BTREE,
    INDEX `idx_tenant_id` (`tenant_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'CRM 客户公海流转记录表';