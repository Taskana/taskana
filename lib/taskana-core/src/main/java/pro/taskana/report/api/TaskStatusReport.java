package pro.taskana.report.api;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.report.internal.header.TaskStatusColumnHeader;
import pro.taskana.report.internal.item.TaskQueryItem;
import pro.taskana.report.internal.structure.Report;
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
    TaskStatusReport buildReport() throws NotAuthorizedException, InvalidArgumentException;

    /**
     * Adds a list of states to the builder. The created report contains only tasks with a state in
     * this list.
     *
     * @param states a list of states
     * @return the Builder
     */
    Builder stateIn(List<TaskState> states);

    /**
     * Adds a list of domains to the builder. The created report contains only tasks with a domain
     * in this list.
     *
     * @param domains a list of domains
     * @return the Builder
     */
    Builder domainIn(List<String> domains);
  }
}
