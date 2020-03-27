package pro.taskana.rest;

/** Collection of Url to Controller mappings. */
public final class Mapping {

  public static final String PRE = "/api/v1/";
  public static final String URL_ACCESSID = PRE + "access-ids";
  public static final String URL_ACCESSID_GROUPS = URL_ACCESSID + "/groups";
  public static final String URL_CLASSIFICATIONS = PRE + "classifications";
  public static final String URL_CLASSIFICATIONS_ID = URL_CLASSIFICATIONS + "/{classificationId}";
  public static final String URL_CLASSIFICATIONDEFINITION = PRE + "classification-definitions";
  public static final String URL_MONITOR = PRE + "monitor";
  public static final String URL_MONITOR_TASKSSTATUS = URL_MONITOR + "/tasks-status-report";
  public static final String URL_MONITOR_TASKSWORKBASKET = URL_MONITOR + "/tasks-workbasket-report";
  public static final String URL_MONITOR_TASKSWORKBASKETPLANNED =
      URL_MONITOR + "/tasks-workbasket-planned-date-report";
  public static final String URL_MONITOR_TASKSCLASSIFICATION =
      URL_MONITOR + "/tasks-classification-report";
  public static final String URL_MONITOR_TIMESTAMP = URL_MONITOR + "/timestamp-report";
  public static final String URL_DOMAIN = PRE + "domains";
  public static final String URL_CLASSIFICATION_CATEGORIES = PRE + "classification-categories";
  public static final String URL_CLASSIFICATION_TYPES = PRE + "classification-types";
  public static final String URL_CLASSIFICATION_CATEGORIES_BY_TYPES =
      PRE + "classifications-by-type";
  public static final String URL_CURRENT_USER = PRE + "current-user-info";
  public static final String URL_HISTORY_ENABLED = PRE + "history-provider-enabled";
  public static final String URL_HISTORY_EVENTS = PRE + "task-history-event";
  public static final String URL_HISTORY_EVENTS_ID = "/{historyEventId}";
  public static final String URL_VERSION = PRE + "version";
  public static final String URL_TASKS = PRE + "tasks";
  public static final String URL_TASKS_ID = URL_TASKS + "/{taskId}";
  public static final String URL_TASK_GET_POST_COMMENTS = URL_TASKS_ID + "/comments";
  public static final String URL_TASK_COMMENTS = URL_TASKS + "/comments";
  public static final String URL_TASK_COMMENT = URL_TASK_COMMENTS + "/{taskCommentId}";
  public static final String URL_TASKS_ID_CLAIM = URL_TASKS_ID + "/claim";
  public static final String URL_TASKS_ID_COMPLETE = URL_TASKS_ID + "/complete";
  public static final String URL_TASKS_ID_TRANSFER_WORKBASKETID =
      URL_TASKS_ID + "/transfer/{workbasketId}";
  public static final String URL_WORKBASKETACCESSITEMS = PRE + "workbasket-access-items";
  public static final String URL_WORKBASKET = PRE + "workbaskets";
  public static final String URL_WORKBASKET_ID = URL_WORKBASKET + "/{workbasketId}";
  public static final String URL_WORKBASKET_ID_ACCESSITEMS =
      URL_WORKBASKET_ID + "/workbasketAccessItems";
  public static final String URL_WORKBASKET_ID_DISTRIBUTION =
      URL_WORKBASKET_ID + "/distribution-targets";
  // TODO @Deprecated
  public static final String URL_WORKBASKET_DISTRIBUTION_ID =
      URL_WORKBASKET + "/distribution-targets/{workbasketId}";
  public static final String URL_WORKBASKETDEFIITIONS = PRE + "workbasket-definitions";

  private Mapping() {}
}
