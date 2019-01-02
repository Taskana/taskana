package pro.taskana.impl.report.row;

import pro.taskana.impl.report.item.DailyEntryExitQueryItem;
import pro.taskana.report.structure.Row;

/**
 * A single Row inside the {@link pro.taskana.report.DailyEntryExitReport}.
 * It contains 4 sub-rows for each org level respectively.
 */
public class DailyEntryExitRow extends FoldableRow<DailyEntryExitQueryItem> {

    public DailyEntryExitRow(int columnSize) {
        super(columnSize, DailyEntryExitQueryItem::getOrgLevel1);
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
     * Row inside the {@link pro.taskana.report.DailyEntryExitReport} containing
     * the information regarding a specific org level 1.
     */
    public static final class OrgLevel1Row extends FoldableRow<DailyEntryExitQueryItem> {

        private OrgLevel1Row(int columnSize) {
            super(columnSize, DailyEntryExitQueryItem::getOrgLevel2);
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
     * Row inside the {@link pro.taskana.report.DailyEntryExitReport} containing
     * the information regarding a specific org level 2.
     */
    public static final class OrgLevel2Row extends FoldableRow<DailyEntryExitQueryItem> {

        private OrgLevel2Row(int columnSize) {
            super(columnSize, DailyEntryExitQueryItem::getOrgLevel3);
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
     * Row inside the {@link pro.taskana.report.DailyEntryExitReport} containing
     * the information regarding a specific org level 3.
     */
    public static final class OrgLevel3Row extends FoldableRow<DailyEntryExitQueryItem> {

        private OrgLevel3Row(int columnSize) {
            super(columnSize, DailyEntryExitQueryItem::getOrgLevel4);
        }

        @Override
        Row<DailyEntryExitQueryItem> buildRow(int columnSize) {
            return new SingleRow<>(columnSize);
        }

        @Override
        public SingleRow<DailyEntryExitQueryItem> getFoldableRow(String key) {
            return (SingleRow<DailyEntryExitQueryItem>) super.getFoldableRow(key);
        }
    }

}
