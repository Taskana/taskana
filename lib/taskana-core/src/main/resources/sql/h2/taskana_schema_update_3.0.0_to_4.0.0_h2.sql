-- this script updates the tables TASKANA_SCHEMA_VERSION and HISTORY_EVENTS.

SET SCHEMA %schemaName%;

INSERT INTO TASKANA_SCHEMA_VERSION (VERSION, CREATED) VALUES ('4.0.0', CURRENT_TIMESTAMP);

ALTER TABLE HISTORY_EVENTS ALTER COLUMN ID VARCHAR(40);

ALTER TABLE HISTORY_EVENTS RENAME TO TASK_HISTORY_EVENT;

UPDATE TASK_HISTORY_EVENT SET ID = REPLACE(ID,'HEI','THI') WHERE ID LIKE '%HEI%';

CREATE TABLE WORKBASKET_HISTORY_EVENT
(
    ID                            VARCHAR(40) NOT NULL,
    EVENT_TYPE                    VARCHAR(40)  NULL,
    CREATED                       TIMESTAMP    NULL,
    USER_ID                       VARCHAR(32)  NULL,
    DOMAIN                        VARCHAR(32)  NULL,
    WORKBASKET_ID                 VARCHAR(40)  NULL,
    KEY                           VARCHAR(64)  NULL,
    TYPE                          VARCHAR(64)  NULL,
    OWNER                         VARCHAR(128) NULL,
    CUSTOM_1                      VARCHAR(255) NULL,
    CUSTOM_2                      VARCHAR(255) NULL,
    CUSTOM_3                      VARCHAR(255) NULL,
    CUSTOM_4                      VARCHAR(255) NULL,
    ORGLEVEL_1                    VARCHAR(255) NULL,
    ORGLEVEL_2                    VARCHAR(255) NULL,
    ORGLEVEL_3                    VARCHAR(255) NULL,
    ORGLEVEL_4                    VARCHAR(255) NULL,
    DETAILS                       CLOB         NULL,
    PRIMARY KEY (ID)
);

CREATE TABLE CLASSIFICATION_HISTORY_EVENT
(
    ID                            VARCHAR(40) NOT NULL,
    EVENT_TYPE                    VARCHAR(40)  NULL,
    CREATED                       TIMESTAMP    NULL,
    USER_ID                       VARCHAR(32)  NULL,
    CLASSIFICATION_ID             VARCHAR(40)  NULL,
    APPLICATION_ENTRY_POINT       VARCHAR(255)  NULL,
    CATEGORY                      VARCHAR(64)  NULL,
    DOMAIN                        VARCHAR(32)  NULL,
    KEY                           VARCHAR(40)  NULL,
    NAME                          VARCHAR(255) NULL,
    PARENT_ID                     VARCHAR(40) NOT NULL,
    PARENT_KEY                    VARCHAR(32) NOT NULL,
    PRIORITY                      INT NOT NULL,
    SERVICE_LEVEL                 VARCHAR(255) NULL,
    TYPE                          VARCHAR(32),
    CUSTOM_1                      VARCHAR(255) NULL,
    CUSTOM_2                      VARCHAR(255) NULL,
    CUSTOM_3                      VARCHAR(255) NULL,
    CUSTOM_4                      VARCHAR(255) NULL,
    CUSTOM_5                      VARCHAR(255) NULL,
    CUSTOM_6                      VARCHAR(255) NULL,
    CUSTOM_7                      VARCHAR(255) NULL,
    CUSTOM_8                      VARCHAR(255) NULL,
    DETAILS                       CLOB         NULL,
    PRIMARY KEY (ID)
);


