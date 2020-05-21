package pro.taskana.monitor.rest.models;

import java.util.Arrays;
import java.util.List;
import org.springframework.hateoas.RepresentationModel;

import pro.taskana.monitor.api.reports.Report;
import pro.taskana.monitor.api.reports.row.SingleRow;

/** EntityModel class for {@link Report}. */
public class ReportRepresentationModel extends RepresentationModel<ReportRepresentationModel> {

  private final MetaInformation meta;

  private final List<RowResource> rows;

  private final List<RowResource> sumRow;

  public ReportRepresentationModel(
      MetaInformation meta, List<RowResource> rows, List<RowResource> sumRow) {
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
   * EntityModel class for {@link SingleRow}.
   */
  public static class RowResource {

    private final int[] cells;
    private final int total;
    private final int depth;
    private final String[] desc;
    private final boolean display;

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
      return String.format(
          "RowResourde [cells=%s, total=%d, depth=%d, desc=%s",
          Arrays.toString(cells), total, depth, Arrays.toString(desc));
    }
  }

  /**
   * Meta Information about this ReportResource.
   */
  public static class MetaInformation {

    private static final String TOTAL_DESC = "Total";

    private final String name;
    private final String date;
    private final String[] header;
    private final String[] rowDesc;

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
      return String.format(
          "MetaInformation [name= %s, date= %s, header= %s, rowDesc= %s]",
          name, date, Arrays.toString(header), Arrays.toString(rowDesc));
    }
  }
}
