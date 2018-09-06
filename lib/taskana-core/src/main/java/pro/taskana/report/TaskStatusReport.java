package pro.taskana.report;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import pro.taskana.TaskState;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.TaskQueryItem;
import pro.taskana.impl.report.TaskStatusColumnHeader;

/**
 *  A TaskStatusReport contains the total number of tasks, clustered in their task status.
 *  Furthermore the report contains a sum line that contains the total numbers
 *  of the different clusters and the total number of all tasks.
 */
public class TaskStatusReport extends Report<TaskQueryItem, TaskStatusColumnHeader> {

    public TaskStatusReport(List<TaskState> filter) {
        super((filter != null ? filter.stream() : Stream.of(TaskState.values()))
            .map(TaskStatusColumnHeader::new)
            .collect(Collectors.toList()), "DOMAINS");
    }

    /**
     * Builder for {@link TaskStatusReport}.
     */
    public interface Builder extends Report.Builder<TaskQueryItem, TaskStatusColumnHeader> {

        /**
         * Adds a list of states to the builder. The created report contains only tasks with a state in this list.
         *
         * @param states
         *            a list of states
         * @return the Builder
         */
        Builder stateIn(List<TaskState> states);

        /**
         * Adds a list of domains to the builder. The created report contains only tasks with a domain in this list.
         *
         * @param domains
         *            a list of domains
         * @return the Builder
         */
        Builder domainIn(List<String> domains);

        @Override
        TaskStatusReport buildReport() throws NotAuthorizedException, InvalidArgumentException;
    }
}
