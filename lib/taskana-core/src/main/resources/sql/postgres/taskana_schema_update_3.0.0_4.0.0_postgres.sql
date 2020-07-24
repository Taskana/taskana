-- this script updates the tables TASKANA_SCHEMA_VERSION and HISTORY_EVENTS.

SET search_path %schemaName%;

INSERT INTO TASKANA_SCHEMA_VERSION (VERSION, CREATED) VALUES ('4.0.0', CURRENT_TIMESTAMP);

ALTER TABLE HISTORY_EVENTS ALTER COLUMN ID DROP IDENTITY;

ALTER TABLE HISTORY_EVENTS ALTER COLUMN ID TYPE VARCHAR(40);
