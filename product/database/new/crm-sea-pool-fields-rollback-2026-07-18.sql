SET NAMES utf8mb4;

-- ============================================
-- CRM 公海机制完善 - 数据库层 DDL（回滚脚本）
-- 日期: 2026-07-18
-- 说明:
--   1. 删除 crm_customer 表新增的公海字段
--   2. 删除 crm_clue 表新增的公海字段
--   3. 删除 crm_sea_pool_daily_limit 表
-- ============================================

-- ============================================
-- 1. 回滚 crm_customer 表 - 移除客户公海字段
-- ============================================
ALTER TABLE `crm_customer`
    DROP COLUMN `pool_status`,
    DROP COLUMN `pool_enter_time`,
    DROP COLUMN `pool_reason`,
    DROP COLUMN `extend_count`,
    DROP COLUMN `countdown_freeze`,
    DROP COLUMN `freeze_reason`;

-- ============================================
-- 2. 回滚 crm_clue 表 - 移除线索公海字段
-- ============================================
ALTER TABLE `crm_clue`
    DROP COLUMN `pool_status`,
    DROP COLUMN `pool_enter_time`,
    DROP COLUMN `pool_reason`,
    DROP COLUMN `extend_count`,
    DROP COLUMN `protect_deadline`,
    DROP COLUMN `protect_user_id`;

-- ============================================
-- 3. 回滚 crm_sea_pool_daily_limit 表 - 删除整表
-- ============================================
DROP TABLE IF EXISTS `crm_sea_pool_daily_limit`;