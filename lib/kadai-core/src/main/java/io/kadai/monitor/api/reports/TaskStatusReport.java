package io.kadai.monitor.api.reports;

import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.monitor.api.reports.header.ColumnHeader;
import io.kadai.monitor.api.reports.header.TaskStatusColumnHeader;
import io.kadai.monitor.api.reports.item.TaskQueryItem;
import io.kadai.monitor.api.reports.row.Row;
import io.kadai.task.api.TaskState;
import io.kadai.task.api.models.Task;
import io.kadai.workbasket.api.models.Workbasket;
import java.util.List;
import java.util.stream.Stream;

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
            .toList(),
        new String[] {"DOMAINS"});
  }

  /** Builder for {@linkplain TaskStatusReport}. */
  public interface Builder extends Report.Builder<TaskQueryItem, TaskStatusColumnHeader> {

    @Override
    TaskStatusReport buildReport() throws NotAuthorizedException;

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
