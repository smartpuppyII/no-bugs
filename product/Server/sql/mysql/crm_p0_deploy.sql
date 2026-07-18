-- Deploy script: permissions for new menus
INSERT IGNORE INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6026, '重复客户查询', 'crm:duplicate:query', 3, 1, 6017, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6027, '重复客户合并', 'crm:duplicate:merge', 3, 2, 6017, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6028, '查重配置查询', 'crm:duplicate-config:query', 3, 1, 6018, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6029, '查重配置更新', 'crm:duplicate-config:update', 3, 2, 6018, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

INSERT IGNORE INTO system_role_menu (role_id, menu_id) VALUES
(1, 6017), (1, 6018), (1, 6026), (1, 6027), (1, 6028), (1, 6029);
