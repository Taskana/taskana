package pro.taskana.model;

/**
 * Each ReportLineItem consists of a {@link ReportLineItemDefinition} that defines the upper and lower limits of this
 * item and a count value that represents the count of tasks of this item.
 */
public class ReportLineItem {

    private ReportLineItemDefinition reportLineItemDefinition;
    private int count;

    public ReportLineItemDefinition getReportLineItemDefinition() {
        return reportLineItemDefinition;
    }

    public void setReportLineItemDefinition(ReportLineItemDefinition reportLineItemDefinition) {
        this.reportLineItemDefinition = reportLineItemDefinition;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
