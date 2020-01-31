package pro.taskana.report.api;

import java.util.List;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.report.api.structure.Report;
import pro.taskana.report.internal.header.TimeIntervalColumnHeader;
import pro.taskana.report.internal.item.MonitorQueryItem;

/**
 * A CustomFieldValueReport contains the total numbers of tasks of the respective custom field as
 * well as the total number of all tasks. The tasks of the report can be filtered by workbaskets,
 * states, categories, domains, classifications and values of a custom field. Classifications can
 * also be excluded from the report. If the {@link TimeIntervalColumnHeader}s are set, the report
 * contains also the number of tasks of the respective cluster. The age of the tasks can be counted
 * in days or in working days. Tasks with Timestamp DUE = null are not considered.
 */
public class CustomFieldValueReport extends Report<MonitorQueryItem, TimeIntervalColumnHeader> {

  public CustomFieldValueReport(List<TimeIntervalColumnHeader> timeIntervalColumnHeaders) {
    super(timeIntervalColumnHeaders, new String[] {"CUSTOM FIELDS"});
  }

  /** Builder for {@link CustomFieldValueReport}. */
  public interface Builder
      extends TimeIntervalReportBuilder<Builder, MonitorQueryItem, TimeIntervalColumnHeader> {

    @Override
    CustomFieldValueReport buildReport() throws NotAuthorizedException, InvalidArgumentException;
  }
}
