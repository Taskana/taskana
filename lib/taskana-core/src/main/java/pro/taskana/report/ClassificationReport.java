package pro.taskana.report;

import java.util.List;

import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.header.TimeIntervalColumnHeader;
import pro.taskana.impl.report.item.DetailedMonitorQueryItem;
import pro.taskana.impl.report.item.MonitorQueryItem;
import pro.taskana.impl.report.row.DetailedClassificationRow;
import pro.taskana.impl.report.structure.Report;

/**
 * The ClassificationReport extends the Report. The {@link pro.taskana.impl.report.structure.Row}s
 * of the ClassificationReport are grouped by classifications.
 */
public class ClassificationReport extends Report<MonitorQueryItem, TimeIntervalColumnHeader> {

  public ClassificationReport(List<TimeIntervalColumnHeader> timeIntervalColumnHeaders) {
    super(timeIntervalColumnHeaders, new String[] {"CLASSIFICATION KEYS"});
  }

  /** Builder for {@link ClassificationReport}. */
  public interface Builder
      extends TimeIntervalReportBuilder<Builder, MonitorQueryItem, TimeIntervalColumnHeader> {

    @Override
    ClassificationReport buildReport() throws NotAuthorizedException, InvalidArgumentException;

    /**
     * Returns a {@link DetailedClassificationReport} containing all tasks after applying the
     * filters. If the column headers are set the report is subdivided into clusters. Its {@link
     * pro.taskana.impl.report.row.FoldableRow}s contain an additional list of {@link
     * pro.taskana.impl.report.structure.Row}s for the classifications of the attachments of the
     * tasks.
     *
     * @throws InvalidArgumentException if the column headers are not initialized
     * @throws NotAuthorizedException if the user has no rights to access the monitor
     * @return the DetailedClassificationReport
     */
    DetailedClassificationReport buildDetailedReport()
        throws InvalidArgumentException, NotAuthorizedException;
  }

  /**
   * The DetailedClassificationReport is a functional extension of the {@link ClassificationReport}.
   * Its {@link pro.taskana.impl.report.row.FoldableRow}s contain an additional list of {@link
   * pro.taskana.impl.report.structure.Row}s for the classifications of the attachments of the
   * tasks.
   */
  public static class DetailedClassificationReport
      extends Report<DetailedMonitorQueryItem, TimeIntervalColumnHeader> {

    public DetailedClassificationReport(
        List<TimeIntervalColumnHeader> workbasketLevelReportColumnHeaders) {
      super(
          workbasketLevelReportColumnHeaders,
          new String[] {"TASK CLASSIFICATION KEYS", "ATTACHMENT"});
    }

    @Override
    public DetailedClassificationRow getRow(String key) {
      return (DetailedClassificationRow) super.getRow(key);
    }

    @Override
    protected DetailedClassificationRow createRow(int columnSize) {
      return new DetailedClassificationRow(columnSize);
    }
  }
}
