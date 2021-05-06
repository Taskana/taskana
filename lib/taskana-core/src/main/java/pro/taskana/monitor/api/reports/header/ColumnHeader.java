package pro.taskana.monitor.api.reports.header;

import pro.taskana.monitor.api.reports.Report;
import pro.taskana.monitor.api.reports.item.QueryItem;

/**
 * A ColumnHeader is an element of a {@linkplain Report}.
 *
 * <p>It determines whether a given {@linkplain QueryItem Item} belongs into the representing
 * column.
 *
 * @param <I> {@linkplain QueryItem} on which the {@linkplain Report} is based on
 */
public interface ColumnHeader<I extends QueryItem> {

  /**
   * The display name is the string representation of this column.
   *
   * <p>Used to give this column a name during presentation.
   *
   * @return String representation of this column.
   */
  String getDisplayName();

  /**
   * Determines if a specific Item is meant part of this column.
   *
   * @param item the given Item to check.
   * @return True, if the Item is supposed to be part of this column. Otherwise false.
   */
  boolean fits(I item);
}
