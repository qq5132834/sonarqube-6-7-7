1、目前插件的问题
    1.1 无法默认规则有效；
    1.2 无法获取有效规则。

2、清理数据
show tables;
select * from active_rule_parameters ;  DELETE FROM active_rule_parameters ;
select * from active_rules ;    DELETE FROM active_rules ;
select * from analysis_properties ;
select * from ce_activity ;
select * from ce_queue ;
select * from ce_scanner_context ;
select * from ce_task_characteristics ;
select * from ce_task_input ;
select * from default_qprofiles ;  DELETE FROM default_qprofiles  ;
select * from duplications_index ;
select * from es_queue ;
select * from events ;
select * from file_sources ;
select * from group_roles ;
select * from groups ;
select * from groups_users ;
select * from internal_properties ;
select * from issue_changes ;
select * from issues ;
select * from loaded_templates ;
select * from manual_measures ;
select * from metrics ; 	  DELETE FROM metrics ;
select * from notifications ; DELETE FROM notifications ;
select * from org_qprofiles ; DELETE FROM org_qprofiles ;
select * from organization_members ;
select * from organizations ;
select * from perm_templates_groups ;
select * from perm_templates_users ;
select * from perm_tpl_characteristics ;
select * from permission_templates ;
select * from plugins ;  DELETE FROM plugins ;
select * from project_branches ;
select * from project_links ;
select * from project_measures ;
select * from project_qprofiles ;
select * from projects ;
select * from properties ;
select * from qprofile_changes ; DELETE FROM qprofile_changes ;
select * from qprofile_edit_groups ;
select * from qprofile_edit_users ;
select * from quality_gate_conditions ;
select * from quality_gates ;
select * from rule_repositories ;  DELETE FROM rule_repositories ;
select * from rules ;   DELETE FROM rules ;
select * from rules_metadata ;     
select * from rules_parameters ;  DELETE FROM rules_parameters ;
select * from rules_profiles ;    DELETE FROM rules_profiles ;
select * from schema_migrations ;  
select * from snapshots ;
select * from user_roles ;
select * from user_tokens ;
select * from users ;
select * from webhook_deliveries ;