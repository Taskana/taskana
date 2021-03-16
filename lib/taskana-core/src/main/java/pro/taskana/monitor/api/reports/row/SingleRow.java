package pro.taskana.monitor.api.reports.row;

import java.util.Map;

import pro.taskana.monitor.api.reports.Report;
import pro.taskana.monitor.api.reports.header.ColumnHeader;
import pro.taskana.monitor.api.reports.item.QueryItem;

/**
 * A SingleRow represents a single {@linkplain Row} in a {@linkplain Report}.
 *
 * <p>It contains an array of cells whose index corresponds to the {@linkplain ColumnHeader} index
 * in the {@linkplain Report}.
 *
 * @param <I> {@linkplain QueryItem} on which the {@linkplain Report} is based on
 */
public class SingleRow<I extends QueryItem> implements Row<I> {

  private final int[] cells;
  private final String key;
  private int total = 0;
  private String displayName;

  public SingleRow(String key, int columnCount) {
    this.key = key;
    this.displayName = key;
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
  public String getKey() {
    return key;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  @Override
  public void setDisplayName(Map<String, String> displayMap) {
    displayName = displayMap.getOrDefault(key, key);
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
