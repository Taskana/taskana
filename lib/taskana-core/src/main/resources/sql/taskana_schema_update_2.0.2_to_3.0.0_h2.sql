-- this script updates the tables TASKANA_SCHEMA_VERSION and HISTORY_EVENTS.

SET SCHEMA %schemaName%;

INSERT INTO TASKANA_SCHEMA_VERSION (VERSION, CREATED) VALUES ('3.0.0', CURRENT_TIMESTAMP);

ALTER TABLE HISTORY_EVENTS DROP COLUMN COMMENT, DROP COLUMN OLD_DATA, DROP COLUMN NEW_DATA, ADD COLUMN DETAILS CLOB;

