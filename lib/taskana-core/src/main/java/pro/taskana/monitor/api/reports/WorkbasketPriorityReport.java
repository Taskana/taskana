package pro.taskana.monitor.api.reports;

import java.util.List;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.monitor.api.reports.header.ColumnHeader;
import pro.taskana.monitor.api.reports.header.PriorityColumnHeader;
import pro.taskana.monitor.api.reports.item.PriorityQueryItem;
import pro.taskana.monitor.api.reports.row.Row;
import pro.taskana.task.api.models.Task;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.models.Workbasket;

/**
 * A WorkbasketReport aggregates {@linkplain Task} related data.
 *
 * <p>Each {@linkplain Row} represents a {@linkplain Workbasket}.
 *
 * <p>Each {@linkplain ColumnHeader} represents a {@linkplain Task#getPriority() priority}.
 */
public class WorkbasketPriorityReport extends Report<PriorityQueryItem, PriorityColumnHeader> {

  public WorkbasketPriorityReport(List<PriorityColumnHeader> priorityColumnHeaders) {
    super(priorityColumnHeaders, new String[] {"WORKBASKET"});
  }

  /** Builder for {@link WorkbasketPriorityReport}. */
  public interface Builder extends Report.Builder<PriorityQueryItem, PriorityColumnHeader> {

    @Override
    WorkbasketPriorityReport buildReport() throws NotAuthorizedException;

    /**
     * Adds {@linkplain WorkbasketType WorkbasketTypes} to the builder. The created report will only
     * contain Tasks from {@linkplain Workbasket}s with one of the provided types.
     *
     * @param workbasketTypes the workbasketTypes to include in the report
     * @return the builder
     */
    Builder workbasketTypeIn(WorkbasketType... workbasketTypes);

    /**
     * Adds a list of {@linkplain PriorityColumnHeader}s to the builder to subdivide the report into
     * clusters.
     *
     * @param columnHeaders the column headers the report should consist of.
     * @return the builder
     */
    Builder withColumnHeaders(List<PriorityColumnHeader> columnHeaders);
  }
}
