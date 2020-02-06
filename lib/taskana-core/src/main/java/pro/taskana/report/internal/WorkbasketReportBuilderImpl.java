package pro.taskana.report.internal;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.report.api.WorkbasketReport;
import pro.taskana.report.api.header.TimeIntervalColumnHeader;
import pro.taskana.report.api.item.MonitorQueryItem;
import pro.taskana.report.internal.preprocessor.DaysToWorkingDaysPreProcessor;
import pro.taskana.task.api.TaskanaRole;

/** The implementation of WorkbasketReportBuilder. */
public class WorkbasketReportBuilderImpl
    extends TimeIntervalReportBuilderImpl<
        WorkbasketReport.Builder, MonitorQueryItem, TimeIntervalColumnHeader>
    implements WorkbasketReport.Builder {

  private static final Logger LOGGER = LoggerFactory.getLogger(WorkbasketReportBuilderImpl.class);
  private List<CombinedClassificationFilter> combinedClassificationFilter;

  WorkbasketReportBuilderImpl(
      InternalTaskanaEngine taskanaEngine, TaskMonitorMapper taskMonitorMapper) {
    super(taskanaEngine, taskMonitorMapper);
  }

  @Override
  public WorkbasketReport buildReport() throws InvalidArgumentException, NotAuthorizedException {
    LOGGER.debug("entry to buildReport(), this = {}", this);
    this.taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR, TaskanaRole.ADMIN);
    try {
      this.taskanaEngine.openConnection();
      WorkbasketReport report = new WorkbasketReport(this.columnHeaders);
      List<MonitorQueryItem> monitorQueryItems =
          this.taskMonitorMapper.getTaskCountOfWorkbaskets(
              this.workbasketIds,
              this.states,
              this.categories,
              this.domains,
              this.classificationIds,
              this.excludedClassificationIds,
              this.customAttributeFilter,
              this.combinedClassificationFilter);
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
  public WorkbasketReport buildPlannedDateBasedReport()
      throws NotAuthorizedException, InvalidArgumentException {
    LOGGER.debug("entry to buildPlannedDateReport(), this = {}", this);
    this.taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR, TaskanaRole.ADMIN);
    try {
      this.taskanaEngine.openConnection();
      WorkbasketReport report = new WorkbasketReport(this.columnHeaders);
      List<MonitorQueryItem> monitorQueryItems =
          this.taskMonitorMapper.getTaskCountOfWorkbasketsBasedOnPlannedDate(
              this.workbasketIds,
              this.states,
              this.categories,
              this.domains,
              this.classificationIds,
              this.excludedClassificationIds,
              this.customAttributeFilter,
              this.combinedClassificationFilter);
      report.addItems(
          monitorQueryItems,
          new DaysToWorkingDaysPreProcessor<>(this.columnHeaders, this.inWorkingDays));
      return report;
    } finally {
      this.taskanaEngine.returnConnection();
      LOGGER.debug("exit from buildPlannedDateReport().");
    }
  }

  @Override
  public WorkbasketReport.Builder combinedClassificationFilterIn(
      List<CombinedClassificationFilter> combinedClassificationFilter) {
    this.combinedClassificationFilter = combinedClassificationFilter;
    return this;
  }

  @Override
  protected WorkbasketReport.Builder _this() {
    return this;
  }

  @Override
  protected String determineGroupedBy() {
    return "WORKBASKET_KEY";
  }
}
