package pro.taskana;

import java.util.List;
import java.util.Map;

import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.impl.SelectedItem;
import pro.taskana.impl.report.impl.CategoryReport;
import pro.taskana.impl.report.impl.ClassificationReport;
import pro.taskana.impl.report.impl.CustomFieldValueReport;
import pro.taskana.impl.report.impl.DetailedClassificationReport;
import pro.taskana.impl.report.impl.TaskStatusReport;
import pro.taskana.impl.report.impl.TimeIntervalColumnHeader;
import pro.taskana.impl.report.impl.WorkbasketLevelReport;

/**
 * The Task Monitor Service manages operations on tasks regarding the monitoring.
 */
public interface TaskMonitorService {

    /**
     * Returns a {@link WorkbasketLevelReport} grouped by workbaskets. The report contains the total numbers of tasks of
     * the respective workbasket as well as the total number of all tasks. If no filter is required, the respective
     * parameter should be null. The tasks of the report are filtered by workbaskets, states, categories, domains and
     * values of a custom field. Tasks with Timestamp DUE = null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids to filter by workbaskets. To omit this filter, use null for this parameter
     * @param states
     *            a list of states to filter by states. To omit this filter, use null for this parameter
     * @param categories
     *            a list of categories to filter by categories. To omit this filter, use null for this parameter
     * @param domains
     *            a list of domains to filter by domains. To omit this filter, use null for this parameter
     * @param customField
     *            a custom field to filter by the values of the custom field. To omit this filter, use null for this
     *            parameter
     * @param customFieldValues
     *            a list of custom field values to filter by the values of the custom field. To omit this filter, use
     *            null for this parameter
     * @return the report
     * @throws InvalidArgumentException
     *             thrown if DaysToWorkingDaysConverter is initialized with null
     */
    WorkbasketLevelReport getWorkbasketLevelReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues)
        throws InvalidArgumentException;

    /**
     * Returns a {@link WorkbasketLevelReport} grouped by workbaskets. For each workbasket the report contains the total
     * number of tasks and the number of tasks of the respective cluster that are specified by the
     * {@link TimeIntervalColumnHeader}s. By default the age of the tasks is counted in working days. Furthermore the
     * Report contains a sum line that contains the total numbers of the different clusters and the total number of all
     * tasks in this report. The tasks of the report are filtered by workbaskets, states, categories, domains and values
     * of a custom field. If no filter is required, the respective parameter should be null. Tasks with Timestamp DUE =
     * null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids objects to filter by workbaskets. To omit this filter, use null for this
     *            parameter
     * @param states
     *            a list of states objects to filter by states. To omit this filter, use null for this parameter
     * @param categories
     *            a list of categories to filter by categories. To omit this filter, use null for this parameter
     * @param domains
     *            a list of domains to filter by domains. To omit this filter, use null for this parameter
     * @param customField
     *            a custom field to filter by the values of the custom field. To omit this filter, use null for this
     *            parameter
     * @param customFieldValues
     *            a list of custom field values to filter by the values of the custom field. To omit this filter, use
     *            null for this parameter
     * @param columnHeaders
     *            a list of columnHeaders that specify the subdivision into different cluster of due dates. Days in past
     *            are represented as negative values and days in the future are represented as positive values. To avoid
     *            tasks are counted multiple times or not be listed in the report, these columnHeaders should not
     *            overlap and should not have gaps. If the ReportLineDefinition should represent a single day,
     *            lowerLimit and upperLimit have to be equal. The outer cluster of a report should have open ends. These
     *            open ends are represented with Integer.MIN_VALUE and Integer.MAX_VALUE.
     * @return the report
     * @throws InvalidArgumentException
     *             thrown if DaysToWorkingDaysConverter is initialized with null
     */
    WorkbasketLevelReport getWorkbasketLevelReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders) throws InvalidArgumentException;

    /**
     * Returns a {@link WorkbasketLevelReport} grouped by workbaskets. For each workbasket the report contains the total
     * number of tasks and the number of tasks of the respective cluster that are specified by the
     * {@link TimeIntervalColumnHeader}s. It can be specified whether the age of the tasks is counted in days or in
     * working days. Furthermore the report contains a sum line that contains the total numbers of the different
     * clusters and the total number of all tasks. The tasks of the report are filtered by workbaskets, states,
     * categories, domains and values of a custom field. If no filter is required, the respective parameter should be
     * null. Tasks with Timestamp DUE = null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids objects to filter by workbaskets. To omit this filter, use null for this
     *            parameter
     * @param states
     *            a list of states objects to filter by states. To omit this filter, use null for this parameter
     * @param categories
     *            a list of categories to filter by categories. To omit this filter, use null for this parameter
     * @param domains
     *            a list of domains to filter by domains. To omit this filter, use null for this parameter
     * @param customField
     *            a custom field to filter by the values of the custom field. To omit this filter, use null for this
     *            parameter
     * @param customFieldValues
     *            a list of custom field values to filter by the values of the custom field. To omit this filter, use
     *            null for this parameter
     * @param columnHeaders
     *            a list of columnHeaders that specify the subdivision into different cluster of due dates. Days in past
     *            are represented as negative values and days in the future are represented as positive values. To avoid
     *            tasks are counted multiple times or not be listed in the report, these columnHeaders should not
     *            overlap and should not have gaps. If the ReportLineDefinition should represent a single day,
     *            lowerLimit and upperLimit have to be equal. The outer cluster of a report should have open ends. These
     *            open ends are represented with Integer.MIN_VALUE and Integer.MAX_VALUE.
     * @param inWorkingDays
     *            a boolean parameter that specifies whether the age of the tasks should be counted in days or in
     *            working days
     * @return the report
     * @throws InvalidArgumentException
     *             thrown if DaysToWorkingDaysConverter is initialized with null
     */
    WorkbasketLevelReport getWorkbasketLevelReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders, boolean inWorkingDays)
        throws InvalidArgumentException;

    /**
     * Returns a {@link CategoryReport} grouped by categories. The report contains the total numbers of tasks of the
     * respective category as well as the total number of all tasks. The tasks of the report are filtered by
     * workbaskets, states, categories, domains and values of a custom field and values of a custom field. If no filter
     * is required, the respective parameter should be null. Tasks with Timestamp DUE = null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids to filter by workbaskets. To omit this filter, use null for this parameter
     * @param states
     *            a list of states to filter by states. To omit this filter, use null for this parameter
     * @param categories
     *            a list of categories to filter by categories. To omit this filter, use null for this parameter
     * @param domains
     *            a list of domains to filter by domains. To omit this filter, use null for this parameter
     * @param customField
     *            a custom field to filter by the values of the custom field. To omit this filter, use null for this
     *            parameter
     * @param customFieldValues
     *            a list of custom field values to filter by the values of the custom field. To omit this filter, use
     *            null for this parameter
     * @return the report
     * @throws InvalidArgumentException
     *             thrown if DaysToWorkingDaysConverter is initialized with null
     */
    CategoryReport getCategoryReport(List<String> workbasketIds, List<TaskState> states, List<String> categories,
        List<String> domains, CustomField customField, List<String> customFieldValues) throws InvalidArgumentException;

    /**
     * Returns a {@link CategoryReport} grouped by categories. For each category the report contains the total number of
     * tasks and the number of tasks of the respective cluster that are specified by the
     * {@link TimeIntervalColumnHeader}s. By default the age of the tasks is counted in working days. Furthermore the
     * Report contains a sum line that contains the total numbers of the different clusters and the total number of all
     * tasks in this report. The tasks of the report are filtered by workbaskets, states, categories, domains and values
     * of a custom field. If no filter is required, the respective parameter should be null. Tasks with Timestamp DUE =
     * null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids objects to filter by workbaskets. To omit this filter, use null for this
     *            parameter
     * @param states
     *            a list of states objects to filter by states. To omit this filter, use null for this parameter
     * @param categories
     *            a list of categories to filter by categories. To omit this filter, use null for this parameter
     * @param domains
     *            a list of domains to filter by domains. To omit this filter, use null for this parameter
     * @param customField
     *            a custom field to filter by the values of the custom field. To omit this filter, use null for this
     *            parameter
     * @param customFieldValues
     *            a list of custom field values to filter by the values of the custom field. To omit this filter, use
     *            null for this parameter
     * @param columnHeaders
     *            a list of columnHeaders that specify the subdivision into different cluster of due dates. Days in past
     *            are represented as negative values and days in the future are represented as positive values. To avoid
     *            tasks are counted multiple times or not be listed in the report, these columnHeaders should not
     *            overlap and should not have gaps. If the ReportLineDefinition should represent a single day,
     *            lowerLimit and upperLimit have to be equal. The outer cluster of a report should have open ends. These
     *            open ends are represented with Integer.MIN_VALUE and Integer.MAX_VALUE.
     * @return the report
     * @throws InvalidArgumentException
     *             thrown if DaysToWorkingDaysConverter is initialized with null
     */
    CategoryReport getCategoryReport(List<String> workbasketIds, List<TaskState> states, List<String> categories,
        List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders) throws InvalidArgumentException;

    /**
     * Returns a {@link CategoryReport} grouped by categories. For each category the report contains the total number of
     * tasks and the number of tasks of the respective cluster that are specified by the
     * {@link TimeIntervalColumnHeader}s. It can be specified whether the age of the tasks is counted in days or in
     * working days. Furthermore the report contains a sum line that contains the total numbers of the different
     * clusters and the total number of all tasks. The tasks of the report are filtered by workbaskets, states,
     * categories, domains and values of a custom field. If no filter is required, the respective parameter should be
     * null. Tasks with Timestamp DUE = null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids objects to filter by workbaskets. To omit this filter, use null for this
     *            parameter
     * @param states
     *            a list of states objects to filter by states. To omit this filter, use null for this parameter
     * @param categories
     *            a list of categories to filter by categories. To omit this filter, use null for this parameter
     * @param domains
     *            a list of domains to filter by domains. To omit this filter, use null for this parameter
     * @param customField
     *            a custom field to filter by the values of the custom field. To omit this filter, use null for this
     *            parameter
     * @param customFieldValues
     *            a list of custom field values to filter by the values of the custom field. To omit this filter, use
     *            null for this parameter
     * @param columnHeaders
     *            a list of columnHeaders that specify the subdivision into different cluster of due dates. Days in past
     *            are represented as negative values and days in the future are represented as positive values. To avoid
     *            tasks are counted multiple times or not be listed in the report, these columnHeaders should not
     *            overlap and should not have gaps. If the ReportLineDefinition should represent a single day,
     *            lowerLimit and upperLimit have to be equal. The outer cluster of a report should have open ends. These
     *            open ends are represented with Integer.MIN_VALUE and Integer.MAX_VALUE.
     * @param inWorkingDays
     *            a boolean parameter that specifies whether the age of the tasks should be counted in days or in
     *            working days
     * @return the report
     * @throws InvalidArgumentException
     *             thrown if DaysToWorkingDaysConverter is initialized with null
     */
    CategoryReport getCategoryReport(List<String> workbasketIds, List<TaskState> states, List<String> categories,
        List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders, boolean inWorkingDays)
        throws InvalidArgumentException;

    /**
     * Returns a {@link ClassificationReport} grouped by classifications. The report contains the total numbers of tasks
     * of the respective classification as well as the total number of all tasks. The tasks of the report are filtered
     * by workbaskets, states, categories, domains and values of a custom field. If no filter is required, the
     * respective parameter should be null. Tasks with Timestamp DUE = null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids to filter by workbaskets. To omit this filter, use null for this parameter
     * @param states
     *            a list of states to filter by states. To omit this filter, use null for this parameter
     * @param categories
     *            a list of categories to filter by categories. To omit this filter, use null for this parameter
     * @param domains
     *            a list of domains to filter by domains. To omit this filter, use null for this parameter
     * @param customField
     *            a custom field to filter by the values of the custom field. To omit this filter, use null for this
     *            parameter
     * @param customFieldValues
     *            a list of custom field values to filter by the values of the custom field. To omit this filter, use
     *            null for this parameter
     * @return the ClassificationReport
     * @throws InvalidArgumentException
     *             thrown if DaysToWorkingDaysConverter is initialized with null
     */
    ClassificationReport getClassificationReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues)
        throws InvalidArgumentException;

    /**
     * Returns a {@link ClassificationReport} grouped by classifications. For each classification the report contains
     * the total number of tasks and the number of tasks of the respective cluster that are specified by the
     * {@link TimeIntervalColumnHeader}s. By default the age of the tasks is counted in working days. Furthermore the
     * Report contains a sum line that contains the total numbers of the different clusters and the total number of all
     * tasks in this report. The tasks of the report are filtered by workbaskets, states, categories, domains and values
     * of a custom field. If no filter is required, the respective parameter should be null. Tasks with Timestamp DUE =
     * null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids objects to filter by workbaskets. To omit this filter, use null for this
     *            parameter
     * @param states
     *            a list of states objects to filter by states. To omit this filter, use null for this parameter
     * @param categories
     *            a list of categories to filter by categories. To omit this filter, use null for this parameter
     * @param domains
     *            a list of domains to filter by domains. To omit this filter, use null for this parameter
     * @param customField
     *            a custom field to filter by the values of the custom field. To omit this filter, use null for this
     *            parameter
     * @param customFieldValues
     *            a list of custom field values to filter by the values of the custom field. To omit this filter, use
     *            null for this parameter
     * @param columnHeaders
     *            a list of columnHeaders that specify the subdivision into different cluster of due dates. Days in past
     *            are represented as negative values and days in the future are represented as positive values. To avoid
     *            tasks are counted multiple times or not be listed in the report, these columnHeaders should not
     *            overlap and should not have gaps. If the ReportLineDefinition should represent a single day,
     *            lowerLimit and upperLimit have to be equal. The outer cluster of a report should have open ends. These
     *            open ends are represented with Integer.MIN_VALUE and Integer.MAX_VALUE.
     * @return the ClassificationReport
     * @throws InvalidArgumentException
     *             thrown if DaysToWorkingDaysConverter is initialized with null
     */
    ClassificationReport getClassificationReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders) throws InvalidArgumentException;

    /**
     * Returns a {@link ClassificationReport} grouped by classification. For each classification the report contains the
     * total number of tasks and the number of tasks of the respective cluster that are specified by the
     * {@link TimeIntervalColumnHeader}s. It can be specified whether the age of the tasks is counted in days or in
     * working days. Furthermore the report contains a sum line that contains the total numbers of the different
     * clusters and the total number of all tasks. The tasks of the report are filtered by workbaskets, states,
     * categories, domains and values of a custom field. If no filter is required, the respective parameter should be
     * null. Tasks with Timestamp DUE = null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids objects to filter by workbaskets. To omit this filter, use null for this
     *            parameter
     * @param states
     *            a list of states objects to filter by states. To omit this filter, use null for this parameter
     * @param categories
     *            a list of categories to filter by categories. To omit this filter, use null for this parameter
     * @param domains
     *            a list of domains to filter by domains. To omit this filter, use null for this parameter
     * @param customField
     *            a custom field to filter by the values of the custom field. To omit this filter, use null for this
     *            parameter
     * @param customFieldValues
     *            a list of custom field values to filter by the values of the custom field. To omit this filter, use
     *            null for this parameter
     * @param columnHeaders
     *            a list of columnHeaders that specify the subdivision into different cluster of due dates. Days in past
     *            are represented as negative values and days in the future are represented as positive values. To avoid
     *            tasks are counted multiple times or not be listed in the report, these columnHeaders should not
     *            overlap and should not have gaps. If the ReportLineDefinition should represent a single day,
     *            lowerLimit and upperLimit have to be equal. The outer cluster of a report should have open ends. These
     *            open ends are represented with Integer.MIN_VALUE and Integer.MAX_VALUE.
     * @param inWorkingDays
     *            a boolean parameter that specifies whether the age of the tasks should be counted in days or in
     *            working days
     * @return the ClassificationReport
     * @throws InvalidArgumentException
     *             thrown if DaysToWorkingDaysConverter is initialized with null
     */
    ClassificationReport getClassificationReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders, boolean inWorkingDays)
        throws InvalidArgumentException;

    /**
     * Returns a {@link DetailedClassificationReport}. The report contains the total numbers of tasks of the respective
     * classification as well as the total number of all tasks. Each ReportLine contains an additional list of
     * ReportLines for the classifications of the attachments of the tasks. The tasks of the report are filtered by
     * workbaskets, states, categories, domains and values of a custom field. If no filter is required, the respective
     * parameter should be null. Tasks with Timestamp DUE = null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids to filter by workbaskets. To omit this filter, use null for this parameter
     * @param states
     *            a list of states to filter by states. To omit this filter, use null for this parameter
     * @param categories
     *            a list of categories to filter by categories. To omit this filter, use null for this parameter
     * @param domains
     *            a list of domains to filter by domains. To omit this filter, use null for this parameter
     * @param customField
     *            a custom field to filter by the values of the custom field. To omit this filter, use null for this
     *            parameter
     * @param customFieldValues
     *            a list of custom field values to filter by the values of the custom field. To omit this filter, use
     *            null for this parameter
     * @return the DetailedClassificationReport
     * @throws InvalidArgumentException
     *             thrown if DaysToWorkingDaysConverter is initialized with null
     */
    DetailedClassificationReport getDetailedClassificationReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues)
        throws InvalidArgumentException;

    /**
     * Returns a {@link DetailedClassificationReport}. For each classification the report contains the total number of
     * tasks and the number of tasks of the respective cluster that are specified by the
     * {@link TimeIntervalColumnHeader}s. By default the age of the tasks is counted in working days. Each ReportLine
     * contains an additional list of ReportLines for the classifications of the attachments of the tasks. Furthermore
     * the Report contains a sum line that contains the total numbers of the different clusters and the total number of
     * all tasks in this report. The tasks of the report are filtered by workbaskets, states, categories, domains and
     * values of a custom field. If no filter is required, the respective parameter should be null. Tasks with Timestamp
     * DUE = null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids objects to filter by workbaskets. To omit this filter, use null for this
     *            parameter
     * @param states
     *            a list of states objects to filter by states. To omit this filter, use null for this parameter
     * @param categories
     *            a list of categories to filter by categories. To omit this filter, use null for this parameter
     * @param domains
     *            a list of domains to filter by domains. To omit this filter, use null for this parameter
     * @param customField
     *            a custom field to filter by the values of the custom field. To omit this filter, use null for this
     *            parameter
     * @param customFieldValues
     *            a list of custom field values to filter by the values of the custom field. To omit this filter, use
     *            null for this parameter
     * @param columnHeaders
     *            a list of columnHeaders that specify the subdivision into different cluster of due dates. Days in past
     *            are represented as negative values and days in the future are represented as positive values. To avoid
     *            tasks are counted multiple times or not be listed in the report, these columnHeaders should not
     *            overlap and should not have gaps. If the ReportLineDefinition should represent a single day,
     *            lowerLimit and upperLimit have to be equal. The outer cluster of a report should have open ends. These
     *            open ends are represented with Integer.MIN_VALUE and Integer.MAX_VALUE.
     * @return the DetailedClassificationReport
     * @throws InvalidArgumentException
     *             thrown if DaysToWorkingDaysConverter is initialized with null
     */
    DetailedClassificationReport getDetailedClassificationReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders) throws InvalidArgumentException;

    /**
     * Returns a {@link DetailedClassificationReport}. For each classification the report contains the total number of
     * tasks and the number of tasks of the respective cluster that are specified by the
     * {@link TimeIntervalColumnHeader}s. It can be specified whether the age of the tasks is counted in days or in
     * working days. Each ReportLine contains an additional list of ReportLines for the classifications of the
     * attachments of the tasks. Furthermore the report contains a sum line that contains the total numbers of the
     * different clusters and the total number of all tasks. The tasks of the report are filtered by workbaskets,
     * states, categories, domains and values of a custom field. If no filter is required, the respective parameter
     * should be null. Tasks with Timestamp DUE = null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids objects to filter by workbaskets. To omit this filter, use null for this
     *            parameter
     * @param states
     *            a list of states objects to filter by states. To omit this filter, use null for this parameter
     * @param categories
     *            a list of categories to filter by categories. To omit this filter, use null for this parameter
     * @param domains
     *            a list of domains to filter by domains. To omit this filter, use null for this parameter
     * @param customField
     *            a custom field to filter by the values of the custom field. To omit this filter, use null for this
     *            parameter
     * @param customFieldValues
     *            a list of custom field values to filter by the values of the custom field. To omit this filter, use
     *            null for this parameter
     * @param columnHeaders
     *            a list of columnHeaders that specify the subdivision into different cluster of due dates. Days in past
     *            are represented as negative values and days in the future are represented as positive values. To avoid
     *            tasks are counted multiple times or not be listed in the report, these columnHeaders should not
     *            overlap and should not have gaps. If the ReportLineDefinition should represent a single day,
     *            lowerLimit and upperLimit have to be equal. The outer cluster of a report should have open ends. These
     *            open ends are represented with Integer.MIN_VALUE and Integer.MAX_VALUE.
     * @param inWorkingDays
     *            a boolean parameter that specifies whether the age of the tasks should be counted in days or in
     *            working days
     * @return the DetailedClassificationReport
     * @throws InvalidArgumentException
     *             thrown if DaysToWorkingDaysConverter is initialized with null
     */
    DetailedClassificationReport getDetailedClassificationReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders, boolean inWorkingDays)
        throws InvalidArgumentException;

    /**
     * Returns a {@link CustomFieldValueReport} grouped by the value of a certain {@link CustomField}. The report
     * contains the total numbers of tasks of the respective custom field as well as the total number of all tasks. The
     * tasks of the report are filtered by workbaskets, states, categories, domains and values of a custom field. If no
     * filter is required, the respective parameter should be null. Tasks with Timestamp DUE = null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids to filter by workbaskets. To omit this filter, use null for this parameter
     * @param states
     *            a list of states to filter by states. To omit this filter, use null for this parameter
     * @param categories
     *            a list of categories to filter by categories. To omit this filter, use null for this parameter
     * @param domains
     *            a list of domains to filter by domains. To omit this filter, use null for this parameter
     * @param customField
     *            a custom field to filter by the values of the custom field
     * @param customFieldValues
     *            a list of custom field values to filter by the values of the custom field. To omit this filter, use
     *            null for this parameter
     * @return the report
     * @throws InvalidArgumentException
     *             thrown if customField is null
     */
    CustomFieldValueReport getCustomFieldValueReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues)
        throws InvalidArgumentException;

    /**
     * Returns a {@link CustomFieldValueReport} grouped by the value of a certain {@link CustomField}. For each value of
     * the custom field the report contains the total number of tasks and the number of tasks of the respective cluster
     * that are specified by the {@link TimeIntervalColumnHeader}s. By default the age of the tasks is counted in
     * working days. Furthermore the Report contains a sum line that contains the total numbers of the different
     * clusters and the total number of all tasks in this report. The tasks of the report are filtered by workbaskets,
     * states, categories, domains and values of a custom field. If no filter is required, the respective parameter
     * should be null. Tasks with Timestamp DUE = null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids objects to filter by workbaskets. To omit this filter, use null for this
     *            parameter
     * @param states
     *            a list of states objects to filter by states. To omit this filter, use null for this parameter
     * @param categories
     *            a list of categories to filter by categories. To omit this filter, use null for this parameter
     * @param domains
     *            a list of domains to filter by domains. To omit this filter, use null for this parameter
     * @param customField
     *            a custom field to filter by the values of the custom field
     * @param customFieldValues
     *            a list of custom field values to filter by the values of the custom field. To omit this filter, use
     *            null for this parameter
     * @param columnHeaders
     *            a list of columnHeaders that specify the subdivision into different cluster of due dates. Days in past
     *            are represented as negative values and days in the future are represented as positive values. To avoid
     *            tasks are counted multiple times or not be listed in the report, these columnHeaders should not
     *            overlap and should not have gaps. If the ReportLineDefinition should represent a single day,
     *            lowerLimit and upperLimit have to be equal. The outer cluster of a report should have open ends. These
     *            open ends are represented with Integer.MIN_VALUE and Integer.MAX_VALUE.
     * @return the report
     * @throws InvalidArgumentException
     *             thrown if customField is null
     */
    CustomFieldValueReport getCustomFieldValueReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders) throws InvalidArgumentException;

    /**
     * Returns a {@link CustomFieldValueReport} grouped by the value of a certain {@link CustomField}. For each value of
     * the custom field the report contains the total number of tasks and the number of tasks of the respective cluster
     * that are specified by the {@link TimeIntervalColumnHeader}s. It can be specified whether the age of the tasks is
     * counted in days or in working days. Furthermore the report contains a sum line that contains the total numbers of
     * the different clusters and the total number of all tasks. The tasks of the report are filtered by workbaskets,
     * states, categories, domains and values of a custom field. If no filter is required, the respective parameter
     * should be null. Tasks with Timestamp DUE = null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids objects to filter by workbaskets. To omit this filter, use null for this
     *            parameter
     * @param states
     *            a list of states objects to filter by states. To omit this filter, use null for this parameter
     * @param categories
     *            a list of categories to filter by categories. To omit this filter, use null for this parameter
     * @param domains
     *            a list of domains to filter by domains. To omit this filter, use null for this parameter
     * @param customField
     *            a custom field to filter by the values of the custom field
     * @param customFieldValues
     *            a list of custom field values to filter by the values of the custom field. To omit this filter, use
     *            null for this parameter
     * @param columnHeaders
     *            a list of columnHeaders that specify the subdivision into different cluster of due dates. Days in past
     *            are represented as negative values and days in the future are represented as positive values. To avoid
     *            tasks are counted multiple times or not be listed in the report, these columnHeaders should not
     *            overlap and should not have gaps. If the ReportLineDefinition should represent a single day,
     *            lowerLimit and upperLimit have to be equal. The outer cluster of a report should have open ends. These
     *            open ends are represented with Integer.MIN_VALUE and Integer.MAX_VALUE.
     * @param inWorkingDays
     *            a boolean parameter that specifies whether the age of the tasks should be counted in days or in
     *            working days
     * @return the report
     * @throws InvalidArgumentException
     *             thrown if customField is null
     */
    CustomFieldValueReport getCustomFieldValueReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories,
        List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders, boolean inWorkingDays)
        throws InvalidArgumentException;

    /**
     * Returns a list of all task ids in the selected items of a {@link pro.taskana.impl.report.Report}. By default the
     * age of the tasks is counted in working days. The tasks of the report are filtered by workbaskets, states,
     * categories, domains and values of a custom field. If no filter is required, the respective parameter should be
     * null. Tasks with Timestamp DUE = null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids objects to filter by workbaskets. To omit this filter, use null for this
     *            parameter
     * @param states
     *            a list of states objects to filter by states. To omit this filter, use null for this parameter
     * @param categories
     *            a list of categories to filter by categories. To omit this filter, use null for this parameter
     * @param domains
     *            a list of domains to filter by domains. To omit this filter, use null for this parameter
     * @param customField
     *            a custom field to filter by the values of the custom field. To omit this filter, use null for this
     *            parameter
     * @param customFieldValues
     *            a list of custom field values to filter by the values of the custom field. To omit this filter, use
     *            null for this parameter
     * @param columnHeaders
     *            a list of columnHeaders that specify the subdivision into different cluster of due dates. Days in past
     *            are represented as negative values and days in the future are represented as positive values. To avoid
     *            tasks are counted multiple times or not be listed in the report, these columnHeaders should not
     *            overlap and should not have gaps. If the ReportLineDefinition should represent a single day,
     *            lowerLimit and upperLimit have to be equal. The outer cluster of a report should have open ends. These
     *            open ends are represented with Integer.MIN_VALUE and Integer.MAX_VALUE.
     * @param selectedItems
     *            a list of {@link SelectedItem}s that are selected from the report whose task ids should be determined.
     * @return the list of task ids
     * @throws InvalidArgumentException
     *             thrown if columnHeaders is null or if selectedItems is empty or null
     */
    List<String> getTaskIdsOfCategoryReportLineItems(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders, List<SelectedItem> selectedItems)
        throws InvalidArgumentException;

    /**
     * Returns a list of all task ids in the selected items of a {@link pro.taskana.impl.report.Report}. By default the
     * age of the tasks is counted in working days. The tasks of the report are filtered by workbaskets, states,
     * categories, domains and values of a custom field. If no filter is required, the respective parameter should be
     * null. Tasks with Timestamp DUE = null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids objects to filter by workbaskets. To omit this filter, use null for this
     *            parameter
     * @param states
     *            a list of states objects to filter by states. To omit this filter, use null for this parameter
     * @param categories
     *            a list of categories to filter by categories. To omit this filter, use null for this parameter
     * @param domains
     *            a list of domains to filter by domains. To omit this filter, use null for this parameter
     * @param customField
     *            a custom field to filter by the values of the custom field. To omit this filter, use null for this
     *            parameter
     * @param customFieldValues
     *            a list of custom field values to filter by the values of the custom field. To omit this filter, use
     *            null for this parameter
     * @param columnHeaders
     *            a list of columnHeaders that specify the subdivision into different cluster of due dates. Days in past
     *            are represented as negative values and days in the future are represented as positive values. To avoid
     *            tasks are counted multiple times or not be listed in the report, these columnHeaders should not
     *            overlap and should not have gaps. If the ReportLineDefinition should represent a single day,
     *            lowerLimit and upperLimit have to be equal. The outer cluster of a report should have open ends. These
     *            open ends are represented with Integer.MIN_VALUE and Integer.MAX_VALUE.
     * @param inWorkingDays
     *            a boolean parameter that specifies whether the age of the tasks should be counted in days or in
     *            working days
     * @param selectedItems
     *            a list of {@link SelectedItem}s that are selected from the report whose task ids should be determined.
     * @return the list of task ids
     * @throws InvalidArgumentException
     *             thrown if columnHeaders is null or if selectedItems is empty or null
     */
    List<String> getTaskIdsOfCategoryReportLineItems(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders, boolean inWorkingDays,
        List<SelectedItem> selectedItems) throws InvalidArgumentException;

    /**
     * Returns a list of distinct custom attribute values for the selection from the entire task pool.
     *
     * @param workbasketIds
     *            a list of workbasket ids objects to filter by workbaskets. To omit this filter, use null for this
     *            parameter
     * @param states
     *            a list of states objects to filter by states. To omit this filter, use null for this parameter
     * @param categories
     *            a list of categories to filter by categories. To omit this filter, use null for this parameter
     * @param domains
     *            a list of domains to filter by domains. To omit this filter, use null for this parameter
     * @param classificationIds
     *            a List of all classification ids to include in the selection.
     * @param excludedClassificationIds
     *            a List of all classification ids to exclude from the selection.
     * @param customAttributeFilter
     *            a Map containing a key value pair for the custom attributes to be applied as a filter criteria
     * @param customAttributeName
     *            the name of the custom attribute to determine the existing values from.
     * @return the list of existing values for the custom attribute with name customAttributeName in the filtered task
     *         pool.
     * @throws InvalidArgumentException
     *             thrown if the customAttributeName is invalid/empty.
     */
    List<String> getCustomAttributeValuesForReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, List<String> classificationIds,
        List<String> excludedClassificationIds, Map<String, String> customAttributeFilter,
        String customAttributeName) throws InvalidArgumentException;

    /**
     * Overloaded method for {@link #getTaskStatusReport(List, List)}. This method omits all filters.
     *
     * @return the {@link TaskStatusReport}
     */
    TaskStatusReport getTaskStatusReport();

    /**
     * Overloaded method for {@link #getTaskStatusReport(List, List)}. This method applies a domain filter and omits the
     * state filter.
     *
     * @param domains
     *            a list of domains to filter by domains. To omit this filter, use null for this parameter
     * @return the {@link TaskStatusReport}
     */
    TaskStatusReport getTaskStatusReport(List<String> domains);

    /**
     * Returns a {@link TaskStatusReport}. For each domain the report contains the total number of tasks, clustered in
     * their task status. Furthermore the report contains a sum line that contains the total numbers of the different
     * clusters and the total number of all tasks.
     *
     * @param states
     *            a list of states objects to filter by states. To omit this filter, use null for this parameter
     * @param domains
     *            a list of domains to filter by domains. To omit this filter, use null for this parameter
     * @return the {@link TaskStatusReport}
     */
    TaskStatusReport getTaskStatusReport(List<String> domains, List<TaskState> states);

}
