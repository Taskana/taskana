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
package pro.taskana.spi.history.api.events.task;

import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;

/** Event fired if a task is completed. */
public class TaskCompletedEvent extends TaskHistoryEvent {

  public TaskCompletedEvent(String id, Task completedTask, String userId) {
    super(id, completedTask, userId, null);
    eventType = TaskHistoryEventType.COMPLETED.getName();
    created = completedTask.getCompleted();
  }

  public TaskCompletedEvent(String id, TaskSummary completedTask, String userId) {
    super(id, completedTask, userId, null);
    eventType = TaskHistoryEventType.COMPLETED.getName();
    created = completedTask.getCompleted();
  }
}
