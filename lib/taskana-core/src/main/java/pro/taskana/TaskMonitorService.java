package pro.taskana;

import java.util.List;

import pro.taskana.impl.ClassificationReport;
import pro.taskana.impl.CustomField;
import pro.taskana.impl.DetailedClassificationReport;
import pro.taskana.impl.Report;
import pro.taskana.impl.ReportLineItemDefinition;
import pro.taskana.impl.TaskState;

/**
 * The Task Monitor Service manages operations on tasks regarding the monitoring.
 */
public interface TaskMonitorService {

    /**
     * Returns a {@link Report} for a given list of {@link Workbasket}s and for a given list of {@link TaskState}s. The
     * report only contains the number of all tasks of the respective workbasket as well as the total sum of all tasks.
     * Only tasks with a state in the list of TaskStates and a category in the list of categories are provided. Task
     * with Timestamp DUE = null are not considered.
     *
     * @param workbaskets
     *            a list of {@link Workbasket} objects that should be listed in the report
     * @param states
     *            a list of {@link TaskState} objects that specify the states of the tasks that are provided
     * @param categories
     *            a list of categories. Only tasks with a classification of this these categories are listed in the
     *            report
     * @return a {@link Report} object that only contains the number of all tasks of the respective workbasket as well
     *         as the total number of all tasks
     */
    Report getWorkbasketLevelReport(List<Workbasket> workbaskets, List<TaskState> states, List<String> categories);

    /**
     * Returns a {@link Report} for a given list of {@link Workbasket}s, a given list of {@link TaskState}s and a given
     * list of {@link ReportLineItemDefinition}s. For each workbasket the report contains a list of ReportLineItems that
     * subdivides the report in to different cluster grouped by the due date. By default the age of the tasks is counted
     * in working days. Only tasks with a state in the list of TaskStates are provided. Tasks with Timestamp DUE = null
     * are not considered.
     *
     * @param workbaskets
     *            a list of {@link Workbasket} objects that should be listed in the report
     * @param states
     *            a list of {@link TaskState} objects that specify the states of the tasks that are provided
     * @param categories
     *            a list of categories. Only tasks with a classification of this these categories are listed in the
     *            report
     * @param reportLineItemDefinitions
     *            a list of {@link ReportLineItemDefinition} objects that specify the subdivision into different cluster
     *            of due dates. Days in past are represented as negative values and days in the future are represented
     *            as positive values. To avoid tasks are counted multiple times or not be listed in the report, these
     *            reportLineItemDefinitions should not overlap and should not have gaps. If the ReportLineDefinition
     *            should represent a single day, lowerLimit and upperLimit have to be equal.
     * @return a {@link Report} object that represents an overview of all tasks in the
     */
    Report getWorkbasketLevelReport(List<Workbasket> workbaskets, List<TaskState> states,
        List<String> categories, List<ReportLineItemDefinition> reportLineItemDefinitions);

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
     * @param categories
     *            a list of categories. Only tasks with a classification of this these categories are listed in the
     *            report
     * @param reportLineItemDefinitions
     *            a list of {@link ReportLineItemDefinition} objects that specify the subdivision into different cluster
     *            of due dates. Days in past are represented as negative values and days in the future are represented
     *            as positive values. To avoid tasks are counted multiple times or not be listed in the report, these
     *            reportLineItemDefinitions should not overlap and should not have gaps. If the ReportLineDefinition
     *            should represent a single day, lowerLimit and upperLimit have to be equal.
     * @param inWorkingDays
     *            a boolean parameter that specifies whether the age of the tasks should be counted in days or in
     *            working days.
     * @return a {@link Report} object that represents an overview of all tasks in the
     */
    Report getWorkbasketLevelReport(List<Workbasket> workbaskets, List<TaskState> states,
        List<String> categories, List<ReportLineItemDefinition> reportLineItemDefinitions, boolean inWorkingDays);

    /**
     * Returns a {@link Report} with categories for a given list of {@link Workbasket}s and for a given list of
     * {@link TaskState}s. The report only contains the number of all tasks of the respective category as well as the
     * total sum of all tasks. Only tasks with a state in the list of TaskStates are provided. Task with Timestamp DUE =
     * null are not considered.
     *
     * @param workbaskets
     *            a list of {@link Workbasket} objects whose tasks should be considered in the report
     * @param states
     *            a list of {@link TaskState} objects that specify the states of the tasks that are provided
     * @param categories
     *            a list of categories. Only tasks with a classification of this these categories are listed in the
     *            report
     * @return a {@link Report} object that only contains the number of all tasks of the respective category as well as
     *         the total number of all tasks
     */
    Report getCategoryReport(List<Workbasket> workbaskets, List<TaskState> states, List<String> categories);

    /**
     * Returns a {@link Report} with categories for a given list of {@link Workbasket}s, a given list of
     * {@link TaskState}s and a given list of {@link ReportLineItemDefinition}s. For each category the report contains a
     * list of ReportLineItems that subdivides the report in to different cluster grouped by the due date. By default
     * the age of the tasks is counted in working days. Only tasks with a state in the list of TaskStates are provided.
     * Tasks with Timestamp DUE = null are not considered.
     *
     * @param workbaskets
     *            a list of {@link Workbasket} objects whose tasks should be considered in the report
     * @param states
     *            a list of {@link TaskState} objects that specify the states of the tasks that are provided
     * @param categories
     *            a list of categories. Only tasks with a classification of this these categories are listed in the
     *            report
     * @param reportLineItemDefinitions
     *            a list of {@link ReportLineItemDefinition} objects that specify the subdivision into different cluster
     *            of due dates. Days in past are represented as negative values and days in the future are represented
     *            as positive values. To avoid tasks are counted multiple times or not be listed in the report, these
     *            reportLineItemDefinitions should not overlap and should not have gaps. If the ReportLineDefinition
     *            should represent a single day, lowerLimit and upperLimit have to be equal.
     * @return a {@link Report} object that represents an overview of all tasks of the respective category
     */
    Report getCategoryReport(List<Workbasket> workbaskets, List<TaskState> states, List<String> categories,
        List<ReportLineItemDefinition> reportLineItemDefinitions);

    /**
     * Returns a {@link Report} with categories for a given list of {@link Workbasket}s, a given list of
     * {@link TaskState}s and a given list of {@link ReportLineItemDefinition}s. For each category the report contains a
     * list of ReportLineItems that subdivides the report in to different cluster grouped by the due date. Only tasks
     * with a state in the list of TaskStates are provided. Tasks with Timestamp DUE = null are not considered.
     *
     * @param workbaskets
     *            a list of {@link Workbasket} objects whose tasks should be considered in the report
     * @param states
     *            a list of {@link TaskState} objects that specify the states of the tasks that are provided
     * @param categories
     *            a list of categories. Only tasks with a classification of this these categories are listed in the
     *            report
     * @param reportLineItemDefinitions
     *            a list of {@link ReportLineItemDefinition} objects that specify the subdivision into different cluster
     *            of due dates. Days in past are represented as negative values and days in the future are represented
     *            as positive values. To avoid tasks are counted multiple times or not be listed in the report, these
     *            reportLineItemDefinitions should not overlap and should not have gaps. If the ReportLineDefinition
     *            should represent a single day, lowerLimit and upperLimit have to be equal.
     * @param inWorkingDays
     *            a boolean parameter that specifies whether the age of the tasks should be counted in days or in
     *            working days.
     * @return a {@link Report} object that represents an overview of all tasks of the respective category
     */
    Report getCategoryReport(List<Workbasket> workbaskets, List<TaskState> states, List<String> categories,
        List<ReportLineItemDefinition> reportLineItemDefinitions, boolean inWorkingDays);

    /**
     * Returns a {@link ClassificationReport} grouped by classifications for a given list of {@link Workbasket}s and for
     * a given list of {@link TaskState}s. The report only contains the number of all tasks of the respective
     * classification as well as the total sum of all tasks. Only tasks with a state in the list of TaskStates are
     * provided. Task with Timestamp DUE = null are not considered.
     *
     * @param workbaskets
     *            a list of {@link Workbasket} objects whose tasks should be considered in the report
     * @param states
     *            a list of {@link TaskState} objects that specify the states of the tasks that are provided
     * @param categories
     *            a list of categories. Only tasks with a classification of this these categories are listed in the
     *            report
     * @return a {@link ClassificationReport} object that only contains the number of all tasks of the respective
     *         classification as well as the total number of all tasks
     */
    ClassificationReport getClassificationReport(List<Workbasket> workbaskets, List<TaskState> states,
        List<String> categories);

    /**
     * Returns a {@link ClassificationReport} grouped by classifications for a given list of {@link Workbasket}s, a
     * given list of {@link TaskState}s and a given list of {@link ReportLineItemDefinition}s. For each classification
     * the report contains a list of ReportLineItems that subdivides the report in to different cluster grouped by the
     * due date. By default the age of the tasks is counted in working days. Only tasks with a state in the list of
     * TaskStates are provided. Tasks with Timestamp DUE = null are not considered.
     *
     * @param workbaskets
     *            a list of {@link Workbasket} objects whose tasks should be considered in the report
     * @param states
     *            a list of {@link TaskState} objects that specify the states of the tasks that are provided
     * @param categories
     *            a list of categories. Only tasks with a classification of this these categories are listed in the
     *            report
     * @param reportLineItemDefinitions
     *            a list of {@link ReportLineItemDefinition} objects that specify the subdivision into different cluster
     *            of due dates. Days in past are represented as negative values and days in the future are represented
     *            as positive values. To avoid tasks are counted multiple times or not be listed in the report, these
     *            reportLineItemDefinitions should not overlap and should not have gaps. If the ReportLineDefinition
     *            should represent a single day, lowerLimit and upperLimit have to be equal.
     * @return a {@link ClassificationReport} object that represents an overview of all tasks of the respective
     *         classification
     */
    ClassificationReport getClassificationReport(List<Workbasket> workbaskets, List<TaskState> states,
        List<String> categories, List<ReportLineItemDefinition> reportLineItemDefinitions);

    /**
     * Returns a {@link DetailedClassificationReport} grouped by classifications for a given list of
     * {@link Workbasket}s, a given list of {@link TaskState}s and a given list of {@link ReportLineItemDefinition}s.
     * For each classification the report contains a list of ReportLineItems that subdivides the report in to different
     * cluster grouped by the due date. Only tasks with a state in the list of TaskStates are provided. Tasks with
     * Timestamp DUE = null are not considered.
     *
     * @param workbaskets
     *            a list of {@link Workbasket} objects whose tasks should be considered in the report
     * @param states
     *            a list of {@link TaskState} objects that specify the states of the tasks that are provided
     * @param categories
     *            a list of categories. Only tasks with a classification of this these categories are listed in the
     *            report
     * @param reportLineItemDefinitions
     *            a list of {@link ReportLineItemDefinition} objects that specify the subdivision into different cluster
     *            of due dates. Days in past are represented as negative values and days in the future are represented
     *            as positive values. To avoid tasks are counted multiple times or not be listed in the report, these
     *            reportLineItemDefinitions should not overlap and should not have gaps. If the ReportLineDefinition
     *            should represent a single day, lowerLimit and upperLimit have to be equal.
     * @param inWorkingDays
     *            a boolean parameter that specifies whether the age of the tasks should be counted in days or in
     *            working days.
     * @return a {@link DetailedClassificationReport} object that represents an overview of all tasks of the respective
     *         classification
     */
    DetailedClassificationReport getDetailedClassificationReport(List<Workbasket> workbaskets, List<TaskState> states,
        List<String> categories, List<ReportLineItemDefinition> reportLineItemDefinitions, boolean inWorkingDays);

    /**
     * Returns a {@link DetailedClassificationReport} grouped by classifications for a given list of {@link Workbasket}s
     * and for a given list of {@link TaskState}s. The report only contains the number of all tasks of the respective
     * classification as well as the total sum of all tasks. Only tasks with a state in the list of TaskStates are
     * provided. Task with Timestamp DUE = null are not considered.
     *
     * @param workbaskets
     *            a list of {@link Workbasket} objects whose tasks should be considered in the report
     * @param states
     *            a list of {@link TaskState} objects that specify the states of the tasks that are provided
     * @param categories
     *            a list of categories. Only tasks with a classification of this these categories are listed in the
     *            report
     * @return a {@link DetailedClassificationReport} object that only contains the number of all tasks of the
     *         respective classification as well as the total number of all tasks
     */
    DetailedClassificationReport getDetailedClassificationReport(List<Workbasket> workbaskets, List<TaskState> states,
        List<String> categories);

    /**
     * Returns a {@link DetailedClassificationReport} grouped by classifications for a given list of
     * {@link Workbasket}s, a given list of {@link TaskState}s and a given list of {@link ReportLineItemDefinition}s.
     * For each classification the report contains a list of ReportLineItems that subdivides the report in to different
     * cluster grouped by the due date. By default the age of the tasks is counted in working days. Only tasks with a
     * state in the list of TaskStates are provided. Tasks with Timestamp DUE = null are not considered.
     *
     * @param workbaskets
     *            a list of {@link Workbasket} objects whose tasks should be considered in the report
     * @param states
     *            a list of {@link TaskState} objects that specify the states of the tasks that are provided
     * @param categories
     *            a list of categories. Only tasks with a classification of this these categories are listed in the
     *            report
     * @param reportLineItemDefinitions
     *            a list of {@link ReportLineItemDefinition} objects that specify the subdivision into different cluster
     *            of due dates. Days in past are represented as negative values and days in the future are represented
     *            as positive values. To avoid tasks are counted multiple times or not be listed in the report, these
     *            reportLineItemDefinitions should not overlap and should not have gaps. If the ReportLineDefinition
     *            should represent a single day, lowerLimit and upperLimit have to be equal.
     * @return a {@link DetailedClassificationReport} object that represents an overview of all tasks of the respective
     *         classification
     */
    DetailedClassificationReport getDetailedClassificationReport(List<Workbasket> workbaskets, List<TaskState> states,
        List<String> categories, List<ReportLineItemDefinition> reportLineItemDefinitions);

    /**
     * Returns a {@link ClassificationReport} grouped by classifications for a given list of {@link Workbasket}s, a
     * given list of {@link TaskState}s and a given list of {@link ReportLineItemDefinition}s. For each classification
     * the report contains a list of ReportLineItems that subdivides the report in to different cluster grouped by the
     * due date. Only tasks with a state in the list of TaskStates are provided. Tasks with Timestamp DUE = null are not
     * considered.
     *
     * @param workbaskets
     *            a list of {@link Workbasket} objects whose tasks should be considered in the report
     * @param states
     *            a list of {@link TaskState} objects that specify the states of the tasks that are provided
     * @param categories
     *            a list of categories. Only tasks with a classification of this these categories are listed in the
     *            report
     * @param reportLineItemDefinitions
     *            a list of {@link ReportLineItemDefinition} objects that specify the subdivision into different cluster
     *            of due dates. Days in past are represented as negative values and days in the future are represented
     *            as positive values. To avoid tasks are counted multiple times or not be listed in the report, these
     *            reportLineItemDefinitions should not overlap and should not have gaps. If the ReportLineDefinition
     *            should represent a single day, lowerLimit and upperLimit have to be equal.
     * @param inWorkingDays
     *            a boolean parameter that specifies whether the age of the tasks should be counted in days or in
     *            working days.
     * @return a {@link ClassificationReport} object that represents an overview of all tasks of the respective
     *         classification
     */
    ClassificationReport getClassificationReport(List<Workbasket> workbaskets, List<TaskState> states,
        List<String> categories, List<ReportLineItemDefinition> reportLineItemDefinitions, boolean inWorkingDays);

    /**
     * Returns a {@link Report} grouped by the value of a certain {@link CustomField} for a given list of
     * {@link Workbasket}s and for a given list of {@link TaskState}s. The report only contains the number of all tasks
     * of the respective value of the custom field as well as the total sum of all tasks. Only tasks with a state in the
     * list of TaskStates are provided. Task with Timestamp DUE = null are not considered.
     *
     * @param workbaskets
     *            a list of {@link Workbasket} objects whose tasks should be considered in the report
     * @param states
     *            a list of {@link TaskState} objects that specify the states of the tasks that are provided
     * @param categories
     *            a list of categories. Only tasks with a classification of this these categories are listed in the
     *            report
     * @param customField
     *            a {@link CustomField} whose values should be listed in the report
     * @return a {@link Report} object that only contains the number of all tasks of the respective value of the custom
     *         field as well as the total number of all tasks
     */
    Report getCustomFieldValueReport(List<Workbasket> workbaskets, List<TaskState> states, List<String> categories,
        CustomField customField);

    /**
     * Returns a {@link Report} grouped by the value of a certain {@link CustomField} for a given list of
     * {@link Workbasket}s, a given list of {@link TaskState}s and a given list of {@link ReportLineItemDefinition}s.
     * For each value of the custom field the report contains a list of ReportLineItems that subdivides the report in to
     * different cluster grouped by the due date. By default the age of the tasks is counted in working days. Only tasks
     * with a state in the list of TaskStates are provided. Tasks with Timestamp DUE = null are not considered.
     *
     * @param workbaskets
     *            a list of {@link Workbasket} objects whose tasks should be considered in the report
     * @param states
     *            a list of {@link TaskState} objects that specify the states of the tasks that are provided
     * @param categories
     *            a list of categories. Only tasks with a classification of this these categories are listed in the
     *            report
     * @param customField
     *            a {@link CustomField} whose values should be listed in the report
     * @param reportLineItemDefinitions
     *            a list of {@link ReportLineItemDefinition} objects that specify the subdivision into different cluster
     *            of due dates. Days in past are represented as negative values and days in the future are represented
     *            as positive values. To avoid tasks are counted multiple times or not be listed in the report, these
     *            reportLineItemDefinitions should not overlap and should not have gaps. If the ReportLineDefinition
     *            should represent a single day, lowerLimit and upperLimit have to be equal. a custom field whose values
     *            should be listed in the report
     * @return a {@link Report} object that represents an overview of all tasks of the respective value of the custom
     *         field
     */
    Report getCustomFieldValueReport(List<Workbasket> workbaskets, List<TaskState> states, List<String> categories,
        CustomField customField, List<ReportLineItemDefinition> reportLineItemDefinitions);

    /**
     * Returns a {@link Report} grouped by the value of a certain {@link CustomField} for a given list of
     * {@link Workbasket}s, a given list of {@link TaskState}s and a given list of {@link ReportLineItemDefinition}s.
     * For each value of the custom field the report contains a list of ReportLineItems that subdivides the report in to
     * different cluster grouped by the due date. Only tasks with a state in the list of TaskStates are provided. Tasks
     * with Timestamp DUE = null are not considered.
     *
     * @param workbaskets
     *            a list of {@link Workbasket} objects whose tasks should be considered in the report
     * @param states
     *            a list of {@link TaskState} objects that specify the states of the tasks that are provided
     * @param categories
     *            a list of categories. Only tasks with a classification of this these categories are listed in the
     *            report
     * @param customField
     *            a {@link CustomField} whose values should be listed in the report
     * @param reportLineItemDefinitions
     *            a list of {@link ReportLineItemDefinition} objects that specify the subdivision into different cluster
     *            of due dates. Days in past are represented as negative values and days in the future are represented
     *            as positive values. To avoid tasks are counted multiple times or not be listed in the report, these
     *            reportLineItemDefinitions should not overlap and should not have gaps. If the ReportLineDefinition
     *            should represent a single day, lowerLimit and upperLimit have to be equal.
     * @param inWorkingDays
     *            a boolean parameter that specifies whether the age of the tasks should be counted in days or in
     *            working days.
     * @return a {@link Report} object that represents an overview of all tasks of the respective value of the custom
     *         field
     */
    Report getCustomFieldValueReport(List<Workbasket> workbaskets, List<TaskState> states, List<String> categories,
        CustomField customField, List<ReportLineItemDefinition> reportLineItemDefinitions, boolean inWorkingDays);
}
