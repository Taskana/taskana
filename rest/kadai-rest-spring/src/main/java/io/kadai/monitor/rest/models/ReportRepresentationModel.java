package io.kadai.monitor.rest.models;

import io.kadai.monitor.api.reports.row.SingleRow;
import io.swagger.v3.oas.annotations.media.Schema;
import java.beans.ConstructorProperties;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.springframework.hateoas.RepresentationModel;

@Schema(description = "EntityModel class for Report.")
public class ReportRepresentationModel extends RepresentationModel<ReportRepresentationModel> {

  @Schema(name = "meta", description = "Object holding meta info on the report.")
  private final MetaInformation meta;
  @Schema(name = "rows", description = "Array holding the rows of the report.")
  private final List<RowRepresentationModel> rows;
  @Schema(name = "sumRow", description = "Array holding the sums in the columns over all rows.")
  private final List<RowRepresentationModel> sumRow;

  @ConstructorProperties({"meta", "rows", "sumRow"})
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
  @Schema(description = "EntityModel class for SingleRow.")
  public static class RowRepresentationModel {

    @Schema(name = "cells", description = "Array holding all the cell values of the given row.")
    private final int[] cells;
    @Schema(name = "cells", description = "Sum of all values of the given row.")
    private final int total;
    @Schema(
        name = "depth",
        description =
            "Depth of the row. If the depth is > 0, then this row is a sub-row of a prior row")
    private final int depth;
    @Schema(name = "desc", description = "Array containing description of the row.")
    private final String[] desc;
    @Schema(
        name = "display",
        description = "Boolean identifying if the given row should be initially displayed or not.")
    private final boolean display;

    @ConstructorProperties({"cells", "total", "depth", "desc", "display"})
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

  @Schema(description = "Meta Information about this ReportResource.")
  public static class MetaInformation {

    @Schema(name = "name", description = "Name of the report.")
    private final String name;
    @Schema(name = "date", description = "Date of the report creation.")
    private final Instant date;
    @Schema(name = "header", description = "Column headers of the report.")
    private final String[] header;
    @Schema(name = "rowDesc", description = "Descriptions for the rows of the report.")
    private final String[] rowDesc;
    @Schema(name = "sumRowDesc", description = "Description for the sum column.")
    private final String sumRowDesc;

    @ConstructorProperties({"name", "date", "header", "rowDesc", "sumRowDesc"})
    public MetaInformation(
        String name, Instant date, String[] header, String[] rowDesc, String sumRowDesc) {
      this.name = name;
      this.date = date;
      this.header = header;
      this.rowDesc = rowDesc;
      this.sumRowDesc = sumRowDesc;
    }

    public String getSumRowDesc() {
      return sumRowDesc;
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
          + sumRowDesc
          + "]";
    }
  }
}
