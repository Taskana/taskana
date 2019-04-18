package pro.taskana.report;

import java.util.List;

import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.header.TimeIntervalColumnHeader;
import pro.taskana.impl.report.item.MonitorQueryItem;
import pro.taskana.impl.report.structure.Report;

/**
 * A CategoryReport contains the total numbers of tasks of the respective category as well as the total number of
 * all tasks. The tasks of the report can be filtered by workbaskets, states, categories, domains, classifications
 * and values of a custom field. Classifications can also be excluded from the report.
 * If the {@link TimeIntervalColumnHeader}s are set, the report contains also the number of tasks of the
 * respective cluster. The age of the tasks can be counted in days or in working days. Tasks with Timestamp DUE = null
 * are not considered.
 */
public class CategoryReport extends Report<MonitorQueryItem, TimeIntervalColumnHeader> {

    public CategoryReport(List<TimeIntervalColumnHeader> timeIntervalColumnHeaders) {
        super(timeIntervalColumnHeaders, new String[] {"CLASSIFICATION CATEGORIES"});
    }

    /**
     * Builder for {@link CategoryReport}.
     */
    public interface Builder extends TimeIntervalReportBuilder<Builder, MonitorQueryItem, TimeIntervalColumnHeader> {

        @Override
        CategoryReport buildReport() throws NotAuthorizedException, InvalidArgumentException;
    }
}
