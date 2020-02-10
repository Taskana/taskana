package pro.taskana.monitor.api.reports.row;

import pro.taskana.monitor.api.reports.Report;
import pro.taskana.monitor.api.reports.header.ColumnHeader;
import pro.taskana.monitor.api.reports.item.QueryItem;

/**
 * A SingleRow represents a single row in a {@link Report}. It contains an array of cells whose
 * index corresponds to the {@link ColumnHeader} index in the {@link Report}.
 *
 * @param <I> {@link QueryItem} on which the {@link Report} is based on.
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
