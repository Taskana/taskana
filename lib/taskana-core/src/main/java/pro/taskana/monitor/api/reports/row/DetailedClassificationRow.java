package pro.taskana.monitor.api.reports.row;

import pro.taskana.monitor.api.reports.ClassificationReport;
import pro.taskana.monitor.api.reports.item.DetailedMonitorQueryItem;

/**
 * Represents a single {@linkplain Row} inside {@linkplain
 * ClassificationReport.DetailedClassificationReport DetailedClassificationReport}.
 *
 * <p>The collapsing criteria is the attachmentKey of each {@linkplain DetailedMonitorQueryItem}.
 */
public class DetailedClassificationRow extends FoldableRow<DetailedMonitorQueryItem> {

  public DetailedClassificationRow(String key, int columnSize) {
    super(
        key, columnSize, item -> item.getAttachmentKey() != null ? item.getAttachmentKey() : "N/A");
  }

  @Override
  public SingleRow<DetailedMonitorQueryItem> getFoldableRow(String key) {
    return (SingleRow<DetailedMonitorQueryItem>) super.getFoldableRow(key);
  }

  @Override
  protected Row<DetailedMonitorQueryItem> buildRow(String key, int columnSize) {
    return new SingleRow<>(key, columnSize);
  }
}
