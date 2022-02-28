package pro.taskana.monitor.api.reports.row;

import pro.taskana.monitor.api.reports.TimestampReport;
import pro.taskana.monitor.api.reports.item.TimestampQueryItem;

/**
 * A single Row inside the {@linkplain TimestampReport}. It contains 4 sub-rows for each org level
 * respectively.
 */
public class TimestampRow extends FoldableRow<TimestampQueryItem> {

  public TimestampRow(String key, int columnSize) {
    super(key, columnSize, TimestampQueryItem::getOrgLevel1);
  }

  @Override
  public OrgLevel1Row getFoldableRow(String key) {
    return (OrgLevel1Row) super.getFoldableRow(key);
  }

  @Override
  protected OrgLevel1Row buildRow(String key, int columnSize) {
    return new OrgLevel1Row(key, columnSize);
  }

  /**
   * Row inside the {@linkplain TimestampReport} containing the information regarding a specific org
   * level 1.
   */
  public static final class OrgLevel1Row extends FoldableRow<TimestampQueryItem> {

    private OrgLevel1Row(String key, int columnSize) {
      super(key, columnSize, TimestampQueryItem::getOrgLevel2);
    }

    @Override
    public OrgLevel2Row getFoldableRow(String key) {
      return (OrgLevel2Row) super.getFoldableRow(key);
    }

    @Override
    protected OrgLevel2Row buildRow(String key, int columnSize) {
      return new OrgLevel2Row(key, columnSize);
    }
  }

  /**
   * Row inside the {@linkplain TimestampReport} containing the information regarding a specific org
   * level 2.
   */
  public static final class OrgLevel2Row extends FoldableRow<TimestampQueryItem> {

    private OrgLevel2Row(String key, int columnSize) {
      super(key, columnSize, TimestampQueryItem::getOrgLevel3);
    }

    @Override
    public OrgLevel3Row getFoldableRow(String key) {
      return (OrgLevel3Row) super.getFoldableRow(key);
    }

    @Override
    protected OrgLevel3Row buildRow(String key, int columnSize) {
      return new OrgLevel3Row(key, columnSize);
    }
  }

  /**
   * Row inside the {@linkplain TimestampReport} containing the information regarding a specific org
   * level 3.
   */
  public static final class OrgLevel3Row extends FoldableRow<TimestampQueryItem> {

    private OrgLevel3Row(String key, int columnSize) {
      super(key, columnSize, TimestampQueryItem::getOrgLevel4);
    }

    @Override
    public SingleRow<TimestampQueryItem> getFoldableRow(String key) {
      return (SingleRow<TimestampQueryItem>) super.getFoldableRow(key);
    }

    @Override
    protected Row<TimestampQueryItem> buildRow(String key, int columnSize) {
      return new SingleRow<>(key, columnSize);
    }
  }
}
