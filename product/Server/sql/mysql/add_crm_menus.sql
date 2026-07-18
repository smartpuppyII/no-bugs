INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6014, 'CRM工作台', '', 2, 0, 2397, 'dashboard', 'ep:home-filled', 'crm/dashboard/index', 'CrmDashboard', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6015, '标签管理', '', 2, 75, 2397, 'tag', 'ep:price-tag', 'crm/tag/index', 'CrmTagManagement', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6016, '离职交接', '', 2, 70, 2397, 'handover', 'ep:switch', 'crm/handover/index', 'CrmHandoverManagement', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6017, '重复客户管理', '', 2, 80, 2397, 'duplicate', 'ep:copy-document', 'crm/customer/duplicate/index', 'CrmDuplicateManagement', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6018, '查重规则配置', '', 2, 85, 2397, 'duplicate/config', 'ep:setting', 'crm/customer/duplicate/config', 'CrmDuplicateConfig', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

INSERT INTO system_role_menu (role_id, menu_id) VALUES (1, 6014), (1, 6015), (1, 6016), (1, 6017), (1, 6018);
