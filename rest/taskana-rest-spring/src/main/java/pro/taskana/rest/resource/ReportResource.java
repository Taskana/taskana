package pro.taskana.rest.resource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.hateoas.ResourceSupport;

import pro.taskana.impl.util.LoggerUtils;

/**
 * Resource class for {@link pro.taskana.impl.report.structure.Report}.
 */
public class ReportResource extends ResourceSupport {

    private MetaInformation meta;

    private Map<String, RowResource> rows;

    private RowResource sumRow;

    public ReportResource(MetaInformation meta, Map<String, RowResource> rows, RowResource sumRow) {
        this.meta = meta;
        this.rows = rows;
        this.sumRow = sumRow;
    }

    public MetaInformation getMeta() {
        return meta;
    }

    public Map<String, RowResource> getRows() {
        return rows;
    }

    public RowResource getSumRow() {
        return sumRow;
    }

    /**
     * Resource Interface for {@link pro.taskana.impl.report.structure.Row}.
     */
    public interface RowResource {

        Map<String, Integer> getCells();

        int getTotal();
    }

    /**
     * Resource class for {@link pro.taskana.impl.report.row.SingleRow}.
     */
    public static class SingleRowResource implements RowResource {

        private Map<String, Integer> cells;
        private int total;

        public SingleRowResource(Map<String, Integer> cells, int total) {
            this.cells = cells;
            this.total = total;
        }

        @Override
        public Map<String, Integer> getCells() {
            return cells;
        }

        @Override
        public int getTotal() {
            return total;
        }

        @Override
        public String toString() {
            return "SingleRowResource ["
                + "rowDesc= " + LoggerUtils.mapToString(this.cells)
                + "taskId= " + this.total
                + "]";
        }
    }

    /**
     * Resource class for {@link pro.taskana.impl.report.row.FoldableRow}.
     */
    public static class FoldableRowResource extends SingleRowResource {

        private Map<String, RowResource> foldableRows = new HashMap<>();

        public FoldableRowResource(SingleRowResource row) {
            super(row.getCells(), row.getTotal());
        }

        public void addRow(String desc, RowResource row) {
            foldableRows.put(desc, row);
        }

        public Map<String, RowResource> getFoldableRows() {
            return foldableRows;
        }
    }

    /**
     * Meta Information about this ReportResource.
     */
    public static class MetaInformation {

        private static final String TOTAL_DESC = "Total";

        private String name;
        private String date;
        private String[] header;
        private String[] expHeader;
        private String rowDesc;

        public MetaInformation(String name, String date, String[] header, String[] expHeader, String rowDesc) {
            this.name = name;
            this.date = date;
            this.header = header;
            this.expHeader = expHeader;
            this.rowDesc = rowDesc;
        }

        public String getTotalDesc() {
            return TOTAL_DESC;
        }

        public String getName() {
            return name;
        }

        public String getDate() {
            return date;
        }

        public String[] getHeader() {
            return header;
        }

        public String[] getExpHeader() {
            return expHeader;
        }

        public String getRowDesc() {
            return rowDesc;
        }

        @Override
        public String toString() {
            return String.format("MetaInformation [name= %s, date= %s, header= %s, expHeader= %s, rowDesc= %s]",
                name, date, Arrays.toString(header), Arrays.toString(expHeader), rowDesc);
        }
    }
}
