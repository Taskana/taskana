-- this script updates the TASKANA database schema from version 4.11.0 to version 5.0.0.

INSERT INTO TASKANA_SCHEMA_VERSION (VERSION, CREATED) VALUES ('5.0.0', CURRENT_TIMESTAMP);


