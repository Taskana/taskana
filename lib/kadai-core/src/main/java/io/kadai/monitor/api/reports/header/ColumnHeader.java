package io.kadai.monitor.api.reports.header;

import io.kadai.monitor.api.reports.Report;
import io.kadai.monitor.api.reports.item.QueryItem;

/**
 * A ColumnHeader is an element of a {@linkplain Report}. It determines weather a given &lt;Item&gt;
 * belongs into the representing column.
 *
 * @param <I> {@linkplain QueryItem} on which the {@linkplain Report} is based on.
 */
public interface ColumnHeader<I extends QueryItem> {

  /**
   * The display name is the string representation of this column. Used to give this column a name
   * during presentation.
   *
   * @return String representation of this column.
   */
  String getDisplayName();

  /**
   * Determines if a specific item is meant part of this column.
   *
   * @param item the given item to check.
   * @return True, if the item is supposed to be part of this column. Otherwise false.
   */
  boolean fits(I item);
}
