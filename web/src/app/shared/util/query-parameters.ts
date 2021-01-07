export class TaskanaQueryParameters {
  static parameters = {
    // Sorting
    SORTBY: 'sort-by',
    SORTDIRECTION: 'order',
    // Filtering
    NAME: 'name',
    NAMELIKE: 'name-like',
    DESCLIKE: 'description-like',
    OWNER: 'owner',
    OWNERLIKE: 'owner-like',
    TYPE: 'type',
    KEY: 'key',
    CREATED: 'created',
    WORKBASKET_KEY: 'workbasket-key',
    KEYLIKE: 'key-like',
    PRIORITY: 'priority',
    STATE: 'state',
    WORKBASKET_ID: 'workbasket-id',
    TASK_PRIMARY_OBJ_REF_TYPE_LIKE: 'por.type',
    TASK_PRIMARY_OBJ_REF_VALUE_LIKE: 'por.value',
    // Access
    REQUIREDPERMISSION: 'required-permission',
    ACCESSIDS: 'access-ids',
    ACCESSIDLIKE: 'access-id-like',
    WORKBASKETKEYLIKE: 'workbasket-key-like',
    // Pagination
    PAGE: 'page',
    PAGESIZE: 'page-size',
    // Domain
    DOMAIN: 'domain',

    // Task history events
    TASK_ID_LIKE: 'task-id-like',
    PARENT_BUSINESS_PROCESS_ID_LIKE: 'parent-business-process-id-like',
    BUSINESS_PROCESS_ID_LIKE: 'business-process-id-like',
    EVENT_TYPE_LIKE: 'event-type-like',
    CREATED_LIKE: 'created-like',
    USER_ID_LIKE: 'user-id-like',
    POR_COMPANY_LIKE: 'por-company-like',
    POR_SYSTEM_LIKE: 'por-system-like',
    POR_INSTANCE_LIKE: 'por-instance-like',
    POR_TYPE_LIKE: 'por-type-like',
    POR_VALUE_LIKE: 'por-value-like',
    TASK_CLASSIFICATION_KEY_LIKE: 'task-classification-key-like',
    TASK_CLASSIFICATION_CATEGORY_LIKE: 'task-classification-category-like',
    ATTACHMENT_CLASSIFICATION_KEY_LIKE: 'attachment-classification-key-like',
    CUSTOM_1_LIKE: 'custom-1-like',
    CUSTOM_2_LIKE: 'custom-2-like',
    CUSTOM_3_LIKE: 'custom-3-like',
    CUSTOM_4_LIKE: 'custom-4-like',
    COMMENT_LIKE: 'comment-like'
  };

  static page = 1;
  static pageSize = 9;
}
