package pro.taskana.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskanaRole;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.header.TimeIntervalColumnHeader;
import pro.taskana.impl.report.item.DetailedMonitorQueryItem;
import pro.taskana.impl.report.item.MonitorQueryItem;
import pro.taskana.impl.report.preprocessor.DaysToWorkingDaysPreProcessor;
import pro.taskana.mappings.TaskMonitorMapper;
import pro.taskana.report.ClassificationReport;
import pro.taskana.report.ClassificationReport.DetailedClassificationReport;

/** The implementation of ClassificationReportBuilder. */
public class ClassificationReportBuilderImpl
    extends TimeIntervalReportBuilderImpl<
        ClassificationReport.Builder, MonitorQueryItem, TimeIntervalColumnHeader>
    implements ClassificationReport.Builder {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationReport.Builder.class);

  ClassificationReportBuilderImpl(
      InternalTaskanaEngine taskanaEngine, TaskMonitorMapper taskMonitorMapper) {
    super(taskanaEngine, taskMonitorMapper);
  }

  @Override
  public ClassificationReport buildReport()
      throws InvalidArgumentException, NotAuthorizedException {
    LOGGER.debug("entry to buildReport(), this = {}", this);
    this.taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR, TaskanaRole.ADMIN);
    try {
      this.taskanaEngine.openConnection();
      ClassificationReport report = new ClassificationReport(this.columnHeaders);
      List<MonitorQueryItem> monitorQueryItems =
          this.taskMonitorMapper.getTaskCountOfClassifications(
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
  public DetailedClassificationReport buildDetailedReport()
      throws InvalidArgumentException, NotAuthorizedException {
    LOGGER.debug("entry to buildDetailedReport(), this = {}", this);
    this.taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR, TaskanaRole.ADMIN);
    try {
      this.taskanaEngine.openConnection();
      DetailedClassificationReport report = new DetailedClassificationReport(this.columnHeaders);
      List<DetailedMonitorQueryItem> detailedMonitorQueryItems =
          this.taskMonitorMapper.getTaskCountOfDetailedClassifications(
              this.workbasketIds,
              this.states,
              this.categories,
              this.domains,
              this.classificationIds,
              this.excludedClassificationIds,
              this.customAttributeFilter);

      report.addItems(
          detailedMonitorQueryItems,
          new DaysToWorkingDaysPreProcessor<>(this.columnHeaders, this.inWorkingDays));

      return report;
    } finally {
      this.taskanaEngine.returnConnection();
      LOGGER.debug("exit from buildDetailedReport().");
    }
  }

  @Override
  protected ClassificationReport.Builder _this() {
    return this;
  }

  @Override
  protected String determineGroupedBy() {
    return "CLASSIFICATION_KEY";
  }
}
