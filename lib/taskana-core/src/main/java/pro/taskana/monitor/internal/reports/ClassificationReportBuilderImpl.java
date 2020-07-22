package pro.taskana.monitor.internal.reports;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.ClassificationReport;
import pro.taskana.monitor.api.reports.ClassificationReport.Builder;
import pro.taskana.monitor.api.reports.ClassificationReport.DetailedClassificationReport;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.DetailedMonitorQueryItem;
import pro.taskana.monitor.api.reports.item.MonitorQueryItem;
import pro.taskana.monitor.api.reports.row.Row;
import pro.taskana.monitor.internal.MonitorMapper;
import pro.taskana.monitor.internal.preprocessor.DaysToWorkingDaysReportPreProcessor;

/** The implementation of ClassificationReportBuilder. */
public class ClassificationReportBuilderImpl
    extends TimeIntervalReportBuilderImpl<Builder, MonitorQueryItem, TimeIntervalColumnHeader>
    implements ClassificationReport.Builder {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ClassificationReportBuilderImpl.class);
  private final ClassificationService classificationService;

  public ClassificationReportBuilderImpl(
      InternalTaskanaEngine taskanaEngine, MonitorMapper monitorMapper) {
    super(taskanaEngine, monitorMapper);
    classificationService = taskanaEngine.getEngine().getClassificationService();
  }

  @Override
  public ClassificationReport buildReport()
      throws NotAuthorizedException, InvalidArgumentException {
    return buildReport(TaskTimestamp.DUE);
  }

  @Override
  public ClassificationReport buildReport(TaskTimestamp timestamp)
      throws InvalidArgumentException, NotAuthorizedException {
    LOGGER.debug("entry to buildReport(), this = {}", this);
    this.taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR, TaskanaRole.ADMIN);
    try {
      this.taskanaEngine.openConnection();
      ClassificationReport report = new ClassificationReport(this.columnHeaders);
      List<MonitorQueryItem> monitorQueryItems =
          this.monitorMapper.getTaskCountOfClassifications(
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
      Map<String, String> displayMap =
          classificationService
              .createClassificationQuery()
              .keyIn(report.getRows().keySet().toArray(new String[0]))
              .domainIn(domains != null ? domains.toArray(new String[0]) : null)
              .list()
              .stream()
              .collect(
                  Collectors.toMap(
                      ClassificationSummary::getKey, ClassificationSummary::getName, (a, b) -> a));
      report.augmentDisplayNames(displayMap);
      return report;
    } finally {
      this.taskanaEngine.returnConnection();
      LOGGER.debug("exit from buildReport().");
    }
  }

  @Override
  public DetailedClassificationReport buildDetailedReport()
      throws InvalidArgumentException, NotAuthorizedException {
    return buildDetailedReport(TaskTimestamp.DUE);
  }

  @Override
  public DetailedClassificationReport buildDetailedReport(TaskTimestamp timestamp)
      throws InvalidArgumentException, NotAuthorizedException {
    LOGGER.debug("entry to buildDetailedReport(), this = {}", this);
    this.taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR, TaskanaRole.ADMIN);
    try {
      this.taskanaEngine.openConnection();
      DetailedClassificationReport report = new DetailedClassificationReport(this.columnHeaders);
      List<DetailedMonitorQueryItem> detailedMonitorQueryItems =
          this.monitorMapper.getTaskCountOfDetailedClassifications(
              this.workbasketIds,
              this.states,
              this.classificationCategory,
              this.domains,
              timestamp,
              this.classificationIds,
              this.excludedClassificationIds,
              this.customAttributeFilter);

      report.addItems(
          detailedMonitorQueryItems,
          new DaysToWorkingDaysReportPreProcessor<>(
              this.columnHeaders, converter, this.inWorkingDays));
      Stream<String> attachmentKeys =
          report.getRows().keySet().stream()
              .map(report::getRow)
              .flatMap(row -> row.getFoldableRows().values().stream())
              .map(Row::getKey);
      Map<String, String> displayMap =
          classificationService
              .createClassificationQuery()
              .keyIn(
                  Stream.concat(attachmentKeys, report.getRows().keySet().stream())
                      .toArray(String[]::new))
              .domainIn(domains != null ? domains.toArray(new String[0]) : null)
              .list()
              .stream()
              .collect(
                  Collectors.toMap(
                      ClassificationSummary::getKey, ClassificationSummary::getName, (a, b) -> a));
      report.augmentDisplayNames(displayMap);
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
