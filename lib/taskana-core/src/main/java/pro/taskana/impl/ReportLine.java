package pro.taskana.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * Each ReportLine consists of a list of {@link ReportLineItem} objects and the number of all tasks of this ReportLine.
 */
public class ReportLine {

    protected List<ReportLineItem> lineItems;
    protected int totalNumberOfTasks;

    public ReportLine() {
        this.lineItems = new ArrayList<>();
        this.totalNumberOfTasks = 0;
    }

    public List<ReportLineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<ReportLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public int getTotalNumberOfTasks() {
        return totalNumberOfTasks;
    }

    public void setTotalNumberOfTasks(int totalNumberOfTasks) {
        this.totalNumberOfTasks = totalNumberOfTasks;
    }

    /**
     * Creates a list of {@link ReportLineItem}s for this {@link ReportLine} by using the list of
     * {@link ReportLineItemDefinition}s.
     *
     * @param reportLineItemDefinitions
     *            a list of {@link ReportLineItemDefinition}s that specify the subdivision into different cluster of
     *            ages.
     */
    public void create(List<ReportLineItemDefinition> reportLineItemDefinitions) {
        if (reportLineItemDefinitions != null) {
            for (ReportLineItemDefinition reportLineItemDefinition : reportLineItemDefinitions) {
                ReportLineItem reportLineItem = new ReportLineItem();
                reportLineItem.setReportLineItemDefinition(reportLineItemDefinition);
                this.getLineItems().add(reportLineItem);
            }
        }
    }

    /**
     * Adds the number of tasks of the {@link MonitorQueryItem} to the respective {@link ReportLineItem}.
     *
     * @param item
     *            a {@link MonitorQueryItem} that contains the number of tasks and the age in days of these tasks.
     */
    public void addNumberOfTasks(MonitorQueryItem item) {
        this.totalNumberOfTasks += item.getNumberOfTasks();
        for (ReportLineItem reportLineItem : lineItems) {
            int lowerAgeLimit = reportLineItem.getReportLineItemDefinition().getLowerAgeLimit();
            int upperAgeLimit = reportLineItem.getReportLineItemDefinition().getUpperAgeLimit();
            if (lowerAgeLimit <= item.getAgeInDays() && upperAgeLimit >= item.getAgeInDays()) {
                reportLineItem.addNumberOfTasks(item.getNumberOfTasks());
                break;
            }
        }
    }

}
