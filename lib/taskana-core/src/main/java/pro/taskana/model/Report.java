package pro.taskana.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A Report represents a table that consists of {@link ReportLine} objects.
 */
public class Report {

    private List<ReportLine> detailLines;
    private ReportLine sumLine;

    public Report() {
        this.detailLines = new ArrayList<>();
        this.sumLine = new ReportLine();
    }

    public List<ReportLine> getDetailLines() {
        return detailLines;
    }

    public void setDetailLines(List<ReportLine> detailLines) {
        this.detailLines = detailLines;
    }

    public ReportLine getSumLine() {
        return sumLine;
    }

    public void setSumLine(ReportLine sumLine) {
        this.sumLine = sumLine;
    }
}
