package pro.taskana;

import java.util.List;
import java.util.Map;

import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.SelectedItem;
import pro.taskana.impl.report.impl.CategoryReport;
import pro.taskana.impl.report.impl.TimeIntervalColumnHeader;

/**
 * The CategoryReportBuilder is used to build a {@link CategoryReport}, list the taskIds of a CategoryReport and list
 * the values of an entered custom field. A CategoryReport contains the total numbers of tasks of the respective
 * category as well as the total number of all tasks. The tasks of the report can be filtered by workbaskets, states,
 * categories, domains, classifications and values of a custom field. Classifications can also be excluded from the
 * report. If the {@link TimeIntervalColumnHeader}s are set, the report contains also the number of tasks of the
 * respective cluster. The age of the tasks can be counted in days or in working days. Tasks with Timestamp DUE = null
 * are not considered.
 */
public interface CategoryReportBuilder {

    /**
     * Adds a list {@link TimeIntervalColumnHeader}s to the builder to subdivide the report into clusters.
     *
     * @param columnHeaders
     *            the column headers the report should consist of.
     * @return the CategoryReportBuilder
     */
    CategoryReportBuilder withColumnHeaders(List<TimeIntervalColumnHeader> columnHeaders);

    /**
     * If this filter is used, the days of the report are counted in working days.
     *
     * @return the CategoryReportBuilder
     */
    CategoryReportBuilder inWorkingDays();

    /**
     * Adds a list of workbasketIds to the builder. The created report contains only tasks with a workbasketId in this
     * list.
     *
     * @param workbasketIds
     *            a list of workbasketIds
     * @return the CategoryReportBuilder
     */
    CategoryReportBuilder workbasketIdIn(List<String> workbasketIds);

    /**
     * Adds a list of states to the builder. The created report contains only tasks with a state in this list.
     *
     * @param states
     *            a list of states
     * @return the CategoryReportBuilder
     */
    CategoryReportBuilder stateIn(List<TaskState> states);

    /**
     * Adds a list of categories to the builder. The created report contains only tasks with a category in this list.
     *
     * @param categories
     *            a list of categories
     * @return the CategoryReportBuilder
     */
    CategoryReportBuilder categoryIn(List<String> categories);

    /**
     * Adds a list of classificationIds to the builder. The created report contains only tasks with a classificationId
     * in this list.
     *
     * @param classificationIds
     *            a list of classificationIds
     * @return the CategoryReportBuilder
     */
    CategoryReportBuilder classificationIdIn(List<String> classificationIds);

    /**
     * Adds a list of excludedClassificationIds to the builder. The created report contains only tasks with a
     * classificationId NOT in this list.
     *
     * @param excludedClassificationIds
     *            a list of excludedClassificationIds
     * @return the CategoryReportBuilder
     */
    CategoryReportBuilder excludedClassificationIdIn(List<String> excludedClassificationIds);

    /**
     * Adds a list of domains to the builder. The created report contains only tasks with a domain in this list.
     *
     * @param domains
     *            a list of domains
     * @return the CategoryReportBuilder
     */
    CategoryReportBuilder domainIn(List<String> domains);

    /**
     * Adds a map of custom attributes and custom attribute values to the builder. The created report contains only
     * tasks with a custom attribute value in this list.
     *
     * @param customAttributeFilter
     *            a map of custom attributes and custom attribute value
     * @return the CategoryReportBuilder
     */
    CategoryReportBuilder customAttributeFilterIn(Map<CustomField, String> customAttributeFilter);

    /**
     * Returns a {@link CategoryReport} containing all tasks after applying the filters. If the column headers are set
     * the report is subdivided into clusters.
     *
     * @throws InvalidArgumentException
     *             if the column headers are not initialized
     * @throws NotAuthorizedException
     *             if the user has no rights to access the monitor
     * @return the CategoryReport
     */
    CategoryReport buildReport() throws InvalidArgumentException, NotAuthorizedException;

    /**
     * Returns a list of all taskIds of the report that are in the list of selected items.
     *
     * @param selectedItems
     *            a list of selectedItems
     * @throws InvalidArgumentException
     *             if the column headers are not initialized
     * @throws NotAuthorizedException
     *             if the user has no rights to access the monitor
     * @return the list of all taskIds
     */
    List<String> listTaskIdsForSelectedItems(List<SelectedItem> selectedItems)
        throws NotAuthorizedException, InvalidArgumentException;

    /**
     * Returns a list of all values of an entered custom field that are in the report.
     *
     * @param customField
     *            the customField whose values should appear in the list
     * @throws NotAuthorizedException
     *             if the user has no rights to access the monitor
     * @return the list of all custom attribute values
     */
    List<String> listCustomAttributeValuesForCustomAttributeName(CustomField customField)
        throws NotAuthorizedException;
}
