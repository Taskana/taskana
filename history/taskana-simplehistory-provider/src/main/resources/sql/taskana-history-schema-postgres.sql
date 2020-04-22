CREATE SCHEMA IF NOT EXISTS %schemaName%;

SET SCHEMA %schemaName%;

CREATE TABLE IF NOT EXISTS HISTORY_EVENTS
(
    ID                            INT          NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1),
    BUSINESS_PROCESS_ID           VARCHAR(128) NULL,
    PARENT_BUSINESS_PROCESS_ID    VARCHAR(128) NULL,
    TASK_ID                       VARCHAR(40)  NULL,
    EVENT_TYPE                    VARCHAR(32)  NULL,
    CREATED                       TIMESTAMP    NULL,
    USER_ID                       VARCHAR(32)  NULL,
    DOMAIN                        VARCHAR(32)  NULL,
    WORKBASKET_KEY                VARCHAR(64)  NULL,
    POR_COMPANY                   VARCHAR(32)  NULL,
    POR_SYSTEM                    VARCHAR(32)  NULL,
    POR_INSTANCE                  VARCHAR(32)  NULL,
    POR_TYPE                      VARCHAR(32)  NULL,
    POR_VALUE                     VARCHAR(128) NULL,
    TASK_CLASSIFICATION_KEY       VARCHAR(32)  NULL,
    TASK_CLASSIFICATION_CATEGORY  VARCHAR(32)  NULL,
    ATTACHMENT_CLASSIFICATION_KEY VARCHAR(32)  NULL,
    OLD_VALUE                     VARCHAR(255) NULL,
    NEW_VALUE                     VARCHAR(255) NULL,
    CUSTOM_1                      VARCHAR(128) NULL,
    CUSTOM_2                      VARCHAR(128) NULL,
    CUSTOM_3                      VARCHAR(128) NULL,
    CUSTOM_4                      VARCHAR(128) NULL,
    DETAILS                       TEXT         NULL,
    PRIMARY KEY (ID)
);
