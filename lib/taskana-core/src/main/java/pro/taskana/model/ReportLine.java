package pro.taskana.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Each ReportLine consists of a name, a list of {@link ReportLineItem} objects and a totalCount that represents the
 * count of all tasks.
 */
public class ReportLine {

    private String name;
    private List<ReportLineItem> lineItems;
    private int totalCount;

    public ReportLine() {
        this.lineItems = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ReportLineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<ReportLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
