package pro.taskana.monitor.api.reports;

import pro.taskana.monitor.api.reports.header.PriorityColumnHeader;
import pro.taskana.monitor.api.reports.item.PriorityQueryItem;

public interface PriorityReportBuilder<
    B extends PriorityReportBuilder<B, I, H>,
    I extends PriorityQueryItem,
    H extends PriorityColumnHeader> extends Report.Builder<I, H> {}
