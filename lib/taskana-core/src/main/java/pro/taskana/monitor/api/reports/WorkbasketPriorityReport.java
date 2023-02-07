package pro.taskana.monitor.api.reports;

import java.util.List;

import pro.taskana.common.api.Interval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.monitor.api.reports.header.ColumnHeader;
import pro.taskana.monitor.api.reports.header.PriorityColumnHeader;
import pro.taskana.monitor.api.reports.item.PriorityQueryItem;
import pro.taskana.monitor.api.reports.row.Row;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskCustomIntField;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.Task;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.models.Workbasket;

/**
 * A WorkbasketReport aggregates {@linkplain Task} related data.
 *
 * <p>Each {@linkplain Row} represents a {@linkplain Workbasket}.
 *
 * <p>Each {@linkplain ColumnHeader} represents a {@linkplain Task#getPriority() priority range}.
 */
public class WorkbasketPriorityReport extends Report<PriorityQueryItem, PriorityColumnHeader> {

  public WorkbasketPriorityReport(List<PriorityColumnHeader> priorityColumnHeaders) {
    super(priorityColumnHeaders, new String[] {"WORKBASKET"});
  }

  /** Builder for {@linkplain WorkbasketPriorityReport}. */
  public interface Builder extends Report.Builder<PriorityQueryItem, PriorityColumnHeader> {

    @Override
    WorkbasketPriorityReport buildReport() throws NotAuthorizedException;

    /**
     * Adds {@linkplain WorkbasketType WorkbasketTypes} to the builder. The created report will only
     * contain Tasks from {@linkplain Workbasket Workbaskets} with one of the provided types.
     *
     * @param workbasketTypes the workbasketTypes to include in the report
     * @return the {@linkplain Builder}
     */
    Builder workbasketTypeIn(WorkbasketType... workbasketTypes);

    /**
     * Adds a list of workbasket ids to the builder. The created report contains only tasks with a
     * workbasket id in this list.
     *
     * @param workbasketIds a list of workbasket ids
     * @return the {@linkplain Builder}
     */
    Builder workbasketIdIn(List<String> workbasketIds);

    /**
     * Adds a list of states to the builder. The created report contains only tasks with a state in
     * this list.
     *
     * @param states a list of states
     * @return the {@linkplain Builder}
     */
    Builder stateIn(List<TaskState> states);

    /**
     * Adds a list of classificationCategories to the builder. The created report contains only
     * tasks with a category in this list.
     *
     * @param classificationCategory a list of classificationCategories
     * @return the {@linkplain Builder}
     */
    Builder classificationCategoryIn(List<String> classificationCategory);

    /**
     * Adds a list of classificationIds to the builder. The created report contains only tasks with
     * a classificationId in this list.
     *
     * @param classificationIds a list of classificationIds
     * @return the {@linkplain Builder}
     */
    Builder classificationIdIn(List<String> classificationIds);

    /**
     * Adds a list of excludedClassificationIds to the builder. The created report contains only
     * tasks with a classificationId NOT in this list.
     *
     * @param excludedClassificationIds a list of excludedClassificationIds
     * @return the {@linkplain Builder}
     */
    Builder excludedClassificationIdIn(List<String> excludedClassificationIds);

    /**
     * Adds a list of domains to the builder. The created report contains only tasks with a domain
     * in this list.
     *
     * @param domains a list of domains
     * @return the {@linkplain Builder}
     */
    Builder domainIn(List<String> domains);

    /**
     * Adds the values of a certain {@linkplain TaskCustomField} for exact matching to the builder.
     *
     * <p>The created report contains only tasks with a {@linkplain
     * Task#getCustomField(TaskCustomField) custom attribute} value exactly matching one of the
     * items in the list.
     *
     * @param customField the specified {@linkplain TaskCustomField}
     * @param strings the values the specified {@linkplain Task#getCustomField(TaskCustomField)
     *     custom attribute} should match
     * @return the modified the {@linkplain Builder}
     * @throws InvalidArgumentException if filter values are not given
     */
    Builder customAttributeIn(TaskCustomField customField, String... strings)
        throws InvalidArgumentException;

    /**
     * Excludes the values of a certain {@linkplain TaskCustomField} to the builder.
     *
     * <p>The created report contains only tasks with a {@linkplain
     * Task#getCustomField(TaskCustomField) custom attribute} value not matching one of the items in
     * the list.
     *
     * @param customField the specified {@linkplain TaskCustomField}
     * @param strings the values the specified {@linkplain Task#getCustomField(TaskCustomField)
     *     custom attribute} should not match
     * @return the modified {@linkplain Builder}
     * @throws InvalidArgumentException if filter values are not given
     */
    Builder customAttributeNotIn(TaskCustomField customField, String... strings)
        throws InvalidArgumentException;

    /**
     * Adds the values of a certain {@linkplain TaskCustomField} for pattern matching to the
     * builder.
     *
     * <p>The created report contains only tasks with a {@linkplain
     * Task#getCustomField(TaskCustomField) custom attribute} value pattern-matching one of the
     * items in the list. They will be compared in SQL with the LIKE operator. You may use a
     * wildcard like % to specify the pattern. If you specify multiple arguments they are combined
     * with the OR keyword.
     *
     * @param customField the specified {@linkplain TaskCustomField}
     * @param strings the values the specified {@linkplain Task#getCustomField(TaskCustomField)
     *     custom attribute} should match
     * @return the modified the {@linkplain Builder}
     * @throws InvalidArgumentException if filter values are not given
     */
    Builder customAttributeLike(TaskCustomField customField, String... strings)
        throws InvalidArgumentException;

    /**
     * Adds the values of a certain {@linkplain TaskCustomIntField} for exact matching to the
     * builder.
     *
     * <p>The created {@linkplain Report} contains only {@linkplain Task Tasks} with a {@linkplain
     * Task#getCustomIntField(TaskCustomIntField) customIntField} value exactly matching one of the
     * items in the list.
     *
     * @param customIntField the specified {@linkplain TaskCustomIntField}
     * @param values the values the specified {@linkplain Task#getCustomIntField(TaskCustomIntField)
     *     customIntField} should match
     * @return the modified {@linkplain Builder}
     * @throws InvalidArgumentException if filter values are not given
     */
    Builder customIntAttributeIn(TaskCustomIntField customIntField, Integer... values)
        throws InvalidArgumentException;

    /**
     * Excludes the values of a certain {@linkplain TaskCustomIntField} to the builder.
     *
     * <p>The created {@linkplain Report} contains only {@linkplain Task Tasks} with a {@linkplain
     * Task#getCustomIntField(TaskCustomIntField) customIntField} value not matching one of the
     * items in the list.
     *
     * @param customIntField the specified {@linkplain TaskCustomIntField}
     * @param values the values the specified {@linkplain Task#getCustomIntField(TaskCustomIntField)
     *     customIntField} should not match
     * @return the modified {@linkplain Builder}
     * @throws InvalidArgumentException if filter values are not given
     */
    Builder customIntAttributeNotIn(TaskCustomIntField customIntField, Integer... values)
        throws InvalidArgumentException;

    /**
     * Adds ranges of {@linkplain TaskCustomIntField} for matching to the builder.
     *
     * <p>The created report contains only {@linkplain Task Tasks} with a {@linkplain
     * Task#getCustomIntField(TaskCustomIntField) customIntField} value being inside the range of
     * one of the items in the list.
     *
     * @param customIntField the specified {@linkplain TaskCustomIntField}
     * @param values the values the specified {@linkplain Task#getCustomIntField(TaskCustomIntField)
     *     customIntField} should match
     * @return the modified {@linkplain Builder}
     * @throws InvalidArgumentException if filter values are not given
     */
    Builder customIntAttributeWithin(TaskCustomIntField customIntField, Interval<Integer>... values)
        throws InvalidArgumentException;

    /**
     * Exclude ranges of {@linkplain TaskCustomIntField} for matching to the builder.
     *
     * <p>The created report contains only {@linkplain Task Tasks} with a {@linkplain
     * Task#getCustomIntField(TaskCustomIntField) customIntField} value being not inside the range
     * of one of the items in the list.
     *
     * @param customIntField the specified {@linkplain TaskCustomIntField}
     * @param values the values the specified {@linkplain Task#getCustomIntField(TaskCustomIntField)
     *     customIntField} should match
     * @return the modified {@linkplain Builder}
     * @throws InvalidArgumentException if filter values are not given
     */
    Builder customIntAttributeNotWithin(
        TaskCustomIntField customIntField, Interval<Integer>... values)
        throws InvalidArgumentException;

    /**
     * Adds a list of {@linkplain PriorityColumnHeader PriorityColumnHeaders} to the builder to
     * subdivide the report into clusters.
     *
     * @param columnHeaders the column headers the report should consist of.
     * @return the {@linkplain Builder}
     */
    Builder withColumnHeaders(List<PriorityColumnHeader> columnHeaders);

    /**
     * If this filter is used, the days of the report are counted in working days.
     *
     * @return the {@linkplain Builder}
     */
    Builder inWorkingDays();
  }
}
