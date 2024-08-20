package io.kadai.monitor.internal.reports;

import io.kadai.common.api.KadaiRole;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.internal.InternalKadaiEngine;
import io.kadai.monitor.api.reports.TaskStatusReport;
import io.kadai.monitor.api.reports.TaskStatusReport.Builder;
import io.kadai.monitor.api.reports.item.TaskQueryItem;
import io.kadai.monitor.internal.MonitorMapper;
import io.kadai.task.api.TaskState;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** The implementation of TaskStatusReportBuilder. */
public class TaskStatusReportBuilderImpl implements TaskStatusReport.Builder {

  private final InternalKadaiEngine kadaiEngine;
  private final MonitorMapper monitorMapper;
  private final WorkbasketService workbasketService;
  private String[] domains;
  private TaskState[] states;
  private String[] workbasketIds;
  private Integer priorityMinimum;

  public TaskStatusReportBuilderImpl(InternalKadaiEngine kadaiEngine, MonitorMapper monitorMapper) {
    this.kadaiEngine = kadaiEngine;
    this.monitorMapper = monitorMapper;
    this.workbasketService = kadaiEngine.getEngine().getWorkbasketService();
  }

  @Override
  public TaskStatusReport buildReport() throws NotAuthorizedException {
    this.kadaiEngine.getEngine().checkRoleMembership(KadaiRole.MONITOR, KadaiRole.ADMIN);
    try {
      this.kadaiEngine.openConnection();
      List<TaskQueryItem> tasks =
          this.monitorMapper.getTasksCountByState(
              this.domains, this.states, this.workbasketIds, this.priorityMinimum);
      TaskStatusReport report =
          new TaskStatusReport(this.states != null ? Arrays.asList(this.states) : null);
      report.addItems(tasks);
      Map<String, String> displayMap =
          kadaiEngine
              .getEngine()
              .runAsAdmin(
                  () ->
                      workbasketService
                          .createWorkbasketQuery()
                          .keyIn(report.getRows().keySet().toArray(new String[0]))
                          .domainIn(domains)
                          .list()
                          .stream()
                          .collect(
                              Collectors.toMap(
                                  WorkbasketSummary::getKey,
                                  WorkbasketSummary::getName,
                                  (a, b) -> a)));
      report.augmentDisplayNames(displayMap);
      return report;
    } finally {
      this.kadaiEngine.returnConnection();
    }
  }

  @Override
  public TaskStatusReportBuilderImpl stateIn(List<TaskState> states) {
    if (states != null) {
      this.states = states.toArray(new TaskState[0]);
    }
    return this;
  }

  @Override
  public TaskStatusReportBuilderImpl priorityMinimum(Integer priorityMinimum) {
    this.priorityMinimum = priorityMinimum;
    return this;
  }

  @Override
  public TaskStatusReportBuilderImpl domainIn(List<String> domains) {
    if (domains != null) {
      this.domains = domains.toArray(new String[0]);
    }
    return this;
  }

  @Override
  public Builder workbasketIdsIn(List<String> workbasketIds) {
    if (workbasketIds != null) {
      this.workbasketIds = workbasketIds.toArray(new String[0]);
    }
    return this;
  }
}
