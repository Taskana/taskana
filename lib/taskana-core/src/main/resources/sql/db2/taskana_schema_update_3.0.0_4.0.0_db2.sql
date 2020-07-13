-- this script updates the tables TASKANA_SCHEMA_VERSION and HISTORY_EVENTS.

SET SCHEMA %schemaName%;

INSERT INTO TASKANA_SCHEMA_VERSION (VERSION, CREATED) VALUES ('4.0.0', CURRENT_TIMESTAMP);

ALTER TABLE HISTORY_EVENTS ALTER COLUMN ID SET DATA TYPE VARCHAR(40);
REORG TABLE HISTORY_EVENTS;