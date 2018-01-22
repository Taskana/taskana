package pro.taskana.model;

/**
 * A ReportLineItemDefinition has a lower and an upper limit which subdivide the count of tasks in a workbasket into
 * different sections.
 */
public class ReportLineItemDefinition {

    private int lowerLimit;
    private int upperLimit;

    public int getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(int lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public int getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(int upperLimit) {
        this.upperLimit = upperLimit;
    }
}
