package pro.taskana.impl.report.row;

import pro.taskana.impl.report.structure.QueryItem;
import pro.taskana.impl.report.structure.Row;

/**
 * A SingleRow represents a single row in a {@link pro.taskana.impl.report.structure.Report}.
 * It contains an array of cells whose index corresponds to the {@link pro.taskana.impl.report.structure.ColumnHeader} index in the {@link pro.taskana.impl.report.structure.Report}.
 *
 * @param <I> {@link QueryItem} on which the {@link pro.taskana.impl.report.structure.Report} is based on.
 */
public class SingleRow<I extends QueryItem> implements Row<I> {

    private final int[] cells;
    private int total = 0;

    public SingleRow(int columnCount) {
        cells = new int[columnCount];
    }

    @Override
    public void addItem(I item, int index) throws IndexOutOfBoundsException {
        total += item.getValue();
        cells[index] += item.getValue();
    }

    @Override
    public void updateTotalValue(I item) {
        total += item.getValue();
    }

    @Override
    public final int getTotalValue() {
        return total;
    }

    @Override
    public final int[] getCells() {
        return cells.clone();
    }
}
