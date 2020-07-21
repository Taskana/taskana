package pro.taskana.monitor.api;

import pro.taskana.monitor.api.reports.ClassificationCategoryReport;
import pro.taskana.monitor.api.reports.ClassificationReport;
import pro.taskana.monitor.api.reports.CustomFieldValueReport;
import pro.taskana.monitor.api.reports.TaskStatusReport;
import pro.taskana.monitor.api.reports.TimestampReport;
import pro.taskana.monitor.api.reports.WorkbasketReport;
import pro.taskana.task.api.CustomField;

/** The Monitor Service manages operations on tasks regarding the monitoring. */
public interface MonitorService {

  /**
   * Provides a {@link WorkbasketReport.Builder} for creating a {@link WorkbasketReport}, list the
   * task ids of this report and list the values of an entered custom attribute.
   *
   * @return a {@link WorkbasketReport.Builder}
   */
  WorkbasketReport.Builder createWorkbasketReportBuilder();

  /**
   * Provides a {@link ClassificationCategoryReport.Builder} for creating a {@link
   * ClassificationCategoryReport}, list the task ids of this report and list the values of an
   * entered custom attribute.
   *
   * @return a {@link ClassificationCategoryReport.Builder}
   */
  ClassificationCategoryReport.Builder createClassificationCategoryReportBuilder();

  /**
   * Provides a {@link ClassificationReport.Builder} for creating a {@link ClassificationReport} or
   * a DetailedClassificationReport, list the task ids of these reports and list the values of an
   * entered custom attribute.
   *
   * @return a {@link ClassificationReport.Builder}
   */
  ClassificationReport.Builder createClassificationReportBuilder();

  /**
   * Provides a {@link CustomFieldValueReport.Builder} for creating a {@link CustomFieldValueReport}
   * and list the values of an entered custom attribute.
   *
   * @param customField the customField whose values should appear in the report
   * @return a {@link CustomFieldValueReport.Builder}
   */
  CustomFieldValueReport.Builder createCustomFieldValueReportBuilder(CustomField customField);

  /**
   * Provides a {@link TaskStatusReport.Builder} for creating a {@link TaskStatusReport}.
   *
   * @return a {@link TaskStatusReport.Builder}
   */
  TaskStatusReport.Builder createTaskStatusReportBuilder();

  /**
   * Provides a {@link TimestampReport.Builder} for creating a {@link TimestampReport}.
   *
   * @return a {@link TimestampReport.Builder}
   */
  TimestampReport.Builder createTimestampReportBuilder();
}
