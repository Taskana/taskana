-- this script updates the KADAI database schema from version 5.0.0 to version 5.1.0.

INSERT INTO KADAI_SCHEMA_VERSION (VERSION, CREATED) VALUES ('5.1.0', CURRENT_TIMESTAMP);
