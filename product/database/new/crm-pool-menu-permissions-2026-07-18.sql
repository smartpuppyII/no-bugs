-- ============================================
-- CRM 公海机制完善 - 菜单与权限配置
-- 日期: 2026-07-18
-- 说明: 新增公海规则配置、领取限制配置菜单项及权限标识
-- ============================================

SET NAMES utf8mb4;

-- ============================================
-- 1. 新增一级菜单
-- ============================================
-- 6030: 公海规则配置
-- 6031: 领取限制配置
-- parent_id=2397 为CRM父菜单
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6030, '公海规则配置', '', 2, 90, 2397, 'sea-pool/rule-config', 'ep:setting', 'crm/seapool/rule-config/index', 'CrmSeaPoolRuleConfig', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6031, '领取限制配置', '', 2, 91, 2397, 'sea-pool/limit-config', 'ep:lock', 'crm/seapool/limit-config/index', 'CrmSeaPoolLimitConfig', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- ============================================
-- 2. 新增权限按钮（type=3）
-- ============================================
-- 公海规则配置 权限
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6032, '公海规则查询', 'crm:sea-pool-rule:query', 3, 1, 6030, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6033, '公海规则更新', 'crm:sea-pool-rule:update', 3, 2, 6030, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- 领取限制配置 权限
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6034, '领取限制查询', 'crm:sea-pool-limit:query', 3, 1, 6031, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6035, '领取限制更新', 'crm:sea-pool-limit:update', 3, 2, 6031, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- 待回收事项 权限（挂载在CRM工作台 6014 下）
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6036, '待回收查询', 'crm:pool-todo:query', 3, 3, 6014, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6037, '待回收延期', 'crm:pool-todo:extend', 3, 4, 6014, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- 公海流转记录 权限
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6038, '流转记录查询', 'crm:pool-record:query', 3, 5, 6014, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- 公海统计 权限
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6039, '公海统计查询', 'crm:pool-statistics:query', 3, 6, 6014, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- ============================================
-- 3. 角色菜单关联
-- ============================================
-- 超级管理员(role_id=1) 拥有全部权限
INSERT INTO system_role_menu (role_id, menu_id) VALUES
(1, 6030), (1, 6031), (1, 6032), (1, 6033), (1, 6034), (1, 6035),
(1, 6036), (1, 6037), (1, 6038), (1, 6039);

-- 销售主管(role_id=2) 拥有查询和团队管理权限
INSERT INTO system_role_menu (role_id, menu_id) VALUES
(2, 6036), (2, 6037), (2, 6038), (2, 6039);

-- 普通销售(role_id=3) 拥有个人查询和延期权限
INSERT INTO system_role_menu (role_id, menu_id) VALUES
(3, 6036), (3, 6037), (3, 6038);

-- 新人销售(role_id=4) 无公海配置权限
-- 财务审核(role_id=5) 无公海相关权限

-- ============================================
-- 4. 权限标识汇总
-- ============================================
-- crm:sea-pool-rule:query     - 公海规则查询（超级管理员）
-- crm:sea-pool-rule:update    - 公海规则更新（超级管理员）
-- crm:sea-pool-limit:query    - 领取限制查询（超级管理员）
-- crm:sea-pool-limit:update   - 领取限制更新（超级管理员）
-- crm:pool-todo:query         - 待回收列表查询（超管/主管/普通销售）
-- crm:pool-todo:extend        - 一键延期/批量延期（超管/主管/普通销售）
-- crm:pool-record:query       - 流转记录查询（超管/主管/普通销售）
-- crm:pool-statistics:query   - 公海统计查询（超管/主管）