package pro.taskana.monitor.api.reports;

import java.util.List;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.monitor.api.SelectedItem;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.AgeQueryItem;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.Task;

/**
 * "Super" Interface for all TimeIntervalReportBuilders.
 *
 * @param <B> the true Builder behind this Interface
 * @param <I> the AgeQueryItem which will be inserted into the Report
 * @param <H> the column Header
 */
public interface TimeIntervalReportBuilder<
        B extends TimeIntervalReportBuilder<B, I, H>,
        I extends AgeQueryItem,
        H extends TimeIntervalColumnHeader>
    extends Report.Builder<I, H> {

  /**
   * Adds a list {@linkplain TimeIntervalColumnHeader TimeIntervalColumnHeaders} to the Builder to
   * subdivide the {@link Report} into clusters.
   *
   * @param columnHeaders the column headers the {@linkplain Report} should consist of.
   * @return the TimeIntervalReportBuilder
   */
  B withColumnHeaders(List<H> columnHeaders);

  /**
   * If this filter is used, the days of the Report are counted in working days.
   *
   * @return the TimeIntervalReportBuilder
   */
  B inWorkingDays();

  /**
   * Adds a list of workbasket ids to the builder. The created {@linkplain Report} contains only
   * {@linkplain Task Tasks} with a workbasketId in this list.
   *
   * @param workbasketIds a list of workbasket ids
   * @return the TimeIntervalReportBuilder
   */
  B workbasketIdIn(List<String> workbasketIds);

  /**
   * Adds a list of states to the builder. The created {@linkplain Report} contains only {@linkplain
   * Task Tasks} with a state in this list.
   *
   * @param states a list of states
   * @return the TimeIntervalReportBuilder
   */
  B stateIn(List<TaskState> states);

  /**
   * Adds a list of classificationCategories to the builder. The created {@linkplain Report}
   * contains only {@linkplain Task Tasks} with a category in this list.
   *
   * @param classificationCategory a list of classificationCategories
   * @return the TimeIntervalReportBuilder
   */
  B classificationCategoryIn(List<String> classificationCategory);

  /**
   * Adds a list of classificationIds to the builder. The created {@linkplain Report} contains only
   * {@linkplain Task Tasks} with a classificationId in this list.
   *
   * @param classificationIds a list of classificationIds
   * @return the TimeIntervalReportBuilder
   */
  B classificationIdIn(List<String> classificationIds);

  /**
   * Adds a list of excludedClassificationIds to the builder. The created {@linkplain Report}
   * contains only {@linkplain Task Tasks} with a classificationId NOT in this list.
   *
   * @param excludedClassificationIds a list of excludedClassificationIds
   * @return the TimeIntervalReportBuilder
   */
  B excludedClassificationIdIn(List<String> excludedClassificationIds);

  /**
   * Adds a list of domains to the builder. The created {@linkplain Report} contains only
   * {@linkplain Task Tasks} with a domain in this list.
   *
   * @param domains a list of domains
   * @return the TimeIntervalReportBuilder
   */
  B domainIn(List<String> domains);

  /**
   * Adds the values of a certain {@linkplain TaskCustomField} for exact matching to the builder.
   *
   * <p>The created {@linkplain Report} contains only {@linkplain Task Tasks} with a {@linkplain
   * Task#getCustomField(TaskCustomField) custom attribute} value exactly matching one of the items
   * in the List.
   *
   * @param customField the specified {@linkplain TaskCustomField}
   * @param strings the values the specified {@linkplain Task#getCustomField(TaskCustomField) custom
   *     attribute} should match
   * @return the modified {@linkplain TimeIntervalReportBuilder}
   * @throws InvalidArgumentException if filter values are not given
   */
  B customAttributeIn(TaskCustomField customField, String... strings)
      throws InvalidArgumentException;

  /**
   * Excludes the values of a certain {@linkplain TaskCustomField} to the builder.
   *
   * <p>The created {@linkplain Report} contains only {@linkplain Task Tasks} with a {@linkplain
   * Task#getCustomField(TaskCustomField) custom attribute} value not matching one of the items in
   * the List.
   *
   * @param customField the specified {@linkplain TaskCustomField}
   * @param strings the values the specified {@linkplain Task#getCustomField(TaskCustomField) custom
   *     attribute} should not match
   * @return the modified TimeIntervalReportBuilder
   * @throws InvalidArgumentException if filter values are not given
   */
  B customAttributeNotIn(TaskCustomField customField, String... strings)
      throws InvalidArgumentException;

  /**
   * Adds the values of a certain {@linkplain TaskCustomField} for pattern matching to the builder.
   *
   * <p>The created {@linkplain Report} contains only {@linkplain Task Tasks} with a {@linkplain
   * Task#getCustomField(TaskCustomField) custom attribute} value pattern-matching one of the items
   * in the list. They will be compared in SQL with the LIKE operator. You may use a wildcard like %
   * to specify the pattern. If you specify multiple arguments they are combined with the OR
   * keyword.
   *
   * @param customField the specified {@linkplain TaskCustomField}
   * @param strings the values the specified {@linkplain Task#getCustomField(TaskCustomField) custom
   *     attribute} should match
   * @return the modified {@linkplain TimeIntervalReportBuilder}
   * @throws InvalidArgumentException if filter values are not given
   */
  B customAttributeLike(TaskCustomField customField, String... strings)
      throws InvalidArgumentException;

  /**
   * Returns a list of all taskIds of the {@linkplain Report} that are in the list of selected
   * items.
   *
   * @param selectedItems a list of selectedItems
   * @param timestamp the {@linkplain TaskTimestamp} of interest
   * @return the list of all taskIds
   * @throws InvalidArgumentException if the column headers are not initialized
   * @throws NotAuthorizedException if the user has no rights to access the monitor
   */
  List<String> listTaskIdsForSelectedItems(
      List<SelectedItem> selectedItems, TaskTimestamp timestamp)
      throws NotAuthorizedException, InvalidArgumentException;

  /**
   * Returns a list of all values of an entered custom field that are in the {@linkplain Report}.
   *
   * @param taskCustomField the {@linkplain TaskCustomField} whose values should appear in the list
   * @return the list of all custom attribute values
   * @throws NotAuthorizedException if the user has no rights to access the monitor
   */
  List<String> listCustomAttributeValuesForCustomAttributeName(TaskCustomField taskCustomField)
      throws NotAuthorizedException;

  /**
   * Builds the {@linkplain Report} for the specified {@linkplain TaskTimestamp}.
   *
   * @param timestamp the {@linkplain TaskTimestamp} of interest
   * @return the build {@linkplain Report}
   * @throws NotAuthorizedException if the user has no rights to access the monitor
   * @throws InvalidArgumentException if an error occurs
   */
  Report<I, H> buildReport(TaskTimestamp timestamp)
      throws NotAuthorizedException, InvalidArgumentException;
}
