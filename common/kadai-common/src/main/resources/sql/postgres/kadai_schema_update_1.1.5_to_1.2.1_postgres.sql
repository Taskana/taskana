-- this script adds a unique constraint to WORKBASKET_ACCESS_LIST
-- allowing a maximum of one WORKBASKET_ACCESS_LIST per workbasket and access_id
-- Please replace %schemaName% before executing the script

SET search_path = %schemaName%;

INSERT INTO KADAI_SCHEMA_VERSION (VERSION, CREATED) VALUES ('1.2.1', CURRENT_TIMESTAMP);

-- If the database contains records that violate this constraint, the following statement will fail.
-- In this case it is required to remove the conflicting records before the constraint can be added

ALTER TABLE WORKBASKET_ACCESS_LIST ADD CONSTRAINT UC_ACCESSID_WBID UNIQUE (ACCESS_ID, WORKBASKET_ID);
