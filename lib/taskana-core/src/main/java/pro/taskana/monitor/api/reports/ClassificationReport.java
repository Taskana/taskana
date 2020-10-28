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
 * The ClassificationReport extends the Report. The {@link Row}s of the ClassificationReport are
 * grouped by classifications.
 */
public class ClassificationReport extends Report<MonitorQueryItem, TimeIntervalColumnHeader> {

  public ClassificationReport(List<TimeIntervalColumnHeader> timeIntervalColumnHeaders) {
    super(timeIntervalColumnHeaders, new String[] {"CLASSIFICATION"});
  }

  /** Builder for {@link ClassificationReport}. */
  public interface Builder
      extends TimeIntervalReportBuilder<Builder, MonitorQueryItem, TimeIntervalColumnHeader> {

    @Override
    ClassificationReport buildReport() throws NotAuthorizedException, InvalidArgumentException;

    @Override
    ClassificationReport buildReport(TaskTimestamp timestamp)
        throws NotAuthorizedException, InvalidArgumentException;

    /**
     * Returns a {@link DetailedClassificationReport} containing all tasks after applying the
     * filters. If the column headers are set the report is subdivided into clusters. Its {@link
     * FoldableRow}s contain an additional list of {@link Row}s for the classifications of the
     * attachments of the tasks.
     *
     * @return the DetailedClassificationReport
     * @throws InvalidArgumentException if the column headers are not initialized
     * @throws NotAuthorizedException if the user has no rights to access the monitor
     */
    DetailedClassificationReport buildDetailedReport()
        throws InvalidArgumentException, NotAuthorizedException;

    DetailedClassificationReport buildDetailedReport(TaskTimestamp timestamp)
        throws InvalidArgumentException, NotAuthorizedException;
  }

  /**
   * The DetailedClassificationReport is a functional extension of the {@link ClassificationReport}.
   * Its {@link FoldableRow}s contain an additional list of {@link Row}s for the classifications of
   * the attachments of the tasks.
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
