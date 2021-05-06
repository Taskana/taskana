package pro.taskana.monitor.api.reports;

import java.util.List;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.monitor.api.CombinedClassificationFilter;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.MonitorQueryItem;

/**
 * A WorkbasketReport contains the total numbers of {@linkplain pro.taskana.task.api.models.Task
 * Tasks} of the respective {@linkplain pro.taskana.workbasket.api.models.Workbasket Workbasket} as
 * well as the total number of all {@linkplain pro.taskana.task.api.models.Task Tasks}.
 *
 * <p>The {@linkplain pro.taskana.task.api.models.Task Tasks} of the WorkbasketReport can be
 * filtered by {@linkplain pro.taskana.workbasket.api.models.Workbasket Workbaskets}, states,
 * categories, domains, {@linkplain pro.taskana.classification.api.models.Classification
 * Classifications} and values of a custom field. {@linkplain
 * pro.taskana.classification.api.models.Classification Classifications} can also be excluded from
 * the WorkbasketReport. It is also possible to filter by the {@linkplain
 * pro.taskana.classification.api.models.Classification Classifications} of the {@linkplain
 * pro.taskana.task.api.models.Attachment Attachments} by using the {@link
 * CombinedClassificationFilter}. If the {@linkplain TimeIntervalColumnHeader
 * TimeIntervalColumnHeaders} are set, the WorkbasketReport contains also the number of {@linkplain
 * pro.taskana.task.api.models.Task Tasks} of the respective cluster. The age of the {@linkplain
 * pro.taskana.task.api.models.Task Tasks} can be counted in days or in working days. {@linkplain
 * pro.taskana.task.api.models.Task Tasks} with Timestamp DUE = null are not considered.
 */
public class WorkbasketReport extends Report<MonitorQueryItem, TimeIntervalColumnHeader> {

  public WorkbasketReport(List<TimeIntervalColumnHeader> timeIntervalColumnHeaders) {
    super(timeIntervalColumnHeaders, new String[] {"WORKBASKET"});
  }

  /** Builder for {@linkplain WorkbasketReport}. */
  public interface Builder
      extends TimeIntervalReportBuilder<Builder, MonitorQueryItem, TimeIntervalColumnHeader> {

    @Override
    WorkbasketReport buildReport() throws NotAuthorizedException, InvalidArgumentException;

    @Override
    WorkbasketReport buildReport(TaskTimestamp timestamp)
        throws NotAuthorizedException, InvalidArgumentException;

    /**
     * Adds a list of {@linkplain CombinedClassificationFilter} to the Builder.
     *
     * <p>The created {@linkplain pro.taskana.workbasket.api.models.Workbasket Workbasket} contains
     * only {@linkplain pro.taskana.task.api.models.Task Tasks} with a pair of a classificationId
     * for a {@linkplain pro.taskana.task.api.models.Task Tasks} and a classificationId for the
     * corresponding {@linkplain pro.taskana.task.api.models.Attachment Attachment} in this list.
     *
     * @param combinedClassificationFilter a list of {@linkplain CombinedClassificationFilter}
     * @return the WorkbasketReportBuilder
     */
    WorkbasketReport.Builder combinedClassificationFilterIn(
        List<CombinedClassificationFilter> combinedClassificationFilter);
  }
}
