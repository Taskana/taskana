package pro.taskana.impl.report;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class Report<Item extends QueryItem, Column extends ReportColumnHeader<? super Item>> {

    protected List<Column> columns = new ArrayList<>(); //this can be done as an array
    private Map<String, ReportRow<Item>> reportLines = new LinkedHashMap<>();
    private ReportRow<Item> sumLine;

    public Report(List<Column> columns) {
        sumLine = new ReportRow<>(columns.size());
        this.columns.addAll(columns);
    }

    protected final void addItem(Item item) {
        ReportRow<Item> row = reportLines.computeIfAbsent(item.getKey(), (s) -> createReportRow(columns.size()));
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).fits(item)) {
                row.addItem(item, i);
                sumLine.addItem(item, i);
            }
        }
    }

    public ReportRow<Item> getRow(String key) {
        return reportLines.get(key);
    }

    public void addItems(List<Item> items) {
        items.forEach(this::addItem);
    }

    public final ReportRow<Item> getSumLine() {
        return sumLine;
    }

    public Map<String, ReportRow<Item>> getReportLines() {
        return reportLines;
    }

    protected ReportRow<Item> createReportRow(int columnSize) {
        return new ReportRow<>(columnSize);
    }
}