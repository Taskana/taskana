package pro.taskana.impl.report;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A Report represents a an abstract table that consists of {@link ReportRow}s and a list of &lt;ColumnHeader&gt;s.
 * Since a report does not specify &lt;Item&gt; and &lt;ColumnHeader&gt; it does not contain functional logic.
 * Due to readability implicit definition of functional logic is prevented and thus prevent
 * initialization of an abstract Report. In order to create a specific Report a subclass has to be created.
 *
 * @param <Item> {@link QueryItem} whose value is relevant for this report.
 * @param <ColumnHeader> {@link ReportColumnHeader} which can determine if an &lt;Item&gt; belongs into that column or not.
 */
public abstract class Report<Item extends QueryItem, ColumnHeader extends ReportColumnHeader<? super Item>> {

    protected List<ColumnHeader> columnHeaders = new ArrayList<>();
    private Map<String, ReportRow<Item>> reportRows = new LinkedHashMap<>();
    private ReportRow<Item> sumRow;
    private String rowDesc;

    public Report(List<ColumnHeader> columnHeaders, String rowDesc) {
        this.rowDesc = rowDesc;
        sumRow = new ReportRow<>(columnHeaders.size());
        this.columnHeaders.addAll(columnHeaders);
    }

    public final Map<String, ReportRow<Item>> getReportRows() {
        return reportRows;
    }

    public final ReportRow<Item> getSumRow() {
        return sumRow;
    }

    public final List<ColumnHeader> getColumnHeaders() {
        return columnHeaders;
    }

    public final String getRowDesc() {
        return rowDesc;
    }

    public ReportRow<Item> getRow(String key) {
        return reportRows.get(key);
    }

    public final Set<String> rowTitles() {
        return reportRows.keySet();
    }

    public final int rowSize() {
        return reportRows.size();
    }

    public final void addItem(Item item) {
        ReportRow<Item> row = reportRows.computeIfAbsent(item.getKey(), (s) -> createReportRow(columnHeaders.size()));
        if (columnHeaders.isEmpty()) {
            row.updateTotalValue(item);
            sumRow.updateTotalValue(item);
        } else {
            for (int i = 0; i < columnHeaders.size(); i++) {
                if (columnHeaders.get(i).fits(item)) {
                    row.addItem(item, i);
                    sumRow.addItem(item, i);
                }
            }
        }
    }

    public final void addItems(List<Item> items, QueryItemPreprocessor<Item> preprocessor) {
        items.stream()
            .map(preprocessor::apply)
            .forEach(this::addItem);
    }

    public final void addItems(List<Item> items) {
        items.forEach(this::addItem);
    }

    protected ReportRow<Item> createReportRow(int columnSize) {
        return new ReportRow<>(columnSize);
    }
}
