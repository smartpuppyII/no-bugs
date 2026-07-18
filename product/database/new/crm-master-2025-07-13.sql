SET NAMES utf8mb4;

-- ============================================================
-- CRM 全部增量 SQL（P0 + P1）
-- 执行顺序：表结构 → 表变更 → 菜单 → 权限 → i18n → 预设数据
-- ============================================================

-- ============================================================
-- 第一部分：P0 表结构
-- ============================================================

CREATE TABLE IF NOT EXISTS crm_tag (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '编号',
    name VARCHAR(50) NOT NULL COMMENT '标签名称',
    color VARCHAR(20) DEFAULT '#409EFF' COMMENT '标签颜色',
    group_name VARCHAR(50) DEFAULT '默认' COMMENT '标签组名称',
    sort_order INT DEFAULT 0 COMMENT '排序',
    tenant_id BIGINT DEFAULT NULL COMMENT '租户编号',
    creator VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (id), INDEX idx_tenant_id (tenant_id), INDEX idx_group_name (group_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CRM 标签定义表';

CREATE TABLE IF NOT EXISTS crm_customer_tag (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '编号',
    customer_id BIGINT NOT NULL COMMENT '客户编号',
    tag_id BIGINT NOT NULL COMMENT '标签编号',
    creator VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id), UNIQUE KEY uk_customer_tag (customer_id, tag_id),
    INDEX idx_customer_id (customer_id), INDEX idx_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CRM 客户-标签关联表';

CREATE TABLE IF NOT EXISTS crm_transfer_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '编号',
    biz_type INT NOT NULL COMMENT '业务类型',
    biz_id BIGINT NOT NULL COMMENT '业务数据编号',
    biz_name VARCHAR(100) DEFAULT NULL COMMENT '业务数据名称',
    from_user_id BIGINT NOT NULL COMMENT '原负责人编号',
    to_user_id BIGINT NOT NULL COMMENT '新负责人编号',
    transfer_type INT DEFAULT 1 COMMENT '转移类型:1-手动 2-离职交接 3-系统自动',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    creator VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    INDEX idx_biz_type_biz_id (biz_type, biz_id),
    INDEX idx_from_user_id (from_user_id),
    INDEX idx_to_user_id (to_user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CRM 转移日志表';

CREATE TABLE IF NOT EXISTS crm_duplicate_config (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '编号',
    check_name BIT(1) NOT NULL DEFAULT 1 COMMENT '是否检查名称',
    check_mobile BIT(1) NOT NULL DEFAULT 1 COMMENT '是否检查手机号',
    check_email BIT(1) NOT NULL DEFAULT 0 COMMENT '是否检查邮箱',
    check_wechat BIT(1) NOT NULL DEFAULT 0 COMMENT '是否检查微信',
    check_scope VARCHAR(20) NOT NULL DEFAULT 'ALL' COMMENT '查重范围',
    tenant_id BIGINT DEFAULT NULL COMMENT '租户编号',
    creator VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CRM 查重规则配置表';

-- ============================================================
-- 第二部分：P1 表结构
-- ============================================================

CREATE TABLE IF NOT EXISTS crm_competitor (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '编号',
    name VARCHAR(100) NOT NULL COMMENT '竞争对手名称',
    advantage VARCHAR(500) DEFAULT NULL COMMENT '优势',
    disadvantage VARCHAR(500) DEFAULT NULL COMMENT '劣势',
    product_compare VARCHAR(500) DEFAULT NULL COMMENT '产品对比',
    website VARCHAR(200) DEFAULT NULL COMMENT '官网',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0-正常 1-禁用',
    tenant_id BIGINT DEFAULT NULL COMMENT '租户编号',
    creator VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (id), INDEX idx_tenant_id (tenant_id), INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CRM 竞争对手表';

CREATE TABLE IF NOT EXISTS crm_visit_plan (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '编号',
    customer_id BIGINT DEFAULT NULL COMMENT '客户编号',
    customer_name VARCHAR(100) DEFAULT NULL COMMENT '客户名称',
    visit_time DATETIME NOT NULL COMMENT '计划拜访时间',
    purpose VARCHAR(200) DEFAULT NULL COMMENT '拜访目的',
    address VARCHAR(200) DEFAULT NULL COMMENT '拜访地址',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0-待拜访 1-已签到 2-已完成 3-已取消',
    owner_user_id BIGINT NOT NULL COMMENT '负责人编号',
    tenant_id BIGINT DEFAULT NULL COMMENT '租户编号',
    creator VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (id), INDEX idx_owner_user_id (owner_user_id),
    INDEX idx_visit_time (visit_time), INDEX idx_customer_id (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CRM 拜访计划表';

CREATE TABLE IF NOT EXISTS crm_visit_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '编号',
    plan_id BIGINT NOT NULL COMMENT '拜访计划编号',
    customer_id BIGINT NOT NULL COMMENT '客户编号',
    customer_name VARCHAR(100) DEFAULT NULL COMMENT '客户名称',
    check_in_time DATETIME DEFAULT NULL COMMENT '签到时间',
    check_in_location VARCHAR(200) DEFAULT NULL COMMENT '签到地点',
    latitude VARCHAR(50) DEFAULT NULL COMMENT '纬度',
    longitude VARCHAR(50) DEFAULT NULL COMMENT '经度',
    content VARCHAR(1000) DEFAULT NULL COMMENT '拜访内容/报告',
    customer_feedback VARCHAR(500) DEFAULT NULL COMMENT '客户反馈',
    next_step VARCHAR(500) DEFAULT NULL COMMENT '下一步计划',
    owner_user_id BIGINT NOT NULL COMMENT '负责人编号',
    tenant_id BIGINT DEFAULT NULL COMMENT '租户编号',
    creator VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (id), INDEX idx_plan_id (plan_id),
    INDEX idx_customer_id (customer_id), INDEX idx_check_in_time (check_in_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CRM 拜访记录表';

CREATE TABLE IF NOT EXISTS crm_sales_target (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '编号',
    target_type VARCHAR(20) NOT NULL COMMENT '类型:user/dept',
    target_id BIGINT NOT NULL COMMENT '目标对象ID',
    target_name VARCHAR(100) DEFAULT NULL COMMENT '目标对象名称',
    period_type VARCHAR(10) NOT NULL COMMENT '周期:month/quarter/year',
    period_value VARCHAR(10) NOT NULL COMMENT '周期值:2025-07',
    contract_amount DECIMAL(15,2) DEFAULT 0 COMMENT '合同金额目标',
    receivable_amount DECIMAL(15,2) DEFAULT 0 COMMENT '回款金额目标',
    new_customer_count INT DEFAULT 0 COMMENT '新客数目标',
    tenant_id BIGINT DEFAULT NULL COMMENT '租户编号',
    creator VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (id), INDEX idx_target (target_type, target_id, period_type, period_value)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CRM 销售目标表';

CREATE TABLE IF NOT EXISTS crm_quotation (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '编号',
    no VARCHAR(50) NOT NULL COMMENT '报价单号',
    business_id BIGINT NOT NULL COMMENT '商机编号',
    business_name VARCHAR(100) DEFAULT NULL COMMENT '商机名称',
    customer_id BIGINT NOT NULL COMMENT '客户编号',
    customer_name VARCHAR(100) DEFAULT NULL COMMENT '客户名称',
    total_price DECIMAL(15,2) DEFAULT 0 COMMENT '报价总金额',
    discount_percent DECIMAL(5,2) DEFAULT 100 COMMENT '折扣(%)',
    discount_price DECIMAL(15,2) DEFAULT 0 COMMENT '优惠金额',
    final_price DECIMAL(15,2) DEFAULT 0 COMMENT '最终报价',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    audit_status TINYINT NOT NULL DEFAULT 0 COMMENT '审批状态:0-草稿 1-审批中 2-通过 3-拒绝',
    process_instance_id VARCHAR(64) DEFAULT NULL COMMENT '工作流实例编号',
    owner_user_id BIGINT NOT NULL COMMENT '负责人编号',
    tenant_id BIGINT DEFAULT NULL COMMENT '租户编号',
    creator VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (id), INDEX idx_business_id (business_id), INDEX idx_no (no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CRM 报价单表';

CREATE TABLE IF NOT EXISTS crm_quotation_product (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '编号',
    quotation_id BIGINT NOT NULL COMMENT '报价单编号',
    product_id BIGINT NOT NULL COMMENT '产品编号',
    product_name VARCHAR(100) DEFAULT NULL COMMENT '产品名称',
    product_no VARCHAR(50) DEFAULT NULL COMMENT '产品编号',
    product_unit VARCHAR(10) DEFAULT NULL COMMENT '单位',
    product_price DECIMAL(15,2) DEFAULT 0 COMMENT '产品单价',
    count INT DEFAULT 1 COMMENT '数量',
    total_price DECIMAL(15,2) DEFAULT 0 COMMENT '小计',
    PRIMARY KEY (id), INDEX idx_quotation_id (quotation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CRM 报价单产品关联表';

-- ============================================================
-- 第三部分：表结构变更
-- ============================================================

-- 安全的列新增（列不存在时才加）
SET @x = (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'crm_business' AND column_name = 'lose_competitor_id');
SET @sql = IF(@x = 0, 'ALTER TABLE crm_business ADD COLUMN lose_competitor_id BIGINT DEFAULT NULL COMMENT ''输单关联竞品编号'' AFTER end_status', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 第四部分：菜单（无 ID 冲突，全用 INSERT IGNORE）
-- ============================================================

-- 订单管理 (6004-6009) — parent: CRM系统 2397
INSERT IGNORE INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6004, '订单管理', '', 2, 55, 2397, 'order', 'ep:document', 'crm/order/index', 'CrmOrder', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6005, '订单查询', 'crm:order:query', 3, 1, 6004, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6006, '订单创建', 'crm:order:create', 3, 2, 6004, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6007, '订单更新', 'crm:order:update', 3, 3, 6004, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6008, '订单删除', 'crm:order:delete', 3, 4, 6004, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6009, '订单导出', 'crm:order:export', 3, 5, 6004, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- 公共线索池 (6010-6012) — parent: CRM系统 2397
INSERT IGNORE INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6010, '公共线索', '', 2, 7, 2397, 'clue-pool', 'ep:share', 'crm/clue/pool/index', 'CrmCluePool', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6011, '公共线索查询', 'crm:clue:query', 3, 1, 6010, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6012, '公共线索领取', 'crm:clue:update', 3, 2, 6010, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- 跟进任务 (6013) — parent: CRM系统 2397
INSERT IGNORE INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6013, '跟进任务', '', 2, 65, 2397, 'followup-task', 'ep:clock', 'crm/followup/task/index', 'CrmFollowupTask', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- 任务查询 (6028) — child of 6013 (ID 6014 已被 add_crm_menus.sql 的 CRM工作台 占用)
INSERT IGNORE INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6028, '任务查询', 'crm:followup:query', 3, 1, 6013, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- CRM 工作台(6014) + 标签(6016) + 离职交接(6017) + 重复客户(6018) + 查重规则(6019)
-- 注意：6014/6016-6019 由 add_crm_menus.sql 创建，此处的 INSERT IGNORE 作为兜底
INSERT IGNORE INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6014, 'CRM工作台', '', 2, 0, 2397, 'dashboard', 'ep:home-filled', 'crm/dashboard/index', 'CrmDashboard', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6016, '标签管理', '', 2, 75, 2397, 'tag', 'ep:price-tag', 'crm/tag/index', 'CrmTagManagement', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6017, '离职交接', '', 2, 70, 2397, 'handover', 'ep:switch', 'crm/handover/index', 'CrmHandoverManagement', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6018, '重复客户管理', '', 2, 80, 2397, 'duplicate', 'ep:copy-document', 'crm/customer/duplicate/index', 'CrmDuplicateManagement', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6019, '查重规则配置', '', 2, 85, 2397, 'duplicate/config', 'ep:setting', 'crm/customer/duplicate/config', 'CrmDuplicateConfig', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- 重复客户权限 (6026-6027) — children of 6018
INSERT IGNORE INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6026, '重复客户查询', 'crm:duplicate:query', 3, 1, 6018, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6027, '重复客户合并', 'crm:duplicate:merge', 3, 2, 6018, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- P1 拜访管理 (6030-6034) — parent: CRM系统 2397
INSERT IGNORE INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6030, '拜访管理', '', 1, 95, 2397, 'visit', 'ep:map-location', 'crm/visit/plan/index', 'CrmVisitPlan', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6031, '拜访计划查询', 'crm:visit:query', 3, 1, 6030, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6032, '拜访计划创建', 'crm:visit:create', 3, 2, 6030, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6033, '拜访计划更新', 'crm:visit:update', 3, 3, 6030, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6034, '拜访计划删除', 'crm:visit:delete', 3, 4, 6030, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- P1 销售目标 (6040)
INSERT IGNORE INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6040, '销售目标', '', 2, 100, 2397, 'target', 'ep:aim', 'crm/target/index', 'CrmTarget', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- P1 报价单管理 (6050)
INSERT IGNORE INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6050, '报价单管理', '', 2, 105, 2397, 'quotation', 'ep:document-copy', 'crm/quotation/index', 'CrmQuotation', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- P1 竞争对手管理 (6055-6059)
INSERT IGNORE INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6055, '竞争对手管理', '', 2, 91, 2397, 'competitor', 'ep:connection', 'crm/competitor/index', 'CrmCompetitor', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6056, '竞争对手查询', 'crm:competitor:query', 3, 1, 6055, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6057, '竞争对手创建', 'crm:competitor:create', 3, 2, 6055, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6058, '竞争对手更新', 'crm:competitor:update', 3, 3, 6055, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6059, '竞争对手删除', 'crm:competitor:delete', 3, 4, 6055, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- ============================================================
-- 第五部分：角色授权（管理员 role_id=1）
-- ============================================================

INSERT IGNORE INTO system_role_menu (role_id, menu_id) VALUES
(1, 6004), (1, 6005), (1, 6006), (1, 6007), (1, 6008), (1, 6009),
(1, 6010), (1, 6011), (1, 6012), (1, 6013), (1, 6028),
(1, 6014), (1, 6016), (1, 6017), (1, 6018), (1, 6019),
(1, 6026), (1, 6027),
(1, 6030), (1, 6031), (1, 6032), (1, 6033), (1, 6034),
(1, 6040), (1, 6050),
(1, 6055), (1, 6056), (1, 6057), (1, 6058), (1, 6059);

-- ============================================================
-- 第六部分：菜单 i18n（zh-CN + en）
-- ============================================================

INSERT IGNORE INTO system_menu_i18n (menu_id, language, name, creator, create_time, updater, update_time, deleted) VALUES
-- 订单
(6004, 'zh-CN', '订单管理', '1', NOW(), '1', NOW(), 0), (6004, 'en', 'Order Management', '1', NOW(), '1', NOW(), 0),
-- 公共线索
(6010, 'zh-CN', '公共线索', '1', NOW(), '1', NOW(), 0), (6010, 'en', 'Public Clues', '1', NOW(), '1', NOW(), 0),
-- 跟进任务
(6013, 'zh-CN', '跟进任务', '1', NOW(), '1', NOW(), 0), (6013, 'en', 'Follow-up Tasks', '1', NOW(), '1', NOW(), 0),
-- 工作台
(6014, 'zh-CN', 'CRM工作台', '1', NOW(), '1', NOW(), 0), (6014, 'en', 'CRM Dashboard', '1', NOW(), '1', NOW(), 0),
-- 标签管理
(6016, 'zh-CN', '标签管理', '1', NOW(), '1', NOW(), 0), (6016, 'en', 'Tag Management', '1', NOW(), '1', NOW(), 0),
-- 离职交接
(6017, 'zh-CN', '离职交接', '1', NOW(), '1', NOW(), 0), (6017, 'en', 'Handover', '1', NOW(), '1', NOW(), 0),
-- 重复客户
(6018, 'zh-CN', '重复客户管理', '1', NOW(), '1', NOW(), 0), (6018, 'en', 'Duplicate Customers', '1', NOW(), '1', NOW(), 0),
-- 查重规则
(6019, 'zh-CN', '查重规则配置', '1', NOW(), '1', NOW(), 0), (6019, 'en', 'Duplicate Rules', '1', NOW(), '1', NOW(), 0),
-- 拜访
(6030, 'zh-CN', '拜访管理', '1', NOW(), '1', NOW(), 0), (6030, 'en', 'Visit Management', '1', NOW(), '1', NOW(), 0),
-- 销售目标
(6040, 'zh-CN', '销售目标', '1', NOW(), '1', NOW(), 0), (6040, 'en', 'Sales Target', '1', NOW(), '1', NOW(), 0),
-- 报价单
(6050, 'zh-CN', '报价单管理', '1', NOW(), '1', NOW(), 0), (6050, 'en', 'Quotation Management', '1', NOW(), '1', NOW(), 0),
-- 竞争对手
(6055, 'zh-CN', '竞争对手管理', '1', NOW(), '1', NOW(), 0), (6055, 'en', 'Competitor Management', '1', NOW(), '1', NOW(), 0);

-- ============================================================
-- 第七部分：预设数据
-- ============================================================

INSERT IGNORE INTO crm_tag (name, color, group_name, sort_order) VALUES
('高意向', '#67C23A', '客户特征', 1), ('中意向', '#E6A23C', '客户特征', 2),
('低意向', '#F56C6C', '客户特征', 3), ('老客户', '#67C23A', '客户特征', 4),
('新客户', '#409EFF', '客户特征', 5), ('VIP客户', '#E6A23C', '客户特征', 6),
('价格敏感', '#E6A23C', '风险类型', 1), ('决策周期长', '#909399', '风险类型', 2),
('竞品对比中', '#F56C6C', '风险类型', 3),
('决策者', '#409EFF', '决策角色', 1), ('影响者', '#409EFF', '决策角色', 2),
('守门人', '#409EFF', '决策角色', 3);

INSERT IGNORE INTO system_notify_template (name, code, nickname, content, type, params, status, remark, creator, create_time, updater, update_time, deleted) VALUES
('CRM待办提醒', 'crm_todo_remind', '系统助手', '您当前有{count}条待办事项需要处理：{content}', 2, '["count","content"]', 0, 'CRM每日待办事项汇总提醒', '1', NOW(), '1', NOW(), 0),
('CRM离职交接通知', 'crm_handover_notify', '系统助手', '{fromUserName}已将{bizTypeName}交接给您，请及时查看处理。', 2, '["fromUserName","bizTypeName"]', 0, 'CRM离职交接站内信通知', '1', NOW(), '1', NOW(), 0);
