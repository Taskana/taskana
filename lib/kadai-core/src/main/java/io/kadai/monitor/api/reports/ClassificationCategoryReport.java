package io.kadai.monitor.api.reports;

import io.kadai.classification.api.models.Classification;
import io.kadai.common.api.TimeInterval;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.monitor.api.TaskTimestamp;
import io.kadai.monitor.api.reports.header.ColumnHeader;
import io.kadai.monitor.api.reports.header.TimeIntervalColumnHeader;
import io.kadai.monitor.api.reports.item.MonitorQueryItem;
import io.kadai.monitor.api.reports.row.Row;
import io.kadai.task.api.models.Task;
import java.util.List;

/**
 * A ClassificationCategoryReport aggregates {@linkplain Task} related data.
 *
 * <p>Each {@linkplain Row} represents a {@linkplain Classification} {@linkplain
 * Classification#getCategory() category}.
 *
 * <p>Each {@linkplain ColumnHeader} represents a {@linkplain TimeInterval}
 */
public class ClassificationCategoryReport
    extends Report<MonitorQueryItem, TimeIntervalColumnHeader> {

  public ClassificationCategoryReport(List<TimeIntervalColumnHeader> timeIntervalColumnHeaders) {
    super(timeIntervalColumnHeaders, new String[] {"CLASSIFICATION CATEGORIES"});
  }

  /** Builder for {@linkplain ClassificationCategoryReport}. */
  public interface Builder
      extends TimeIntervalReportBuilder<Builder, MonitorQueryItem, TimeIntervalColumnHeader> {

    @Override
    ClassificationCategoryReport buildReport()
        throws InvalidArgumentException, NotAuthorizedException;

    @Override
    ClassificationCategoryReport buildReport(TaskTimestamp timestamp)
        throws InvalidArgumentException, NotAuthorizedException;
  }
}
