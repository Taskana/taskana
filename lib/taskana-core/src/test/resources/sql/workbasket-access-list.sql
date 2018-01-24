INSERT INTO WORKBASKET_ACCESS_LIST VALUES ('1', 'key1', 'Elena', true, true, true, true, true, false, false, false, false, false, false, false, false);
INSERT INTO WORKBASKET_ACCESS_LIST VALUES ('2', 'key2', 'Max', true, true, true, true, true, true, true, true, true, false, false, false, false);
INSERT INTO WORKBASKET_ACCESS_LIST VALUES ('3', 'key3', 'Simone', true, true, true, true, true, true, true, true, true, true, true, true, true);
INSERT INTO WORKBASKET_ACCESS_LIST VALUES ('4', 'key4', 'user_1_1', true, true, true, true, true, true, true, true, true, true, true, true, true);

-- KSC authorizations (ID, WB_KEY, ACCESS_ID, READ, OPEN, APPEND, TRANSFER, DISTRIBUTE, C1, .., C8)
-- PPKs
INSERT INTO WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000001', 'TEAMLEAD_1', 'teamlead_1', true, true, true, true, true, true, true, true, true, true, true, true, true);
INSERT INTO WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000002', 'TEAMLEAD_2', 'teamlead_2', true, true, true, true, true, true, true, true, true, true, true, true, true);
INSERT INTO WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000003', 'USER_1_1', 'user_1_1', true, true, true, true, true, true, true, true, true, true, true, true, true);
INSERT INTO WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000004', 'USER_1_2', 'user_1_2', true, true, true, true, true, true, true, true, true, true, true, true, true);
INSERT INTO WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000005', 'USER_2_1', 'user_2_1', true, true, true, true, true, true, true, true, true, true, true, true, true);
INSERT INTO WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000006', 'USER_2_2', 'user_2_2', true, true, true, true, true, true, true, true, true, true, true, true, true);
-- group internal access
INSERT INTO WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000007', 'TEAMLEAD_1', 'group_1', true, true, true, true, false, false, false, false, false, false, false, false, false);
INSERT INTO WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000008', 'TEAMLEAD_2', 'group_2', true, true, true, true, false, false, false, false, false, false, false, false, false);
INSERT INTO WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000009', 'USER_1_1', 'group_1', true, true, true, true, false, false, false, false, false, false, false, false, false);
INSERT INTO WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000010', 'USER_1_2', 'group_1', true, true, true, true, false, false, false, false, false, false, false, false, false);
INSERT INTO WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000011', 'USER_2_1', 'group_2', true, true, true, true, false, false, false, false, false, false, false, false, false);
INSERT INTO WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000012', 'USER_2_2', 'group_2', true, true, true, true, false, false, false, false, false, false, false, false, false);
-- teamlead substitution
INSERT INTO WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000013', 'TEAMLEAD_1', 'teamlead_2', true, true, true, true, false, false, false, false, false, false, false, false, false);
INSERT INTO WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000014', 'TEAMLEAD_2', 'teamlead_1', true, true, true, true, false, false, false, false, false, false, false, false, false);
-- cross team tranfers
INSERT INTO WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000015', 'USER_1_1', 'group_2', true, false, true, false, false, false, false, false, false, false, false, false, false);
INSERT INTO WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000016', 'USER_1_2', 'group_2', true, false, true, false, false, false, false, false, false, false, false, false, false);
INSERT INTO WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000017', 'USER_2_1', 'group_1', true, false, true, false, false, false, false, false, false, false, false, false, false);
INSERT INTO WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000018', 'USER_2_2', 'group_1', true, false, true, false, false, false, false, false, false, false, false, false, false);
-- Team GPK access
INSERT INTO WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000019', 'GPK_KSC_1', 'group_1', true, true, true, true, true, true, true, true, true, true, true, true, true);
INSERT INTO WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000020', 'GPK_KSC_2', 'group_2', true, true, true, true, true, true, true, true, true, true, true, true, true);
-- Cross team GPK access
INSERT INTO WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000021', 'GPK_KSC', 'teamlead_1', true, true, true, true, true, true, true, true, true, true, true, true, true);
INSERT INTO WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000022', 'GPK_KSC', 'teamlead_2', true, true, true, true, true, true, true, true, true, true, true, true, true);

-- Access to other domains
INSERT INTO WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000023', 'GPK_B_KSC_1', 'group_1', true, false, true, true, false, false, false, false, false, false, false, false, false);
INSERT INTO WORKBASKET_ACCESS_LIST VALUES ('WAI:100000000000000000000000000000000024', 'GPK_B_KSC_2', 'group_2', true, false, true, true, false, false, false, false, false, false, false, false, false);
