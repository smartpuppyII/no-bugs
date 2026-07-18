SET NAMES utf8mb4;

-- ============================================
-- CRM 公海机制完善 - 数据库层 DDL（迁移脚本）
-- 日期: 2026-07-18
-- 说明:
--   1. 新建 crm_sea_pool_daily_limit 表（公海每日领取限量表）
--   2. 扩展 crm_customer 表（客户公海相关字段）
--   3. 扩展 crm_clue 表（线索公海相关字段）
-- ============================================

-- ============================================
-- 1. 新建 crm_sea_pool_daily_limit 公海每日领取限量表
-- ============================================
DROP TABLE IF EXISTS `crm_sea_pool_daily_limit`;
CREATE TABLE `crm_sea_pool_daily_limit` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
    `user_id` bigint NOT NULL COMMENT '用户编号',
    `resource_type` int NOT NULL COMMENT '资源类型: 1-客户 2-线索',
    `count` int NOT NULL DEFAULT 0 COMMENT '当日已领取数量',
    `limit_date` date NOT NULL COMMENT '限量日期',
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_user_limit_date` (`user_id`, `limit_date`) USING BTREE,
    INDEX `idx_tenant_id` (`tenant_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'CRM 公海每日领取限量表';

-- ============================================
-- 2. 扩展 crm_customer 表 - 客户公海字段
-- ============================================
ALTER TABLE `crm_customer`
    ADD COLUMN `pool_status` tinyint NULL DEFAULT 0 COMMENT '公海状态: 0-非公海(已领取) 1-在公海中',
    ADD COLUMN `pool_enter_time` datetime NULL DEFAULT NULL COMMENT '进入公海时间',
    ADD COLUMN `pool_reason` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '进入公海原因',
    ADD COLUMN `extend_count` int NULL DEFAULT 0 COMMENT '延期次数',
    ADD COLUMN `countdown_freeze` tinyint(1) NULL DEFAULT 0 COMMENT '倒计时冻结: 0-未冻结 1-已冻结',
    ADD COLUMN `freeze_reason` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '冻结原因';

-- ============================================
-- 3. 扩展 crm_clue 表 - 线索公海字段
-- ============================================
ALTER TABLE `crm_clue`
    ADD COLUMN `pool_status` tinyint NULL DEFAULT 0 COMMENT '公海状态: 0-非公海(已领取) 1-在公海中',
    ADD COLUMN `pool_enter_time` datetime NULL DEFAULT NULL COMMENT '进入公海时间',
    ADD COLUMN `pool_reason` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '进入公海原因',
    ADD COLUMN `extend_count` int NULL DEFAULT 0 COMMENT '延期次数',
    ADD COLUMN `protect_deadline` datetime NULL DEFAULT NULL COMMENT '保护截止时间（新领取后保护期内不可被回收）',
    ADD COLUMN `protect_user_id` bigint NULL DEFAULT NULL COMMENT '保护期归属用户编号';