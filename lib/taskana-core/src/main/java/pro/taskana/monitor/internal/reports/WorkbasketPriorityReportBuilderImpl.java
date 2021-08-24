package pro.taskana.monitor.internal.reports;

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

/** The implementation of WorkbasketReportBuilder. */
public class WorkbasketPriorityReportBuilderImpl implements WorkbasketPriorityReport.Builder {

  protected InternalTaskanaEngine taskanaEngine;
  protected MonitorMapper monitorMapper;
  private WorkbasketType[] workbasketType;
  private List<PriorityColumnHeader> columnHeaders;

  public WorkbasketPriorityReportBuilderImpl(
      InternalTaskanaEngine taskanaEngine, MonitorMapper monitorMapper) {
    this.taskanaEngine = taskanaEngine;
    this.monitorMapper = monitorMapper;
  }

  @Override
  public WorkbasketPriorityReport buildReport() throws NotAuthorizedException {
    // create report
    // sql magic from monitor mapper
    // data in report
    this.taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR, TaskanaRole.ADMIN);

    WorkbasketPriorityReport report = new WorkbasketPriorityReport(columnHeaders);
    List<PriorityQueryItem> items =
        this.taskanaEngine.executeInDatabaseConnection(
            () -> this.monitorMapper.getTaskCountByPriority(this));
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
