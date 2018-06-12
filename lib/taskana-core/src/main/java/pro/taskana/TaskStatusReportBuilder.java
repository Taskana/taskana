package pro.taskana;

import java.util.List;

import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.impl.TaskStatusReport;

/**
 * The TaskStatusReportBuilder is used to build a {@link TaskStatusReport}. A TaskStatusReport contains the total number
 * of tasks, clustered in their task status. Furthermore the report contains a sum line that contains the total numbers
 * of the different clusters and the total number of all tasks.
 */
public interface TaskStatusReportBuilder {

    /**
     * Adds a list of states to the builder. The created report contains only tasks with a state in this list.
     *
     * @param states
     *            a list of states
     * @return the TaskStatusReportBuilder
     */
    TaskStatusReportBuilder stateIn(List<TaskState> states);

    /**
     * Adds a list of domains to the builder. The created report contains only tasks with a domain in this list.
     *
     * @param domains
     *            a list of domains
     * @return the TaskStatusReportBuilder
     */
    TaskStatusReportBuilder domainIn(List<String> domains);

    /**
     * Returns a {@link TaskStatusReport} containing all tasks after applying the filters.
     *
     * @throws NotAuthorizedException
     *             if the user has no rights to access the monitor
     * @return the TaskStatusReport
     */
    TaskStatusReport buildReport() throws NotAuthorizedException;
}
