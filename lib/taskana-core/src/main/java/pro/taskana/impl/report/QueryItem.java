package pro.taskana.impl.report;

/**
 * A QueryItem is en entity on which a {@link Report} is based on.
 * Its value will be added to the existing cell value during the insertion into a report.
 * Its key will determine in which {@link ReportRow} the item will be inserted.
 */
public interface QueryItem {

    String getKey();

    int getValue();

}
