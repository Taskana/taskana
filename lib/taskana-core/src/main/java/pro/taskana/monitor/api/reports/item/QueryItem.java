package pro.taskana.monitor.api.reports.item;

import pro.taskana.monitor.api.reports.Report;

/** A QueryItem is en entity on which a {@link Report} is based on. */
public interface QueryItem {

  /**
   * The key of a QueryItem determines its row within a {@link Report}.
   *
   * @return the key of this query item.
   */
  String getKey();

  /**
   * Its value will be added to the existing cell value during the insertion into a report.
   *
   * @return the value
   */
  int getValue();
}
