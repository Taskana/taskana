package pro.taskana;

import java.util.List;

import pro.taskana.impl.ClassificationReport;
import pro.taskana.impl.CustomField;
import pro.taskana.impl.DetailedClassificationReport;
import pro.taskana.impl.Report;
import pro.taskana.impl.ReportLineItemDefinition;

/**
 * The Task Monitor Service manages operations on tasks regarding the monitoring.
 */
public interface TaskMonitorService {

    /**
     * Returns a {@link Report} grouped by workbaskets. The report contains the total numbers of tasks of the respective
     * workbasket as well as the total number of all tasks. The tasks of the report are filtered by workbaskets, states,
     * categories and domains. Task with Timestamp DUE = null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids to filter by workbaskets
     * @param states
     *            a list of states to filter by states
     * @param categories
     *            a list of categories to filter by categories
     * @param domains
     *            a list of domains to filter by domains
     * @return the report
     */
    Report getWorkbasketLevelReport(List<String> workbasketIds, List<TaskState> states, List<String> categories,
        List<String> domains);

    /**
     * Returns a {@link Report} grouped by workbaskets. For each workbasket the report contains the total number of
     * tasks and the number of tasks of the respective cluster that are specified by the
     * {@link ReportLineItemDefinition}s. By default the age of the tasks is counted in working days. Furthermore the
     * Report contains a sum line that contains the total numbers of the different clusters and the total number of all
     * tasks in this report. The tasks of the report are filtered by workbaskets, states, categories and domains. Task
     * with Timestamp DUE = null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids objects to filter by workbaskets
     * @param states
     *            a list of states objects to filter by states
     * @param categories
     *            a list of categories to filter by categories
     * @param domains
     *            a list of domains to filter by domains
     * @param reportLineItemDefinitions
     *            a list of reportLineItemDefinitions that specify the subdivision into different cluster of due dates.
     *            Days in past are represented as negative values and days in the future are represented as positive
     *            values. To avoid tasks are counted multiple times or not be listed in the report, these
     *            reportLineItemDefinitions should not overlap and should not have gaps. If the ReportLineDefinition
     *            should represent a single day, lowerLimit and upperLimit have to be equal.
     * @return the report
     */
    Report getWorkbasketLevelReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, List<ReportLineItemDefinition> reportLineItemDefinitions);

    /**
     * Returns a {@link Report} grouped by workbaskets. For each workbasket the report contains the total number of
     * tasks and the number of tasks of the respective cluster that are specified by the
     * {@link ReportLineItemDefinition}s. It can be specified whether the age of the tasks is counted in days or in
     * working days. Furthermore the report contains a sum line that contains the total numbers of the different
     * clusters and the total number of all tasks. The tasks of the report are filtered by workbaskets, states,
     * categories and domains. Task with Timestamp DUE = null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids objects to filter by workbaskets
     * @param states
     *            a list of states objects to filter by states
     * @param categories
     *            a list of categories to filter by categories
     * @param domains
     *            a list of domains to filter by domains
     * @param reportLineItemDefinitions
     *            a list of reportLineItemDefinitions that specify the subdivision into different cluster of due dates.
     *            Days in past are represented as negative values and days in the future are represented as positive
     *            values. To avoid tasks are counted multiple times or not be listed in the report, these
     *            reportLineItemDefinitions should not overlap and should not have gaps. If the ReportLineDefinition
     *            should represent a single day, lowerLimit and upperLimit have to be equal.
     * @param inWorkingDays
     *            a boolean parameter that specifies whether the age of the tasks should be counted in days or in
     *            working days
     * @return the report
     */
    Report getWorkbasketLevelReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, List<ReportLineItemDefinition> reportLineItemDefinitions,
        boolean inWorkingDays);

    /**
     * Returns a {@link Report} grouped by categories. The report contains the total numbers of tasks of the respective
     * category as well as the total number of all tasks. The tasks of the report are filtered by workbaskets, states,
     * categories and domains. Task with Timestamp DUE = null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids to filter by workbaskets
     * @param states
     *            a list of states to filter by states
     * @param categories
     *            a list of categories to filter by categories
     * @param domains
     *            a list of domains to filter by domains
     * @return the report
     */
    Report getCategoryReport(List<String> workbasketIds, List<TaskState> states, List<String> categories,
        List<String> domains);

    /**
     * Returns a {@link Report} grouped by categories. For each category the report contains the total number of tasks
     * and the number of tasks of the respective cluster that are specified by the {@link ReportLineItemDefinition}s. By
     * default the age of the tasks is counted in working days. Furthermore the Report contains a sum line that contains
     * the total numbers of the different clusters and the total number of all tasks in this report. The tasks of the
     * report are filtered by workbaskets, states, categories and domains. Task with Timestamp DUE = null are not
     * considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids objects to filter by workbaskets
     * @param states
     *            a list of states objects to filter by states
     * @param categories
     *            a list of categories to filter by categories
     * @param domains
     *            a list of domains to filter by domains
     * @param reportLineItemDefinitions
     *            a list of reportLineItemDefinitions that specify the subdivision into different cluster of due dates.
     *            Days in past are represented as negative values and days in the future are represented as positive
     *            values. To avoid tasks are counted multiple times or not be listed in the report, these
     *            reportLineItemDefinitions should not overlap and should not have gaps. If the ReportLineDefinition
     *            should represent a single day, lowerLimit and upperLimit have to be equal.
     * @return the report
     */
    Report getCategoryReport(List<String> workbasketIds, List<TaskState> states, List<String> categories,
        List<String> domains, List<ReportLineItemDefinition> reportLineItemDefinitions);

    /**
     * Returns a {@link Report} grouped by categories. For each category the report contains the total number of tasks
     * and the number of tasks of the respective cluster that are specified by the {@link ReportLineItemDefinition}s. It
     * can be specified whether the age of the tasks is counted in days or in working days. Furthermore the report
     * contains a sum line that contains the total numbers of the different clusters and the total number of all tasks.
     * The tasks of the report are filtered by workbaskets, states, categories and domains. Task with Timestamp DUE =
     * null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids objects to filter by workbaskets
     * @param states
     *            a list of states objects to filter by states
     * @param categories
     *            a list of categories to filter by categories
     * @param domains
     *            a list of domains to filter by domains
     * @param reportLineItemDefinitions
     *            a list of reportLineItemDefinitions that specify the subdivision into different cluster of due dates.
     *            Days in past are represented as negative values and days in the future are represented as positive
     *            values. To avoid tasks are counted multiple times or not be listed in the report, these
     *            reportLineItemDefinitions should not overlap and should not have gaps. If the ReportLineDefinition
     *            should represent a single day, lowerLimit and upperLimit have to be equal.
     * @param inWorkingDays
     *            a boolean parameter that specifies whether the age of the tasks should be counted in days or in
     *            working days
     * @return the report
     */
    Report getCategoryReport(List<String> workbasketIds, List<TaskState> states, List<String> categories,
        List<String> domains, List<ReportLineItemDefinition> reportLineItemDefinitions, boolean inWorkingDays);

    /**
     * Returns a {@link Classification} grouped by classifications. The report contains the total numbers of tasks of
     * the respective classification as well as the total number of all tasks. The tasks of the report are filtered by
     * workbaskets, states, categories and domains. Task with Timestamp DUE = null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids to filter by workbaskets
     * @param states
     *            a list of states to filter by states
     * @param categories
     *            a list of categories to filter by categories
     * @param domains
     *            a list of domains to filter by domains
     * @return the ClassificationReport
     */
    ClassificationReport getClassificationReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains);

    /**
     * Returns a {@link Classification} grouped by classifications. For each classification the report contains the
     * total number of tasks and the number of tasks of the respective cluster that are specified by the
     * {@link ReportLineItemDefinition}s. By default the age of the tasks is counted in working days. Furthermore the
     * Report contains a sum line that contains the total numbers of the different clusters and the total number of all
     * tasks in this report. The tasks of the report are filtered by workbaskets, states, categories and domains. Task
     * with Timestamp DUE = null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids objects to filter by workbaskets
     * @param states
     *            a list of states objects to filter by states
     * @param categories
     *            a list of categories to filter by categories
     * @param domains
     *            a list of domains to filter by domains
     * @param reportLineItemDefinitions
     *            a list of reportLineItemDefinitions that specify the subdivision into different cluster of due dates.
     *            Days in past are represented as negative values and days in the future are represented as positive
     *            values. To avoid tasks are counted multiple times or not be listed in the report, these
     *            reportLineItemDefinitions should not overlap and should not have gaps. If the ReportLineDefinition
     *            should represent a single day, lowerLimit and upperLimit have to be equal.
     * @return the ClassificationReport
     */
    ClassificationReport getClassificationReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, List<ReportLineItemDefinition> reportLineItemDefinitions);

    /**
     * Returns a {@link ClassificationReport} grouped by classification. For each classification the report contains the
     * total number of tasks and the number of tasks of the respective cluster that are specified by the
     * {@link ReportLineItemDefinition}s. It can be specified whether the age of the tasks is counted in days or in
     * working days. Furthermore the report contains a sum line that contains the total numbers of the different
     * clusters and the total number of all tasks. The tasks of the report are filtered by workbaskets, states,
     * categories and domains. Task with Timestamp DUE = null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids objects to filter by workbaskets
     * @param states
     *            a list of states objects to filter by states
     * @param categories
     *            a list of categories to filter by categories
     * @param domains
     *            a list of domains to filter by domains
     * @param reportLineItemDefinitions
     *            a list of reportLineItemDefinitions that specify the subdivision into different cluster of due dates.
     *            Days in past are represented as negative values and days in the future are represented as positive
     *            values. To avoid tasks are counted multiple times or not be listed in the report, these
     *            reportLineItemDefinitions should not overlap and should not have gaps. If the ReportLineDefinition
     *            should represent a single day, lowerLimit and upperLimit have to be equal.
     * @param inWorkingDays
     *            a boolean parameter that specifies whether the age of the tasks should be counted in days or in
     *            working days
     * @return the ClassificationReport
     */
    ClassificationReport getClassificationReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, List<ReportLineItemDefinition> reportLineItemDefinitions,
        boolean inWorkingDays);

    /**
     * Returns a {@link DetailedClassificationReport}. The report contains the total numbers of tasks of the respective
     * classification as well as the total number of all tasks. Each ReportLine contains an additional list of
     * ReportLines for the classifications of the attachments of the tasks. The tasks of the report are filtered by
     * workbaskets, states, categories and domains. Task with Timestamp DUE = null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids to filter by workbaskets
     * @param states
     *            a list of states to filter by states
     * @param categories
     *            a list of categories to filter by categories
     * @param domains
     *            a list of domains to filter by domains
     * @return the DetailedClassificationReport
     */
    DetailedClassificationReport getDetailedClassificationReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains);

    /**
     * Returns a {@link DetailedClassificationReport}. For each classification the report contains the total number of
     * tasks and the number of tasks of the respective cluster that are specified by the
     * {@link ReportLineItemDefinition}s. By default the age of the tasks is counted in working days. Each ReportLine
     * contains an additional list of ReportLines for the classifications of the attachments of the tasks. Furthermore
     * the Report contains a sum line that contains the total numbers of the different clusters and the total number of
     * all tasks in this report. The tasks of the report are filtered by workbaskets, states, categories and domains.
     * Task with Timestamp DUE = null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids objects to filter by workbaskets
     * @param states
     *            a list of states objects to filter by states
     * @param categories
     *            a list of categories to filter by categories
     * @param domains
     *            a list of domains to filter by domains
     * @param reportLineItemDefinitions
     *            a list of reportLineItemDefinitions that specify the subdivision into different cluster of due dates.
     *            Days in past are represented as negative values and days in the future are represented as positive
     *            values. To avoid tasks are counted multiple times or not be listed in the report, these
     *            reportLineItemDefinitions should not overlap and should not have gaps. If the ReportLineDefinition
     *            should represent a single day, lowerLimit and upperLimit have to be equal.
     * @return the DetailedClassificationReport
     */
    DetailedClassificationReport getDetailedClassificationReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, List<ReportLineItemDefinition> reportLineItemDefinitions);

    /**
     * Returns a {@link DetailedClassificationReport}. For each classification the report contains the total number of
     * tasks and the number of tasks of the respective cluster that are specified by the
     * {@link ReportLineItemDefinition}s. It can be specified whether the age of the tasks is counted in days or in
     * working days. Each ReportLine contains an additional list of ReportLines for the classifications of the
     * attachments of the tasks. Furthermore the report contains a sum line that contains the total numbers of the
     * different clusters and the total number of all tasks. The tasks of the report are filtered by workbaskets,
     * states, categories and domains. Task with Timestamp DUE = null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids objects to filter by workbaskets
     * @param states
     *            a list of states objects to filter by states
     * @param categories
     *            a list of categories to filter by categories
     * @param domains
     *            a list of domains to filter by domains
     * @param reportLineItemDefinitions
     *            a list of reportLineItemDefinitions that specify the subdivision into different cluster of due dates.
     *            Days in past are represented as negative values and days in the future are represented as positive
     *            values. To avoid tasks are counted multiple times or not be listed in the report, these
     *            reportLineItemDefinitions should not overlap and should not have gaps. If the ReportLineDefinition
     *            should represent a single day, lowerLimit and upperLimit have to be equal.
     * @param inWorkingDays
     *            a boolean parameter that specifies whether the age of the tasks should be counted in days or in
     *            working days
     * @return the DetailedClassificationReport
     */
    DetailedClassificationReport getDetailedClassificationReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, List<ReportLineItemDefinition> reportLineItemDefinitions,
        boolean inWorkingDays);

    /**
     * Returns a {@link Report} grouped by the value of a certain {@link CustomField}. The report contains the total
     * numbers of tasks of the respective custom field as well as the total number of all tasks. The tasks of the report
     * are filtered by workbaskets, states, categories and domains. Task with Timestamp DUE = null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids to filter by workbaskets
     * @param states
     *            a list of states to filter by states
     * @param categories
     *            a list of categories to filter by categories
     * @param domains
     *            a list of domains to filter by domains
     * @param customField
     *            a custom field whose values should be listed in the report
     * @return the report
     */
    Report getCustomFieldValueReport(List<String> workbasketIds, List<TaskState> states, List<String> categories,
        List<String> domains, CustomField customField);

    /**
     * Returns a {@link Report} grouped by the value of a certain {@link CustomField}. For each value of the custom
     * field the report contains the total number of tasks and the number of tasks of the respective cluster that are
     * specified by the {@link ReportLineItemDefinition}s. By default the age of the tasks is counted in working days.
     * Furthermore the Report contains a sum line that contains the total numbers of the different clusters and the
     * total number of all tasks in this report. The tasks of the report are filtered by workbaskets, states, categories
     * and domains. Task with Timestamp DUE = null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids objects to filter by workbaskets
     * @param states
     *            a list of states objects to filter by states
     * @param categories
     *            a list of categories to filter by categories
     * @param domains
     *            a list of domains to filter by domains
     * @param customField
     *            a custom field whose values should be listed in the report
     * @param reportLineItemDefinitions
     *            a list of reportLineItemDefinitions that specify the subdivision into different cluster of due dates.
     *            Days in past are represented as negative values and days in the future are represented as positive
     *            values. To avoid tasks are counted multiple times or not be listed in the report, these
     *            reportLineItemDefinitions should not overlap and should not have gaps. If the ReportLineDefinition
     *            should represent a single day, lowerLimit and upperLimit have to be equal.
     * @return the report
     */
    Report getCustomFieldValueReport(List<String> workbasketIds, List<TaskState> states, List<String> categories,
        List<String> domains, CustomField customField, List<ReportLineItemDefinition> reportLineItemDefinitions);

    /**
     * Returns a {@link Report} grouped by the value of a certain {@link CustomField}. For each value of the custom
     * field the report contains the total number of tasks and the number of tasks of the respective cluster that are
     * specified by the {@link ReportLineItemDefinition}s. It can be specified whether the age of the tasks is counted
     * in days or in working days. Furthermore the report contains a sum line that contains the total numbers of the
     * different clusters and the total number of all tasks. The tasks of the report are filtered by workbaskets,
     * states, categories and domains. Task with Timestamp DUE = null are not considered.
     *
     * @param workbasketIds
     *            a list of workbasket ids objects to filter by workbaskets
     * @param states
     *            a list of states objects to filter by states
     * @param categories
     *            a list of categories to filter by categories
     * @param domains
     *            a list of domains to filter by domains
     * @param customField
     *            a custom field whose values should be listed in the report
     * @param reportLineItemDefinitions
     *            a list of reportLineItemDefinitions that specify the subdivision into different cluster of due dates.
     *            Days in past are represented as negative values and days in the future are represented as positive
     *            values. To avoid tasks are counted multiple times or not be listed in the report, these
     *            reportLineItemDefinitions should not overlap and should not have gaps. If the ReportLineDefinition
     *            should represent a single day, lowerLimit and upperLimit have to be equal.
     * @param inWorkingDays
     *            a boolean parameter that specifies whether the age of the tasks should be counted in days or in
     *            working days
     * @return the report
     */
    Report getCustomFieldValueReport(List<String> workbasketIds, List<TaskState> states, List<String> categories,
        List<String> domains, CustomField customField, List<ReportLineItemDefinition> reportLineItemDefinitions,
        boolean inWorkingDays);
}
