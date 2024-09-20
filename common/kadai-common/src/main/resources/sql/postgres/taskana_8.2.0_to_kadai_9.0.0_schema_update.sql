-- this script updates the database schema from TASKANA version 8.2.0 to KADAI version 9.0.0.

SET search_path = %schemaName%;

ALTER TABLE taskana_schema_version
  RENAME TO kadai_schema_version;

ALTER TABLE kadai_schema_version
  RENAME constraint taskana_schema_version_pkey TO kadai_schema_version_pkey;

ALTER SEQUENCE taskana_schema_version_id_seq RENAME TO kadai_schema_version_id_seq;

ALTER SEQUENCE kadai_schema_version_id_seq RESTART WITH 1000;

INSERT INTO KADAI_SCHEMA_VERSION (ID, VERSION, CREATED)
VALUES (nextval('KADAI_SCHEMA_VERSION_ID_SEQ'), '9.0.0', CURRENT_TIMESTAMP);
