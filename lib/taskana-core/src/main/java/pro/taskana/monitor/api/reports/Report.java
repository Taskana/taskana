package pro.taskana.monitor.api.reports;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.monitor.api.reports.header.ColumnHeader;
import pro.taskana.monitor.api.reports.item.QueryItem;
import pro.taskana.monitor.api.reports.item.QueryItemPreprocessor;
import pro.taskana.monitor.api.reports.row.Row;
import pro.taskana.monitor.api.reports.row.SingleRow;

/**
 * A Report represents an abstract table that consists of {@linkplain Row Rows} and a list of
 * {@linkplain ColumnHeader ColumnHeaders}.
 *
 * <p>Since a Report does not specify {@linkplain QueryItem Item} and {@linkplain ColumnHeader} it
 * does not contain functional logic. Due to readability implicit definition of functional logic is
 * prevented and thus prevent initialization of an abstract Report. In order to create a specific
 * Report a subclass has to be created.
 *
 * @param <I> {@linkplain QueryItem} whose value is relevant for this Report
 * @param <H> {@linkplain ColumnHeader} which can determine if an {@linkplain QueryItem Item}
 *     belongs into that column or not
 */
public abstract class Report<I extends QueryItem, H extends ColumnHeader<? super I>> {

  private final Map<String, Row<I>> reportRows = new LinkedHashMap<>();
  private final Row<I> sumRow;
  private final String[] rowDesc;
  protected List<H> columnHeaders;

  protected Report(List<H> columnHeaders, String[] rowDesc) {
    this.rowDesc = rowDesc;
    this.columnHeaders = new ArrayList<>(columnHeaders);
    sumRow = createRow("Total");
  }

  public final Map<String, Row<I>> getRows() {
    return reportRows;
  }

  public final Row<I> getSumRow() {
    return sumRow;
  }

  public final List<H> getColumnHeaders() {
    return columnHeaders;
  }

  public final String[] getRowDesc() {
    return rowDesc;
  }

  public Row<I> getRow(String key) {
    return reportRows.get(key);
  }

  public final Set<String> rowTitles() {
    return reportRows.keySet();
  }

  public final int rowSize() {
    return reportRows.size();
  }

  public final void addItem(I item) {
    Row<I> row = null;
    if (columnHeaders.isEmpty()) {
      row = reportRows.computeIfAbsent(item.getKey(), this::createRow);
      row.updateTotalValue(item);
      sumRow.updateTotalValue(item);
    } else {
      for (int i = 0; i < columnHeaders.size(); i++) {
        if (columnHeaders.get(i).fits(item)) {
          if (row == null) {
            row = reportRows.computeIfAbsent(item.getKey(), this::createRow);
          }
          row.addItem(item, i);
          sumRow.addItem(item, i);
        }
      }
    }
  }

  public final void addItem(I item, QueryItemPreprocessor<I> preprocessor) {
    addItem(preprocessor.apply(item));
  }

  public final void addItems(List<? extends I> items, QueryItemPreprocessor<I> preprocessor) {
    items.stream().map(preprocessor::apply).forEach(this::addItem);
  }

  public final void addItems(List<I> items) {
    items.forEach(this::addItem);
  }

  public final void augmentDisplayNames(Map<String, String> displayMap) {
    reportRows.values().forEach(row -> row.setDisplayName(displayMap));
    sumRow.setDisplayName(displayMap);
  }

  public final Row<I> createRow(String key) {
    return createRow(key, columnHeaders.size());
  }

  protected Row<I> createRow(String key, int columnSize) {
    return new SingleRow<>(key, columnSize);
  }

  /**
   * Builder for {@linkplain Report}.
   *
   * @param <I> {@linkplain QueryItem} whose value is relevant for this Report
   * @param <H> {@linkplain ColumnHeader} which can determine if an {@linkplain QueryItem Item}
   *     belongs into that column or not
   */
  public interface Builder<I extends QueryItem, H extends ColumnHeader<? super I>> {

    Report<I, H> buildReport() throws NotAuthorizedException, InvalidArgumentException;
  }
}
