package pro.taskana.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The DetailedReportLine extends the {@link ReportLine}. In contrast to the ReportLine there is an additional list of
 * ReportLines for the classifications of the attachments of the tasks.
 */
public class DetailedReportLine extends ReportLine {

    private Map<String, ReportLine> detailLines;

    public DetailedReportLine() {
        super();
        this.detailLines = new LinkedHashMap<>();
    }

    public Map<String, ReportLine> getDetailLines() {
        return detailLines;
    }

    public void setDetailLines(Map<String, ReportLine> reportLines) {
        this.detailLines = reportLines;
    }

    /**
     * Adds the number of tasks of the {@link DetailedMonitorQueryItem} to the respective {@link ReportLineItem} of this
     * {@link DetailedReportLine} and and of the suitable sub line.
     *
     * @param item
     *            a {@link DetailedMonitorQueryItem} that contains the number of tasks, the age in days of these tasks
     *            and the Classification key of the corresponding attachment.
     * @param reportLineItemDefinitions
     *            a list of {@link ReportLineItemDefinition}s that is needed to create a sub line if necessary.
     */
    public void addNumberOfTasks(DetailedMonitorQueryItem item,
        List<ReportLineItemDefinition> reportLineItemDefinitions) {
        super.addNumberOfTasks(item);

        if (item.getAttachmentKey() != null) {
            if (!this.detailLines.containsKey(item.getAttachmentKey())) {
                ReportLine reportLine = new ReportLine();
                reportLine.create(reportLineItemDefinitions);
                this.detailLines.put(item.getAttachmentKey(), reportLine);
            }
            this.detailLines.get(item.getAttachmentKey()).addNumberOfTasks(item);
        } else {
            if (!this.detailLines.containsKey("N/A")) {
                ReportLine reportLine = new ReportLine();
                reportLine.create(reportLineItemDefinitions);
                this.detailLines.put("N/A", reportLine);
            }
            this.detailLines.get("N/A").addNumberOfTasks(item);
        }
    }

}
