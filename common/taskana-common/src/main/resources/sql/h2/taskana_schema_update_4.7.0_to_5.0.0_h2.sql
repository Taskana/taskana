-- this script updates the tables TASKANA_SCHEMA_VERSION and TASK.
INSERT INTO TASKANA_SCHEMA_VERSION (VERSION, CREATED) VALUES ('5.0.0', CURRENT_TIMESTAMP);

ALTER TABLE TASK ADD COLUMN RECEIVED TIMESTAMP NULL;
