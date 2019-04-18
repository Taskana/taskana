package pro.taskana.rest.resource;

import java.util.Arrays;
import java.util.List;

import org.springframework.hateoas.ResourceSupport;

/**
 * Resource class for {@link pro.taskana.impl.report.structure.Report}.
 */
public class ReportResource extends ResourceSupport {

    private MetaInformation meta;

    private List<RowResource> rows;

    private List<RowResource> sumRow;

    public ReportResource(MetaInformation meta, List<RowResource> rows, List<RowResource> sumRow) {
        this.meta = meta;
        this.rows = rows;
        this.sumRow = sumRow;
    }

    public MetaInformation getMeta() {
        return meta;
    }

    public List<RowResource> getRows() {
        return rows;
    }

    public List<RowResource> getSumRow() {
        return sumRow;
    }

    /**
     * Resource class for {@link pro.taskana.impl.report.row.SingleRow}.
     */
    public static class RowResource {

        private int[] cells;
        private int total;
        private int depth;
        private String[] desc;
        private boolean display;

        public RowResource(int[] cells, int total, int depth, String[] desc, boolean display) {
            this.cells = cells;
            this.total = total;
            this.depth = depth;
            this.desc = desc;
            this.display = display;
        }

        @SuppressWarnings("unused")
        public int[] getCells() {
            return cells;
        }

        @SuppressWarnings("unused")
        public int getTotal() {
            return total;
        }

        @SuppressWarnings("unused")
        public int getDepth() {
            return depth;
        }

        @SuppressWarnings("unused")
        public String[] getDesc() {
            return desc;
        }

        @SuppressWarnings("unused")
        public boolean isDisplay() {
            return display;
        }

        @Override
        public String toString() {
            return String.format("RowResourde [cells=%s, total=%d, depth=%d, desc=%s",
                Arrays.toString(cells), total, depth, Arrays.toString(desc));
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
        private String[] rowDesc;

        public MetaInformation(String name, String date, String[] header, String[] rowDesc) {
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

        public String[] getRowDesc() {
            return rowDesc;
        }

        @Override
        public String toString() {
            return String.format("MetaInformation [name= %s, date= %s, header= %s, rowDesc= %s]",
                name, date, Arrays.toString(header), Arrays.toString(rowDesc));
        }
    }
}
