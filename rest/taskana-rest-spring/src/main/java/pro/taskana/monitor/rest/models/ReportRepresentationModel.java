package pro.taskana.monitor.rest.models;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.springframework.hateoas.RepresentationModel;

import pro.taskana.monitor.api.reports.Report;
import pro.taskana.monitor.api.reports.row.SingleRow;

/** EntityModel class for {@link Report}. */
public class ReportRepresentationModel extends RepresentationModel<ReportRepresentationModel> {

  /** Object holding metainfo on the report. */
  private final MetaInformation meta;

  /** Array holding the rows of the report. */
  private final List<RowRepresentationModel> rows;

  /** Array holding the sums in the columns over all rows. */
  private final List<RowRepresentationModel> sumRow;

  public ReportRepresentationModel(
      MetaInformation meta,
      List<RowRepresentationModel> rows,
      List<RowRepresentationModel> sumRow) {
    this.meta = meta;
    this.rows = rows;
    this.sumRow = sumRow;
  }

  public MetaInformation getMeta() {
    return meta;
  }

  public List<RowRepresentationModel> getRows() {
    return rows;
  }

  public List<RowRepresentationModel> getSumRow() {
    return sumRow;
  }

  /** EntityModel class for {@link SingleRow}. */
  public static class RowRepresentationModel {

    /** Array holding all the cell values of the given row. */
    private final int[] cells;
    /** Sum of all values of the given row. */
    private final int total;
    /** Depth of the row. If the depth is > 0, then this row is a sub-row of a prior row */
    private final int depth;
    /** Array containing description of the row. */
    private final String[] desc;
    /** Boolean identifying if the given row should be initially displayed or not. */
    private final boolean display;

    public RowRepresentationModel(
        int[] cells, int total, int depth, String[] desc, boolean display) {
      this.cells = cells;
      this.total = total;
      this.depth = depth;
      this.desc = desc;
      this.display = display;
    }

    public int[] getCells() {
      return cells;
    }

    public int getTotal() {
      return total;
    }

    public int getDepth() {
      return depth;
    }

    public String[] getDesc() {
      return desc;
    }

    public boolean isDisplay() {
      return display;
    }

    @Override
    public String toString() {
      return "RowResource [cells="
          + Arrays.toString(cells)
          + ", total="
          + total
          + ", depth="
          + depth
          + ", desc="
          + Arrays.toString(desc)
          + ", display="
          + display
          + "]";
    }
  }

  /** Meta Information about this ReportResource. */
  public static class MetaInformation {

    /** Name of the report. */
    private final String name;
    /** Date of the report creation. */
    private final Instant date;
    /** Column-headers of the report. */
    private final String[] header;
    /** Descriptions for the rows the report. */
    private final String[] rowDesc;
    /** Description for the sum column. */
    private final String totalDesc;

    public MetaInformation(
        String name, Instant date, String[] header, String[] rowDesc, String totalDesc) {
      this.name = name;
      this.date = date;
      this.header = header;
      this.rowDesc = rowDesc;
      this.totalDesc = totalDesc;
    }

    public String getTotalDesc() {
      return totalDesc;
    }

    public String getName() {
      return name;
    }

    public Instant getDate() {
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
      return "MetaInformation [name="
          + name
          + ", date="
          + date
          + ", header="
          + Arrays.toString(header)
          + ", rowDesc="
          + Arrays.toString(rowDesc)
          + ", totalDesc="
          + totalDesc
          + "]";
    }
  }
}
