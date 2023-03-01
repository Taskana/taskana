/*-
 * #%L
 * pro.taskana:taskana-core
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package pro.taskana.task.internal.jobs.helper;

import static java.util.Objects.nonNull;

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
    return prio -> nonNull(taskSummary) && prio != taskSummary.getPriority();
  }
}
