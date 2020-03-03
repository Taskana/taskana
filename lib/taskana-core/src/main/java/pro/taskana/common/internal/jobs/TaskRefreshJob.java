package pro.taskana.common.internal.jobs;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.LoggerUtils;
import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;
import pro.taskana.task.internal.TaskServiceImpl;

/**
 * This class executes a job of type CLASSIFICATIONCHANGEDJOB.
 *
 * @author bbr
 */
public class TaskRefreshJob extends AbstractTaskanaJob {

  public static final String ARG_TASK_IDS = "taskIds";
  private static final Logger LOGGER = LoggerFactory.getLogger(TaskRefreshJob.class);
  private List<String> affectedTaskIds;
  private boolean priorityChanged;
  private boolean serviceLevelChanged;

  public TaskRefreshJob(
      TaskanaEngine engine, TaskanaTransactionProvider<Object> txProvider, ScheduledJob job) {
    super(engine, txProvider, job);
    Map<String, String> args = job.getArguments();
    String taskIdsString = args.get(ClassificationChangedJob.TASK_IDS);
    affectedTaskIds = Arrays.asList(taskIdsString.split(","));
    priorityChanged = Boolean.parseBoolean(args.get(ClassificationChangedJob.PRIORITY_CHANGED));
    serviceLevelChanged =
        Boolean.parseBoolean(args.get(ClassificationChangedJob.SERVICE_LEVEL_CHANGED));
  }

  @Override
  public void run() throws TaskanaException {
    LOGGER.info("Running TaskRefreshJob for {} tasks", affectedTaskIds.size());
    try {
      TaskServiceImpl taskService = (TaskServiceImpl) taskanaEngineImpl.getTaskService();
      taskService.refreshPriorityAndDueDatesOfTasksOnClassificationUpdate(
          affectedTaskIds, serviceLevelChanged, priorityChanged);
      LOGGER.info("TaskRefreshJob ended successfully.");
    } catch (Exception e) {
      throw new TaskanaException("Error while processing TaskRefreshJob.", e);
    }
  }

  @Override
  public String toString() {
    return "TaskRefreshJob [affectedTaskIds= " + LoggerUtils.listToString(affectedTaskIds) + "]";
  }
}
