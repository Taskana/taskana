package pro.taskana.impl.report.structure;

/**
 * A QueryItem is en entity on which a {@link Report} is based on. Its value will be added to the
 * existing cell value during the insertion into a report. Its key will determine in which {@link
 * Row} the item will be inserted.
 */
public interface QueryItem {

  /**
   * The key of a QueryItem determines its row within a {@link Report}.
   *
   * @return the key of this query item.
   */
  String getKey();

  int getValue();
}
