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
package pro.taskana.monitor.api.reports.header;

import pro.taskana.monitor.api.reports.item.TaskQueryItem;
import pro.taskana.task.api.TaskState;

/** The TaskStatusColumnHeader represents a column for each {@linkplain TaskState}. */
public class TaskStatusColumnHeader implements ColumnHeader<TaskQueryItem> {

  private final TaskState state;

  public TaskStatusColumnHeader(TaskState state) {
    this.state = state;
  }

  @Override
  public String getDisplayName() {
    return this.state.name();
  }

  @Override
  public boolean fits(TaskQueryItem item) {
    return item.getState() == this.state;
  }

  @Override
  public String toString() {
    return getDisplayName();
  }
}
