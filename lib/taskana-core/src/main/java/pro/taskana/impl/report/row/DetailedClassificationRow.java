package pro.taskana.impl.report.row;

import pro.taskana.impl.report.item.DetailedMonitorQueryItem;
import pro.taskana.report.structure.Row;

/**
 * Represents a single Row inside {@link pro.taskana.report.ClassificationReport.DetailedClassificationReport}.
 * The collapsing criteria is the attachement key of each {@link DetailedMonitorQueryItem}.
 */
public class DetailedClassificationRow extends FoldableRow<DetailedMonitorQueryItem> {

    public DetailedClassificationRow(int columnSize) {
        super(columnSize, (item) -> item.getAttachmentKey() != null ? item.getAttachmentKey() : "N/A");
    }

    @Override
    Row<DetailedMonitorQueryItem> buildRow(int columnSize) {
        return new SingleRow<>(columnSize);
    }

    @Override
    public SingleRow<DetailedMonitorQueryItem> getFoldableRow(String key) {
        return (SingleRow<DetailedMonitorQueryItem>) super.getFoldableRow(key);
    }
}
