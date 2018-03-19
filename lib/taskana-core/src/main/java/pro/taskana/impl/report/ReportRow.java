package pro.taskana.impl.report;

/**
 * A ReportRow represents a row in a {@link Report}.
 * It contains an array of cells whose index corresponds to the {@link ReportColumnHeader} index in the {@link Report}.
 *
 * @param <Item> {@link QueryItem} on which the {@link Report} is based on.
 */
public class ReportRow<Item extends QueryItem> {

    private final int[] cells;
    private int totalValue = 0;

    public ReportRow(int columnCount) {
        cells = new int[columnCount];
    }

    public int[] getCells() {
        return cells;
    }

    public int getTotalValue() {
        return totalValue;
    }

    public void updateTotalValue(Item item) {
        totalValue += item.getValue();
    }

    public void addItem(Item item, int index) throws IndexOutOfBoundsException {
        totalValue += item.getValue();
        cells[index] += item.getValue();
    }
}
