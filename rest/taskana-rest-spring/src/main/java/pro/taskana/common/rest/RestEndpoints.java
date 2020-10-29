package pro.taskana.common.rest;

/** Collection of Url to Controller mappings. */
public final class RestEndpoints {

  public static final String API_V1 = "/api/v1/";

  public static final String URL_VERSION = API_V1 + "version";
  public static final String URL_DOMAIN = API_V1 + "domains";
  public static final String URL_CURRENT_USER = API_V1 + "current-user-info";
  public static final String URL_ACCESS_ID = API_V1 + "access-ids";
  public static final String URL_ACCESS_ID_GROUPS = URL_ACCESS_ID + "/groups";

  public static final String URL_CLASSIFICATIONS = API_V1 + "classifications";
  public static final String URL_CLASSIFICATIONS_ID = URL_CLASSIFICATIONS + "/{classificationId}";
  public static final String URL_CLASSIFICATION_DEFINITIONS = API_V1 + "classification-definitions";
  public static final String URL_CLASSIFICATION_CATEGORIES = API_V1 + "classification-categories";
  public static final String URL_CLASSIFICATION_TYPES = API_V1 + "classification-types";
  public static final String URL_CLASSIFICATION_CATEGORIES_BY_TYPES =
          API_V1 + "classifications-by-type";

  public static final String URL_WORKBASKET_ACCESS_ITEMS = API_V1 + "workbasket-access-items";
  public static final String URL_WORKBASKET = API_V1 + "workbaskets";
  public static final String URL_WORKBASKET_DEFINITIONS = API_V1 + "workbasket-definitions";
  public static final String URL_WORKBASKET_ID = URL_WORKBASKET + "/{workbasketId}";
  public static final String URL_WORKBASKET_ID_ACCESS_ITEMS =
          URL_WORKBASKET_ID + "/workbasketAccessItems";
  public static final String URL_WORKBASKET_ID_DISTRIBUTION =
          URL_WORKBASKET_ID + "/distribution-targets";

  public static final String URL_TASKS = API_V1 + "tasks";
  public static final String URL_TASKS_ID = URL_TASKS + "/{taskId}";
  public static final String URL_TASKS_ID_CLAIM = URL_TASKS_ID + "/claim";
  public static final String URL_TASKS_ID_SELECT_AND_CLAIM = URL_TASKS + "/select-and-claim";
  public static final String URL_TASKS_ID_COMPLETE = URL_TASKS_ID + "/complete";
  public static final String URL_TASKS_ID_TRANSFER_WORKBASKET_ID =
          URL_TASKS_ID + "/transfer/{workbasketId}";

  public static final String URL_TASK_COMMENTS = URL_TASKS_ID + "/comments";
  public static final String URL_TASK_COMMENT = URL_TASKS + "/comments/{taskCommentId}";

  public static final String URL_MONITOR = API_V1 + "monitor";
  public static final String URL_MONITOR_TASKS_STATUS = URL_MONITOR + "/tasks-status-report";
  public static final String URL_MONITOR_TASKS_WORKBASKET =
      URL_MONITOR + "/tasks-workbasket-report";
  public static final String URL_MONITOR_TASKS_WORKBASKET_PLANNED =
      URL_MONITOR + "/tasks-workbasket-planned-date-report";
  public static final String URL_MONITOR_TASKS_CLASSIFICATION =
      URL_MONITOR + "/tasks-classification-report";
  public static final String URL_MONITOR_TIMESTAMP = URL_MONITOR + "/timestamp-report";

  public static final String URL_HISTORY_ENABLED = API_V1 + "history-provider-enabled";
  public static final String URL_HISTORY_EVENTS = API_V1 + "task-history-event";
  public static final String URL_HISTORY_EVENTS_ID = "/{historyEventId}";

  private RestEndpoints() {}
}
