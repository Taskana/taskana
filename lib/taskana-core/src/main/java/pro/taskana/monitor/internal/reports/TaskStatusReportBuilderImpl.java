package pro.taskana.monitor.internal.reports;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.monitor.api.reports.TaskStatusReport;
import pro.taskana.monitor.api.reports.TaskStatusReport.Builder;
import pro.taskana.monitor.api.reports.item.TaskQueryItem;
import pro.taskana.monitor.internal.MonitorMapper;
import pro.taskana.task.api.TaskState;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** The implementation of TaskStatusReportBuilder. */
public class TaskStatusReportBuilderImpl implements TaskStatusReport.Builder {

  private final InternalTaskanaEngine taskanaEngine;
  private final MonitorMapper monitorMapper;
  private final WorkbasketService workbasketService;
  private List<String> domains;
  private List<TaskState> states;
  private List<String> workbasketIds;
  private Integer priorityMinimum;

  public TaskStatusReportBuilderImpl(
      InternalTaskanaEngine taskanaEngine, MonitorMapper monitorMapper) {
    this.taskanaEngine = taskanaEngine;
    this.monitorMapper = monitorMapper;
    this.workbasketService = taskanaEngine.getEngine().getWorkbasketService();
  }

  @Override
  public TaskStatusReport buildReport() throws NotAuthorizedException {
    this.taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR, TaskanaRole.ADMIN);
    try {
      this.taskanaEngine.openConnection();
      List<TaskQueryItem> tasks =
          this.monitorMapper.getTasksCountByState(
              this.domains, this.states, this.workbasketIds, this.priorityMinimum);
      TaskStatusReport report = new TaskStatusReport(this.states);
      report.addItems(tasks);
      Map<String, String> displayMap =
          taskanaEngine.runAsAdmin(
              () ->
                  workbasketService.createWorkbasketQuery()
                      .keyIn(report.getRows().keySet().toArray(new String[0]))
                      .domainIn(domains != null ? domains.toArray(new String[0]) : null).list()
                      .stream()
                      .collect(
                          Collectors.toMap(
                              WorkbasketSummary::getKey, WorkbasketSummary::getName, (a, b) -> a)));
      report.augmentDisplayNames(displayMap);
      return report;
    } finally {
      this.taskanaEngine.returnConnection();
    }
  }

  @Override
  public TaskStatusReportBuilderImpl stateIn(List<TaskState> states) {
    this.states = states;
    return this;
  }

  @Override
  public TaskStatusReportBuilderImpl priorityMinimum(Integer priorityMinimum) {
    this.priorityMinimum = priorityMinimum;
    return this;
  }

  @Override
  public TaskStatusReportBuilderImpl domainIn(List<String> domains) {
    this.domains = domains;
    return this;
  }

  @Override
  public Builder workbasketIdsIn(List<String> workbasketIds) {
    this.workbasketIds = workbasketIds;
    return this;
  }
}
