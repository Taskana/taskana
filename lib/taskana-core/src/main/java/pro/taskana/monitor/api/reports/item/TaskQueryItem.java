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
package pro.taskana.monitor.api.reports.item;

import pro.taskana.task.api.TaskState;

/**
 * The TaskQueryItem entity contains the number of tasks for a domain which have a specific state.
 */
public class TaskQueryItem implements QueryItem {

  private String workbasketKey;
  private TaskState state;
  private int count;

  public void setWorkbasketKey(String workbasketKey) {
    this.workbasketKey = workbasketKey;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public TaskState getState() {
    return state;
  }

  public void setState(TaskState state) {
    this.state = state;
  }

  @Override
  public String getKey() {
    return workbasketKey;
  }

  @Override
  public int getValue() {
    return count;
  }

  @Override
  public String toString() {
    return "TaskQueryItem [workbasketKey="
        + workbasketKey
        + ", state="
        + state
        + ", count="
        + count
        + "]";
  }
}
