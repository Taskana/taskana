--                                                ID,                                         KEY,               PARENT_ID,                                  CATEGORY,     TYPE,       DOMAIN,     VALID_IN_DOMAIN,  CREATED,           MODIFIED,          NAME,                             DESCRIPTION,                                           PRIORITY, SERVICE_LEVEL, APPLICATION_ENTRY_POINT, CUSTOM_1 - 8
-- ROOT CLASSIFICATIONS
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:000000000000000000000000000000000001', 'L10000',          '',                                         'EXTERNAL',   'TASK',     '',           0  ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'OLD-Leistungsfall',               'OLD-Leistungsfall',                                   999,      'PT5H',        '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:000000000000000000000000000000000002', 'L10303',          '',                                         'EXTERNAL',   'TASK',     '',           0  ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Beratungsprotokoll',              'Beratungsprotokoll',                                  1,        'P2D',         '',                      'VNR,RVNR,KOLVNR, ANR', '', '', '', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:000000000000000000000000000000000003', 'L1050',           '',                                         'EXTERNAL',   'TASK',     '',           0  ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Widerruf',                        'Widerruf',                                            1,        'P3D',         '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:000000000000000000000000000000000004', 'L11010',          '',                                         'EXTERNAL',   'TASK',     '',           0  ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Dynamikänderung',                 'Dynamikänderung',                                     7,        'P4D',         '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:000000000000000000000000000000000005', 'L110102',         'CLI:000000000000000000000000000000000004', 'EXTERNAL',   'TASK',     '',           0  ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Dynamik-Ablehnung',               'Dynamik-Ablehnung',                                   5,        'P5D',         '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:000000000000000000000000000000000006', 'L110105',         'CLI:000000000000000000000000000000000004', 'EXTERNAL',   'TASK',     '',           0  ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Dynamik-Ausschluss',              'Dynamik-Ausschluss',                                  5,        'P5D',         '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:000000000000000000000000000000000007', 'L110107',         'CLI:000000000000000000000000000000000004', 'EXTERNAL',   'TASK',     '',           0  ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Dynamik-Einschluss/Änd.',         'Dynamik-Einschluss/Änd.',                             5,        'P6D',         '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:000000000000000000000000000000000008', 'L12010',          '',                                         'EXTERNAL',   'TASK',     '',           0  ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Gewährung-Policendarlehen',       'Gewährung-Policendarlehen',                           8,        'P7D',         '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:000000000000000000000000000000000009', 'L140101',         '',                                         'EXTERNAL',   'TASK',     '',           0  ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Zustimmungserklärung',            'Zustimmungserklärung',                                9,        'P8D',         '',                      'VNR', '', '', '', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:000000000000000000000000000000000010', 'T2100',           '',                                         'MANUAL',     'TASK',     '',           0  ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'T-Vertragstermin VERA',           'T-Vertragstermin VERA',                               2,        'P10D',        '',                      'VNR', '', '', '', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:000000000000000000000000000000000011', 'T6310',           '',                                         'AUTOMATIC',  'TASK',     '',           0  ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'T-GUK Honorarrechnung erstellen', 'Generali Unterstützungskasse Honorar wird fällig',    2,        'P11D',         '',                      'VNR', '', '', '', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:000000000000000000000000000000000013', 'DOCTYPE_DEFAULT', '',                                         'EXTERNAL',   'DOCUMENT', '',           0  ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'EP allgemein',                    'EP allgemein',                                        99,       'P2000D',      '',                      'VNR', '', '', '', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:000000000000000000000000000000000017', 'L1060',           '',                                         'EXTERNAL',   'TASK',     '',           0  ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Widerruf neu',                    'Widerruf neu',                                         1,        'P1D',         '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:300000000000000000000000000000000017', 'L3060',           '',                                         'EXTERNAL',   'TASK',     '',           0  ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Widerruf neu',                    'Widerruf neu',                                         1,        'P1D',         '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');

-- DOMAIN_A CLASSIFICATIONS
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:100000000000000000000000000000000002', 'L10303',          '',                                         'EXTERNAL',   'TASK',     'DOMAIN_A',   1 ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Beratungsprotokoll',              'Beratungsprotokoll',                                  101,      'PT7H',        '',                      'VNR,RVNR,KOLVNR, ANR', '', '', '', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:100000000000000000000000000000000003', 'L1050',           '',                                         'EXTERNAL',   'TASK',     'DOMAIN_A',   1 ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Widerruf',                        'Widerruf',                                            1,        'P13D',        '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:100000000000000000000000000000000004', 'L11010',          '',                                         'EXTERNAL',   'TASK',     'DOMAIN_A',   1 ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Dynamikänderung',                 'Dynamikänderung',                                     1,        'P14D',        '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:100000000000000000000000000000000005', 'L110102',         'CLI:100000000000000000000000000000000004', 'EXTERNAL',   'TASK',     'DOMAIN_A',   1 ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Dynamik-Ablehnung',               'Dynamik-Ablehnung',                                   5,        'P15D',        '',                      'VNR,RVNR,KOLVNR', 'TEXT_1', '', '', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:100000000000000000000000000000000006', 'L110105',         'CLI:100000000000000000000000000000000004', 'EXTERNAL',   'TASK',     'DOMAIN_A',   1 ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Dynamik-Ausschluss',              'Dynamik-Ausschluss',                                  5,        'P16D',        '',                      'VNR,RVNR,KOLVNR', 'TEXT_2', '', '', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:100000000000000000000000000000000007', 'L110107',         'CLI:100000000000000000000000000000000004', 'EXTERNAL',   'TASK',     'DOMAIN_A',   1 ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Dynamik-Einschluss/Änd.',         'Dynamik-Einschluss/Änd.',                             5,        'P5D',         '',                      'VNR,RVNR,KOLVNR', 'TEXT_1', '', '', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:100000000000000000000000000000000008', 'L12010',          '',                                         'EXTERNAL',   'TASK',     'DOMAIN_A',   1 ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Gewährung-Policendarlehen',       'Gewährung-Policendarlehen',                           1,        'P1D',         '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:100000000000000000000000000000000009', 'L140101',         '',                                         'EXTERNAL',   'TASK',     'DOMAIN_A',   1 ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Zustimmungserklärung',            'Zustimmungserklärung',                                2,        'P2D',         '',                      'VNR', '', '', '', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:100000000000000000000000000000000010', 'T2100',           '',                                         'MANUAL',     'TASK',     'DOMAIN_A',   1 ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'T-Vertragstermin VERA',           'T-Vertragstermin VERA',                               2,        'P2D',         '',                      'VNR', '', '', '', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:100000000000000000000000000000000011', 'T6310',           '',                                         'AUTOMATIC',  'TASK',     'DOMAIN_A',   1 ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'T-GUK Honorarrechnung erstellen', 'Generali Unterstützungskasse Honorar wird fällig',    2,        'P2D',         '',                      'VNR', '', '', '', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:100000000000000000000000000000000013', 'DOCTYPE_DEFAULT', '',                                         'EXTERNAL',   'DOCUMENT', 'DOMAIN_A',   1 ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'EP allgemein',                    'EP allgemein',                                        99,       'P2000D',      '',                      'VNR', '', '', '', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:100000000000000000000000000000000014', 'L10000',          '',                                         'EXTERNAL',   'TASK',     'DOMAIN_A',   1 ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'BUZ-Leistungsfall',               'BUZ-Leistungsfall',                                   1,        'P1D',         '',                      'VNR,RVNR,KOLVNR', 'VNR', 'VNR', 'VNR', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:100000000000000000000000000000000016', 'T2000',           '',                                         'MANUAL',     'TASK',     'DOMAIN_A',   1 ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'T-Vertragstermin',                'T-Vertragstermin',                                    1,        'P1D',         '',                      'VNR,KOLVNR,RVNR', 'CUSTOM_2', 'Custom_3', 'custom_4', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:100000000000000000000000000000000017', 'L1060',           '',                                         'EXTERNAL',   'TASK',     'DOMAIN_A',   1 ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Widerruf neu',                    'Widerruf neu',                                            1,        'P1D',         '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:400000000000000000000000000000000017', 'L3060',           '',                                         'EXTERNAL',   'TASK',     'DOMAIN_A',   0  ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Widerruf neu',                    'Widerruf neu',                                         1,        'P1D',         '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');

-- DOMAIN_B CLASSIFICATIONS
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:200000000000000000000000000000000015', 'T2100',           '',                                         'MANUAL',     'TASK',     'DOMAIN_B',   1 ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'T-Vertragstermin VERA',           'T-Vertragstermin VERA',                               22,       'P2D',         '',                      'VNR', '', '', '', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:200000000000000000000000000000000017', 'L1060',           '',                                         'EXTERNAL',   'TASK',     'DOMAIN_B',   1 ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Widerruf neu',                    'Widerruf neu',                                            1,        'P1D',         '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');

-- WITH PARENT CLASSIFICATIONS (MIXED DOMAIN) ---
-- DOMAIN_A
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:200000000000000000000000000000000001', 'A12',             'CLI:100000000000000000000000000000000014', 'EXTERNAL',   'TASK',     'DOMAIN_A',   1 ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'OLD-Leistungsfall',               'OLD-Leistungsfall',                                   1,        'P1D',         '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:200000000000000000000000000000000002', 'A13',             'CLI:100000000000000000000000000000000011', 'AUTOMATIC',  'TASK',     'DOMAIN_A',   1 ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Beratungsprotokoll',              'Beratungsprotokoll',                                  1,        'P1D',         '',                      'VNR,RVNR,KOLVNR, ANR', '', '', '', '', '', '', '');
-- DOMAIN_B
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:200000000000000000000000000000000003', 'A12',             'CLI:100000000000000000000000000000000015', 'MANUAL',     'TASK',     'DOMAIN_B',   1 ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Widerruf',                        'Widerruf',                                            1,        'P1D',         '',                      'VNR,RVNR,KOLVNR', '', '', '', '', '', '', '');
INSERT INTO TASKANA.CLASSIFICATION VALUES('CLI:200000000000000000000000000000000004', 'T21001',          'CLI:100000000000000000000000000000000015', 'MANUAL',     'TASK',     'DOMAIN_B',   1 ,            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Beratungsprotokoll',              'Beratungsprotokoll',                                  1,        'P1D',         '',                      'VNR,RVNR,KOLVNR, ANR', '', '', '', '', '', '', '');
