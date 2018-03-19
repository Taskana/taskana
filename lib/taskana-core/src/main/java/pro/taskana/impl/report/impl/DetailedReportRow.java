package pro.taskana.impl.report.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import pro.taskana.impl.report.ReportRow;

/**
 * The DetailedReportRow extends the {@link ReportRow}.
 * In contrast to the ReportRow there is an additional list of ReportRows for the classifications of the
 * attachments of the tasks.
 */
public class DetailedReportRow extends ReportRow<DetailedMonitorQueryItem> {

    private Map<String, ReportRow<DetailedMonitorQueryItem>> detailRows = new LinkedHashMap<>();
    private int columnCount;

    public DetailedReportRow(int columnCount) {
        super(columnCount);
        this.columnCount = columnCount;
    }

    public Map<String, ReportRow<DetailedMonitorQueryItem>> getDetailRows() {
        return detailRows;
    }

    @Override
    public void updateTotalValue(DetailedMonitorQueryItem item) {
        super.updateTotalValue(item);

        if (item.getAttachmentKey() != null) {
            detailRows.computeIfAbsent(item.getAttachmentKey(), (s) -> new ReportRow<>(columnCount))
                .updateTotalValue(item);
        } else {
            detailRows.computeIfAbsent("N/A", (s) -> new ReportRow<>(columnCount))
                .updateTotalValue(item);
        }
    }

    @Override
    public void addItem(DetailedMonitorQueryItem item, int index) {
        super.addItem(item, index);

        if (item.getAttachmentKey() != null) {
            detailRows.computeIfAbsent(item.getAttachmentKey(), (s) -> new ReportRow<>(columnCount))
                .addItem(item, index);
        } else {
            detailRows.computeIfAbsent("N/A", (s) -> new ReportRow<>(columnCount))
                .addItem(item, index);
        }
    }

}
