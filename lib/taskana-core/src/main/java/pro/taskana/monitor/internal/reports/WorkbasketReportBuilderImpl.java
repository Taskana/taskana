package pro.taskana.monitor.internal.reports;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.monitor.api.CombinedClassificationFilter;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.WorkbasketReport;
import pro.taskana.monitor.api.reports.WorkbasketReport.Builder;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.MonitorQueryItem;
import pro.taskana.monitor.internal.MonitorMapper;
import pro.taskana.monitor.internal.preprocessor.DaysToWorkingDaysReportPreProcessor;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** The implementation of WorkbasketReportBuilder. */
public class WorkbasketReportBuilderImpl
    extends TimeIntervalReportBuilderImpl<Builder, MonitorQueryItem, TimeIntervalColumnHeader>
    implements WorkbasketReport.Builder {

  private static final Logger LOGGER = LoggerFactory.getLogger(WorkbasketReportBuilderImpl.class);
  private final WorkbasketService workbasketService;
  private List<CombinedClassificationFilter> combinedClassificationFilter;

  public WorkbasketReportBuilderImpl(
      InternalTaskanaEngine taskanaEngine, MonitorMapper monitorMapper) {
    super(taskanaEngine, monitorMapper);
    workbasketService = taskanaEngine.getEngine().getWorkbasketService();
  }

  @Override
  public WorkbasketReport buildReport() throws NotAuthorizedException, InvalidArgumentException {
    return buildReport(TaskTimestamp.DUE);
  }

  @Override
  public WorkbasketReport buildReport(TaskTimestamp timestamp)
      throws InvalidArgumentException, NotAuthorizedException {
    LOGGER.debug("entry to buildReport(), this = {}", this);
    this.taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR, TaskanaRole.ADMIN);
    try {
      this.taskanaEngine.openConnection();
      WorkbasketReport report = new WorkbasketReport(this.columnHeaders);
      List<MonitorQueryItem> monitorQueryItems =
          this.monitorMapper.getTaskCountOfWorkbaskets(
              this.workbasketIds,
              this.states,
              this.classificationCategory,
              this.domains,
              timestamp,
              this.classificationIds,
              this.excludedClassificationIds,
              this.customAttributeFilter,
              this.combinedClassificationFilter);
      report.addItems(
          monitorQueryItems,
          new DaysToWorkingDaysReportPreProcessor<>(
              this.columnHeaders, converter, this.inWorkingDays));

      Map<String, String> displayMap =
          taskanaEngine.runAsAdmin(
              () ->
                  workbasketService
                      .createWorkbasketQuery()
                      .keyIn(report.getRows().keySet().toArray(new String[0]))
                      .domainIn(domains != null ? domains.toArray(new String[0]) : null)
                      .list()
                      .stream()
                      .collect(
                          Collectors.toMap(
                              WorkbasketSummary::getKey, WorkbasketSummary::getName, (a, b) -> a)));
      report.augmentDisplayNames(displayMap);
      return report;
    } finally {
      this.taskanaEngine.returnConnection();
      LOGGER.debug("exit from buildReport().");
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
