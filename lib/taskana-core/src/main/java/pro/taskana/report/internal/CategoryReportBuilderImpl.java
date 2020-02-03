package pro.taskana.report.internal;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.report.api.CategoryReport;
import pro.taskana.report.internal.header.TimeIntervalColumnHeader;
import pro.taskana.report.internal.item.MonitorQueryItem;
import pro.taskana.report.internal.preprocessor.DaysToWorkingDaysPreProcessor;
import pro.taskana.task.api.TaskanaRole;

/** The implementation of CategoryReportBuilder. */
public class CategoryReportBuilderImpl
    extends TimeIntervalReportBuilderImpl<
        CategoryReport.Builder, MonitorQueryItem, TimeIntervalColumnHeader>
    implements CategoryReport.Builder {

  private static final Logger LOGGER = LoggerFactory.getLogger(CategoryReport.Builder.class);

  CategoryReportBuilderImpl(
      InternalTaskanaEngine taskanaEngine, TaskMonitorMapper taskMonitorMapper) {
    super(taskanaEngine, taskMonitorMapper);
  }

  @Override
  public CategoryReport buildReport() throws InvalidArgumentException, NotAuthorizedException {
    LOGGER.debug("entry to buildReport(), this = {}", this);
    this.taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR);
    try {
      this.taskanaEngine.openConnection();
      CategoryReport report = new CategoryReport(this.columnHeaders);
      List<MonitorQueryItem> monitorQueryItems =
          this.taskMonitorMapper.getTaskCountOfCategories(
              this.workbasketIds,
              this.states,
              this.categories,
              this.domains,
              this.classificationIds,
              this.excludedClassificationIds,
              this.customAttributeFilter);
      report.addItems(
          monitorQueryItems,
          new DaysToWorkingDaysPreProcessor<>(this.columnHeaders, this.inWorkingDays));
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
