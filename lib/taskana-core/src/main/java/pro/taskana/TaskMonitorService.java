package pro.taskana;

import pro.taskana.impl.CustomFieldValueReportBuilderImpl;
import pro.taskana.impl.TaskStatusReportBuilderImpl;
import pro.taskana.impl.WorkbasketReportBuilderImpl;

/**
 * The Task Monitor Service manages operations on tasks regarding the monitoring.
 */
public interface TaskMonitorService {

    /**
     * Provides a {@link WorkbasketReportBuilderImpl} for creating a WorkbasketReport, list the task ids of this report
     * and list the values of an entered custom attribute.
     *
     * @return a {@link WorkbasketReportBuilderImpl}
     */
    WorkbasketReportBuilderImpl createWorkbasketReportBuilder();

    /**
     * Provides a {@link CategoryReportBuilder} for creating a CategoryReport, list the task ids of this report and list
     * the values of an entered custom attribute.
     *
     * @return a {@link CategoryReportBuilder}
     */
    CategoryReportBuilder createCategoryReportBuilder();

    /**
     * Provides a {@link ClassificationReportBuilder} for creating a ClassificationReport or a
     * DetailedClassificationReport, list the task ids of these reports and list the values of an entered custom
     * attribute.
     *
     * @return a {@link ClassificationReportBuilder}
     */
    ClassificationReportBuilder createClassificationReportBuilder();

    /**
     * Provides a {@link CustomFieldValueReportBuilderImpl} for creating a CustomFieldValueReport and list the values of
     * an entered custom attribute.
     *
     * @param customField
     *            the customField whose values should appear in the report
     * @return a {@link CustomFieldValueReportBuilderImpl}
     */
    CustomFieldValueReportBuilderImpl createCustomFieldValueReportBuilder(CustomField customField);

    /**
     * Provides a {@link TaskStatusReportBuilderImpl} for creating a TaskStatusReport.
     *
     * @return a {@link TaskStatusReportBuilderImpl}
     */
    TaskStatusReportBuilderImpl createTaskStatusReportBuilder();
}
