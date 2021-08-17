package pro.taskana.task.internal.jobs.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.spi.priority.internal.PriorityServiceManager;
import pro.taskana.task.api.TaskQueryColumnName;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.TaskSummary;

public class TaskUpdatePriorityWorker {

  private final SqlConnectionRunner sqlConnectionRunner;
  private final TaskanaEngine taskanaEngine;

  public TaskUpdatePriorityWorker(TaskanaEngine taskanaEngine) {
    this.taskanaEngine = taskanaEngine;
    this.sqlConnectionRunner = new SqlConnectionRunner(taskanaEngine);
  }

  public List<String> executeBatch(List<String> taskIds) {

    List<String> updatedTaskIds = new ArrayList<>();
    sqlConnectionRunner.runWithConnection(
        connection -> {
          TaskUpdatePriorityBatchStatement taskUpdateBatch =
              new TaskUpdatePriorityBatchStatement(connection);

          List<TaskSummary> list = getTaskSummariesByIds(taskIds);
          for (TaskSummary taskSummary : list) {
            Optional<Integer> calculatedPriority = getCalculatedPriority(taskSummary);
            if (calculatedPriority.isPresent()) {
              final String taskId = taskSummary.getId();
              updatedTaskIds.add(taskId);
              taskUpdateBatch.addPriorityUpdate(taskId, calculatedPriority.get());
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

  public Optional<Integer> getCalculatedPriority(TaskSummary taskSummary) {
    return PriorityServiceManager.getInstance()
        .calculatePriorityOfTask(taskSummary)
        .filter(hasDifferentPriority(taskSummary));
  }

  public static Predicate<Integer> hasDifferentPriority(TaskSummary taskSummary) {
    return prio -> taskSummary != null && prio != taskSummary.getPriority();
  }
}
