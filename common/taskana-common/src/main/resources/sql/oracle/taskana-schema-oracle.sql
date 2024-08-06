ALTER SESSION SET CURRENT_SCHEMA = %schemaName%;

CREATE TABLE TASKANA_SCHEMA_VERSION (
    ID      NUMBER(10) NOT NULL,
    VERSION VARCHAR2(255) NOT NULL,
    CREATED TIMESTAMP NOT NULL,
    CONSTRAINT TASKANA_SCHEMA_VERSION_PKEY PRIMARY KEY (ID)
);

CREATE SEQUENCE TASKANA_SCHEMA_VERSION_ID_SEQ
    START WITH 100
    INCREMENT BY 1
    CACHE 10;

-- The VERSION value must be equal or higher then the value of TaskanaEngineImpl.MINIMAL_TASKANA_SCHEMA_VERSION
INSERT INTO TASKANA_SCHEMA_VERSION (ID, VERSION, CREATED)
VALUES (TASKANA_SCHEMA_VERSION_ID_SEQ.NEXTVAL, '7.1.0', CURRENT_TIMESTAMP);

CREATE TABLE CLASSIFICATION
(
    ID                      VARCHAR2(40) NOT NULL,
    KEY                     VARCHAR2(32) NOT NULL,
    PARENT_ID               VARCHAR2(40) NOT NULL,
    PARENT_KEY              VARCHAR2(32) NOT NULL,
    CATEGORY                VARCHAR2(32),
    TYPE                    VARCHAR2(32),
    DOMAIN                  VARCHAR2(32) NOT NULL,
    VALID_IN_DOMAIN         NUMBER(1) NOT NULL CHECK (VALID_IN_DOMAIN IN (0,1)),
    CREATED                 TIMESTAMP NULL,
    MODIFIED                TIMESTAMP NULL,
    NAME                    VARCHAR2(255) NULL,
    DESCRIPTION             VARCHAR2(255) NULL,
    PRIORITY                NUMBER(10) NOT NULL,
    SERVICE_LEVEL           VARCHAR2(255) NULL,
    APPLICATION_ENTRY_POINT VARCHAR2(255) NULL,
    CUSTOM_1                VARCHAR2(255) NULL,
    CUSTOM_2                VARCHAR2(255) NULL,
    CUSTOM_3                VARCHAR2(255) NULL,
    CUSTOM_4                VARCHAR2(255) NULL,
    CUSTOM_5                VARCHAR2(255) NULL,
    CUSTOM_6                VARCHAR2(255) NULL,
    CUSTOM_7                VARCHAR2(255) NULL,
    CUSTOM_8                VARCHAR2(255) NULL,
    CONSTRAINT CLASSIFICATION_PKEY PRIMARY KEY (ID),
    CONSTRAINT UC_CLASS_KEY_DOMAIN UNIQUE (KEY, DOMAIN)
);

CREATE TABLE WORKBASKET
(
    ID          VARCHAR2(40) NOT NULL,
    KEY         VARCHAR2(64) NOT NULL,
    CREATED     TIMESTAMP NULL,
    MODIFIED    TIMESTAMP NULL,
    NAME        VARCHAR2(255) NOT NULL,
    DOMAIN      VARCHAR2(32) NOT NULL,
    TYPE        VARCHAR2(16) NOT NULL,
    DESCRIPTION VARCHAR2(255) NULL,
    OWNER       VARCHAR2(128) NULL,
    CUSTOM_1    VARCHAR2(255) NULL,
    CUSTOM_2    VARCHAR2(255) NULL,
    CUSTOM_3    VARCHAR2(255) NULL,
    CUSTOM_4    VARCHAR2(255) NULL,
    ORG_LEVEL_1 VARCHAR2(255) NULL,
    ORG_LEVEL_2 VARCHAR2(255) NULL,
    ORG_LEVEL_3 VARCHAR2(255) NULL,
    ORG_LEVEL_4 VARCHAR2(255) NULL,
    MARKED_FOR_DELETION NUMBER(1) NOT NULL CHECK (MARKED_FOR_DELETION IN (0,1)),
    CUSTOM_5    VARCHAR2(255) NULL,
    CUSTOM_6    VARCHAR2(255) NULL,
    CUSTOM_7    VARCHAR2(255) NULL,
    CUSTOM_8    VARCHAR2(255) NULL,
    CONSTRAINT WORKBASKET_PKEY PRIMARY KEY (ID),
    CONSTRAINT WB_KEY_DOMAIN UNIQUE (KEY, DOMAIN)
);

CREATE TABLE TASK
(
    ID                          VARCHAR2(40) NOT NULL,
    EXTERNAL_ID                 VARCHAR2(64) NOT NULL,
    CREATED                     TIMESTAMP NULL,
    CLAIMED                     TIMESTAMP NULL,
    COMPLETED                   TIMESTAMP NULL,
    MODIFIED                    TIMESTAMP NULL,
    RECEIVED                    TIMESTAMP NULL,
    PLANNED                     TIMESTAMP NULL,
    DUE                         TIMESTAMP NULL,
    NAME                        VARCHAR2(255) NULL,
    CREATOR                     VARCHAR2(32) NULL,
    DESCRIPTION                 VARCHAR2(1024) NULL,
    NOTE                        VARCHAR2(4000) NULL,
    PRIORITY                    NUMBER(10) NULL,
    MANUAL_PRIORITY             NUMBER(10) NULL,
    STATE                       VARCHAR2(20) NULL,
    CLASSIFICATION_CATEGORY     VARCHAR2(32) NULL,
    CLASSIFICATION_KEY          VARCHAR2(32) NULL,
    CLASSIFICATION_ID           VARCHAR2(40) NULL,
    WORKBASKET_ID               VARCHAR2(40) NULL,
    WORKBASKET_KEY              VARCHAR2(64) NULL,
    DOMAIN                      VARCHAR2(32) NULL,
    BUSINESS_PROCESS_ID         VARCHAR2(128) NULL,
    PARENT_BUSINESS_PROCESS_ID  VARCHAR2(128) NULL,
    OWNER                       VARCHAR2(32) NULL,
    POR_COMPANY                 VARCHAR2(32) NOT NULL,
    POR_SYSTEM                  VARCHAR2(32),
    POR_INSTANCE                VARCHAR2(32),
    POR_TYPE                    VARCHAR2(32) NOT NULL,
    POR_VALUE                   VARCHAR2(128) NOT NULL,
    IS_READ                     NUMBER(1) NOT NULL CHECK (IS_READ IN (0,1)),
    IS_TRANSFERRED              NUMBER(1) NOT NULL CHECK (IS_TRANSFERRED IN (0,1)),
    CALLBACK_INFO               CLOB NULL,
    CALLBACK_STATE              VARCHAR2(30) NULL,
    CUSTOM_ATTRIBUTES           CLOB NULL,
    CUSTOM_1                    VARCHAR2(255) NULL,
    CUSTOM_2                    VARCHAR2(255) NULL,
    CUSTOM_3                    VARCHAR2(255) NULL,
    CUSTOM_4                    VARCHAR2(255) NULL,
    CUSTOM_5                    VARCHAR2(255) NULL,
    CUSTOM_6                    VARCHAR2(255) NULL,
    CUSTOM_7                    VARCHAR2(255) NULL,
    CUSTOM_8                    VARCHAR2(255) NULL,
    CUSTOM_9                    VARCHAR2(255) NULL,
    CUSTOM_10                   VARCHAR2(255) NULL,
    CUSTOM_11                   VARCHAR2(255) NULL,
    CUSTOM_12                   VARCHAR2(255) NULL,
    CUSTOM_13                   VARCHAR2(255) NULL,
    CUSTOM_14                   VARCHAR2(255) NULL,
    CUSTOM_15                   VARCHAR2(255) NULL,
    CUSTOM_16                   VARCHAR2(255) NULL,
    CUSTOM_INT_1                NUMBER(10) NULL,
    CUSTOM_INT_2                NUMBER(10) NULL,
    CUSTOM_INT_3                NUMBER(10) NULL,
    CUSTOM_INT_4                NUMBER(10) NULL,
    CUSTOM_INT_5                NUMBER(10) NULL,
    CUSTOM_INT_6                NUMBER(10) NULL,
    CUSTOM_INT_7                NUMBER(10) NULL,
    CUSTOM_INT_8                NUMBER(10) NULL,
    NUMBER_OF_COMMENTS          INT NULL,
    CONSTRAINT TASK_PKEY PRIMARY KEY (ID),
    CONSTRAINT UC_EXTERNAL_ID UNIQUE (EXTERNAL_ID),
    CONSTRAINT TASK_WB FOREIGN KEY (WORKBASKET_ID) REFERENCES WORKBASKET(ID),
    CONSTRAINT TASK_CLASS FOREIGN KEY (CLASSIFICATION_ID) REFERENCES CLASSIFICATION(ID)
);

CREATE TABLE DISTRIBUTION_TARGETS
(
    SOURCE_ID VARCHAR2(40) NOT NULL,
    TARGET_ID VARCHAR2(40) NOT NULL,
    CONSTRAINT DISTRIBUTION_TARGETS_PKEY PRIMARY KEY (SOURCE_ID, TARGET_ID)
);

CREATE TABLE WORKBASKET_ACCESS_LIST
(
    ID              VARCHAR2(40) NOT NULL,
    WORKBASKET_ID   VARCHAR2(40) NOT NULL,
    ACCESS_ID       VARCHAR2(255) NOT NULL,
    ACCESS_NAME     VARCHAR2(255) NULL,
    PERM_READ       NUMBER(1) NOT NULL CHECK (PERM_READ IN (0,1)),
    PERM_OPEN       NUMBER(1) NOT NULL CHECK (PERM_OPEN IN (0,1)),
    PERM_APPEND     NUMBER(1) NOT NULL CHECK (PERM_APPEND IN (0,1)),
    PERM_TRANSFER   NUMBER(1) NOT NULL CHECK (PERM_TRANSFER IN (0,1)),
    PERM_DISTRIBUTE NUMBER(1) NOT NULL CHECK (PERM_DISTRIBUTE IN (0,1)),
    PERM_CUSTOM_1   NUMBER(1) NOT NULL CHECK (PERM_CUSTOM_1 IN (0,1)),
    PERM_CUSTOM_2   NUMBER(1) NOT NULL CHECK (PERM_CUSTOM_2 IN (0,1)),
    PERM_CUSTOM_3   NUMBER(1) NOT NULL CHECK (PERM_CUSTOM_3 IN (0,1)),
    PERM_CUSTOM_4   NUMBER(1) NOT NULL CHECK (PERM_CUSTOM_4 IN (0,1)),
    PERM_CUSTOM_5   NUMBER(1) NOT NULL CHECK (PERM_CUSTOM_5 IN (0,1)),
    PERM_CUSTOM_6   NUMBER(1) NOT NULL CHECK (PERM_CUSTOM_6 IN (0,1)),
    PERM_CUSTOM_7   NUMBER(1) NOT NULL CHECK (PERM_CUSTOM_7 IN (0,1)),
    PERM_CUSTOM_8   NUMBER(1) NOT NULL CHECK (PERM_CUSTOM_8 IN (0,1)),
    PERM_CUSTOM_9   NUMBER(1) NOT NULL CHECK (PERM_CUSTOM_9 IN (0,1)),
    PERM_CUSTOM_10  NUMBER(1) NOT NULL CHECK (PERM_CUSTOM_10 IN (0,1)),
    PERM_CUSTOM_11  NUMBER(1) NOT NULL CHECK (PERM_CUSTOM_11 IN (0,1)),
    PERM_CUSTOM_12  NUMBER(1) NOT NULL CHECK (PERM_CUSTOM_12 IN (0,1)),
    PERM_READTASKS  NUMBER(1) NOT NULL CHECK (PERM_READTASKS IN (0,1)),
    PERM_EDITTASKS  NUMBER(1) NOT NULL CHECK (PERM_EDITTASKS IN (0,1)),
    CONSTRAINT WORKBASKET_ACCESS_LIST_PKEY PRIMARY KEY (ID),
    CONSTRAINT UC_ACCESSID_WBID UNIQUE (ACCESS_ID, WORKBASKET_ID),
    CONSTRAINT ACCESS_LIST_WB FOREIGN KEY (WORKBASKET_ID) REFERENCES WORKBASKET(ID) ON DELETE CASCADE
);

CREATE TABLE OBJECT_REFERENCE
(
    ID              VARCHAR2(40) NOT NULL,
    TASK_ID         VARCHAR2(40) NOT NULL,
    COMPANY         VARCHAR2(32) NOT NULL,
    SYSTEM          VARCHAR2(32),
    SYSTEM_INSTANCE VARCHAR2(32),
    TYPE            VARCHAR2(32) NOT NULL,
    VALUE           VARCHAR2(128) NOT NULL
);

CREATE TABLE ATTACHMENT
(
    ID                  VARCHAR2(40) NOT NULL,
    TASK_ID             VARCHAR2(40) NOT NULL,
    CREATED             TIMESTAMP NULL,
    MODIFIED            TIMESTAMP NULL,
    CLASSIFICATION_KEY  VARCHAR2(32) NULL,
    CLASSIFICATION_ID   VARCHAR2(40) NULL,
    REF_COMPANY         VARCHAR2(32) NOT NULL,
    REF_SYSTEM          VARCHAR2(32),
    REF_INSTANCE        VARCHAR2(32),
    REF_TYPE            VARCHAR2(32) NOT NULL,
    REF_VALUE           VARCHAR2(128) NOT NULL,
    CHANNEL             VARCHAR2(64) NULL,
    RECEIVED            TIMESTAMP NULL,
    CUSTOM_ATTRIBUTES   CLOB NULL,
    CONSTRAINT ATTACHMENT_PKEY PRIMARY KEY (ID),
    CONSTRAINT ATT_CLASS FOREIGN KEY (CLASSIFICATION_ID) REFERENCES CLASSIFICATION(ID)
);

CREATE TABLE CONFIGURATION
(
    NAME              VARCHAR2(8) NOT NULL,
    ENFORCE_SECURITY  NUMBER(1) NULL CHECK (ENFORCE_SECURITY IN (0,1)),
    CUSTOM_ATTRIBUTES CLOB NULL,
    CONSTRAINT CONFIGURATION_PKEY PRIMARY KEY (NAME)
);

INSERT INTO CONFIGURATION (NAME)
VALUES ('MASTER');

CREATE TABLE TASK_COMMENT
(
    ID          VARCHAR2(40) NOT NULL,
    TASK_ID     VARCHAR2(40) NOT NULL,
    TEXT_FIELD  VARCHAR2(1024) NULL,
    CREATOR     VARCHAR2(32) NULL,
    CREATED     TIMESTAMP NULL,
    MODIFIED    TIMESTAMP NULL,
    CONSTRAINT TASK_COMMENT_PKEY PRIMARY KEY (ID),
    CONSTRAINT COMMENT_TASK FOREIGN KEY (TASK_ID) REFERENCES TASK(ID) ON DELETE CASCADE
);

CREATE TABLE SCHEDULED_JOB
(
    JOB_ID          NUMBER(10) NOT NULL,
    PRIORITY        NUMBER(10) NULL,
    CREATED         TIMESTAMP NULL,
    DUE             TIMESTAMP NULL,
    STATE           VARCHAR2(32) NULL,
    LOCKED_BY       VARCHAR2(128) NULL,
    LOCK_EXPIRES    TIMESTAMP NULL,
    TYPE            VARCHAR2(255) NULL,
    RETRY_COUNT     NUMBER(10) NOT NULL,
    ARGUMENTS       CLOB NULL,
    CONSTRAINT SCHEDULED_JOB_PKEY PRIMARY KEY (JOB_ID)
);

CREATE TABLE TASK_HISTORY_EVENT
(
    ID                              VARCHAR2(40) NOT NULL,
    BUSINESS_PROCESS_ID             VARCHAR2(128) NULL,
    PARENT_BUSINESS_PROCESS_ID      VARCHAR2(128) NULL,
    TASK_ID                         VARCHAR2(40) NULL,
    EVENT_TYPE                      VARCHAR2(32) NULL,
    CREATED                         TIMESTAMP NULL,
    USER_ID                         VARCHAR2(32) NULL,
    DOMAIN                          VARCHAR2(32) NULL,
    WORKBASKET_KEY                  VARCHAR2(64) NULL,
    WORKBASKET_NAME                 VARCHAR2(255) NULL,
    POR_COMPANY                     VARCHAR2(32) NULL,
    POR_SYSTEM                      VARCHAR2(32) NULL,
    POR_INSTANCE                    VARCHAR2(32) NULL,
    POR_TYPE                        VARCHAR2(32) NULL,
    POR_VALUE                       VARCHAR2(128) NULL,
    TASK_PRIORITY                   NUMBER(10) NULL,
    TASK_PLANNED                    TIMESTAMP NULL,
    TASK_DUE                        TIMESTAMP NULL,
    TASK_OWNER                      VARCHAR2(32) NULL,
    TASK_CLASSIFICATION_KEY         VARCHAR2(32) NULL,
    TASK_CLASSIFICATION_NAME        VARCHAR2(32) NULL,
    TASK_CLASSIFICATION_CATEGORY    VARCHAR2(32) NULL,
    ATTACHMENT_CLASSIFICATION_KEY   VARCHAR2(32) NULL,
    ATTACHMENT_CLASSIFICATION_NAME  VARCHAR2(255) NULL,
    OLD_VALUE                       VARCHAR2(255) NULL,
    NEW_VALUE                       VARCHAR2(255) NULL,
    CUSTOM_1                        VARCHAR2(128) NULL,
    CUSTOM_2                        VARCHAR2(128) NULL,
    CUSTOM_3                        VARCHAR2(128) NULL,
    CUSTOM_4                        VARCHAR2(128) NULL,
    DETAILS                         CLOB NULL,
    CONSTRAINT TASK_HISTORY_EVENT_PKEY PRIMARY KEY (ID)
);

CREATE TABLE WORKBASKET_HISTORY_EVENT
(
    ID              VARCHAR2(40) NOT NULL,
    EVENT_TYPE      VARCHAR2(40) NULL,
    CREATED         TIMESTAMP NULL,
    USER_ID         VARCHAR2(32) NULL,
    DOMAIN          VARCHAR2(32) NULL,
    WORKBASKET_ID   VARCHAR2(40) NULL,
    KEY             VARCHAR2(64) NULL,
    TYPE            VARCHAR2(64) NULL,
    OWNER           VARCHAR2(128) NULL,
    CUSTOM_1        VARCHAR2(255) NULL,
    CUSTOM_2        VARCHAR2(255) NULL,
    CUSTOM_3        VARCHAR2(255) NULL,
    CUSTOM_4        VARCHAR2(255) NULL,
    ORGLEVEL_1      VARCHAR2(255) NULL,
    ORGLEVEL_2      VARCHAR2(255) NULL,
    ORGLEVEL_3      VARCHAR2(255) NULL,
    ORGLEVEL_4      VARCHAR2(255) NULL,
    DETAILS         CLOB NULL,
    CONSTRAINT WORKBASKET_HISTORY_EVENT_PKEY PRIMARY KEY (ID)
);

CREATE TABLE CLASSIFICATION_HISTORY_EVENT
(
    ID                      VARCHAR2(40) NOT NULL,
    EVENT_TYPE              VARCHAR2(40) NULL,
    CREATED                 TIMESTAMP NULL,
    USER_ID                 VARCHAR2(32) NULL,
    CLASSIFICATION_ID       VARCHAR2(40) NULL,
    APPLICATION_ENTRY_POINT VARCHAR2(255) NULL,
    CATEGORY                VARCHAR2(64) NULL,
    DOMAIN                  VARCHAR2(32) NULL,
    KEY                     VARCHAR2(40) NULL,
    NAME                    VARCHAR2(255) NULL,
    PARENT_ID               VARCHAR2(40) NOT NULL,
    PARENT_KEY              VARCHAR2(32) NOT NULL,
    PRIORITY                NUMBER(38) NOT NULL,
    SERVICE_LEVEL           VARCHAR2(255) NULL,
    TYPE                    VARCHAR2(32),
    CUSTOM_1                VARCHAR2(255) NULL,
    CUSTOM_2                VARCHAR2(255) NULL,
    CUSTOM_3                VARCHAR2(255) NULL,
    CUSTOM_4                VARCHAR2(255) NULL,
    CUSTOM_5                VARCHAR2(255) NULL,
    CUSTOM_6                VARCHAR2(255) NULL,
    CUSTOM_7                VARCHAR2(255) NULL,
    CUSTOM_8                VARCHAR2(255) NULL,
    DETAILS                 CLOB NULL,
    CONSTRAINT CLASSIFICATION_HISTORY_EVENT_PKEY PRIMARY KEY (ID)
);

-- USER can not be taken as table name because it is a reserved keyword.
CREATE TABLE USER_INFO
(
    USER_ID      VARCHAR2(32) NOT NULL,
    FIRST_NAME   VARCHAR2(32) NULL,
    LASTNAME     VARCHAR2(32) NULL,
    FULL_NAME    VARCHAR2(64) NULL,
    LONG_NAME    VARCHAR2(64) NULL,
    E_MAIL       VARCHAR2(64) NULL,
    PHONE        VARCHAR2(32) NULL,
    MOBILE_PHONE VARCHAR2(32) NULL,
    ORG_LEVEL_4  VARCHAR2(32) NULL,
    ORG_LEVEL_3  VARCHAR2(32) NULL,
    ORG_LEVEL_2  VARCHAR2(32) NULL,
    ORG_LEVEL_1  VARCHAR2(32) NULL,
    DATA         CLOB NULL,
    CONSTRAINT USER_INFO_PKEY PRIMARY KEY (USER_ID)
);

CREATE TABLE GROUP_INFO
(
    USER_ID     VARCHAR2(32) NOT NULL,
    GROUP_ID    VARCHAR2(256) NOT NULL,
    CONSTRAINT GROUP_INFO_PKEY PRIMARY KEY (USER_ID, GROUP_ID)
);

CREATE TABLE PERMISSION_INFO
(
    USER_ID     VARCHAR2(32) NOT NULL,
    PERMISSION_ID    VARCHAR2(256) NOT NULL,
    CONSTRAINT PERMISSION_INFO_PKEY PRIMARY KEY (USER_ID, PERMISSION_ID)
);

CREATE SEQUENCE SCHEDULED_JOB_SEQ
    START WITH 1
    INCREMENT BY 1
    CACHE 10;

-- LIST OF RECOMMENDED INDEXES
-- This list is provided on a as-is basis. It is used for tuning of the internal performance tests.
-- The script needs to be reviewed and adapted for each indiviual TASKANA setup.
-- ===========================
CREATE UNIQUE INDEX IDX_CLASSIFICATION_ID ON CLASSIFICATION
   (ID ASC, CUSTOM_8, CUSTOM_7, CUSTOM_6, CUSTOM_5, CUSTOM_4,
   CUSTOM_3, CUSTOM_2, CUSTOM_1, APPLICATION_ENTRY_POINT,
   SERVICE_LEVEL, PRIORITY, DESCRIPTION, NAME, MODIFIED,
   CREATED, VALID_IN_DOMAIN, DOMAIN, TYPE, CATEGORY, PARENT_KEY,
   PARENT_ID, KEY);
COMMIT WORK;

 CREATE INDEX IDX_CLASSIFICATION_CATEGORY ON CLASSIFICATION
   (CATEGORY ASC, DOMAIN ASC, TYPE ASC, CUSTOM_1
   ASC, CUSTOM_8 ASC, CUSTOM_7 ASC, CUSTOM_6 ASC,
   CUSTOM_5 ASC, CUSTOM_4 ASC, CUSTOM_3 ASC, CUSTOM_2
   ASC, APPLICATION_ENTRY_POINT ASC, SERVICE_LEVEL
   ASC, PRIORITY ASC, DESCRIPTION ASC, NAME ASC,
   CREATED ASC, VALID_IN_DOMAIN ASC, PARENT_KEY ASC, PARENT_ID
   ASC, KEY ASC, ID ASC);
COMMIT WORK;

CREATE UNIQUE INDEX IDX_CLASSIFICATION_KEY_DOMAIN ON CLASSIFICATION
  (KEY ASC, DOMAIN ASC, CUSTOM_8, CUSTOM_7, CUSTOM_6,
   CUSTOM_5, CUSTOM_4, CUSTOM_3, CUSTOM_2, CUSTOM_1,
   APPLICATION_ENTRY_POINT, SERVICE_LEVEL, PRIORITY,
   DESCRIPTION, NAME, CREATED, VALID_IN_DOMAIN,
   TYPE, CATEGORY, PARENT_KEY, PARENT_ID, ID);
COMMIT WORK;

CREATE INDEX IDX_TASK_WORKBASKET_KEY_DOMAIN ON TASK
   (WORKBASKET_KEY ASC, DOMAIN DESC);
COMMIT WORK;

 CREATE INDEX IDX_TASK_POR_VALUE ON TASK
   (POR_VALUE ASC, WORKBASKET_ID ASC);
COMMIT WORK;

 CREATE INDEX IDX_TASK_LOWER_POR_VALUE ON TASK
   (LOWER(POR_VALUE) ASC, WORKBASKET_ID ASC);
COMMIT WORK;

CREATE INDEX IDX_ATTACHMENT_TASK_ID ON ATTACHMENT
   (TASK_ID ASC, RECEIVED ASC, CLASSIFICATION_ID
   ASC, CLASSIFICATION_KEY ASC, MODIFIED ASC, CREATED ASC, ID ASC);
COMMIT WORK;

CREATE INDEX IDX_WORKBASKET_ID ON WORKBASKET
   (ID ASC, ORG_LEVEL_4, ORG_LEVEL_3, ORG_LEVEL_2,
   ORG_LEVEL_1, OWNER, DESCRIPTION, TYPE, DOMAIN, NAME, KEY);
COMMIT WORK;

CREATE INDEX IDX_WORKBASKET_KEY_DOMAIN ON WORKBASKET
   (KEY ASC, DOMAIN ASC, ORG_LEVEL_4,
   ORG_LEVEL_3, ORG_LEVEL_2, ORG_LEVEL_1, CUSTOM_4,
   CUSTOM_3, CUSTOM_2, CUSTOM_1, OWNER, DESCRIPTION,
   TYPE, NAME, MODIFIED, CREATED, ID);
COMMIT WORK;

CREATE INDEX IDX_WORKBASKET_KEY_DOMAIN_ID ON WORKBASKET
   (KEY ASC, DOMAIN ASC, ID);
COMMIT WORK;

CREATE INDEX IDX_WB_ACCESS_LIST_ACCESS_ID ON WORKBASKET_ACCESS_LIST
   (ACCESS_ID ASC, WORKBASKET_ID ASC, PERM_READ ASC);
COMMIT WORK;

 CREATE INDEX IDX_WB_ACCESS_LIST_WB_ID ON WORKBASKET_ACCESS_LIST
   (WORKBASKET_ID ASC, PERM_CUSTOM_12 ASC, PERM_CUSTOM_11
   ASC, PERM_CUSTOM_10 ASC, PERM_CUSTOM_9 ASC, PERM_CUSTOM_8
   ASC, PERM_CUSTOM_7 ASC, PERM_CUSTOM_6 ASC, PERM_CUSTOM_5
   ASC, PERM_CUSTOM_4 ASC, PERM_CUSTOM_3 ASC, PERM_CUSTOM_2
   ASC, PERM_CUSTOM_1 ASC, PERM_DISTRIBUTE ASC, PERM_TRANSFER
   ASC, PERM_APPEND ASC, PERM_OPEN ASC, PERM_READ
   ASC, ACCESS_ID ASC);
COMMIT WORK;

CREATE INDEX IDX_OBJECT_REFERE_PK_ID ON OBJECT_REFERENCE
    (ID ASC);
COMMIT WORK ;
CREATE INDEX IDX_OBJECT_REFERE_FK_TASK_ID ON OBJECT_REFERENCE
    (TASK_ID ASC);
COMMIT WORK ;
CREATE INDEX IDX_OBJECT_REFERE_ACCESS_LIST ON OBJECT_REFERENCE
    (VALUE ASC, TYPE ASC, SYSTEM_INSTANCE ASC, SYSTEM ASC, COMPANY ASC, ID ASC);
COMMIT WORK ;
CREATE INDEX IDX_TASK_ID_HISTORY_EVENT ON TASK_HISTORY_EVENT
    (TASK_ID ASC);
COMMIT WORK ;

