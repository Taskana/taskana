-- this script updates the TASKANA database schema from version 5.1.0 to version 5.2.0.

SET search_path = %schemaName%;

INSERT INTO TASKANA_SCHEMA_VERSION (VERSION, CREATED) VALUES ('5.2.0', CURRENT_TIMESTAMP);

CREATE TABLE GROUP_INFO
(
    USER_ID     VARCHAR(32) NOT NULL,
    GROUP_ID    VARCHAR(256) NOT NULL,
    PRIMARY KEY (USER_ID, GROUP_ID)
);
