/*-
 * #%L
 * pro.taskana:taskana-core
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package pro.taskana.monitor.internal.reports;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.MismatchedRoleException;
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
  private String[] domains;
  private TaskState[] states;
  private String[] workbasketIds;
  private Integer priorityMinimum;

  public TaskStatusReportBuilderImpl(
      InternalTaskanaEngine taskanaEngine, MonitorMapper monitorMapper) {
    this.taskanaEngine = taskanaEngine;
    this.monitorMapper = monitorMapper;
    this.workbasketService = taskanaEngine.getEngine().getWorkbasketService();
  }

  @Override
  public TaskStatusReport buildReport() throws MismatchedRoleException {
    this.taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR, TaskanaRole.ADMIN);
    try {
      this.taskanaEngine.openConnection();
      List<TaskQueryItem> tasks =
          this.monitorMapper.getTasksCountByState(
              this.domains, this.states, this.workbasketIds, this.priorityMinimum);
      TaskStatusReport report =
          new TaskStatusReport(this.states != null ? Arrays.asList(this.states) : null);
      report.addItems(tasks);
      Map<String, String> displayMap =
          taskanaEngine
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
      this.taskanaEngine.returnConnection();
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
