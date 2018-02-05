package pro.taskana.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Each ReportLine consists of a list of {@link ReportLineItem} objects and the number of all tasks of this ReportLine.
 */
public class ReportLine {

    private List<ReportLineItem> lineItems;
    private int totalNumberOfTasks;

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
