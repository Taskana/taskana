package pro.taskana.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskState;
import pro.taskana.TaskStatus;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaRole;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.header.TimeIntervalColumnHeader;
import pro.taskana.impl.report.item.DailyEntryExitQueryItem;
import pro.taskana.impl.report.preprocessor.DaysToWorkingDaysPreProcessor;
import pro.taskana.mappings.TaskMonitorMapper;
import pro.taskana.report.DailyEntryExitReport;

/**
 * The implementation of {@link DailyEntryExitReport.Builder}.
 */
public class DailyEntryExitReportBuilderImpl extends
    TimeIntervalReportBuilderImpl<DailyEntryExitReport.Builder, DailyEntryExitQueryItem, TimeIntervalColumnHeader.Date>
    implements DailyEntryExitReport.Builder {

    private static final Logger LOGGER = LoggerFactory.getLogger(DailyEntryExitReport.Builder.class);
    private List<TaskStatus> status = Arrays.asList(TaskStatus.CREATED, TaskStatus.COMPLETED);

    DailyEntryExitReportBuilderImpl(TaskanaEngine taskanaEngine, TaskMonitorMapper taskMonitorMapper) {
        super(taskanaEngine, taskMonitorMapper);
    }

    @Override
    public DailyEntryExitReport.Builder stateIn(List<TaskState> states) {
        throw new UnsupportedOperationException(
            "The states have no influence regarding this report. Use statusIn instead");
    }

    @Override
    protected DailyEntryExitReport.Builder _this() {
        return this;
    }

    // since this method is not documented I have no idea what the proper groupedBy should be,
    // thus this method is not supported on this builder.
    @Override
    public List<String> listTaskIdsForSelectedItems(List<SelectedItem> selectedItems) {
        throw new UnsupportedOperationException("");
    }

    @Override
    protected String determineGroupedBy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DailyEntryExitReport.Builder statusIn(List<TaskStatus> statuses) {
        this.status = new ArrayList<>(statuses);
        return _this();
    }

    @Override
    public DailyEntryExitReport buildReport() throws NotAuthorizedException, InvalidArgumentException {
        LOGGER.debug("entry to buildDetailedReport(), this = {}", this);
        this.taskanaEngine.checkRoleMembership(TaskanaRole.MONITOR, TaskanaRole.ADMIN);
        try {
            this.taskanaEngine.openConnection();
            DailyEntryExitReport report = new DailyEntryExitReport(this.columnHeaders);
            List<DailyEntryExitQueryItem> items = status.stream()
                // This can also be implemented into a single sql query which combines all statuses with the union
                // operator. That would reduce the readability of the sql template. That's why "the loop" is done
                // outside of mybatis.
                .map(this::getTasksCountForStatusGroupedByOrgLevel)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

            report.addItems(items,
                new DaysToWorkingDaysPreProcessor<>(this.columnHeaders, this.inWorkingDays));
            return report;
        } finally {
            this.taskanaEngine.returnConnection();
            LOGGER.debug("exit from buildDetailedReport().");
        }
    }

    private List<DailyEntryExitQueryItem> getTasksCountForStatusGroupedByOrgLevel(TaskStatus s) {
        return taskMonitorMapper.getTasksCountForStatusGroupedByOrgLevel(s, categories, classificationIds,
            excludedClassificationIds, domains, customAttributeFilter);
    }
}
