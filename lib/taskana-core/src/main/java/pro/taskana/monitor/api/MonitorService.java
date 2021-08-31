package pro.taskana.monitor.api;

import pro.taskana.monitor.api.reports.ClassificationCategoryReport;
import pro.taskana.monitor.api.reports.ClassificationReport;
import pro.taskana.monitor.api.reports.TaskCustomFieldValueReport;
import pro.taskana.monitor.api.reports.TaskStatusReport;
import pro.taskana.monitor.api.reports.TimestampReport;
import pro.taskana.monitor.api.reports.WorkbasketPriorityReport;
import pro.taskana.monitor.api.reports.WorkbasketReport;
import pro.taskana.task.api.TaskCustomField;

/** The Monitor Service manages operations on tasks regarding the monitoring. */
public interface MonitorService {

  /**
   * Provides a {@link WorkbasketReport.Builder} for creating a {@link WorkbasketReport}.
   *
   * @return a {@link WorkbasketReport.Builder}
   */
  WorkbasketReport.Builder createWorkbasketReportBuilder();

  /**
   * Provides a {@link WorkbasketPriorityReport.Builder} for creating a {@link
   * WorkbasketPriorityReport}.
   *
   * @return a {@link WorkbasketReport.Builder}
   */
  WorkbasketPriorityReport.Builder createWorkbasketPriorityReportBuilder();

  /**
   * Provides a {@link ClassificationCategoryReport.Builder} for creating a {@link
   * ClassificationCategoryReport}.
   *
   * @return a {@link ClassificationCategoryReport.Builder}
   */
  ClassificationCategoryReport.Builder createClassificationCategoryReportBuilder();

  /**
   * Provides a {@link ClassificationReport.Builder} for creating a {@link ClassificationReport} or
   * a {@link pro.taskana.monitor.api.reports.ClassificationReport.DetailedClassificationReport}.
   *
   * @return a {@link ClassificationReport.Builder}
   */
  ClassificationReport.Builder createClassificationReportBuilder();

  /**
   * Provides a {@link TaskCustomFieldValueReport.Builder} for creating a {@link
   * TaskCustomFieldValueReport}.
   *
   * @param taskCustomField the customField whose values should appear in the report
   * @return a {@link TaskCustomFieldValueReport.Builder}
   */
  TaskCustomFieldValueReport.Builder createTaskCustomFieldValueReportBuilder(
      TaskCustomField taskCustomField);

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
