package pro.taskana.impl;

/**
 * Each ReportLineItem consists of a {@link ReportLineItemDefinition} that defines the upper and lower age limits of
 * this item and a number of tasks of this item.
 */
public class ReportLineItem {

    private ReportLineItemDefinition reportLineItemDefinition;
    private int numberOfTasks;

    public ReportLineItem() {
        this.numberOfTasks = 0;
    }

    public ReportLineItemDefinition getReportLineItemDefinition() {
        return reportLineItemDefinition;
    }

    public void setReportLineItemDefinition(ReportLineItemDefinition reportLineItemDefinition) {
        this.reportLineItemDefinition = reportLineItemDefinition;
    }

    public int getNumberOfTasks() {
        return numberOfTasks;
    }

    public void setNumberOfTasks(int numberOfTasks) {
        this.numberOfTasks = numberOfTasks;
    }

    public void addNumberOfTasks(int numberOfTasks) {
        this.numberOfTasks += numberOfTasks;
    }

}
