package pro.taskana.monitor.api.reports.item;

import pro.taskana.monitor.api.reports.Report;

/**
 * A QueryItem is en entity on which a {@linkplain Report} is based on. It represents the content of
 * a cell in the {@linkplain Report}.
 */
public interface QueryItem {

  /**
   * The key of a QueryItem determines its {@linkplain pro.taskana.monitor.api.reports.row.Row row}
   * within a {@linkplain Report}.
   *
   * @return the key of this QueryItem
   */
  String getKey();

  /**
   * Its value will be added to the existing cell value during the insertion into a {@linkplain
   * Report}.
   *
   * @return the value
   */
  int getValue();
}
