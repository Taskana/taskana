package pro.taskana.monitor.api.reports;

import java.util.List;

import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.header.ColumnHeader;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.MonitorQueryItem;
import pro.taskana.monitor.api.reports.row.Row;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.models.Task;

/**
 * A TaskCustomFieldValueReport aggregates {@linkplain Task} related data.
 *
 * <p>Each {@linkplain Row} represents a value of the requested {@linkplain TaskCustomField}.
 *
 * <p>Each {@linkplain ColumnHeader} represents a {@linkplain TimeInterval}.
 */
public class TaskCustomFieldValueReport extends Report<MonitorQueryItem, TimeIntervalColumnHeader> {

  public TaskCustomFieldValueReport(List<TimeIntervalColumnHeader> timeIntervalColumnHeaders) {
    super(timeIntervalColumnHeaders, new String[] {"TASK CUSTOM FIELDS"});
  }

  /** Builder for {@linkplain TaskCustomFieldValueReport}. */
  public interface Builder
      extends TimeIntervalReportBuilder<Builder, MonitorQueryItem, TimeIntervalColumnHeader> {

    @Override
    TaskCustomFieldValueReport buildReport()
        throws InvalidArgumentException, NotAuthorizedException;

    @Override
    TaskCustomFieldValueReport buildReport(TaskTimestamp timestamp)
        throws InvalidArgumentException, NotAuthorizedException;
  }
}
