package pro.taskana.monitor.api.reports.row;

import pro.taskana.monitor.api.reports.TimestampReport;
import pro.taskana.monitor.api.reports.item.TimestampQueryItem;

/**
 * A single Row inside the {@link TimestampReport}. It contains 4 sub-rows for each org level
 * respectively.
 */
public class TimestampRow extends FoldableRow<TimestampQueryItem> {

  public TimestampRow(int columnSize) {
    super(columnSize, TimestampQueryItem::getOrgLevel1);
  }

  @Override
  public OrgLevel1Row getFoldableRow(String key) {
    return (OrgLevel1Row) super.getFoldableRow(key);
  }

  @Override
  OrgLevel1Row buildRow(int columnSize) {
    return new OrgLevel1Row(columnSize);
  }

  /**
   * Row inside the {@link TimestampReport} containing the information regarding a specific org
   * level 1.
   */
  public static final class OrgLevel1Row extends FoldableRow<TimestampQueryItem> {

    private OrgLevel1Row(int columnSize) {
      super(columnSize, TimestampQueryItem::getOrgLevel2);
    }

    @Override
    public OrgLevel2Row getFoldableRow(String key) {
      return (OrgLevel2Row) super.getFoldableRow(key);
    }

    @Override
    OrgLevel2Row buildRow(int columnSize) {
      return new OrgLevel2Row(columnSize);
    }
  }

  /**
   * Row inside the {@link TimestampReport} containing the information regarding a specific org
   * level 2.
   */
  public static final class OrgLevel2Row extends FoldableRow<TimestampQueryItem> {

    private OrgLevel2Row(int columnSize) {
      super(columnSize, TimestampQueryItem::getOrgLevel3);
    }

    @Override
    public OrgLevel3Row getFoldableRow(String key) {
      return (OrgLevel3Row) super.getFoldableRow(key);
    }

    @Override
    OrgLevel3Row buildRow(int columnSize) {
      return new OrgLevel3Row(columnSize);
    }
  }

  /**
   * Row inside the {@link TimestampReport} containing the information regarding a specific org
   * level 3.
   */
  public static final class OrgLevel3Row extends FoldableRow<TimestampQueryItem> {

    private OrgLevel3Row(int columnSize) {
      super(columnSize, TimestampQueryItem::getOrgLevel4);
    }

    @Override
    public SingleRow<TimestampQueryItem> getFoldableRow(String key) {
      return (SingleRow<TimestampQueryItem>) super.getFoldableRow(key);
    }

    @Override
    Row<TimestampQueryItem> buildRow(int columnSize) {
      return new SingleRow<>(columnSize);
    }
  }
}
