package io.kadai.task.internal.jobs;

import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.ScheduledJob;
import io.kadai.common.api.exceptions.KadaiException;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.internal.jobs.AbstractKadaiJob;
import io.kadai.common.internal.transaction.KadaiTransactionProvider;
import io.kadai.task.internal.TaskServiceImpl;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class executes a job of type {@linkplain TaskRefreshJob}. */
public class TaskRefreshJob extends AbstractKadaiJob {

  public static final String TASK_IDS = "taskIds";
  public static final String PRIORITY_CHANGED = "priorityChanged";
  public static final String SERVICE_LEVEL_CHANGED = "serviceLevelChanged";
  private static final Logger LOGGER = LoggerFactory.getLogger(TaskRefreshJob.class);
  private final List<String> affectedTaskIds;
  private final boolean priorityChanged;
  private final boolean serviceLevelChanged;

  public TaskRefreshJob(KadaiEngine engine, KadaiTransactionProvider txProvider, ScheduledJob job) {
    super(engine, txProvider, job, false);
    Map<String, String> args = job.getArguments();
    String taskIdsString = args.get(TASK_IDS);
    affectedTaskIds = Arrays.asList(taskIdsString.split(","));
    priorityChanged = Boolean.parseBoolean(args.get(PRIORITY_CHANGED));
    serviceLevelChanged = Boolean.parseBoolean(args.get(SERVICE_LEVEL_CHANGED));
  }

  @Override
  public void execute() throws KadaiException {
    LOGGER.info("Running TaskRefreshJob for {} tasks", affectedTaskIds.size());
    try {
      TaskServiceImpl taskService = (TaskServiceImpl) kadaiEngineImpl.getTaskService();
      taskService.refreshPriorityAndDueDatesOfTasksOnClassificationUpdate(
          affectedTaskIds, serviceLevelChanged, priorityChanged);
      LOGGER.info("TaskRefreshJob ended successfully.");
    } catch (Exception e) {
      throw new SystemException("Error while processing TaskRefreshJob.", e);
    }
  }

  @Override
  protected String getType() {
    return TaskRefreshJob.class.getName();
  }

  @Override
  public String toString() {
    return "TaskRefreshJob [firstRun="
        + firstRun
        + ", runEvery="
        + runEvery
        + ", kadaiEngineImpl="
        + kadaiEngineImpl
        + ", txProvider="
        + txProvider
        + ", scheduledJob="
        + scheduledJob
        + ", affectedTaskIds="
        + affectedTaskIds
        + ", priorityChanged="
        + priorityChanged
        + ", serviceLevelChanged="
        + serviceLevelChanged
        + "]";
  }
}
