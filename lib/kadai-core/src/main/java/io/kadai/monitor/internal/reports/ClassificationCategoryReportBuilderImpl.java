package io.kadai.monitor.internal.reports;

import io.kadai.common.api.KadaiRole;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.internal.InternalKadaiEngine;
import io.kadai.monitor.api.TaskTimestamp;
import io.kadai.monitor.api.reports.ClassificationCategoryReport;
import io.kadai.monitor.api.reports.ClassificationCategoryReport.Builder;
import io.kadai.monitor.api.reports.header.TimeIntervalColumnHeader;
import io.kadai.monitor.api.reports.item.MonitorQueryItem;
import io.kadai.monitor.internal.MonitorMapper;
import io.kadai.monitor.internal.preprocessor.DaysToWorkingDaysReportPreProcessor;
import java.time.Instant;
import java.util.List;

/** The implementation of CategoryReportBuilder. */
public class ClassificationCategoryReportBuilderImpl
    extends TimeIntervalReportBuilderImpl<Builder, MonitorQueryItem, TimeIntervalColumnHeader>
    implements ClassificationCategoryReport.Builder {

  public ClassificationCategoryReportBuilderImpl(
      InternalKadaiEngine kadaiEngine, MonitorMapper monitorMapper) {
    super(kadaiEngine, monitorMapper);
  }

  @Override
  public ClassificationCategoryReport buildReport()
      throws InvalidArgumentException, NotAuthorizedException {
    return buildReport(TaskTimestamp.DUE);
  }

  @Override
  public ClassificationCategoryReport buildReport(TaskTimestamp timestamp)
      throws InvalidArgumentException, NotAuthorizedException {
    this.kadaiEngine.getEngine().checkRoleMembership(KadaiRole.MONITOR, KadaiRole.ADMIN);
    try {
      this.kadaiEngine.openConnection();
      ClassificationCategoryReport report = new ClassificationCategoryReport(this.columnHeaders);
      List<MonitorQueryItem> monitorQueryItems =
          this.monitorMapper.getTaskCountOfCategories(Instant.now(), timestamp, this);
      report.addItems(
          monitorQueryItems,
          new DaysToWorkingDaysReportPreProcessor<>(
              this.columnHeaders, workingTimeCalculator, this.inWorkingDays));
      return report;
    } finally {
      this.kadaiEngine.returnConnection();
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
