-- this script updates the TASKANA database schema from version 4.8.0 to version 4.11.0.

SET search_path = %schemaName%;

INSERT INTO TASKANA_SCHEMA_VERSION (VERSION, CREATED) VALUES ('4.11.0', CURRENT_TIMESTAMP);

ALTER TABLE CONFIGURATION ADD COLUMN NAME VARCHAR(8) NOT NULL DEFAULT 'MASTER';
ALTER TABLE CONFIGURATION ALTER COLUMN ENFORCE_SECURITY DROP NOT NULL;
ALTER TABLE CONFIGURATION ALTER COLUMN NAME DROP DEFAULT;

UPDATE TASK SET CUSTOM_1 = '' WHERE CUSTOM_1 IS NULL;
UPDATE TASK SET CUSTOM_2 = '' WHERE CUSTOM_2 IS NULL;
UPDATE TASK SET CUSTOM_3 = '' WHERE CUSTOM_3 IS NULL;
UPDATE TASK SET CUSTOM_4 = '' WHERE CUSTOM_4 IS NULL;
UPDATE TASK SET CUSTOM_5 = '' WHERE CUSTOM_5 IS NULL;
UPDATE TASK SET CUSTOM_6 = '' WHERE CUSTOM_6 IS NULL;
UPDATE TASK SET CUSTOM_7 = '' WHERE CUSTOM_7 IS NULL;
UPDATE TASK SET CUSTOM_8 = '' WHERE CUSTOM_8 IS NULL;
UPDATE TASK SET CUSTOM_9 = '' WHERE CUSTOM_9 IS NULL;
UPDATE TASK SET CUSTOM_10 = '' WHERE CUSTOM_10 IS NULL;
UPDATE TASK SET CUSTOM_11 = '' WHERE CUSTOM_11 IS NULL;
UPDATE TASK SET CUSTOM_12 = '' WHERE CUSTOM_12 IS NULL;
UPDATE TASK SET CUSTOM_13 = '' WHERE CUSTOM_13 IS NULL;
UPDATE TASK SET CUSTOM_14 = '' WHERE CUSTOM_14 IS NULL;
UPDATE TASK SET CUSTOM_15 = '' WHERE CUSTOM_15 IS NULL;
UPDATE TASK SET CUSTOM_16 = '' WHERE CUSTOM_16 IS NULL;

UPDATE WORKBASKET SET CUSTOM_1 = '' WHERE CUSTOM_1 IS NULL;
UPDATE WORKBASKET SET CUSTOM_2 = '' WHERE CUSTOM_2 IS NULL;
UPDATE WORKBASKET SET CUSTOM_3 = '' WHERE CUSTOM_3 IS NULL;
UPDATE WORKBASKET SET CUSTOM_4 = '' WHERE CUSTOM_4 IS NULL;

UPDATE CLASSIFICATION SET CUSTOM_1 = '' WHERE CUSTOM_1 IS NULL;
UPDATE CLASSIFICATION SET CUSTOM_2 = '' WHERE CUSTOM_2 IS NULL;
UPDATE CLASSIFICATION SET CUSTOM_3 = '' WHERE CUSTOM_3 IS NULL;
UPDATE CLASSIFICATION SET CUSTOM_4 = '' WHERE CUSTOM_4 IS NULL;
UPDATE CLASSIFICATION SET CUSTOM_5 = '' WHERE CUSTOM_5 IS NULL;
UPDATE CLASSIFICATION SET CUSTOM_6 = '' WHERE CUSTOM_6 IS NULL;
UPDATE CLASSIFICATION SET CUSTOM_7 = '' WHERE CUSTOM_7 IS NULL;
UPDATE CLASSIFICATION SET CUSTOM_8 = '' WHERE CUSTOM_8 IS NULL;
