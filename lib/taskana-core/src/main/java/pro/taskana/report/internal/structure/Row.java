package pro.taskana.report.internal.structure;

/**
 * Representation of a row in a {@link Report}. It contains an array of cells whose index
 * corresponds to the {@link ColumnHeader} index in the {@link Report}.
 *
 * @param <I> {@link QueryItem} on which the {@link Report} is based on.
 */
public interface Row<I extends QueryItem> {

  /**
   * Appends a specific item value at a specific index.
   *
   * @param item the item which will be appended
   * @param index the index at which the item will be appended at.
   * @throws IndexOutOfBoundsException if the given index is invalid.
   */
  void addItem(I item, int index) throws IndexOutOfBoundsException;

  /**
   * updates the total value of the row without changing any cell value.
   *
   * @param item the item whose value will be added to the total value of this row.
   */
  void updateTotalValue(I item);

  int getTotalValue();

  int[] getCells();
}
