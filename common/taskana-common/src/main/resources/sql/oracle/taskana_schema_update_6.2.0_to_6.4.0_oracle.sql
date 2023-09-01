-- this script updates the TASKANA database schema from version 6.2.0 to version 6.4.0.
ALTER SESSION SET CURRENT_SCHEMA = %schemaName%;

INSERT INTO TASKANA_SCHEMA_VERSION (ID, VERSION, CREATED)
VALUES (nextval('TASKANA_SCHEMA_VERSION_ID_SEQ'), '6.4.0', CURRENT_TIMESTAMP);

CREATE TABLE PERMISSION_INFO
(
    USER_ID     VARCHAR2(32) NOT NULL,
    PERMISSION_ID    VARCHAR2(256) NOT NULL,
    CONSTRAINT PERMISSION_INFO_PKEY PRIMARY KEY (USER_ID, PERMISSION_ID)
);
