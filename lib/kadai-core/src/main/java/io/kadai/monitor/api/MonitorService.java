package io.kadai.monitor.api;

import io.kadai.monitor.api.reports.ClassificationCategoryReport;
import io.kadai.monitor.api.reports.ClassificationReport;
import io.kadai.monitor.api.reports.TaskCustomFieldValueReport;
import io.kadai.monitor.api.reports.TaskStatusReport;
import io.kadai.monitor.api.reports.TimestampReport;
import io.kadai.monitor.api.reports.WorkbasketPriorityReport;
import io.kadai.monitor.api.reports.WorkbasketReport;
import io.kadai.task.api.TaskCustomField;

/** The Monitor Service manages operations on tasks regarding the monitoring. */
public interface MonitorService {

  /**
   * Provides a {@linkplain WorkbasketReport.Builder} for creating a {@linkplain WorkbasketReport}.
   *
   * @return a {@linkplain WorkbasketReport.Builder}
   */
  WorkbasketReport.Builder createWorkbasketReportBuilder();

  /**
   * Provides a {@linkplain WorkbasketPriorityReport.Builder} for creating a {@link
   * WorkbasketPriorityReport}.
   *
   * @return a {@linkplain WorkbasketReport.Builder}
   */
  WorkbasketPriorityReport.Builder createWorkbasketPriorityReportBuilder();

  /**
   * Provides a {@linkplain ClassificationCategoryReport.Builder} for creating a {@link
   * ClassificationCategoryReport}.
   *
   * @return a {@linkplain ClassificationCategoryReport.Builder}
   */
  ClassificationCategoryReport.Builder createClassificationCategoryReportBuilder();

  /**
   * Provides a {@linkplain ClassificationReport.Builder} for creating a {@linkplain
   * ClassificationReport} or a {@linkplain
   * io.kadai.monitor.api.reports.ClassificationReport.DetailedClassificationReport}.
   *
   * @return a {@linkplain ClassificationReport.Builder}
   */
  ClassificationReport.Builder createClassificationReportBuilder();

  /**
   * Provides a {@linkplain TaskCustomFieldValueReport.Builder} for creating a {@link
   * TaskCustomFieldValueReport}.
   *
   * @param taskCustomField the customField whose values should appear in the report
   * @return a {@linkplain TaskCustomFieldValueReport.Builder}
   */
  TaskCustomFieldValueReport.Builder createTaskCustomFieldValueReportBuilder(
      TaskCustomField taskCustomField);

  /**
   * Provides a {@linkplain TaskStatusReport.Builder} for creating a {@linkplain TaskStatusReport}.
   *
   * @return a {@linkplain TaskStatusReport.Builder}
   */
  TaskStatusReport.Builder createTaskStatusReportBuilder();

  /**
   * Provides a {@linkplain TimestampReport.Builder} for creating a {@linkplain TimestampReport}.
   *
   * @return a {@linkplain TimestampReport.Builder}
   */
  TimestampReport.Builder createTimestampReportBuilder();
}
