package io.kadai.monitor.internal.reports;

import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.common.api.KadaiRole;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.internal.InternalKadaiEngine;
import io.kadai.monitor.api.TaskTimestamp;
import io.kadai.monitor.api.reports.ClassificationReport;
import io.kadai.monitor.api.reports.ClassificationReport.Builder;
import io.kadai.monitor.api.reports.ClassificationReport.DetailedClassificationReport;
import io.kadai.monitor.api.reports.header.TimeIntervalColumnHeader;
import io.kadai.monitor.api.reports.item.DetailedMonitorQueryItem;
import io.kadai.monitor.api.reports.item.MonitorQueryItem;
import io.kadai.monitor.api.reports.row.Row;
import io.kadai.monitor.internal.MonitorMapper;
import io.kadai.monitor.internal.preprocessor.DaysToWorkingDaysReportPreProcessor;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** The implementation of ClassificationReportBuilder. */
public class ClassificationReportBuilderImpl
    extends TimeIntervalReportBuilderImpl<Builder, MonitorQueryItem, TimeIntervalColumnHeader>
    implements ClassificationReport.Builder {

  private final ClassificationService classificationService;

  public ClassificationReportBuilderImpl(
      InternalKadaiEngine kadaiEngine, MonitorMapper monitorMapper) {
    super(kadaiEngine, monitorMapper);
    classificationService = kadaiEngine.getEngine().getClassificationService();
  }

  @Override
  public ClassificationReport buildReport()
      throws InvalidArgumentException, NotAuthorizedException {
    return buildReport(TaskTimestamp.DUE);
  }

  @Override
  public ClassificationReport buildReport(TaskTimestamp timestamp)
      throws InvalidArgumentException, NotAuthorizedException {
    this.kadaiEngine.getEngine().checkRoleMembership(KadaiRole.MONITOR, KadaiRole.ADMIN);
    try {
      this.kadaiEngine.openConnection();
      ClassificationReport report = new ClassificationReport(this.columnHeaders);
      List<MonitorQueryItem> monitorQueryItems =
          this.monitorMapper.getTaskCountOfClassifications(Instant.now(), timestamp, this);
      report.addItems(
          monitorQueryItems,
          new DaysToWorkingDaysReportPreProcessor<>(
              this.columnHeaders, workingTimeCalculator, this.inWorkingDays));
      Map<String, String> displayMap =
          classificationService
              .createClassificationQuery()
              .keyIn(
                  report.getRows().isEmpty()
                      ? null
                      : report.getRows().keySet().toArray(new String[0]))
              .domainIn(this.domains)
              .list()
              .stream()
              .collect(
                  Collectors.toMap(
                      ClassificationSummary::getKey, ClassificationSummary::getName, (a, b) -> a));
      report.augmentDisplayNames(displayMap);
      return report;
    } finally {
      this.kadaiEngine.returnConnection();
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
    this.kadaiEngine.getEngine().checkRoleMembership(KadaiRole.MONITOR, KadaiRole.ADMIN);
    try {
      this.kadaiEngine.openConnection();
      DetailedClassificationReport report = new DetailedClassificationReport(this.columnHeaders);
      List<DetailedMonitorQueryItem> detailedMonitorQueryItems =
          this.monitorMapper.getTaskCountOfDetailedClassifications(Instant.now(), timestamp, this);

      report.addItems(
          detailedMonitorQueryItems,
          new DaysToWorkingDaysReportPreProcessor<>(
              this.columnHeaders, workingTimeCalculator, this.inWorkingDays));
      Stream<String> attachmentKeys =
          report.getRows().keySet().stream()
              .map(report::getRow)
              .flatMap(row -> row.getFoldableRows().values().stream())
              .map(Row::getKey);
      String[] keys =
          Stream.concat(attachmentKeys, report.getRows().keySet().stream()).toArray(String[]::new);
      Map<String, String> displayMap =
          classificationService
              .createClassificationQuery()
              .keyIn(keys.length == 0 ? null : keys)
              .domainIn(this.domains)
              .list()
              .stream()
              .collect(
                  Collectors.toMap(
                      ClassificationSummary::getKey, ClassificationSummary::getName, (a, b) -> a));
      report.augmentDisplayNames(displayMap);
      return report;
    } finally {
      this.kadaiEngine.returnConnection();
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
