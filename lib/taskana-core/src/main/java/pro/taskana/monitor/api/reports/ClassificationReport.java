package pro.taskana.monitor.api.reports;

import java.util.List;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.DetailedMonitorQueryItem;
import pro.taskana.monitor.api.reports.item.MonitorQueryItem;
import pro.taskana.monitor.api.reports.row.DetailedClassificationRow;
import pro.taskana.monitor.api.reports.row.FoldableRow;
import pro.taskana.monitor.api.reports.row.Row;

/**
 * The ClassificationReport extends the {@linkplain Report}.
 *
 * <p>The {@linkplain Row Rows} of the ClassificationReport are grouped by {@linkplain
 * pro.taskana.classification.api.models.Classification Classifications}.
 */
public class ClassificationReport extends Report<MonitorQueryItem, TimeIntervalColumnHeader> {

  public ClassificationReport(List<TimeIntervalColumnHeader> timeIntervalColumnHeaders) {
    super(timeIntervalColumnHeaders, new String[] {"CLASSIFICATION"});
  }

  /** Builder for {@linkplain ClassificationReport}. */
  public interface Builder
      extends TimeIntervalReportBuilder<Builder, MonitorQueryItem, TimeIntervalColumnHeader> {

    @Override
    ClassificationReport buildReport() throws NotAuthorizedException, InvalidArgumentException;

    @Override
    ClassificationReport buildReport(TaskTimestamp timestamp)
        throws NotAuthorizedException, InvalidArgumentException;

    /**
     * Returns a {@linkplain DetailedClassificationReport DetailedClassificationReport} containing
     * all {@linkplain pro.taskana.task.api.models.Task Tasks} after applying the filters.
     *
     * <p>If the {@linkplain pro.taskana.monitor.api.reports.header.ColumnHeader ColumnHeaders} are
     * set the {@linkplain DetailedClassificationReport DetailedClassificationReport} is subdivided
     * into clusters. Its {@linkplain FoldableRow FoldableRows} contain an additional list of
     * {@linkplain Row Rows} for the {@linkplain
     * pro.taskana.classification.api.models.Classification Classifications} of the {@linkplain
     * pro.taskana.task.api.models.Attachment Attachments} of the {@linkplain
     * pro.taskana.task.api.models.Task Tasks}.
     *
     * @return the {@linkplain DetailedClassificationReport DetailedClassificationReport}
     * @throws InvalidArgumentException if the {@linkplain
     *     pro.taskana.monitor.api.reports.header.ColumnHeader ColumnHeads} are not initialized
     * @throws NotAuthorizedException if the user has no rights to access the monitor
     */
    DetailedClassificationReport buildDetailedReport()
        throws InvalidArgumentException, NotAuthorizedException;

    DetailedClassificationReport buildDetailedReport(TaskTimestamp timestamp)
        throws InvalidArgumentException, NotAuthorizedException;
  }

  /**
   * The DetailedClassificationReport is a functional extension of the {@linkplain
   * ClassificationReport}.
   *
   * <p>Its {@linkplain FoldableRow FoldableRows} contain an additional list of {@linkplain Row
   * Rows} for the {@linkplain pro.taskana.classification.api.models.Classification Classifications}
   * of the {@linkplain pro.taskana.task.api.models.Attachment Attachments} of the {@linkplain
   * pro.taskana.task.api.models.Task Tasks}.
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
