package pro.taskana.impl;

import java.util.List;

/**
 * The DetailedClassificationReport extends the {@link ClassificationReport}. In contrast to the ClassificationReport
 * there are DetailedReportLines instead of ReportLines. That means each ReportLine contains an additional list of
 * ReportLines for the classifications of the attachments of the tasks. The additional addDetailedMonitoringQueryItems
 * method allows to add {@link DetailedMonitorQueryItem}s to the DetailedClassificationReport.
 */
public class DetailedClassificationReport extends ClassificationReport {

    public DetailedClassificationReport() {
        super();
    }

    /**
     * Adds the information of the {@link DetailedMonitorQueryItem}s to the DetailedClassificationReport.
     *
     * @param detailedMonitorQueryItems
     *            a list of {@link DetailedMonitorQueryItem} with the information of the database
     * @param reportLineItemDefinitions
     *            a list of {@link ReportLineItemDefinition}s that is needed to create the {@link ReportLine}s.
     * @param inWorkingDays
     *            a boolean parameter that specifies whether the age of the tasks should be counted in days or in
     *            working days.
     */
    public void addDetailedMonitoringQueryItems(List<DetailedMonitorQueryItem> detailedMonitorQueryItems,
        List<ReportLineItemDefinition> reportLineItemDefinitions, boolean inWorkingDays) {

        DaysToWorkingDaysConverter instance = null;
        if (reportLineItemDefinitions != null && inWorkingDays) {
            instance = DaysToWorkingDaysConverter.initialize(reportLineItemDefinitions);
        }

        for (DetailedMonitorQueryItem item : detailedMonitorQueryItems) {
            if (instance != null) {
                item.setAgeInDays(instance.convertDaysToWorkingDays(item.getAgeInDays()));
            }
            if (!this.reportLines.containsKey(item.getKey())) {
                DetailedReportLine detailedReportLine = new DetailedReportLine();
                detailedReportLine.create(reportLineItemDefinitions);
                this.reportLines.put(item.getKey(), detailedReportLine);
            }
            DetailedReportLine detailedReportLine = (DetailedReportLine) this.reportLines.get(item.getKey());
            detailedReportLine.addNumberOfTasks(item, reportLineItemDefinitions);
        }
        this.sumLine = createSumLine(reportLineItemDefinitions);
    }

}
