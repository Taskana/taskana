package pro.taskana.impl;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A Report represents a table that consists of {@link ReportLine} objects. The detailLines are the rows of the table
 * that contains the total number of all tasks and could be optionally subdivided into different sections. The sumLine
 * contains the sums of all tasks and if the detailLines are subdivided into different sections the sumLine also
 * contains the number of tasks of the respective section.
 */
public class Report {

    protected Map<String, ReportLine> reportLines;
    protected ReportLine sumLine;

    public Report() {
        this.reportLines = new LinkedHashMap<String, ReportLine>();
    }

    public Map<String, ReportLine> getReportLines() {
        return reportLines;
    }

    public void setReportLines(Map<String, ReportLine> reportLines) {
        this.reportLines = reportLines;
    }

    public ReportLine getSumLine() {
        return sumLine;
    }

    public void setSumLine(ReportLine sumLine) {
        this.sumLine = sumLine;
    }

    /**
     * Adds the information of the {@link MonitorQueryItem}s to the Report.
     *
     * @param monitorQueryItems
     *            a list of {@link MonitorQueryItem} with the information of the database
     * @param reportLineItemDefinitions
     *            a list of {@link ReportLineItemDefinition}s that is needed to create the {@link ReportLine}s.
     * @param inWorkingDays
     *            a boolean parameter that specifies whether the age of the tasks should be counted in days or in
     *            working days.
     */
    public void addMonitoringQueryItems(List<MonitorQueryItem> monitorQueryItems,
        List<ReportLineItemDefinition> reportLineItemDefinitions, boolean inWorkingDays) {
        DaysToWorkingDaysConverter instance = null;
        if (reportLineItemDefinitions != null && inWorkingDays) {
            instance = DaysToWorkingDaysConverter.initialize(reportLineItemDefinitions);
        }

        for (MonitorQueryItem item : monitorQueryItems) {
            if (instance != null) {
                item.setAgeInDays(instance.convertDaysToWorkingDays(item.getAgeInDays()));
            }
            if (!this.reportLines.containsKey(item.getKey())) {
                ReportLine reportLine = new ReportLine();
                reportLine.create(reportLineItemDefinitions);
                this.reportLines.put(item.getKey(), reportLine);
            }
            this.reportLines.get(item.getKey()).addNumberOfTasks(item);
        }

        this.sumLine = createSumLine(reportLineItemDefinitions);
    }

    /**
     * Creates the sum line of this {@link Report}.
     *
     * @param reportLineItemDefinitions
     *            a list of {@link ReportLineItemDefinition}s that specify the subdivision into different cluster of
     *            ages.
     * @return a {@link ReportLine} that contains the sums of the different cluster of this {@link Report}.
     */
    protected ReportLine createSumLine(List<ReportLineItemDefinition> reportLineItemDefinitions) {
        ReportLine sumLine = new ReportLine();
        sumLine.create(reportLineItemDefinitions);
        int totalNumberOfTasks = 0;
        for (ReportLine reportLine : this.reportLines.values()) {
            Iterator<ReportLineItem> reportLineItemIterator = reportLine.getLineItems().iterator();
            Iterator<ReportLineItem> sumLineItemIterator = sumLine.getLineItems().iterator();
            while (reportLineItemIterator.hasNext() && sumLineItemIterator.hasNext()) {
                int numberOfTasks = reportLineItemIterator.next().getNumberOfTasks();
                sumLineItemIterator.next().addNumberOfTasks(numberOfTasks);
            }
            totalNumberOfTasks += reportLine.getTotalNumberOfTasks();
        }
        sumLine.setTotalNumberOfTasks(totalNumberOfTasks);
        return sumLine;
    }

}
