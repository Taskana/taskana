package io.kadai.monitor.internal.reports;

import io.kadai.common.api.KadaiRole;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.internal.InternalKadaiEngine;
import io.kadai.monitor.api.CombinedClassificationFilter;
import io.kadai.monitor.api.TaskTimestamp;
import io.kadai.monitor.api.reports.WorkbasketReport;
import io.kadai.monitor.api.reports.WorkbasketReport.Builder;
import io.kadai.monitor.api.reports.header.TimeIntervalColumnHeader;
import io.kadai.monitor.api.reports.item.MonitorQueryItem;
import io.kadai.monitor.internal.MonitorMapper;
import io.kadai.monitor.internal.preprocessor.DaysToWorkingDaysReportPreProcessor;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WorkbasketReportBuilderImpl
    extends TimeIntervalReportBuilderImpl<Builder, MonitorQueryItem, TimeIntervalColumnHeader>
    implements WorkbasketReport.Builder {

  private final WorkbasketService workbasketService;
  private List<CombinedClassificationFilter> combinedClassificationFilter;

  public WorkbasketReportBuilderImpl(InternalKadaiEngine kadaiEngine, MonitorMapper monitorMapper) {
    super(kadaiEngine, monitorMapper);
    workbasketService = kadaiEngine.getEngine().getWorkbasketService();
  }

  @Override
  public WorkbasketReport buildReport() throws InvalidArgumentException, NotAuthorizedException {
    return buildReport(TaskTimestamp.DUE);
  }

  @Override
  public WorkbasketReport buildReport(TaskTimestamp timestamp)
      throws InvalidArgumentException, NotAuthorizedException {
    this.kadaiEngine.getEngine().checkRoleMembership(KadaiRole.MONITOR, KadaiRole.ADMIN);
    try {
      this.kadaiEngine.openConnection();
      WorkbasketReport report = new WorkbasketReport(this.columnHeaders);
      List<MonitorQueryItem> monitorQueryItems =
          this.monitorMapper.getTaskCountOfWorkbaskets(Instant.now(), timestamp, this);
      report.addItems(
          monitorQueryItems,
          new DaysToWorkingDaysReportPreProcessor<>(
              this.columnHeaders, workingTimeCalculator, this.inWorkingDays));

      Map<String, String> displayMap =
          kadaiEngine
              .getEngine()
              .runAsAdmin(
                  () ->
                      workbasketService
                          .createWorkbasketQuery()
                          .keyIn(report.getRows().keySet().toArray(new String[0]))
                          .domainIn(domains)
                          .list()
                          .stream()
                          .collect(
                              Collectors.toMap(
                                  WorkbasketSummary::getKey,
                                  WorkbasketSummary::getName,
                                  (a, b) -> a)));
      report.augmentDisplayNames(displayMap);
      return report;
    } finally {
      this.kadaiEngine.returnConnection();
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

  @Override
  protected List<CombinedClassificationFilter> getCombinedClassificationFilter() {
    return combinedClassificationFilter;
  }
}
