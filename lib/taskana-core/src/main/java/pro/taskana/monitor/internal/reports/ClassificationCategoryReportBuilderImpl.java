package pro.taskana.monitor.internal.reports;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.ClassificationCategoryReport;
import pro.taskana.monitor.api.reports.ClassificationCategoryReport.Builder;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.MonitorQueryItem;
import pro.taskana.monitor.internal.MonitorMapper;
import pro.taskana.monitor.internal.preprocessor.DaysToWorkingDaysReportPreProcessor;

/** The implementation of CategoryReportBuilder. */
public class ClassificationCategoryReportBuilderImpl
    extends TimeIntervalReportBuilderImpl<Builder, MonitorQueryItem, TimeIntervalColumnHeader>
    implements ClassificationCategoryReport.Builder {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ClassificationCategoryReportBuilderImpl.class);

  public ClassificationCategoryReportBuilderImpl(
      InternalTaskanaEngine taskanaEngine, MonitorMapper monitorMapper) {
    super(taskanaEngine, monitorMapper);
  }

  @Override
  public ClassificationCategoryReport buildReport()
      throws NotAuthorizedException, InvalidArgumentException {
    return buildReport(TaskTimestamp.DUE);
  }

  @Override
  public ClassificationCategoryReport buildReport(TaskTimestamp timestamp)
      throws InvalidArgumentException, NotAuthorizedException {
    LOGGER.debug("entry to buildReport(), this = {}", this);
    this.taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR);
    try {
      this.taskanaEngine.openConnection();
      ClassificationCategoryReport report = new ClassificationCategoryReport(this.columnHeaders);
      List<MonitorQueryItem> monitorQueryItems =
          this.monitorMapper.getTaskCountOfCategories(
              this.workbasketIds,
              this.states,
              this.classificationCategory,
              this.domains,
              timestamp,
              this.classificationIds,
              this.excludedClassificationIds,
              this.customAttributeFilter);
      report.addItems(
          monitorQueryItems,
          new DaysToWorkingDaysReportPreProcessor<>(
              this.columnHeaders, converter, this.inWorkingDays));
      return report;
    } finally {
      this.taskanaEngine.returnConnection();
      LOGGER.debug("exit from buildReport().");
    }
  }

  @Override
  protected ClassificationCategoryReport.Builder _this() {
    return this;
  }

  @Override
  protected String determineGroupedBy() {
    return "CLASSIFICATION_CATEGORY";
  }
}
