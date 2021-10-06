package pro.taskana.task.internal.jobs.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.IntPredicate;

import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.internal.TaskanaEngineImpl;
import pro.taskana.spi.priority.internal.PriorityServiceManager;
import pro.taskana.task.api.TaskQueryColumnName;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.TaskSummary;

public class TaskUpdatePriorityWorker {

  private final SqlConnectionRunner sqlConnectionRunner;
  private final TaskanaEngine taskanaEngine;
  private final PriorityServiceManager priorityServiceManager;

  public TaskUpdatePriorityWorker(TaskanaEngine taskanaEngine) {
    this.taskanaEngine = taskanaEngine;
    sqlConnectionRunner = new SqlConnectionRunner(taskanaEngine);
    priorityServiceManager = ((TaskanaEngineImpl) taskanaEngine).getPriorityServiceManager();
  }

  public List<String> executeBatch(List<String> taskIds) {
    List<String> updatedTaskIds = new ArrayList<>();
    sqlConnectionRunner.runWithConnection(
        connection -> {
          TaskUpdatePriorityBatchStatement taskUpdateBatch =
              new TaskUpdatePriorityBatchStatement(connection);

          List<TaskSummary> list = getTaskSummariesByIds(taskIds);
          for (TaskSummary taskSummary : list) {
            OptionalInt calculatedPriority = getCalculatedPriority(taskSummary);
            if (calculatedPriority.isPresent()) {
              final String taskId = taskSummary.getId();
              updatedTaskIds.add(taskId);
              taskUpdateBatch.addPriorityUpdate(taskId, calculatedPriority.getAsInt());
            }
          }
          taskUpdateBatch.executeBatch();

          // don't forget to save changes
          if (!connection.getAutoCommit()) {
            connection.commit();
          }
        });

    return updatedTaskIds;
  }

  /**
   * This will return all relevant task ids. This may result in a LOT! of ids
   *
   * @return list of task ids.
   */
  public List<String> getAllRelevantTaskIds() {
    return taskanaEngine
        .getTaskService()
        .createTaskQuery()
        .stateNotIn(TaskState.END_STATES)
        .listValues(TaskQueryColumnName.ID, SortDirection.ASCENDING);
  }

  public List<TaskSummary> getTaskSummariesByIds(List<String> taskIds) {
    return taskanaEngine
        .getTaskService()
        .createTaskQuery()
        .idIn(taskIds.toArray(new String[0]))
        .list();
  }

  public OptionalInt getCalculatedPriority(TaskSummary taskSummary) {
    OptionalInt computedPriority = priorityServiceManager.calculatePriorityOfTask(taskSummary);
    if (computedPriority.isPresent()
        && hasDifferentPriority(taskSummary).test(computedPriority.getAsInt())) {
      return computedPriority;
    }
    return OptionalInt.empty();
  }

  public static IntPredicate hasDifferentPriority(TaskSummary taskSummary) {
    return prio -> taskSummary != null && prio != taskSummary.getPriority();
  }
}
