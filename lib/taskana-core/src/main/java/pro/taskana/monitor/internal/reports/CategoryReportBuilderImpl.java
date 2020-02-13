package pro.taskana.monitor.internal.reports;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.monitor.api.reports.CategoryReport;
import pro.taskana.monitor.api.reports.CategoryReport.Builder;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.MonitorQueryItem;
import pro.taskana.monitor.internal.MonitorMapper;
import pro.taskana.monitor.internal.preprocessor.DaysToWorkingDaysReportPreProcessor;

/** The implementation of CategoryReportBuilder. */
public class CategoryReportBuilderImpl
    extends TimeIntervalReportBuilderImpl<Builder, MonitorQueryItem, TimeIntervalColumnHeader>
    implements CategoryReport.Builder {

  private static final Logger LOGGER = LoggerFactory.getLogger(CategoryReport.Builder.class);

  public CategoryReportBuilderImpl(
      InternalTaskanaEngine taskanaEngine, MonitorMapper monitorMapper) {
    super(taskanaEngine, monitorMapper);
  }

  @Override
  public CategoryReport buildReport() throws InvalidArgumentException, NotAuthorizedException {
    LOGGER.debug("entry to buildReport(), this = {}", this);
    this.taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR);
    try {
      this.taskanaEngine.openConnection();
      CategoryReport report = new CategoryReport(this.columnHeaders);
      List<MonitorQueryItem> monitorQueryItems =
          this.monitorMapper.getTaskCountOfCategories(
              this.workbasketIds,
              this.states,
              this.categories,
              this.domains,
              this.classificationIds,
              this.excludedClassificationIds,
              this.customAttributeFilter);
      report.addItems(
          monitorQueryItems,
          new DaysToWorkingDaysReportPreProcessor<>(this.columnHeaders, this.inWorkingDays));
      return report;
    } finally {
      this.taskanaEngine.returnConnection();
      LOGGER.debug("exit from buildReport().");
    }
  }

  @Override
  protected CategoryReport.Builder _this() {
    return this;
  }

  @Override
  protected String determineGroupedBy() {
    return "CLASSIFICATION_CATEGORY";
  }
}
