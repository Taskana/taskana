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
package pro.taskana.monitor.api.reports;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.monitor.api.reports.header.ColumnHeader;
import pro.taskana.monitor.api.reports.header.TaskStatusColumnHeader;
import pro.taskana.monitor.api.reports.item.TaskQueryItem;
import pro.taskana.monitor.api.reports.row.Row;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.Task;
import pro.taskana.workbasket.api.models.Workbasket;

/**
 * A TaskStatusReport aggregates {@linkplain Task} related data.
 *
 * <p>Each {@linkplain Row} represents a {@linkplain Workbasket}.
 *
 * <p>Each {@linkplain ColumnHeader} represents a {@linkplain TaskState}.
 */
public class TaskStatusReport extends Report<TaskQueryItem, TaskStatusColumnHeader> {

  public TaskStatusReport(List<TaskState> filter) {
    super(
        (filter != null ? filter.stream() : Stream.of(TaskState.values()))
            .map(TaskStatusColumnHeader::new)
            .collect(Collectors.toList()),
        new String[] {"DOMAINS"});
  }

  /** Builder for {@linkplain TaskStatusReport}. */
  public interface Builder extends Report.Builder<TaskQueryItem, TaskStatusColumnHeader> {

    @Override
    TaskStatusReport buildReport() throws MismatchedRoleException;

    /**
     * Adds a list of states to the builder. The created report contains only tasks with a state in
     * this list.
     *
     * @param states a list of states
     * @return the Builder
     */
    Builder stateIn(List<TaskState> states);

    /**
     * Adds a priority Integer to the builder. The created report contains only Tasks with a
     * priority greater or equal than this provided Integer.
     *
     * @param priority an Integer for the minimum priority
     * @return the Builder
     */
    Builder priorityMinimum(Integer priority);

    /**
     * Adds a list of domains to the builder. The created report contains only tasks with a domain
     * in this list.
     *
     * @param domains a list of domains
     * @return the Builder
     */
    Builder domainIn(List<String> domains);

    /**
     * Adds a list of workbasketIds to the builder. The created report contains only tasks from a
     * workbakset in this list
     *
     * @param workbasketIds a list of workbasketIds
     * @return the Builder
     */
    Builder workbasketIdsIn(List<String> workbasketIds);
  }
}
