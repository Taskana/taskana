package pro.taskana.monitor.api.reports.item;

import pro.taskana.monitor.api.reports.Report;
import pro.taskana.monitor.api.reports.row.Row;

/**
 * A QueryItem is en entity on which a {@linkplain Report} is based on.
 *
 * <p>Its value will be added to the existing cell value during the insertion into a {@linkplain
 * Report}. Its key will determine in which {@linkplain Row} the QueryItem will be inserted.
 */
public interface QueryItem {

  /**
   * The key of a QueryItem determines its row within a {@linkplain Report}.
   *
   * @return the key of this query item.
   */
  String getKey();

  int getValue();
}
