package pro.taskana.monitor.api.reports;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.monitor.api.reports.header.TaskStatusColumnHeader;
import pro.taskana.monitor.api.reports.item.TaskQueryItem;
import pro.taskana.task.api.TaskState;

/**
 * A TaskStatusReport contains the total number of tasks, clustered in their task status.
 * Furthermore the report contains a sum line that contains the total numbers of the different
 * clusters and the total number of all tasks.
 */
public class TaskStatusReport extends Report<TaskQueryItem, TaskStatusColumnHeader> {

  public TaskStatusReport(List<TaskState> filter) {
    super(
        (filter != null ? filter.stream() : Stream.of(TaskState.values()))
            .map(TaskStatusColumnHeader::new)
            .collect(Collectors.toList()),
        new String[] {"DOMAINS"});
  }

  /** Builder for {@link TaskStatusReport}. */
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
