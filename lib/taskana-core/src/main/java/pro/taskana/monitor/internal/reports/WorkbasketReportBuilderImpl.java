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

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.monitor.api.CombinedClassificationFilter;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.WorkbasketReport;
import pro.taskana.monitor.api.reports.WorkbasketReport.Builder;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.MonitorQueryItem;
import pro.taskana.monitor.internal.MonitorMapper;
import pro.taskana.monitor.internal.preprocessor.DaysToWorkingDaysReportPreProcessor;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

public class WorkbasketReportBuilderImpl
    extends TimeIntervalReportBuilderImpl<Builder, MonitorQueryItem, TimeIntervalColumnHeader>
    implements WorkbasketReport.Builder {

  private final WorkbasketService workbasketService;
  private List<CombinedClassificationFilter> combinedClassificationFilter;

  public WorkbasketReportBuilderImpl(
      InternalTaskanaEngine taskanaEngine, MonitorMapper monitorMapper) {
    super(taskanaEngine, monitorMapper);
    workbasketService = taskanaEngine.getEngine().getWorkbasketService();
  }

  @Override
  public WorkbasketReport buildReport() throws InvalidArgumentException, MismatchedRoleException {
    return buildReport(TaskTimestamp.DUE);
  }

  @Override
  public WorkbasketReport buildReport(TaskTimestamp timestamp)
      throws InvalidArgumentException, MismatchedRoleException {
    this.taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR, TaskanaRole.ADMIN);
    try {
      this.taskanaEngine.openConnection();
      WorkbasketReport report = new WorkbasketReport(this.columnHeaders);
      List<MonitorQueryItem> monitorQueryItems =
          this.monitorMapper.getTaskCountOfWorkbaskets(Instant.now(), timestamp, this);
      report.addItems(
          monitorQueryItems,
          new DaysToWorkingDaysReportPreProcessor<>(
              this.columnHeaders, workingTimeCalculator, this.inWorkingDays));

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
  public WorkbasketReport.Builder combinedClassificationFilterIn(
      List<CombinedClassificationFilter> combinedClassificationFilter) {
    this.combinedClassificationFilter = combinedClassificationFilter;
    return this;
  }

  @Override
  protected WorkbasketReport.Builder _this() {
    return this;
  }

  @Override
  protected String determineGroupedBy() {
    return "WORKBASKET_KEY";
  }

  @Override
  protected List<CombinedClassificationFilter> getCombinedClassificationFilter() {
    return combinedClassificationFilter;
  }
}
