package pro.taskana.monitor.rest.models;

import java.beans.ConstructorProperties;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

import pro.taskana.monitor.api.reports.Report;
import pro.taskana.monitor.api.reports.row.SingleRow;

/** EntityModel class for {@link Report}. */
@Getter
@RequiredArgsConstructor(onConstructor = @__({@ConstructorProperties({"meta", "rows", "sumRow"})}))
public class ReportRepresentationModel extends RepresentationModel<ReportRepresentationModel> {

  /** Object holding meta info on the report. */
  private final MetaInformation meta;
  /** Array holding the rows of the report. */
  private final List<RowRepresentationModel> rows;
  /** Array holding the sums in the columns over all rows. */
  private final List<RowRepresentationModel> sumRow;

  /** EntityModel class for {@link SingleRow}. */
  @Getter
  @RequiredArgsConstructor(
      onConstructor = @__({@ConstructorProperties({"cells", "total", "depth", "desc", "display"})}))
  @ToString
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
  }

  /** Meta Information about this ReportResource. */
  @Getter
  @RequiredArgsConstructor(
      onConstructor =
          @__({@ConstructorProperties({"name", "date", "header", "rowDesc", "sumRowDesc"})}))
  @ToString
  public static class MetaInformation {

    /** Name of the report. */
    private final String name;
    /** Date of the report creation. */
    private final Instant date;
    /** Column headers of the report. */
    private final String[] header;
    /** Descriptions for the rows of the report. */
    private final String[] rowDesc;
    /** Description for the sum column. */
    private final String sumRowDesc;
  }
}
