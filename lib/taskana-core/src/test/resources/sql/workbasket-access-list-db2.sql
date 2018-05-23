-- KSC authorizations                             (ID                                        , WB_ID                                     ,    ACCESS_ID, READ, OPEN, APPEND, TRANSFER, DISTRIBUTE, C1, .., C12)
-- PPKs
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000001', 'WBI:100000000000000000000000000000000004', 'teamlead_1',   1 ,   1 ,   1 ,     1 ,       1 ,         1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 );
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000002', 'WBI:100000000000000000000000000000000005', 'teamlead_2',   1 ,   1 ,   1 ,     1 ,       1 ,         1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 );
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000003', 'WBI:100000000000000000000000000000000006',   'user_1_1',   1 ,   1 ,   1 ,     1 ,       1 ,         1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 );
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000004', 'WBI:100000000000000000000000000000000007',   'user_1_2',   1 ,   1 ,   1 ,     1 ,       1 ,         1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 );
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000005', 'WBI:100000000000000000000000000000000008',   'user_2_1',   1 ,   1 ,   1 ,     1 ,       1 ,         1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 );
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000006', 'WBI:100000000000000000000000000000000009',   'user_2_2',   1 ,   1 ,   1 ,     1 ,       1 ,         1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 );
-- group internal access
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000007', 'WBI:100000000000000000000000000000000004',    'group_1',   1 ,   1 ,   1 ,     1 ,       0  ,        0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  );
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000008', 'WBI:100000000000000000000000000000000005',    'group_2',   1 ,   1 ,   1 ,     1 ,       0  ,        0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  );
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000009', 'WBI:100000000000000000000000000000000006',    'group_1',   1 ,   1 ,   1 ,     1 ,       0  ,        0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  );
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000010', 'WBI:100000000000000000000000000000000007',    'group_1',   1 ,   1 ,   1 ,     1 ,       0  ,        0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  );
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000011', 'WBI:100000000000000000000000000000000008',    'group_2',   1 ,   1 ,   1 ,     1 ,       0  ,        0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  );
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000012', 'WBI:100000000000000000000000000000000009',    'group_2',   1 ,   1 ,   1 ,     1 ,       0  ,        0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  );
-- teamlead substitution
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000013', 'WBI:100000000000000000000000000000000004', 'teamlead_2',   1 ,   1 ,   1 ,     1 ,       0  ,        0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  );
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000014', 'WBI:100000000000000000000000000000000005', 'teamlead_1',   1 ,   1 ,   1 ,     0  ,      0  ,        0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  );
-- cross team tranfers
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000015', 'WBI:100000000000000000000000000000000006',    'group_2',   1 ,   0  ,   1 ,    0  ,      0  ,        0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  );
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000016', 'WBI:100000000000000000000000000000000007',    'group_2',   1 ,   0  ,   1 ,    0  ,      0  ,        0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  );
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000017', 'WBI:100000000000000000000000000000000008',    'group_1',   1 ,   0  ,   0  ,   0  ,      0  ,        0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  );
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000018', 'WBI:100000000000000000000000000000000009',    'group_1',   1 ,   0  ,   1 ,    0  ,      0  ,        0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  );
-- Team GPK access
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000019', 'WBI:100000000000000000000000000000000002',    'group_1',   1 ,   1 ,   1 ,     1 ,       1 ,         1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 );
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000020', 'WBI:100000000000000000000000000000000003',    'group_2',   1 ,   1 ,   1 ,     1 ,       1 ,         1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 );
-- Cross team GPK access
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000021', 'WBI:100000000000000000000000000000000001', 'teamlead_1',   1 ,   1 ,   1 ,     1 ,       1 ,         1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 );
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000022', 'WBI:100000000000000000000000000000000001', 'teamlead_2',   1 ,   1 ,   1 ,     1 ,       1 ,         1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,   1 );
-- TPK access
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000123', 'WBI:100000000000000000000000000000000010', 'teamlead_1',   1 ,   0  ,   0  ,     0  ,       0  ,        0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  );

-- Access to other domains
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000023', 'WBI:100000000000000000000000000000000012',    'group_1',   1 ,   0  ,   1 ,    1 ,       0  ,        0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  );
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000024', 'WBI:100000000000000000000000000000000013',    'group_2',   1 ,   0  ,   1 ,    1 ,       0  ,        0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  );
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000025', 'WBI:100000000000000000000000000000000014',    'group_2',   1 ,   1 ,   1 ,    1 ,       0  ,        0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  );
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000026', 'WBI:100000000000000000000000000000000015',    'group_2',   1 ,   1 ,   1 ,    1 ,       0  ,        0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  ,   0  );

-- Access to workbaskets for sorting test
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('900', '900', 'max',   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 );
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('901', '901', 'max',   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 );
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('902', '902', 'max',   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 );
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('903', '903', 'max',   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 );
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('904', '904', 'max',   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 );
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('905', '905', 'max',   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 );
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('906', '906', 'max',   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 );
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('907', '907', 'max',   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 );
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('908', '908', 'max',   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 );
INSERT INTO TASKANA.WORKBASKET_ACCESS_LIST VALUES ('909', '909', 'max',   1 ,   1 ,   1 ,   1 ,   1 ,   1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 ,    1 );
