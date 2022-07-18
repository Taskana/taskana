package pro.taskana.monitor.api.reports.header;

import pro.taskana.monitor.api.reports.Report;
import pro.taskana.monitor.api.reports.item.QueryItem;

/**
 * The ColumnHeader is an element of a {@linkplain Report}. It determines weather a given
 * &lt;Item&gt; belongs into the representing column.
 *
 * @param <I> {@linkplain QueryItem} on which the {@linkplain Report} is based on.
 */
public interface ColumnHeader<I extends QueryItem> {

  /**
   * The display name is the string representation of this column. Used to give this column a name
   * during presentation.
   *
   * @return the String representation of this column
   */
  String getDisplayName();

  /**
   * Determines if a specific item is meant part of this column.
   *
   * @param item the given item to check
   * @return true, if the item is supposed to be part of this column; otherwise false
   */
  boolean fits(I item);
}
