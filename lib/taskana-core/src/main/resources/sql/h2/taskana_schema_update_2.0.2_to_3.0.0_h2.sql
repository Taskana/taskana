-- this script updates the tables TASKANA_SCHEMA_VERSION and HISTORY_EVENTS.

SET SCHEMA %schemaName%;

INSERT INTO TASKANA_SCHEMA_VERSION (VERSION, CREATED) VALUES ('3.0.0', CURRENT_TIMESTAMP);

ALTER TABLE HISTORY_EVENTS DROP COLUMN COMMENT, DROP COLUMN OLD_DATA, DROP COLUMN NEW_DATA, ADD COLUMN DETAILS CLOB;

ALTER TABLE CLASSIFICATION ALTER COLUMN ID VARCHAR(40);

ALTER TABLE WORKBASKET ALTER COLUMN ID VARCHAR(40);

ALTER TABLE TASK ALTER COLUMN ID VARCHAR(40);

ALTER TABLE TASK ALTER COLUMN WORKBASKET_ID VARCHAR(40);

ALTER TABLE TASK ALTER COLUMN CLASSIFICATION_ID VARCHAR(40);

ALTER TABLE DISTRIBUTION_TARGETS ALTER COLUMN SOURCE_ID VARCHAR(40);

ALTER TABLE DISTRIBUTION_TARGETS ALTER COLUMN TARGET_ID VARCHAR(40);

ALTER TABLE WORKBASKET_ACCESS_LIST ALTER COLUMN ID VARCHAR(40);

ALTER TABLE WORKBASKET_ACCESS_LIST ALTER COLUMN WORKBASKET_ID VARCHAR(40);

ALTER TABLE OBJECT_REFERENCE ALTER COLUMN ID VARCHAR(40);

ALTER TABLE ATTACHMENT ALTER COLUMN ID VARCHAR(40);

ALTER TABLE ATTACHMENT ALTER COLUMN TASK_ID VARCHAR(40);

ALTER TABLE ATTACHMENT ALTER COLUMN CLASSIFICATION_ID VARCHAR(40);

ALTER TABLE TASK_COMMENT ALTER COLUMN ID VARCHAR(40);

ALTER TABLE TASK_COMMENT ALTER COLUMN TASK_ID VARCHAR(40);

CREATE TABLE CONFIGURATION (
    ENFORCE_SECURITY BOOLEAN NOT NULL
);
