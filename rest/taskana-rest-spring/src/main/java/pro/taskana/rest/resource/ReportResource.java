package pro.taskana.rest.resource;

import java.util.Map;

import org.springframework.hateoas.ResourceSupport;

/**
 * Resource class for {@link pro.taskana.impl.report.Report}.
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
     * Meta Information about this ReportResource.
     */
    public static class MetaInformation {

        private static final String TOTAL_DESC = "Total";

        private String name;
        private String date;
        private String[] header;
        private String rowDesc;

        public MetaInformation(String name, String date, String[] header, String rowDesc) {
            this.name = name;
            this.date = date;
            this.header = header;
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

        public String getRowDesc() {
            return rowDesc;
        }
    }

    /**
     * Resource class for {@link pro.taskana.impl.report.ReportRow}.
     */
    public static class RowResource {

        private Map<String, Integer> cells;
        private int total;

        public RowResource(Map<String, Integer> cells, int total) {
            this.cells = cells;
            this.total = total;
        }

        public Map<String, Integer> getCells() {
            return cells;
        }

        public int getTotal() {
            return total;
        }
    }
}
