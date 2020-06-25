package pro.taskana.task.internal.util;

import static pro.taskana.task.internal.util.TaskQueryMapperQueryProviderUtil.getDefaultColumnTaskSummariesQuery;
import static pro.taskana.task.internal.util.TaskQueryMapperQueryProviderUtil.getDefaultCountObjectReferencesQuery;
import static pro.taskana.task.internal.util.TaskQueryMapperQueryProviderUtil.getDefaultCountTaskSummariesDb2Query;
import static pro.taskana.task.internal.util.TaskQueryMapperQueryProviderUtil.getDefaultCountTaskSummariesQuery;
import static pro.taskana.task.internal.util.TaskQueryMapperQueryProviderUtil.getDefaultObjectReferenceColumnValuesQuery;
import static pro.taskana.task.internal.util.TaskQueryMapperQueryProviderUtil.getDefaultObjectReferencesQuery;
import static pro.taskana.task.internal.util.TaskQueryMapperQueryProviderUtil.getDefaultSelectTaskSummariesDb2Query;
import static pro.taskana.task.internal.util.TaskQueryMapperQueryProviderUtil.getDefaultSelectTaskSummariesQuery;

public class TaskQueryMapperQueryProvider {

  private static final String OPENING_SCRIPT_TAG = "<script>";
  private static final String CLOSING_SCRIPT_TAG = "</script>";

  public String getSelectTaskSummariesQuery() {
    return OPENING_SCRIPT_TAG + getDefaultSelectTaskSummariesQuery() + CLOSING_SCRIPT_TAG;
  }

  public String getSelectTaskSummariesDb2Query() {
    return OPENING_SCRIPT_TAG + getDefaultSelectTaskSummariesDb2Query() + CLOSING_SCRIPT_TAG;
  }

  public String getCountTaskSummariesQuery() {
    return OPENING_SCRIPT_TAG + getDefaultCountTaskSummariesQuery() + CLOSING_SCRIPT_TAG;
  }

  public String getCountTaskSummariesDb2Query() {
    return OPENING_SCRIPT_TAG + getDefaultCountTaskSummariesDb2Query() + CLOSING_SCRIPT_TAG;
  }

  public String getTaskColumnValuesQuery() {
    return OPENING_SCRIPT_TAG + getDefaultColumnTaskSummariesQuery() + CLOSING_SCRIPT_TAG;
  }

  public String getObjectReferencesQuery() {
    return OPENING_SCRIPT_TAG + getDefaultObjectReferencesQuery() + CLOSING_SCRIPT_TAG;
  }

  public String getCountObjectReferencesQuery() {
    return OPENING_SCRIPT_TAG + getDefaultCountObjectReferencesQuery() + CLOSING_SCRIPT_TAG;
  }

  public String getObjectReferenceColumnValuesQuery() {
    return OPENING_SCRIPT_TAG + getDefaultObjectReferenceColumnValuesQuery() + CLOSING_SCRIPT_TAG;
  }
}
