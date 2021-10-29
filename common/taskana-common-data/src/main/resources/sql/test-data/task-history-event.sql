INSERT INTO TASK_HISTORY_EVENT (ID,BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, TASK_ID, EVENT_TYPE, CREATED, USER_ID, DOMAIN, WORKBASKET_KEY, POR_COMPANY, POR_SYSTEM, POR_INSTANCE, POR_TYPE, POR_VALUE, TASK_OWNER, TASK_CLASSIFICATION_KEY,
                                TASK_CLASSIFICATION_CATEGORY, ATTACHMENT_CLASSIFICATION_KEY, OLD_VALUE, NEW_VALUE, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, DETAILS) VALUES
-- BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, TASK_ID, 	EVENT_TYPE, CREATED, 							 USER_ID, 	DOMAIN, 	WORKBASKET_KEY, 							POR_COMPANY	, POR_SYSTEM, POR_INSTANCE	, POR_TYPE	, POR_VALUE	, TASK_CLASSIFICATION_KEY, TASK_CLASSIFICATION_CATEGORY	, ATTACHMENT_CLASSIFICATION_KEY	, OLD_VALUE	, NEW_VALUE	, CUSTOM_1	, CUSTOM_2	, CUSTOM_3	, CUSTOM_4,          details
('THI:000000000000000000000000000000000000','BPI:01'	,''		,'TKI:000000000000000000000000000000000000', 'UPDATED', 	'2018-01-29 15:55:00'					, 'user-1-1', 'DOMAIN_B', 	'WBI:100000000000000000000000000000000001', '00'		, 'PASystem', '00'			, 'VNR'		, '11223344', 'user-1-1', 'L140101'				, 'TASK'						,''								,'old_val'	,'new_val'	,'custom1'	,'custom2'	, 'custom3'	,'custom4',  '{"changes":[{"newValue":"BPI:01","fieldName":"businessProcessId","oldValue":"BPI:02"},{"newValue":"user-1-1","fieldName":"owner","oldValue":"owner1"}]}'	),
('THI:000000000000000000000000000000000001','BPI:02'	,''		,'TKI:000000000000000000000000000000000000', 'CREATED', 	'2018-01-29 15:55:01','peter', 'DOMAIN_A', 	'WBI:100000000000000000000000000000000001', '00'		, 'PASystem', '00'			, 'VNR'		, '11223344', 'user-1-1', ''					, ''							,''								,'2old_val'	,'new_val2'	,'custom1'	,''			, 'custom2'	,''           ,'someDetails'			),
('THI:000000000000000000000000000000000002','BPI:03'	,'BPI:01','TKI:000000000000000000000000000000000036','CREATED',    '2018-01-29 15:55:02'					, 'admin', 'DOMAIN_A', 	'WBI:100000000000000000000000000000000002', '11'		, ''		, '22'			, ''		, ''		, 'user-1-2', 'L140101'				, 'TASK'						,'DOCTYPE_DEFAULT'				,''			,''			,'custom1'	,''			, 'custom3'	,''	          ,'someDetails'		),
('THI:000000000000000000000000000000000003','BPI:02'	,''		,'TKI:000000000000000000000000000000000036', 'CREATED', 	'2018-01-29 15:55:03','peter', 'DOMAIN_A', 	'WBI:100000000000000000000000000000000001', '00'		, 'PASystem', '00'			, 'VNR'		, '11223344', 'user-1-2', ''					, ''							,''								,'2old_val'	,'new_val2'	,'custom1'	,''			, 'custom2'	,''           ,'someDetails'			),
('THI:000000000000000000000000000000000004','BPI:03'	,'BPI:01','TKI:000000000000000000000000000000000037','CREATED',    '2018-01-29 15:55:04'					, 'admin', 'DOMAIN_A', 	'WBI:100000000000000000000000000000000002', '11'		, ''		, '22'			, ''		, ''		, 'user-1-2', 'L140101'				, 'TASK'						,'DOCTYPE_DEFAULT'				,''			,''			,'custom1'	,''			, 'custom3'	,''	          ,'someDetails'		),
('THI:000000000000000000000000000000000005','BPI:02'	,''		,'TKI:000000000000000000000000000000000038', 'CREATED', 	'2018-01-29 15:55:05','peter', 'DOMAIN_A', 	'WBI:100000000000000000000000000000000001', '00'		, 'PASystem', '00'			, 'VNR'		, '11223344', 'user-1-2', ''					, ''							,''								,'2old_val'	,'new_val2'	,'custom1'	,''			, 'custom2'	,''           ,'someDetails'			),
('THI:000000000000000000000000000000000006','BPI:03'	,'BPI:01','TKI:000000000000000000000000000000000038','CREATED',    '2018-01-29 15:55:06'					, 'admin', 'DOMAIN_A', 	'WBI:100000000000000000000000000000000002', '11'		, ''		, '22'			, ''		, ''		, 'user-1-2', 'L140101'				, 'TASK'						,'DOCTYPE_DEFAULT'				,''			,''			,'custom1'	,''			, 'custom3'	,''	          ,'someDetails'		),
('THI:000000000000000000000000000000000007','BPI:02'	,''		,'TKI:000000000000000000000000000000000039', 'CREATED', 	'2018-01-29 15:55:07','peter', 'DOMAIN_A', 	'WBI:100000000000000000000000000000000001', '00'		, 'PASystem', '00'			, 'VNR'		, '11223344', 'user-1-2', ''					, ''							,''								,'2old_val'	,'new_val2'	,'custom1'	,''			, 'custom2'	,''           ,'someDetails'			),
('THI:000000000000000000000000000000000008','BPI:03'	,'BPI:01','TKI:000000000000000000000000000000000039','CREATED',    '2018-01-29 15:55:08'					, 'admin', 'DOMAIN_A', 	'WBI:100000000000000000000000000000000002', '11'		, ''		, '22'			, ''		, ''		, 'user-1-2', 'L140101'				, 'TASK'						,'DOCTYPE_DEFAULT'				,''			,''			,'custom1'	,''			, 'custom3'	,''	          ,'someDetails'		),
('THI:000000000000000000000000000000000009','BPI:02'	,''		,'TKI:000000000000000000000000000000000040', 'CREATED', 	'2018-01-29 15:55:09','peter', 'DOMAIN_A', 	'WBI:100000000000000000000000000000000001', '00'		, 'PASystem', '00'			, 'VNR'		, '11223344', 'user-1-2', ''					, ''							,''								,'2old_val'	,'new_val2'	,'custom1'	,''			, 'custom2'	,''           ,'someDetails'			),
('THI:000000000000000000000000000000000010','BPI:03'	,'BPI:01','TKI:000000000000000000000000000000000040','CREATED',    '2018-01-29 15:55:10'					, 'admin', 'DOMAIN_A', 	'WBI:100000000000000000000000000000000002', '11'		, ''		, '22'			, ''		, ''		, 'user-1-2', 'L140101'				, 'TASK'						,'DOCTYPE_DEFAULT'				,''			,''			,'custom1'	,''			, 'custom3'	,''	          ,'someDetails'		),
('THI:000000000000000000000000000000000011','BPI:02'	,''		,'TKI:000000000000000000000000000000000066', 'CREATED', 	'2018-01-29 15:55:11','peter', 'DOMAIN_A', 	'WBI:100000000000000000000000000000000001', '00'		, 'PASystem', '00'			, 'VNR'		, '11223344', 'user-1-1', ''					, ''							,''								,'2old_val'	,'new_val2'	,'custom1'	,''			, 'custom2'	,''           ,'someDetails'			),
('THI:000000000000000000000000000000000012','BPI:03'	,'BPI:01','TKI:000000000000000000000000000000000066','CREATED',    '2018-01-29 15:55:12'					, 'admin', 'DOMAIN_A', 	'WBI:100000000000000000000000000000000002', '11'		, ''		, '22'			, ''		, ''		, 'user-1-1', 'L140101'				, 'TASK'						,'DOCTYPE_DEFAULT'				,''			,''			,'custom1'	,''			, 'custom3'	,''	          ,'someDetails'		),
('THI:000000000000000000000000000000000013','BPI:03'	,'BPI:01','TKI:000000000000000000000000000000000000','CREATED',    '2018-01-29 15:55:12'					, 'user-1-2', 'DOMAIN_A', 	'WBI:100000000000000000000000000000000002', '11'		, ''		, '22'			, ''		, ''		, 'user-1-1', 'L140101'				, 'TASK'						,'DOCTYPE_DEFAULT'				,''			,''			,'custom1'	,''			, 'custom3'	,''	          ,'someDetails'		)
;
