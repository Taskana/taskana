package pro.taskana.monitor.api.reports;

import java.util.List;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.MonitorQueryItem;

/**
 * A TaskCustomFieldValueReport contains the total numbers of {@linkplain
 * pro.taskana.task.api.models.Task Tasks} of the respective custom field as well as the total
 * number of all {@linkplain pro.taskana.task.api.models.Task Tasks}.
 *
 * <p>The {@linkplain pro.taskana.task.api.models.Task Tasks} of the TaskCustomFieldValueReport can
 * be filtered by {@linkplain pro.taskana.workbasket.api.models.Workbasket Workbaskets}, states,
 * categories, domains, {@linkplain pro.taskana.classification.api.models.Classification
 * Classifications} and values of a custom field. {@linkplain
 * pro.taskana.classification.api.models.Classification Classifications} can also be excluded from
 * the TaskCustomFieldValueReport. If the {@linkplain TimeIntervalColumnHeader
 * TimeIntervalColumnHeaders} are set, the TaskCustomFieldValueRepor} contains also the number of
 * {@linkplain pro.taskana.task.api.models.Task Tasks} of the respective cluster. The age of the
 * {@linkplain pro.taskana.task.api.models.Task Tasks} can be counted in days or in working days.
 * {@linkplain pro.taskana.task.api.models.Task Tasks} with Timestamp DUE = null are not considered.
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
        throws NotAuthorizedException, InvalidArgumentException;

    @Override
    TaskCustomFieldValueReport buildReport(TaskTimestamp timestamp)
        throws NotAuthorizedException, InvalidArgumentException;
  }
}
