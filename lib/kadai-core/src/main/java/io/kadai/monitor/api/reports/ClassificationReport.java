package io.kadai.monitor.api.reports;

import io.kadai.classification.api.models.Classification;
import io.kadai.common.api.KadaiRole;
import io.kadai.common.api.TimeInterval;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.monitor.api.TaskTimestamp;
import io.kadai.monitor.api.reports.header.ColumnHeader;
import io.kadai.monitor.api.reports.header.TimeIntervalColumnHeader;
import io.kadai.monitor.api.reports.item.DetailedMonitorQueryItem;
import io.kadai.monitor.api.reports.item.MonitorQueryItem;
import io.kadai.monitor.api.reports.row.DetailedClassificationRow;
import io.kadai.monitor.api.reports.row.FoldableRow;
import io.kadai.monitor.api.reports.row.Row;
import io.kadai.task.api.models.Attachment;
import io.kadai.task.api.models.Task;
import java.util.List;

/**
 * A ClassificationReport aggregates {@linkplain Task} related data.
 *
 * <p>Each {@linkplain Row} represents a {@linkplain Classification}.
 *
 * <p>Each {@linkplain ColumnHeader} represents a {@linkplain TimeInterval}.
 */
public class ClassificationReport extends Report<MonitorQueryItem, TimeIntervalColumnHeader> {

  public ClassificationReport(List<TimeIntervalColumnHeader> timeIntervalColumnHeaders) {
    super(timeIntervalColumnHeaders, new String[] {"CLASSIFICATION"});
  }

  /** Builder for {@linkplain ClassificationReport}. */
  public interface Builder
      extends TimeIntervalReportBuilder<Builder, MonitorQueryItem, TimeIntervalColumnHeader> {

    @Override
    ClassificationReport buildReport() throws InvalidArgumentException, NotAuthorizedException;

    @Override
    ClassificationReport buildReport(TaskTimestamp timestamp)
        throws InvalidArgumentException, NotAuthorizedException;

    /**
     * Returns a {@linkplain DetailedClassificationReport} containing all tasks after applying the
     * filters. If the column headers are set the report is subdivided into clusters. Its {@link
     * FoldableRow}s contain an additional list of {@linkplain Row}s for the classifications of the
     * attachments of the tasks.
     *
     * @return the DetailedClassificationReport
     * @throws InvalidArgumentException if the column headers are not initialized
     * @throws NotAuthorizedException if the current user is not member of {@linkplain
     *     KadaiRole#MONITOR} or {@linkplain KadaiRole#ADMIN}
     */
    DetailedClassificationReport buildDetailedReport()
        throws InvalidArgumentException, NotAuthorizedException;

    DetailedClassificationReport buildDetailedReport(TaskTimestamp timestamp)
        throws InvalidArgumentException, NotAuthorizedException;
  }

  /**
   * A DetailedClassificationReport aggregates {@linkplain Task} related data.
   *
   * <p>Each {@linkplain FoldableRow} represents a {@linkplain Classification} and can be expanded
   * to show the {@linkplain Classification} of {@linkplain Attachment}s.
   *
   * <p>Each {@linkplain ColumnHeader} represents a {@linkplain TimeInterval}.
   */
  public static class DetailedClassificationReport
      extends Report<DetailedMonitorQueryItem, TimeIntervalColumnHeader> {

    public DetailedClassificationReport(
        List<TimeIntervalColumnHeader> workbasketLevelReportColumnHeaders) {
      super(workbasketLevelReportColumnHeaders, new String[] {"TASK CLASSIFICATION", "ATTACHMENT"});
    }

    @Override
    public DetailedClassificationRow getRow(String key) {
      return (DetailedClassificationRow) super.getRow(key);
    }

    @Override
    protected DetailedClassificationRow createRow(String key, int columnSize) {
      return new DetailedClassificationRow(key, columnSize);
    }
  }
}
