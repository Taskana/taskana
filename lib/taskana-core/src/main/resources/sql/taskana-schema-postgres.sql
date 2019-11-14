CREATE SCHEMA IF NOT EXISTS %schemaName%;

SET search_path TO %schemaName%;

CREATE TABLE TASKANA_SCHEMA_VERSION(
        ID INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1),
        VERSION VARCHAR(255) NOT NULL,
        CREATED TIMESTAMP NOT NULL,
        PRIMARY KEY (ID)
);
-- The VERSION value must match the value of TaskanaEngineConfiguration.TASKANA_SCHEMA_VERSION
INSERT INTO TASKANA_SCHEMA_VERSION (VERSION, CREATED) VALUES ('1.0.8', CURRENT_TIMESTAMP);

CREATE TABLE CLASSIFICATION(
    ID CHAR(40) NOT NULL,
    KEY VARCHAR(32) NOT NULL,
    PARENT_ID VARCHAR(40) NOT NULL,
    PARENT_KEY VARCHAR(32) NOT NULL,
    CATEGORY VARCHAR(32),
    TYPE VARCHAR(32),
    DOMAIN VARCHAR(255) NOT NULL,
    VALID_IN_DOMAIN BOOLEAN NOT NULL,
    CREATED TIMESTAMP NULL,
    MODIFIED TIMESTAMP NULL,
    NAME VARCHAR(255) NULL,
    DESCRIPTION VARCHAR(255) NULL,
    PRIORITY INT NOT NULL,
    SERVICE_LEVEL VARCHAR(255) NULL,
    APPLICATION_ENTRY_POINT VARCHAR(255) NULL,
    CUSTOM_1 VARCHAR(255) NULL,
    CUSTOM_2 VARCHAR(255) NULL,
    CUSTOM_3 VARCHAR(255) NULL,
    CUSTOM_4 VARCHAR(255) NULL,
    CUSTOM_5 VARCHAR(255) NULL,
    CUSTOM_6 VARCHAR(255) NULL,
    CUSTOM_7 VARCHAR(255) NULL,
    CUSTOM_8 VARCHAR(255) NULL,
    PRIMARY KEY (ID),
    CONSTRAINT UC_CLASS_KEY_DOMAIN UNIQUE (KEY, DOMAIN)
);

CREATE TABLE WORKBASKET(
    ID CHAR(40) NOT NULL,
    KEY VARCHAR(64) NOT NULL,
    CREATED TIMESTAMP NULL,
    MODIFIED TIMESTAMP NULL,
    NAME VARCHAR(255) NOT NULL,
    DOMAIN VARCHAR(32) NOT NULL,
    TYPE VARCHAR(16) NOT NULL,
    DESCRIPTION VARCHAR(255) NULL,
    OWNER VARCHAR(128) NULL,
    CUSTOM_1 VARCHAR(255) NULL,
    CUSTOM_2 VARCHAR(255) NULL,
    CUSTOM_3 VARCHAR(255) NULL,
    CUSTOM_4 VARCHAR(255) NULL,
    ORG_LEVEL_1 VARCHAR(255) NULL,
    ORG_LEVEL_2 VARCHAR(255) NULL,
    ORG_LEVEL_3 VARCHAR(255) NULL,
    ORG_LEVEL_4 VARCHAR(255) NULL,
    MARKED_FOR_DELETION BOOLEAN NOT NULL,
    PRIMARY KEY (ID),
    CONSTRAINT WB_KEY_DOMAIN UNIQUE (KEY, DOMAIN)
);

CREATE TABLE TASK (
        ID CHAR(40) NOT NULL,
        EXTERNAL_ID VARCHAR(64) NOT NULL,
        CREATED TIMESTAMP NULL,
        CLAIMED TIMESTAMP NULL,
        COMPLETED TIMESTAMP NULL,
        MODIFIED TIMESTAMP NULL,
        PLANNED TIMESTAMP NULL,
        DUE TIMESTAMP NULL,
        NAME VARCHAR(255) NULL,
        CREATOR VARCHAR(32) NULL,
        DESCRIPTION VARCHAR(1024) NULL,
        NOTE VARCHAR(4096) NULL,
        PRIORITY INT NULL,
        STATE VARCHAR(20) NULL,
        CLASSIFICATION_CATEGORY VARCHAR(32) NULL,
        CLASSIFICATION_KEY VARCHAR(32) NULL,
        CLASSIFICATION_ID CHAR(40) NULL,
        WORKBASKET_ID CHAR(40) NULL,
        WORKBASKET_KEY VARCHAR(64) NULL,
        DOMAIN VARCHAR(32) NULL,
        BUSINESS_PROCESS_ID VARCHAR(128) NULL,
        PARENT_BUSINESS_PROCESS_ID VARCHAR(128) NULL,
        OWNER VARCHAR(32) NULL,
        POR_COMPANY VARCHAR(32) NOT NULL,
        POR_SYSTEM VARCHAR(32) NOT NULL,
        POR_INSTANCE VARCHAR(32) NOT NULL,
        POR_TYPE VARCHAR(32) NOT NULL,
        POR_VALUE VARCHAR(128) NOT NULL,
        IS_READ BOOLEAN NOT NULL,
        IS_TRANSFERRED BOOLEAN NOT NULL,
        CALLBACK_INFO TEXT NULL,
        CALLBACK_STATE VARCHAR(30) NULL,
        CUSTOM_ATTRIBUTES TEXT NULL,
        CUSTOM_1 VARCHAR(255) NULL,
        CUSTOM_2 VARCHAR(255) NULL,
        CUSTOM_3 VARCHAR(255) NULL,
        CUSTOM_4 VARCHAR(255) NULL,
        CUSTOM_5 VARCHAR(255) NULL,
        CUSTOM_6 VARCHAR(255) NULL,
        CUSTOM_7 VARCHAR(255) NULL,
        CUSTOM_8 VARCHAR(255) NULL,
        CUSTOM_9 VARCHAR(255) NULL,
        CUSTOM_10 VARCHAR(255) NULL,
        CUSTOM_11 VARCHAR(255) NULL,
        CUSTOM_12 VARCHAR(255) NULL,
        CUSTOM_13 VARCHAR(255) NULL,
        CUSTOM_14 VARCHAR(255) NULL,
        CUSTOM_15 VARCHAR(255) NULL,
        CUSTOM_16 VARCHAR(255) NULL,
        PRIMARY KEY (ID),
        CONSTRAINT UC_EXTERNAL_ID UNIQUE (EXTERNAL_ID),
        CONSTRAINT TASK_WB FOREIGN KEY  (WORKBASKET_ID) REFERENCES WORKBASKET ON DELETE NO ACTION,
        CONSTRAINT TASK_CLASS FOREIGN KEY (CLASSIFICATION_ID) REFERENCES CLASSIFICATION ON DELETE NO ACTION
);

CREATE TABLE DISTRIBUTION_TARGETS(
        SOURCE_ID CHAR(40) NOT NULL,
        TARGET_ID CHAR(40) NOT NULL,
        PRIMARY KEY (SOURCE_ID, TARGET_ID)
);

CREATE TABLE WORKBASKET_ACCESS_LIST(
        ID CHAR(40) NOT NULL,
        WORKBASKET_ID CHAR(40) NOT NULL,
        ACCESS_ID VARCHAR(255) NOT NULL,
        ACCESS_NAME VARCHAR(255) NULL,
        PERM_READ BOOLEAN NOT NULL,
        PERM_OPEN BOOLEAN NOT NULL,
        PERM_APPEND BOOLEAN NOT NULL,
        PERM_TRANSFER BOOLEAN NOT NULL,
        PERM_DISTRIBUTE BOOLEAN NOT NULL,
        PERM_CUSTOM_1 BOOLEAN NOT NULL,
        PERM_CUSTOM_2 BOOLEAN NOT NULL,
        PERM_CUSTOM_3 BOOLEAN NOT NULL,
        PERM_CUSTOM_4 BOOLEAN NOT NULL,
        PERM_CUSTOM_5 BOOLEAN NOT NULL,
        PERM_CUSTOM_6 BOOLEAN NOT NULL,
        PERM_CUSTOM_7 BOOLEAN NOT NULL,
        PERM_CUSTOM_8 BOOLEAN NOT NULL,
        PERM_CUSTOM_9 BOOLEAN NOT NULL,
        PERM_CUSTOM_10 BOOLEAN NOT NULL,
        PERM_CUSTOM_11 BOOLEAN NOT NULL,
        PERM_CUSTOM_12 BOOLEAN NOT NULL,
        PRIMARY KEY (ID),
        CONSTRAINT ACCESS_LIST_WB FOREIGN KEY  (WORKBASKET_ID) REFERENCES WORKBASKET ON DELETE CASCADE
);

CREATE TABLE OBJECT_REFERENCE(
        ID CHAR(40) NOT NULL,
        COMPANY VARCHAR(32) NOT NULL,
        SYSTEM VARCHAR(32) NOT NULL,
        SYSTEM_INSTANCE VARCHAR(32) NOT NULL,
        TYPE VARCHAR(32) NOT NULL,
        VALUE VARCHAR(128) NOT NULL
);

CREATE TABLE ATTACHMENT(
    ID CHAR(40) NOT NULL,
    TASK_ID CHAR(40) NOT NULL,
    CREATED TIMESTAMP NULL,
    MODIFIED TIMESTAMP NULL,
    CLASSIFICATION_KEY VARCHAR(32) NULL,
    CLASSIFICATION_ID CHAR(40) NULL,
    REF_COMPANY VARCHAR(32) NOT NULL,
    REF_SYSTEM VARCHAR(32) NOT NULL,
    REF_INSTANCE VARCHAR(32) NOT NULL,
    REF_TYPE VARCHAR(32) NOT NULL,
    REF_VALUE VARCHAR(128) NOT NULL,
    CHANNEL VARCHAR(64) NULL,
    RECEIVED TIMESTAMP NULL,
    CUSTOM_ATTRIBUTES TEXT NULL,
    PRIMARY KEY (ID),
    CONSTRAINT ATT_CLASS FOREIGN KEY (CLASSIFICATION_ID) REFERENCES CLASSIFICATION ON DELETE NO ACTION
);

CREATE TABLE SCHEDULED_JOB(
        JOB_ID          INTEGER NOT NULL,
        PRIORITY        INTEGER NULL,
        CREATED         TIMESTAMP NULL,
        DUE             TIMESTAMP NULL,
        STATE           VARCHAR(32) NULL,
        LOCKED_BY       VARCHAR(32) NULL,
        LOCK_EXPIRES    TIMESTAMP NULL,
        TYPE            VARCHAR(32) NULL,
        RETRY_COUNT     INTEGER NOT NULL,
        ARGUMENTS       TEXT NULL,
        PRIMARY KEY (JOB_ID)
);

CREATE SEQUENCE SCHEDULED_JOB_SEQ
  MINVALUE 1
  START WITH 1
  INCREMENT BY 1
  CACHE 10;

