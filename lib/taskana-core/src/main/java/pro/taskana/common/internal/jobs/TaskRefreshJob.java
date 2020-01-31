package pro.taskana.common.internal.jobs;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;
import pro.taskana.common.internal.util.LoggerUtils;
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

  public TaskRefreshJob(
      TaskanaEngine engine, TaskanaTransactionProvider<Object> txProvider, ScheduledJob job) {
    super(engine, txProvider, job);
    Map<String, String> args = job.getArguments();
    String taskIdsString = args.get(ARG_TASK_IDS);
    affectedTaskIds = Arrays.asList(taskIdsString.split(","));
  }

  @Override
  public void run() throws TaskanaException {
    LOGGER.info("Running TaskRefreshJob for {} tasks", affectedTaskIds.size());
    try {
      TaskServiceImpl taskService = (TaskServiceImpl) taskanaEngineImpl.getTaskService();
      for (String taskId : affectedTaskIds) {
        try {
          taskService.refreshPriorityAndDueDate(taskId);
        } catch (Exception e) {
          LOGGER.warn(
              "Task {} could not be refreshed because of exception: {}", taskId, e.getMessage());
        }
      }
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
