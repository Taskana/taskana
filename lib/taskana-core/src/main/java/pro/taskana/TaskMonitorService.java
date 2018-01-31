package pro.taskana;

import java.util.List;

import pro.taskana.model.DueWorkbasketCounter;
import pro.taskana.model.Report;
import pro.taskana.model.ReportLineItemDefinition;
import pro.taskana.model.TaskState;
import pro.taskana.model.TaskStateCounter;

/**
 * The Task Monitor Service manages operations on tasks regarding the monitoring.
 */
public interface TaskMonitorService {

    /**
     * This method counts all tasks with a given state.
     *
     * @param states
     *            the countable states
     * @return a List of {@link TaskStateCounter} objects that specifies how many tasks in the specified states exist in
     *         the available work baskets
     */
    List<TaskStateCounter> getTaskCountForState(List<TaskState> states);

    /**
     * Count all Tasks in a given work basket where the due date is after "daysInPast" days from today in the past and
     * the tasks are in specified states.
     *
     * @param workbasketId
     *            the id of the work basket
     * @param daysInPast
     *            identifies the days in the past from today
     * @param states
     *            {@link List} of {@link TaskState} that identifies the states of the tasks to be searched for
     * @return the number of Task objects in the given work basket that match the query parameters
     */
    long getTaskCountForWorkbasketByDaysInPastAndState(String workbasketId, long daysInPast, List<TaskState> states);

    /**
     * Count all Tasks for all work basket objects where the due date is after "daysInPast" days from today in the past
     * and the tasks are in specified states.
     *
     * @param daysInPast
     *            identifies the days in the past from today
     * @param states
     *            {@link List} of {@link TaskState} objects that identifies the states of the tasks searched
     * @return a list of of {@link DueWorkbasketCounter} objects that specifies how many tasks in the requested states
     *         with appropriate due date exist in the various work baskets
     */
    List<DueWorkbasketCounter> getTaskCountByWorkbasketAndDaysInPastAndState(long daysInPast, List<TaskState> states);

    /**
     * Returns a {@link Report} for a given list of {@link Workbasket}s and for a given list of {@link TaskState}s. The
     * report only contains the number of all tasks of the respective workbasket as well as the total sum of all tasks.
     * Only tasks with a state in the list of TaskStates are provided. Task with Timestamp DUE = null are not
     * considered.
     *
     * @param workbaskets
     *            a list of {@link Workbasket} objects that should be listed in the report
     * @param states
     *            a list of {@link TaskState} objects that specify the states of the tasks that are provided
     * @return a {@link Report} object that only contains the number of all tasks of the respective workbasket as well
     *         as the total number of all tasks
     */
    Report getWorkbasketLevelReport(List<Workbasket> workbaskets, List<TaskState> states);

    /**
     * Returns a {@link Report} for a given list of {@link Workbasket}s, a given list of {@link TaskState}s and a given
     * list of {@link ReportLineItemDefinition}s. For each workbasket the report contains a list of ReportLineItems that
     * subdivides the report in to different cluster grouped by the due date. Only tasks with a state in the list of
     * TaskStates are provided. Tasks with Timestamp DUE = null are not considered.
     *
     * @param workbaskets
     *            a list of {@link Workbasket} objects that should be listed in the report
     * @param states
     *            a list of {@link TaskState} objects that specify the states of the tasks that are provided
     * @param reportLineItemDefinitions
     *            a list of {@link ReportLineItemDefinition} objects that specify the subdivision into different cluster
     *            of due dates. Days in past are represented as negative values and days in the future are represented
     *            as positive values. To avoid tasks are counted multiple times or not be listed in the report, these
     *            reportLineItemDefinitions should not overlap and should not have gaps. If the ReportLineDefinition
     *            should represent a single day, lowerLimit and upperLimit have to be equal.
     * @return a {@link Report} object that represents an overview of all tasks in the
     */
    Report getWorkbasketLevelReport(List<Workbasket> workbaskets, List<TaskState> states,
        List<ReportLineItemDefinition> reportLineItemDefinitions);

    /**
     * Returns a {@link Report} with categories for a given list of {@link Workbasket}s and for a given list of
     * {@link TaskState}s. The report only contains the number of all tasks of the respective category as well as the
     * total sum of all tasks. Only tasks with a state in the list of TaskStates are provided. Task with Timestamp DUE =
     * null are not considered.
     *
     * @param workbaskets
     *            a list of {@link Workbasket} objects that whose task should be counted in the report
     * @param states
     *            a list of {@link TaskState} objects that specify the states of the tasks that are provided
     * @return a {@link Report} object that only contains the number of all tasks of the respective category as well as
     *         the total number of all tasks
     */
    Report getCategoryReport(List<Workbasket> workbaskets, List<TaskState> states);

    /**
     * Returns a {@link Report} with categories for a given list of {@link Workbasket}s, a given list of
     * {@link TaskState}s and a given list of {@link ReportLineItemDefinition}s. For each category the report contains a
     * list of ReportLineItems that subdivides the report in to different cluster grouped by the due date. Only tasks
     * with a state in the list of TaskStates are provided. Tasks with Timestamp DUE = null are not considered.
     *
     * @param workbaskets
     *            a list of {@link Workbasket} objects that should be listed in the report
     * @param states
     *            a list of {@link TaskState} objects that specify the states of the tasks that are provided
     * @param reportLineItemDefinitions
     *            a list of {@link ReportLineItemDefinition} objects that specify the subdivision into different cluster
     *            of due dates. Days in past are represented as negative values and days in the future are represented
     *            as positive values. To avoid tasks are counted multiple times or not be listed in the report, these
     *            reportLineItemDefinitions should not overlap and should not have gaps. If the ReportLineDefinition
     *            should represent a single day, lowerLimit and upperLimit have to be equal.
     * @return a {@link Report} object that represents an overview of all tasks of the respective category
     */
    Report getCategoryReport(List<Workbasket> workbaskets, List<TaskState> states,
        List<ReportLineItemDefinition> reportLineItemDefinitions);
}
