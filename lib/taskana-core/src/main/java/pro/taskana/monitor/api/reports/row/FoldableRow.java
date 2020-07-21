package pro.taskana.monitor.api.reports.row;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import pro.taskana.monitor.api.reports.Report;
import pro.taskana.monitor.api.reports.item.QueryItem;

/**
 * The FoldableRow extends the {@link SingleRow}. In contrast to the {@link SingleRow} the
 * FoldableRow contains rows which can be collapsed or expanded. The FoldableRow itself displays the
 * sum of all foldable rows.
 *
 * @param <I> the {@link QueryItem} on which the {@link Report} is based on.
 */
public abstract class FoldableRow<I extends QueryItem> extends SingleRow<I> {

  private final Map<String, Row<I>> foldableRows = new LinkedHashMap<>();
  private final Function<? super I, String> calcFoldableRowKey;
  private final int columnSize;

  protected FoldableRow(
      String key, int columnSize, Function<? super I, String> calcFoldableRowKey) {
    super(key, columnSize);
    this.columnSize = columnSize;
    this.calcFoldableRowKey = calcFoldableRowKey;
  }

  public final int getFoldableRowCount() {
    return foldableRows.size();
  }

  public final Set<String> getFoldableRowKeySet() {
    return foldableRows.keySet();
  }

  @Override
  public void addItem(I item, int index) throws IndexOutOfBoundsException {
    super.addItem(item, index);
    foldableRows
        .computeIfAbsent(calcFoldableRowKey.apply(item), key -> buildRow(key, columnSize))
        .addItem(item, index);
  }

  @Override
  public void updateTotalValue(I item) {
    super.updateTotalValue(item);
    foldableRows
        .computeIfAbsent(calcFoldableRowKey.apply(item), key -> buildRow(key, columnSize))
        .updateTotalValue(item);
  }

  public Row<I> getFoldableRow(String key) {
    return foldableRows.get(key);
  }

  protected abstract Row<I> buildRow(String key, int columnSize);

  @Override
  public String toString() {
    return String.format(
        "FoldableRow [detailRows= %s, columnSize= %d]", this.foldableRows, columnSize);
  }
}
