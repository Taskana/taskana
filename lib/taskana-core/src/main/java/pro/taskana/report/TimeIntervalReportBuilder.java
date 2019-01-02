package pro.taskana.report;

import java.util.List;
import java.util.Map;

import pro.taskana.CustomField;
import pro.taskana.TaskState;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.SelectedItem;
import pro.taskana.impl.report.header.TimeIntervalColumnHeader;
import pro.taskana.impl.report.item.DateQueryItem;
import pro.taskana.report.structure.Report;

/**
 * "Super" Interface for all TimeIntervalReportBuilders.
 * @param <B> the true Builder behind this Interface.
 * @param <I> the DateQueryItem which will be inserted into the Report.
 * @param <H> the column Header
 */
public interface TimeIntervalReportBuilder<B extends TimeIntervalReportBuilder<B, I, H>, I extends DateQueryItem, H extends TimeIntervalColumnHeader>
    extends Report.Builder<I, H> {

    /**
     * Adds a list {@link TimeIntervalColumnHeader}s to the builder to subdivide the report into clusters.
     *
     * @param columnHeaders
     *            the column headers the report should consist of.
     * @return the TimeIntervalReportBuilder
     */
    B withColumnHeaders(List<H> columnHeaders);

    /**
     * If this filter is used, the days of the report are counted in working days.
     *
     * @return the TimeIntervalReportBuilder
     */
    B inWorkingDays();

    /**
     * Adds a list of workbasket ids to the builder. The created report contains only tasks with a workbasket id in this
     * list.
     *
     * @param workbasketIds
     *            a list of workbasket ids
     * @return the TimeIntervalReportBuilder
     */
    B workbasketIdIn(List<String> workbasketIds);

    /**
     * Adds a list of states to the builder. The created report contains only tasks with a state in this list.
     *
     * @param states
     *            a list of states
     * @return the TimeIntervalReportBuilder
     */
    B stateIn(List<TaskState> states);

    /**
     * Adds a list of categories to the builder. The created report contains only tasks with a category in this list.
     *
     * @param categories
     *            a list of categories
     * @return the TimeIntervalReportBuilder
     */
    B categoryIn(List<String> categories);

    /**
     * Adds a list of classificationIds to the builder. The created report contains only tasks with a classificationId
     * in this list.
     *
     * @param classificationIds
     *            a list of classificationIds
     * @return the TimeIntervalReportBuilder
     */
    B classificationIdIn(List<String> classificationIds);

    /**
     * Adds a list of excludedClassificationIds to the builder. The created report contains only tasks with a
     * classificationId NOT in this list.
     *
     * @param excludedClassificationIds
     *            a list of excludedClassificationIds
     * @return the TimeIntervalReportBuilder
     */
    B excludedClassificationIdIn(List<String> excludedClassificationIds);

    /**
     * Adds a list of domains to the builder. The created report contains only tasks with a domain in this list.
     *
     * @param domains
     *            a list of domains
     * @return the TimeIntervalReportBuilder
     */
    B domainIn(List<String> domains);

    /**
     * Adds a map of custom attributes and custom attribute values to the builder. The created report contains only
     * tasks with a custom attribute value in this list.
     *
     * @param customAttributeFilter
     *            a map of custom attributes and custom attribute value
     * @return the TimeIntervalReportBuilder
     */
    B customAttributeFilterIn(Map<CustomField, String> customAttributeFilter);

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
