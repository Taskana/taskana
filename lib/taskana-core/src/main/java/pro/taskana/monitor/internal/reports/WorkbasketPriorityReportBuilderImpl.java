package pro.taskana.monitor.internal.reports;

import java.util.Collections;
import java.util.List;

import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.monitor.api.reports.WorkbasketPriorityReport;
import pro.taskana.monitor.api.reports.WorkbasketPriorityReport.Builder;
import pro.taskana.monitor.api.reports.header.PriorityColumnHeader;
import pro.taskana.monitor.api.reports.item.PriorityQueryItem;
import pro.taskana.monitor.internal.MonitorMapper;
import pro.taskana.workbasket.api.WorkbasketType;

public class WorkbasketPriorityReportBuilderImpl implements WorkbasketPriorityReport.Builder {

  private final InternalTaskanaEngine taskanaEngine;
  private final MonitorMapper monitorMapper;

  @SuppressWarnings("unused")
  private WorkbasketType[] workbasketType;

  private List<PriorityColumnHeader> columnHeaders;

  public WorkbasketPriorityReportBuilderImpl(
      InternalTaskanaEngine taskanaEngine, MonitorMapper monitorMapper) {
    this.taskanaEngine = taskanaEngine;
    this.monitorMapper = monitorMapper;
    columnHeaders = Collections.emptyList();
  }

  @Override
  public WorkbasketPriorityReport buildReport() throws NotAuthorizedException {
    taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR, TaskanaRole.ADMIN);

    WorkbasketPriorityReport report = new WorkbasketPriorityReport(columnHeaders);
    List<PriorityQueryItem> items =
        taskanaEngine.executeInDatabaseConnection(
            () -> monitorMapper.getTaskCountByPriority(this));
    report.addItems(items);
    return report;
  }

  @Override
  public Builder workbasketTypeIn(WorkbasketType... workbasketTypes) {
    this.workbasketType = workbasketTypes;
    return this;
  }

  @Override
  public Builder withColumnHeaders(List<PriorityColumnHeader> columnHeaders) {
    this.columnHeaders = columnHeaders;
    return this;
  }
}
