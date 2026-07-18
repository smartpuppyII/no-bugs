-- Add missing CRM permissions for new features
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6020, '标签查询', 'crm:tag:query', 3, 1, 6015, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6021, '标签创建', 'crm:tag:create', 3, 2, 6015, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6022, '标签更新', 'crm:tag:update', 3, 3, 6015, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6023, '标签删除', 'crm:tag:delete', 3, 4, 6015, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6024, '交接查询', 'crm:handover:query', 3, 1, 6016, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6025, '交接执行', 'crm:handover:execute', 3, 2, 6016, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6026, '重复客户查询', 'crm:duplicate:query', 3, 1, 6017, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6027, '重复客户合并', 'crm:duplicate:merge', 3, 2, 6017, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6028, '查重配置查询', 'crm:duplicate-config:query', 3, 1, 6018, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6029, '查重配置更新', 'crm:duplicate-config:update', 3, 2, 6018, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- Assign to admin role
INSERT INTO system_role_menu (role_id, menu_id) VALUES
(1, 6020), (1, 6021), (1, 6022), (1, 6023), (1, 6024), (1, 6025),
(1, 6026), (1, 6027), (1, 6028), (1, 6029);
