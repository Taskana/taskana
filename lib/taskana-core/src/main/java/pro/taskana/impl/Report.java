package pro.taskana.impl;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A Report represents a table that consists of {@link ReportLine} objects. The detailLines are the rows of the table
 * that contains the total number of all tasks and could be optionally subdivided into different sections. The sumLine
 * contains the sums of all tasks and if the detailLines are subdivided into different sections the sumLine also
 * contains the number of tasks of the respective section.
 */
public class Report {

    private Map<String, ReportLine> detailLines;
    private ReportLine sumLine;

    public Report() {
        this.detailLines = new LinkedHashMap<>();
        this.sumLine = new ReportLine();
    }

    public Map<String, ReportLine> getDetailLines() {
        return detailLines;
    }

    public void setDetailLines(Map<String, ReportLine> detailLines) {
        this.detailLines = detailLines;
    }

    public ReportLine getSumLine() {
        return sumLine;
    }

    public void setSumLine(ReportLine sumLine) {
        this.sumLine = sumLine;
    }

    public void generateSumLine(ReportLine sumLine) {
        this.sumLine = sumLine;
        int totalNumberOfTasks = 0;
        for (ReportLine reportLine : this.getDetailLines().values()) {
            Iterator<ReportLineItem> reportLineItemIterator = reportLine.getLineItems().iterator();
            Iterator<ReportLineItem> sumLineItemIterator = this.sumLine.getLineItems().iterator();
            while (reportLineItemIterator.hasNext() && sumLineItemIterator.hasNext()) {
                int numberOfTasks = reportLineItemIterator.next().getNumberOfTasks();
                sumLineItemIterator.next().addNumberOfTasks(numberOfTasks);
            }
            totalNumberOfTasks += reportLine.getTotalNumberOfTasks();
        }
        this.sumLine.setTotalNumberOfTasks(totalNumberOfTasks);
    }

}
