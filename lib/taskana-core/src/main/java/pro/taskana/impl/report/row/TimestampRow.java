package pro.taskana.impl.report.row;

import pro.taskana.impl.report.item.TimestampQueryItem;
import pro.taskana.impl.report.structure.Row;

/**
 * A single Row inside the {@link pro.taskana.report.TimestampReport}.
 * It contains 4 sub-rows for each org level respectively.
 */
public class TimestampRow extends FoldableRow<TimestampQueryItem> {

    public TimestampRow(int columnSize) {
        super(columnSize, TimestampQueryItem::getOrgLevel1);
    }

    @Override
    OrgLevel1Row buildRow(int columnSize) {
        return new OrgLevel1Row(columnSize);
    }

    @Override
    public OrgLevel1Row getFoldableRow(String key) {
        return (OrgLevel1Row) super.getFoldableRow(key);
    }

    /**
     * Row inside the {@link pro.taskana.report.TimestampReport} containing
     * the information regarding a specific org level 1.
     */
    public static final class OrgLevel1Row extends FoldableRow<TimestampQueryItem> {

        private OrgLevel1Row(int columnSize) {
            super(columnSize, TimestampQueryItem::getOrgLevel2);
        }

        @Override
        OrgLevel2Row buildRow(int columnSize) {
            return new OrgLevel2Row(columnSize);
        }

        @Override
        public OrgLevel2Row getFoldableRow(String key) {
            return (OrgLevel2Row) super.getFoldableRow(key);
        }
    }

    /**
     * Row inside the {@link pro.taskana.report.TimestampReport} containing
     * the information regarding a specific org level 2.
     */
    public static final class OrgLevel2Row extends FoldableRow<TimestampQueryItem> {

        private OrgLevel2Row(int columnSize) {
            super(columnSize, TimestampQueryItem::getOrgLevel3);
        }

        @Override
        OrgLevel3Row buildRow(int columnSize) {
            return new OrgLevel3Row(columnSize);
        }

        @Override
        public OrgLevel3Row getFoldableRow(String key) {
            return (OrgLevel3Row) super.getFoldableRow(key);
        }
    }

    /**
     * Row inside the {@link pro.taskana.report.TimestampReport} containing
     * the information regarding a specific org level 3.
     */
    public static final class OrgLevel3Row extends FoldableRow<TimestampQueryItem> {

        private OrgLevel3Row(int columnSize) {
            super(columnSize, TimestampQueryItem::getOrgLevel4);
        }

        @Override
        Row<TimestampQueryItem> buildRow(int columnSize) {
            return new SingleRow<>(columnSize);
        }

        @Override
        public SingleRow<TimestampQueryItem> getFoldableRow(String key) {
            return (SingleRow<TimestampQueryItem>) super.getFoldableRow(key);
        }
    }

}
