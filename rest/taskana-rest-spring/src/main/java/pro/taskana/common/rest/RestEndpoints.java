package pro.taskana.common.rest;

/** Collection of Url to Controller mappings. */
public final class RestEndpoints {

  public static final String API_V1 = "/api/v1/";

  // configuration endpoints
  public static final String URL_VERSION = API_V1 + "version";
  public static final String URL_DOMAIN = API_V1 + "domains";
  public static final String URL_CURRENT_USER = API_V1 + "current-user-info";
  public static final String URL_CLASSIFICATION_CATEGORIES = API_V1 + "classification-categories";
  public static final String URL_CLASSIFICATION_TYPES = API_V1 + "classification-types";
  public static final String URL_CLASSIFICATION_CATEGORIES_BY_TYPES =
      API_V1 + "classifications-by-type";
  public static final String URL_HISTORY_ENABLED = API_V1 + "history-provider-enabled";

  // access id endpoints
  public static final String URL_ACCESS_ID = API_V1 + "access-ids";
  public static final String URL_ACCESS_ID_GROUPS = API_V1 + "access-ids/groups";

  // import / export endpoints
  public static final String URL_CLASSIFICATION_DEFINITIONS = API_V1 + "classification-definitions";
  public static final String URL_WORKBASKET_DEFINITIONS = API_V1 + "workbasket-definitions";

  // classification endpoints
  public static final String URL_CLASSIFICATIONS = API_V1 + "classifications";
  public static final String URL_CLASSIFICATIONS_ID = API_V1 + "classifications/{classificationId}";

  // workbasket endpoints
  public static final String URL_WORKBASKET = API_V1 + "workbaskets";
  public static final String URL_WORKBASKET_ID = API_V1 + "workbaskets/{workbasketId}";
  public static final String URL_WORKBASKET_ID_ACCESS_ITEMS =
      API_V1 + "workbaskets/{workbasketId}/workbasketAccessItems";
  public static final String URL_WORKBASKET_ID_DISTRIBUTION =
      API_V1 + "workbaskets/{workbasketId}/distribution-targets";

  // access item endpoints
  public static final String URL_WORKBASKET_ACCESS_ITEMS = API_V1 + "workbasket-access-items";

  // task endpoints
  public static final String URL_TASKS = API_V1 + "tasks";
  public static final String URL_TASKS_ID = API_V1 + "tasks/{taskId}";
  public static final String URL_TASKS_ID_CLAIM = API_V1 + "tasks/{taskId}/claim";
  public static final String URL_TASKS_ID_SELECT_AND_CLAIM = API_V1 + "tasks/select-and-claim";
  public static final String URL_TASKS_ID_COMPLETE = API_V1 + "tasks/{taskId}/complete";
  public static final String URL_TASKS_ID_TRANSFER_WORKBASKET_ID =
      API_V1 + "tasks/{taskId}/transfer/{workbasketId}";

  // task comment endpoints
  public static final String URL_TASK_COMMENTS = API_V1 + "tasks/{taskId}/comments";
  public static final String URL_TASK_COMMENT = API_V1 + "tasks/comments/{taskCommentId}";

  // monitor endpoints
  public static final String URL_MONITOR_TASKS_STATUS_REPORT =
      API_V1 + "monitor/tasks-status-report";
  public static final String URL_MONITOR_TASKS_WORKBASKET_REPORT =
      API_V1 + "monitor/tasks-workbasket-report";
  public static final String URL_MONITOR_TASKS_WORKBASKET_PLANNED_REPORT =
      API_V1 + "monitor/tasks-workbasket-planned-date-report";
  public static final String URL_MONITOR_TASKS_CLASSIFICATION_REPORT =
      API_V1 + "monitor/tasks-classification-report";
  public static final String URL_MONITOR_TIMESTAMP_REPORT = API_V1 + "monitor/timestamp-report";

  private RestEndpoints() {}
}
