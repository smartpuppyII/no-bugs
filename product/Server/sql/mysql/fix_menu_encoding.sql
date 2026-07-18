-- Fix menu items for new CRM pages
-- First delete the garbled ones
DELETE FROM system_role_menu WHERE menu_id IN (6015, 6016);
DELETE FROM system_menu WHERE id IN (6015, 6016);

-- Re-insert with proper encoding
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6015, '标签管理', '', 2, 75, 2397, 'tag', 'ep:price-tag', 'crm/tag/index', 'CrmTagManagement', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(6016, '离职交接', '', 2, 70, 2397, 'handover', 'ep:switch', 'crm/handover/index', 'CrmHandoverManagement', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- Re-assign to admin role
INSERT INTO system_role_menu (role_id, menu_id) VALUES (1, 6015), (1, 6016);
