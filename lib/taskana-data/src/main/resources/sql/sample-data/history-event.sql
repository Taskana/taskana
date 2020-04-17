INSERT INTO HISTORY_EVENTS (BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, TASK_ID, EVENT_TYPE, CREATED, USER_ID, DOMAIN, WORKBASKET_KEY, POR_COMPANY, POR_SYSTEM, POR_INSTANCE, POR_TYPE, POR_VALUE, TASK_CLASSIFICATION_KEY, TASK_CLASSIFICATION_CATEGORY, ATTACHMENT_CLASSIFICATION_KEY, OLD_VALUE, NEW_VALUE, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, DETAILS) VALUES
-- BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, TASK_ID,                                        EVENT_TYPE, TASK_CREATEDD,                                        USER_ID,    DOMAIN,         WORKBASKET_KEY,                                                     POR_COMPANY     , POR_SYSTEM, POR_INSTANCE      , POR_TYPE      , POR_VALUE     , TASK_CLASSIFICATION_KEY, TASK_CLASSIFICATION_CATEGORY , ATTACHMENT_CLASSIFICATION_KEY , OLD_VALUE     , NEW_VALUE     , CUSTOM_1      , CUSTOM_2      , CUSTOM_3      , CUSTOM_4
('BPI:01'               ,'',                        'TKI:000000000000000000000000000000000000',     'TASK_CREATED',   CURRENT_TIMESTAMP ,                      'USER_2_2', 'DOMAIN_B',         'WBI:100000000000000000000000000000000001', '00'                , 'PASystem', '00'                      , 'VNR'         , '11223344'                     ,' L140101'                         , 'TASK'                       ,''                    ,'old_val12'      ,'new_val12'      ,'custom1'      ,'custom2'      , 'custom3'     ,'custom4', 'some Details'),
('BPI:02'               ,'',                        'TKI:000000000000000000000000000000000000',     'TASK_UPDATED',   DATEADD('DAY', -2, CURRENT_TIMESTAMP ),  'USER_1_1', 'DOMAIN_A',         'WBI:100000000000000000000000000000000001', '00'                , 'PASystem', '00'                      , 'VNR'         , '65464564'                     ,  ''                               ,  ''                          ,''                    ,'2old_val'     ,'new_val2'     ,'custom1'      ,''             , 'custom3'     ,'','some Details'),
('BPI:03'               ,'BPI:01',                  'TKI:000000000000000000000000000000000001',     'TASK_DELETED', CURRENT_TIMESTAMP ,                        'USER_2_1', 'DOMAIN_B',         'WBI:100000000000000000000000000000000002', '11'                , ''        , '22'                      , ''            , ''                             , 'L140101'                         , 'TASK'                       ,'DOCTYPE_DEFAULT'     ,''             ,''             ,'custom1'      ,''             , 'custom3'     ,'','some Details'),
('BPI:04'               ,''             ,           'TKI:000000000000000000000000000000000000',     'TASK_UPDATED',   DATEADD('DAY', -1, CURRENT_TIMESTAMP ),  'USER_1_2', 'DOMAIN_B',         'WBI:100000000000000000000000000000000001', '00'                , 'PASystem', '00'                      , 'VNR'         , '11223344'                     ,  ''                               , ''                           ,''                    ,'2old_val'     ,'new_val2'     ,'custom1'      ,''             , 'custom3'     ,'custom4','some Details'),
('BPI:03'               ,'BPI:01',                  'TKI:000000000000000000000000000000000001',     'TASK_DELETED', CURRENT_TIMESTAMP ,                        'USER_2_1', 'DOMAIN_B',         'WBI:100000000000000000000000000000000002', '11'                , ''        , '22'                      , ''            , ''                             , 'L140101'                         , 'TASK'                       ,'DOCTYPE_DEFAULT'     ,''             ,''             ,'custom1'      ,''             , 'custom3'     ,'','some Details'),
('BPI:02'               ,''             ,           'TKI:000000000000000000000000000000000000',     'TASK_CREATED',   DATEADD('DAY', -1, CURRENT_TIMESTAMP ),  'USER_1_2', 'DOMAIN_B',         'WBI:100000000000000000000000000000000001', '00'                , 'PASystem', '00'                      , 'VNR'         , '11223344'                     ,  ''                               , ''                           ,''                    ,'2old_val'     ,'new_val2'     ,'custom1'      ,''             , 'custom3'     ,'custom4','some Details'),
('BPI:06'               ,'BPI:01',                  'TKI:000000000000000000000000000000000001',     'TASK_DELETED', CURRENT_TIMESTAMP ,                        'USER_2_1', 'DOMAIN_B',         'WBI:100000000000000000000000000000000002', '11'                , ''        , '22'                      , ''            , ''                             , 'L140101'                         , 'TASK'                       ,'DOCTYPE_DEFAULT'     ,''             ,''             ,'custom1'      ,''             , 'custom3'     ,'','some Details'),
('BPI:02'               ,''             ,           'TKI:000000000000000000000000000000000000',     'TASK_UPDATED',   DATEADD('DAY', -1, CURRENT_TIMESTAMP ),  'USER_1_2', 'DOMAIN_B',         'WBI:100000000000000000000000000000000001', '00'                , 'PASystem', '00'                      , 'VNR'         , '11223344'                     ,  ''                               , ''                           ,''                    ,'2old_val'     ,'new_val2'     ,'custom1'      ,''             , 'custom3'     ,'custom4','some Details'),
('BPI:04'               ,'BPI:01',                  'TKI:000000000000000000000000000000000001',     'TASK_CREATED', CURRENT_TIMESTAMP ,                        'USER_2_1', 'DOMAIN_B',         'WBI:100000000000000000000000000000000002', '11'                , ''        , '22'                      , ''            , ''                             , 'L140101'                         , 'TASK'                       ,'DOCTYPE_DEFAULT'     ,''             ,''             ,'custom1'      ,''             , 'custom3'     ,'','some Details'),
('BPI:02'               ,''             ,           'TKI:000000000000000000000000000000000000',     'TASK_UPDATED',   DATEADD('DAY', -1, CURRENT_TIMESTAMP ),  'USER_1_2', 'DOMAIN_B',         'WBI:100000000000000000000000000000000001', '00'                , 'PASystem', '00'                      , 'VNR'         , '11223344'                     ,  ''                               , ''                           ,''                    ,'2old_val'     ,'new_val2'     ,'custom1'      ,''             , 'custom3'     ,'custom4','some Details'),
('BPI:02'               ,'BPI:01',                  'TKI:000000000000000000000000000000000001',     'TASK_DELETED', CURRENT_TIMESTAMP ,                        'USER_2_1', 'DOMAIN_B',         'WBI:100000000000000000000000000000000002', '11'                , ''        , '22'                      , ''            , ''                             , 'L140101'                         , 'TASK'                       ,'DOCTYPE_DEFAULT'     ,''             ,''             ,'custom1'      ,''             , 'custom3'     ,'','some Details'),
('BPI:03'               ,''             ,           'TKI:000000000000000000000000000000000000',     'TASK_UPDATED',   DATEADD('DAY', -1, CURRENT_TIMESTAMP ),  'USER_1_2', 'DOMAIN_B',         'WBI:100000000000000000000000000000000001', '00'                , 'PASystem', '00'                      , 'VNR'         , '11223344'                     ,  ''                               , ''                           ,''                    ,'2old_val'     ,'new_val2'     ,'custom1'      ,''             , 'custom3'     ,'custom4','some Details'),
('BPI:02'               ,'BPI:01',                  'TKI:000000000000000000000000000000000001',     'TASK_DELETED', CURRENT_TIMESTAMP ,                        'USER_2_1', 'DOMAIN_B',         'WBI:100000000000000000000000000000000002', '11'                , ''        , '22'                      , ''            , ''                             , 'L140101'                         , 'TASK'                       ,'DOCTYPE_DEFAULT'     ,''             ,''             ,'custom1'      ,''             , 'custom3'     ,'','some Details'),
('BPI:03'               ,''             ,           'TKI:000000000000000000000000000000000000',     'TASK_UPDATED',   DATEADD('DAY', -1, CURRENT_TIMESTAMP ),  'USER_1_2', 'DOMAIN_B',         'WBI:100000000000000000000000000000000001', '00'                , 'PASystem', '00'                      , 'VNR'         , '11223344'                     ,  ''                               , ''                           ,''                    ,'2old_val'     ,'new_val2'     ,'custom1'      ,''             , 'custom3'     ,'custom4','some Details'),
('BPI:02'               ,'BPI:01',                  'TKI:000000000000000000000000000000000001',     'TASK_DELETED', CURRENT_TIMESTAMP ,                        'USER_2_1', 'DOMAIN_B',         'WBI:100000000000000000000000000000000002', '11'                , ''        , '22'                      , ''            , ''                             , 'L140101'                         , 'TASK'                       ,'DOCTYPE_DEFAULT'     ,''             ,''             ,'custom1'      ,''             , 'custom3'     ,'','some Details'),
('BPI:05'               ,''             ,           'TKI:000000000000000000000000000000000000',     'TASK_UPDATED',   DATEADD('DAY', -1, CURRENT_TIMESTAMP ),  'USER_1_2', 'DOMAIN_B',         'WBI:100000000000000000000000000000000001', '00'                , 'PASystem', '00'                      , 'VNR'         , '11223344'                     ,  ''                               , ''                           ,''                    ,'2old_val'     ,'new_val2'     ,'custom1'      ,''             , 'custom3'     ,'custom4','some Details'),
('BPI:02'               ,'BPI:01',                  'TKI:000000000000000000000000000000000001',     'TASK_CREATED', CURRENT_TIMESTAMP ,                        'USER_2_1', 'DOMAIN_B',         'WBI:100000000000000000000000000000000002', '11'                , ''        , '22'                      , ''            , ''                             , 'L140101'                         , 'TASK'                       ,'DOCTYPE_DEFAULT'     ,''             ,''             ,'custom1'      ,''             , 'custom3'     ,'','some Details'),
('BPI:04'               ,''             ,           'TKI:000000000000000000000000000000000000',     'TASK_UPDATED',   DATEADD('DAY', -1, CURRENT_TIMESTAMP ),  'USER_1_2', 'DOMAIN_B',         'WBI:100000000000000000000000000000000001', '00'                , 'PASystem', '00'                      , 'VNR'         , '11223344'                     ,  ''                               , ''                           ,''                    ,'2old_val'     ,'new_val2'     ,'custom1'      ,''             , 'custom3'     ,'custom4','some Details'),
('BPI:02'               ,'BPI:01',                  'TKI:000000000000000000000000000000000001',     'TASK_DELETED', CURRENT_TIMESTAMP ,                        'USER_2_1', 'DOMAIN_B',         'WBI:100000000000000000000000000000000002', '11'                , ''        , '22'                      , ''            , ''                             , 'L140101'                         , 'TASK'                       ,'DOCTYPE_DEFAULT'     ,''             ,''             ,'custom1'      ,''             , 'custom3'     ,'','some Details'),
('BPI:03'               ,''             ,           'TKI:000000000000000000000000000000000000',     'TASK_UPDATED',   DATEADD('DAY', -1, CURRENT_TIMESTAMP ),  'USER_1_2', 'DOMAIN_B',         'WBI:100000000000000000000000000000000001', '00'                , 'PASystem', '00'                      , 'VNR'         , '11223344'                     ,  ''                               , ''                           ,''                    ,'2old_val'     ,'new_val2'     ,'custom1'      ,''             , 'custom3'     ,'custom4','some Details'),
('BPI:02'               ,'BPI:01',                  'TKI:000000000000000000000000000000000001',     'TASK_DELETED', CURRENT_TIMESTAMP ,                        'USER_2_1', 'DOMAIN_B',         'WBI:100000000000000000000000000000000002', '11'                , ''        , '22'                      , ''            , ''                             , 'L140101'                         , 'TASK'                       ,'DOCTYPE_DEFAULT'     ,''             ,''             ,'custom1'      ,''             , 'custom3'     ,'','some Details'),
('BPI:05'               ,''             ,           'TKI:000000000000000000000000000000000000',     'TASK_UPDATED',   DATEADD('DAY', -1, CURRENT_TIMESTAMP ),  'USER_1_2', 'DOMAIN_B',         'WBI:100000000000000000000000000000000001', '00'                , 'PASystem', '00'                      , 'VNR'         , '11223344'                     ,  ''                               , ''                           ,''                    ,'2old_val'     ,'new_val2'     ,'custom1'      ,''             , 'custom3'     ,'custom4','some Details'),
('BPI:02'               ,'BPI:01',                  'TKI:000000000000000000000000000000000001',     'TASK_DELETED', CURRENT_TIMESTAMP ,                        'USER_2_1', 'DOMAIN_B',         'WBI:100000000000000000000000000000000002', '11'                , ''        , '22'                      , ''            , ''                             , 'L140101'                         , 'TASK'                       ,'DOCTYPE_DEFAULT'     ,''             ,''             ,'custom1'      ,''             , 'custom3'     ,'','some Details'),
('BPI:05'               ,''             ,           'TKI:000000000000000000000000000000000000',     'TASK_CREATED',   DATEADD('DAY', -1, CURRENT_TIMESTAMP ),  'USER_1_2', 'DOMAIN_B',         'WBI:100000000000000000000000000000000001', '00'                , 'PASystem', '00'                      , 'VNR'         , '11223344'                     ,  ''                               , ''                           ,''                    ,'2old_val'     ,'new_val2'     ,'custom1'      ,''             , 'custom3'     ,'custom4','some Details'),
('BPI:02'               ,'BPI:01',                  'TKI:000000000000000000000000000000000001',     'TASK_DELETED', CURRENT_TIMESTAMP ,                        'USER_2_1', 'DOMAIN_B',         'WBI:100000000000000000000000000000000002', '11'                , ''        , '22'                      , ''            , ''                             , 'L140101'                         , 'TASK'                       ,'DOCTYPE_DEFAULT'     ,''             ,''             ,'custom1'      ,''             , 'custom3'     ,'','some Details'),
('BPI:03'               ,''             ,           'TKI:000000000000000000000000000000000000',     'TASK_UPDATED',   DATEADD('DAY', -1, CURRENT_TIMESTAMP ),  'USER_1_2', 'DOMAIN_B',         'WBI:100000000000000000000000000000000001', '00'                , 'PASystem', '00'                      , 'VNR'         , '11223344'                     ,  ''                               , ''                           ,''                    ,'2old_val'     ,'new_val2'     ,'custom1'      ,''             , 'custom3'     ,'custom4','some Details'),
('BPI:02'               ,'BPI:01',                  'TKI:000000000000000000000000000000000001',     'TASK_CREATED', CURRENT_TIMESTAMP ,                        'USER_2_1', 'DOMAIN_B',         'WBI:100000000000000000000000000000000002', '11'                , ''        , '22'                      , ''            , ''                             , 'L140101'                         , 'TASK'                       ,'DOCTYPE_DEFAULT'     ,''             ,''             ,'custom1'      ,''             , 'custom3'     ,'','some Details'),
('BPI:04'               ,''             ,           'TKI:000000000000000000000000000000000000',     'TASK_UPDATED',   DATEADD('DAY', -1, CURRENT_TIMESTAMP ),  'USER_1_2', 'DOMAIN_B',         'WBI:100000000000000000000000000000000001', '00'                , 'PASystem', '00'                      , 'VNR'         , '11223344'                     ,  ''                               , ''                           ,''                    ,'2old_val'     ,'new_val2'     ,'custom1'      ,''             , 'custom3'     ,'custom4','some Details'),
('BPI:02'               ,'BPI:01',                  'TKI:000000000000000000000000000000000001',     'TASK_DELETED', CURRENT_TIMESTAMP ,                        'USER_2_1', 'DOMAIN_B',         'WBI:100000000000000000000000000000000002', '11'                , ''        , '22'                      , ''            , ''                             , 'L140101'                         , 'TASK'                       ,'DOCTYPE_DEFAULT'     ,''             ,''             ,'custom1'      ,''             , 'custom3'     ,'','some Details'),
('BPI:03'               ,''             ,           'TKI:000000000000000000000000000000000000',     'TASK_UPDATED',   DATEADD('DAY', -1, CURRENT_TIMESTAMP ),  'USER_1_2', 'DOMAIN_B',         'WBI:100000000000000000000000000000000001', '00'                , 'PASystem', '00'                      , 'VNR'         , '11223344'                     ,  ''                               , ''                           ,''                    ,'2old_val'     ,'new_val2'     ,'custom1'      ,''             , 'custom3'     ,'custom4','some Details'),
('BPI:02'               ,'BPI:01',                  'TKI:000000000000000000000000000000000001',     'TASK_DELETED', CURRENT_TIMESTAMP ,                        'USER_2_1', 'DOMAIN_B',         'WBI:100000000000000000000000000000000002', '11'                , ''        , '22'                      , ''            , ''                             , 'L140101'                         , 'TASK'                       ,'DOCTYPE_DEFAULT'     ,''             ,''             ,'custom1'      ,''             , 'custom3'     ,'','some Details'),
('BPI:05'               ,''             ,           'TKI:000000000000000000000000000000000000',     'TASK_CREATED',   DATEADD('DAY', -1, CURRENT_TIMESTAMP ),  'USER_1_2', 'DOMAIN_B',         'WBI:100000000000000000000000000000000001', '00'                , 'PASystem', '00'                      , 'VNR'         , '11223344'                     ,  ''                               , ''                           ,''                    ,'2old_val'     ,'new_val2'     ,'custom1'      ,''             , 'custom3'     ,'custom4','some Details'),
('BPI:02'               ,'BPI:01',                  'TKI:000000000000000000000000000000000001',     'TASK_DELETED', CURRENT_TIMESTAMP ,                        'USER_2_1', 'DOMAIN_B',         'WBI:100000000000000000000000000000000002', '11'                , ''        , '22'                      , ''            , ''                             , 'L140101'                         , 'TASK'                       ,'DOCTYPE_DEFAULT'     ,''             ,''             ,'custom1'      ,''             , 'custom3'     ,'','some Details'),
('BPI:06'               ,''             ,           'TKI:000000000000000000000000000000000000',     'TASK_UPDATED',   DATEADD('DAY', -1, CURRENT_TIMESTAMP ),  'USER_1_2', 'DOMAIN_B',         'WBI:100000000000000000000000000000000001', '00'                , 'PASystem', '00'                      , 'VNR'         , '11223344'                     ,  ''                               , ''                           ,''                    ,'2old_val'     ,'new_val2'     ,'custom1'      ,''             , 'custom3'     ,'custom4','some Details'),
('BPI:02'               ,'BPI:01',                  'TKI:000000000000000000000000000000000001',     'TASK_CREATED', CURRENT_TIMESTAMP ,                        'USER_2_1', 'DOMAIN_B',         'WBI:100000000000000000000000000000000002', '11'                , ''        , '22'                      , ''            , ''                             , 'L140101'                         , 'TASK'                       ,'DOCTYPE_DEFAULT'     ,''             ,''             ,'custom1'      ,''             , 'custom3'     ,'','some Details'),
('BPI:04'               ,''             ,           'TKI:000000000000000000000000000000000000',     'TASK_UPDATED',   DATEADD('DAY', -1, CURRENT_TIMESTAMP ),  'USER_1_2', 'DOMAIN_B',         'WBI:100000000000000000000000000000000001', '00'                , 'PASystem', '00'                      , 'VNR'         , '11223344'                     ,  ''                               , ''                           ,''                    ,'2old_val'     ,'new_val2'     ,'custom1'      ,''             , 'custom3'     ,'custom4','some Details'),
('BPI:03'               ,'BPI:01',                  'TKI:000000000000000000000000000000000001',     'TASK_DELETED', CURRENT_TIMESTAMP ,                        'USER_2_1', 'DOMAIN_B',         'WBI:100000000000000000000000000000000002', '11'                , ''        , '22'                      , ''            , ''                             , 'L140101'                         , 'TASK'                       ,'DOCTYPE_DEFAULT'     ,''             ,''             ,'custom1'      ,''             , 'custom3'     ,'','some Details'),
('BPI:02'               ,''             ,           'TKI:000000000000000000000000000000000000',     'TASK_UPDATED',   DATEADD('DAY', -1, CURRENT_TIMESTAMP ),  'USER_1_2', 'DOMAIN_B',         'WBI:100000000000000000000000000000000001', '00'                , 'PASystem', '00'                      , 'VNR'         , '11223344'                     ,  ''                               , ''                           ,''                    ,'2old_val'     ,'new_val2'     ,'custom1'      ,''             , 'custom3'     ,'custom4','some Details'),
('BPI:03'               ,'BPI:01',                  'TKI:000000000000000000000000000000000001',     'TASK_DELETED', CURRENT_TIMESTAMP ,                        'USER_2_1', 'DOMAIN_B',         'WBI:100000000000000000000000000000000002', '11'                , ''        , '22'                      , ''            , ''                             , 'L140101'                         , 'TASK'                       ,'DOCTYPE_DEFAULT'     ,''             ,''             ,'custom1'      ,''             , 'custom3'     ,'','some Details'),
('BPI:02'               ,''             ,           'TKI:000000000000000000000000000000000000',     'TASK_CREATED',   DATEADD('DAY', -1, CURRENT_TIMESTAMP ),  'USER_1_2', 'DOMAIN_B',         'WBI:100000000000000000000000000000000001', '00'                , 'PASystem', '00'                      , 'VNR'         , '11223344'                     ,  ''                               , ''                           ,''                    ,'2old_val'     ,'new_val2'     ,'custom1'      ,''             , 'custom3'     ,'custom4','some Details'),
('BPI:06'               ,'BPI:01',                  'TKI:000000000000000000000000000000000001',     'TASK_DELETED', CURRENT_TIMESTAMP ,                        'USER_2_1', 'DOMAIN_B',         'WBI:100000000000000000000000000000000002', '11'                , ''        , '22'                      , ''            , ''                             , 'L140101'                         , 'TASK'                       ,'DOCTYPE_DEFAULT'     ,''             ,''             ,'custom1'      ,''             , 'custom3'     ,'','some Details'),
('BPI:02'               ,''             ,           'TKI:000000000000000000000000000000000000',     'TASK_UPDATED',   DATEADD('DAY', -1, CURRENT_TIMESTAMP ),  'USER_1_2', 'DOMAIN_B',         'WBI:100000000000000000000000000000000001', '00'                , 'PASystem', '00'                      , 'VNR'         , '11223344'                     ,  ''                               , ''                           ,''                    ,'2old_val'     ,'new_val2'     ,'custom1'      ,''             , 'custom3'     ,'custom4','some Details'),
('BPI:03'               ,'BPI:01',                  'TKI:000000000000000000000000000000000001',     'TASK_DELETED', CURRENT_TIMESTAMP ,                        'USER_2_1', 'DOMAIN_B',         'WBI:100000000000000000000000000000000002', '11'                , ''        , '22'                      , ''            , ''                             , 'L140101'                         , 'TASK'                       ,'DOCTYPE_DEFAULT'     ,''             ,''             ,'custom1'      ,''             , 'custom3'     ,'','some Details'),
('BPI:04'               ,''             ,           'TKI:000000000000000000000000000000000000',     'TASK_CREATED',   DATEADD('DAY', -1, CURRENT_TIMESTAMP ),  'USER_1_2', 'DOMAIN_B',         'WBI:100000000000000000000000000000000001', '00'                , 'PASystem', '00'                      , 'VNR'         , '11223344'                     ,  ''                               , ''                           ,''                    ,'2old_val'     ,'new_val2'     ,'custom1'      ,''             , 'custom3'     ,'custom4','some Details'),
('BPI:03'               ,'BPI:01',                  'TKI:000000000000000000000000000000000001',     'TASK_DELETED', CURRENT_TIMESTAMP ,                        'USER_2_1', 'DOMAIN_B',         'WBI:100000000000000000000000000000000002', '11'                , ''        , '22'                      , ''            , ''                             , 'L140101'                         , 'TASK'                       ,'DOCTYPE_DEFAULT'     ,''             ,''             ,'custom1'      ,''             , 'custom3'     ,'','some Details');