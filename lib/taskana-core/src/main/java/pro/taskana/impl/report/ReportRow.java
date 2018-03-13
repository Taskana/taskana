package pro.taskana.impl.report;

public class ReportRow<Item extends QueryItem> {

    private final int[] lineItems;
    private int totalValue = 0;

    public ReportRow(int columnCount) {
        //TODO: do you use an assert / throw an exception?
        lineItems = new int[columnCount];
    }

    public int[] getLineItems() {
        return lineItems;
    }

    public int getTotalValue() {
        return totalValue;
    }

    public void addItem(Item item, int index) throws IndexOutOfBoundsException {
        totalValue += item.getValue();
        lineItems[index] += item.getValue();
    }
}