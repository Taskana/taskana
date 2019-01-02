package pro.taskana.report;

import java.util.List;

import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.CombinedClassificationFilter;
import pro.taskana.impl.report.header.TimeIntervalColumnHeader;
import pro.taskana.impl.report.item.MonitorQueryItem;
import pro.taskana.report.structure.Report;

/**
 * A WorkbasketReport contains the total numbers of tasks of the respective workbasket as well as the
 * total number of all tasks. The tasks of the report can be filtered by workbaskets, states,
 * categories, domains, classifications and values of a custom field. Classifications can also be excluded from the
 * report. It is also possible to filter by the classifications of the attachments by using the
 * {@link CombinedClassificationFilter}. If the {@link TimeIntervalColumnHeader}s are set, the report contains also the
 * number of tasks of the respective cluster. The age of the tasks can be counted in days or in working days. Tasks with
 * Timestamp DUE = null are not considered.
 */

public class WorkbasketReport extends Report<MonitorQueryItem, TimeIntervalColumnHeader> {

    public WorkbasketReport(List<TimeIntervalColumnHeader> timeIntervalColumnHeaders) {
        super(timeIntervalColumnHeaders, "WORKBASKET KEYS");
    }

    /**
     * Builder for {@link WorkbasketReport}.
     */
    public interface Builder extends TimeIntervalReportBuilder<Builder, MonitorQueryItem, TimeIntervalColumnHeader> {

        @Override
        WorkbasketReport buildReport() throws NotAuthorizedException, InvalidArgumentException;

        /**
         * Adds a list of {@link CombinedClassificationFilter} to the builder. The created report contains only tasks with a
         * pair of a classificationId for a task and a classificationId for the corresponding attachment in this list.
         *
         * @param combinedClassificationFilter
         *            a list of combinedClassificationFilter
         * @return the WorkbasketReportBuilder
         */
        WorkbasketReport.Builder combinedClassificationFilterIn(
            List<CombinedClassificationFilter> combinedClassificationFilter);
    }
}
