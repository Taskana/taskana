package pro.taskana.impl;

/**
 * A ReportLineItemDefinition has a lower and an upper age limit which subdivide the count of tasks into different
 * sections. Days in past are represented as negative values and days in the future are represented as positive values.
 * To avoid tasks are counted multiple times or not be listed in the report, these reportLineItemDefinitions should not
 * overlap and should not have gaps. If the ReportLineDefinition should represent a single day, lowerAgeLimit and
 * upperAgeLimit have to be equal.
 */
public class ReportLineItemDefinition {

    private int lowerAgeLimit;
    private int upperAgeLimit;

    public ReportLineItemDefinition() {
    }

    public ReportLineItemDefinition(int ageInDays) {
        this.lowerAgeLimit = ageInDays;
        this.upperAgeLimit = ageInDays;
    }

    public ReportLineItemDefinition(int lowerAgeLimit, int upperAgeLimit) {
        this.lowerAgeLimit = lowerAgeLimit;
        this.upperAgeLimit = upperAgeLimit;
    }

    public int getLowerAgeLimit() {
        return lowerAgeLimit;
    }

    public void setLowerAgeLimit(int lowerAgeLimit) {
        this.lowerAgeLimit = lowerAgeLimit;
    }

    public int getUpperAgeLimit() {
        return upperAgeLimit;
    }

    public void setUpperAgeLimit(int upperAgeLimit) {
        this.upperAgeLimit = upperAgeLimit;
    }

    @Override
    public String toString() {
        return "(" + this.lowerAgeLimit + "," + this.upperAgeLimit + ")";
    }
}
