-- =============================================
-- CRM P0 Feature Migration
-- Requirements: Tag System, Transfer Log, Duplicate Config
-- =============================================

-- 1. 标签定义表
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
    PRIMARY KEY (id),
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_group_name (group_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CRM 标签定义表';

-- 2. 客户-标签关联表
CREATE TABLE IF NOT EXISTS crm_customer_tag (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '编号',
    customer_id BIGINT NOT NULL COMMENT '客户编号',
    tag_id BIGINT NOT NULL COMMENT '标签编号',
    creator VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_customer_tag (customer_id, tag_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CRM 客户-标签关联表';

-- 3. 转移日志表
CREATE TABLE IF NOT EXISTS crm_transfer_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '编号',
    biz_type INT NOT NULL COMMENT '业务类型：1-线索 2-客户 3-联系人 4-商机 5-合同 6-产品 7-回款 8-回款计划 9-订单',
    biz_id BIGINT NOT NULL COMMENT '业务数据编号',
    biz_name VARCHAR(100) DEFAULT NULL COMMENT '业务数据名称',
    from_user_id BIGINT NOT NULL COMMENT '原负责人编号',
    to_user_id BIGINT NOT NULL COMMENT '新负责人编号',
    transfer_type INT DEFAULT 1 COMMENT '转移类型：1-手动转移 2-离职交接 3-系统自动',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    creator VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    INDEX idx_biz_type_biz_id (biz_type, biz_id),
    INDEX idx_from_user_id (from_user_id),
    INDEX idx_to_user_id (to_user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CRM 转移日志表';

-- 4. 查重规则配置表（扩展现有 pool_config 的配置维度）
CREATE TABLE IF NOT EXISTS crm_duplicate_config (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '编号',
    check_name BIT(1) NOT NULL DEFAULT 1 COMMENT '是否检查名称',
    check_mobile BIT(1) NOT NULL DEFAULT 1 COMMENT '是否检查手机号',
    check_email BIT(1) NOT NULL DEFAULT 0 COMMENT '是否检查邮箱',
    check_wechat BIT(1) NOT NULL DEFAULT 0 COMMENT '是否检查微信',
    check_scope VARCHAR(20) NOT NULL DEFAULT 'ALL' COMMENT '查重范围：ALL/DEPT/SELF',
    tenant_id BIGINT DEFAULT NULL COMMENT '租户编号',
    creator VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CRM 查重规则配置表';

-- 5. 预设标签数据
INSERT INTO crm_tag (name, color, group_name, sort_order) VALUES
('高意向', '#67C23A', '客户特征', 1),
('中意向', '#E6A23C', '客户特征', 2),
('低意向', '#F56C6C', '客户特征', 3),
('价格敏感', '#E6A23C', '风险类型', 1),
('决策周期长', '#909399', '风险类型', 2),
('竞品对比中', '#F56C6C', '风险类型', 3),
('决策者', '#409EFF', '决策角色', 1),
('影响者', '#409EFF', '决策角色', 2),
('守门人', '#409EFF', '决策角色', 3),
('老客户', '#67C23A', '客户特征', 4),
('新客户', '#409EFF', '客户特征', 5),
('VIP客户', '#E6A23C', '客户特征', 6);

-- 6. 站内信通知模板（CRM 待办提醒 + 离职交接通知）
INSERT INTO system_notify_template (name, code, nickname, content, type, params, status, remark, creator, create_time, updater, update_time, deleted) VALUES
('CRM待办提醒', 'crm_todo_remind', '系统助手', '您当前有{count}条待办事项需要处理：{content}', 2, '["count","content"]', 0, 'CRM每日待办事项汇总提醒', '1', NOW(), '1', NOW(), 0),
('CRM离职交接通知', 'crm_handover_notify', '系统助手', '{fromUserName}已将{bizTypeName}交接给您，请及时查看处理。', 2, '["fromUserName","bizTypeName"]', 0, 'CRM离职交接站内信通知', '1', NOW(), '1', NOW(), 0);
