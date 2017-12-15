package pro.taskana;

import java.util.List;

import pro.taskana.model.DueWorkbasketCounter;
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

}
