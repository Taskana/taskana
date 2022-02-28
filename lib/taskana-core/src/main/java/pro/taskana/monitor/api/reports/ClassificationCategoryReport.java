package pro.taskana.monitor.api.reports;

import java.util.List;

import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.header.ColumnHeader;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.MonitorQueryItem;
import pro.taskana.monitor.api.reports.row.Row;
import pro.taskana.task.api.models.Task;

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
        throws NotAuthorizedException, InvalidArgumentException;

    @Override
    ClassificationCategoryReport buildReport(TaskTimestamp timestamp)
        throws NotAuthorizedException, InvalidArgumentException;
  }
}
