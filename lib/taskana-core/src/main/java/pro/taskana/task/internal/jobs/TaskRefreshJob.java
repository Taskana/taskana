package pro.taskana.task.internal.jobs;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.jobs.AbstractTaskanaJob;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;
import pro.taskana.task.internal.TaskServiceImpl;

/** This class executes a job of type {@linkplain TaskRefreshJob}. */
public class TaskRefreshJob extends AbstractTaskanaJob {

  public static final String TASK_IDS = "taskIds";
  public static final String PRIORITY_CHANGED = "priorityChanged";
  public static final String SERVICE_LEVEL_CHANGED = "serviceLevelChanged";
  private static final Logger LOGGER = LoggerFactory.getLogger(TaskRefreshJob.class);
  private final List<String> affectedTaskIds;
  private final boolean priorityChanged;
  private final boolean serviceLevelChanged;

  public TaskRefreshJob(
      TaskanaEngine engine, TaskanaTransactionProvider txProvider, ScheduledJob job) {
    super(engine, txProvider, job, false);
    Map<String, String> args = job.getArguments();
    String taskIdsString = args.get(TASK_IDS);
    affectedTaskIds = Arrays.asList(taskIdsString.split(","));
    priorityChanged = Boolean.parseBoolean(args.get(PRIORITY_CHANGED));
    serviceLevelChanged = Boolean.parseBoolean(args.get(SERVICE_LEVEL_CHANGED));
  }

  @Override
  public void execute() throws TaskanaException {
    LOGGER.info("Running TaskRefreshJob for {} tasks", affectedTaskIds.size());
    try {
      TaskServiceImpl taskService = (TaskServiceImpl) taskanaEngineImpl.getTaskService();
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
        + ", taskanaEngineImpl="
        + taskanaEngineImpl
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
