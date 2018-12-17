SET SCHEMA TASKANA;

INSERT INTO HISTORY_EVENTS (BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, TASK_ID, EVENT_TYPE, CREATED, USER_ID, DOMAIN, WORKBASKET_KEY, POR_COMPANY, POR_SYSTEM, POR_INSTANCE, POR_TYPE, POR_VALUE, TASK_CLASSIFICATION_KEY, TASK_CLASSIFICATION_CATEGORY, ATTACHMENT_CLASSIFICATION_KEY, COMMENT, OLD_VALUE, NEW_VALUE, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, OLD_DATA, NEW_DATA) VALUES
-- BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, TASK_ID, 	EVENT_TYPE, CREATED, 							 USER_ID, 	DOMAIN, 	WORKBASKET_KEY, 							POR_COMPANY	, POR_SYSTEM, POR_INSTANCE	, POR_TYPE	, POR_VALUE	, TASK_CLASSIFICATION_KEY, TASK_CLASSIFICATION_CATEGORY	, ATTACHMENT_CLASSIFICATION_KEY	, COMMENT						, OLD_VALUE	, NEW_VALUE	, CUSTOM_1	, CUSTOM_2	, CUSTOM_3	, CUSTOM_4	, OLD_DATA	, NEW_DATA
('BPI:01'	,''		,'TKI:000000000000000000000000000000000000', 'CREATE', 	CURRENT_TIMESTAMP					, 'USER_2_2', 'DOMAIN_B', 	'WBI:100000000000000000000000000000000001', '00'		, 'PASystem', '00'			, 'VNR'		, '11223344', 'L140101'				, 'TASK'						,''								, 'this task has been created'	,'old_val'	,'new_val'	,'custom1'	,'custom2'	, 'custom3'	,'custom4'	,'123'		,'456'),
('BPI:02'	,''		,'TKI:000000000000000000000000000000000000', 'CREATE', 	DATEADD('DAY', -2, CURRENT_TIMESTAMP),'USER_1_1', 'DOMAIN_A', 	'WBI:100000000000000000000000000000000001', '00'		, 'PASystem', '00'			, 'VNR'		, '11223344', ''					, ''							,''								, 'created by Peter'			,'2old_val'	,'new_val2'	,'custom1'	,''			, 'custom2'	,''			,'234'		,'456'),
('BPI:03'	,'BPI:01','TKI:000000000000000000000000000000000001', 'CREATE', CURRENT_TIMESTAMP					, 'USER_2_1', 'DOMAIN_A', 	'WBI:100000000000000000000000000000000002', '11'		, ''		, '22'			, ''		, ''		, 'L140101'				, 'TASK'						,'DOCTYPE_DEFAULT'				, 'created a bug'				,''			,''			,'custom1'	,''			, 'custom3'	,''			,'123'		,'456');
