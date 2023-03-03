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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.monitor.api.SelectedItem;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.Report;
import pro.taskana.monitor.api.reports.TimestampReport;
import pro.taskana.monitor.api.reports.TimestampReport.Builder;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.TimestampQueryItem;
import pro.taskana.monitor.internal.MonitorMapper;
import pro.taskana.monitor.internal.preprocessor.DaysToWorkingDaysReportPreProcessor;
import pro.taskana.task.api.TaskState;

/** The implementation of {@linkplain TimestampReport.Builder}. */
public class TimestampReportBuilderImpl
    extends TimeIntervalReportBuilderImpl<Builder, TimestampQueryItem, TimeIntervalColumnHeader>
    implements TimestampReport.Builder {

  private List<TaskTimestamp> status =
      Arrays.asList(TaskTimestamp.CREATED, TaskTimestamp.COMPLETED);

  public TimestampReportBuilderImpl(
      InternalTaskanaEngine taskanaEngine, MonitorMapper monitorMapper) {
    super(taskanaEngine, monitorMapper);
  }

  @Override
  public TimestampReport.Builder stateIn(List<TaskState> states) {
    throw new UnsupportedOperationException(
        "The states have no influence regarding this report. Use withTimestamps instead");
  }

  @Override
  public List<String> listTaskIdsForSelectedItems(
      List<SelectedItem> selectedItems, TaskTimestamp timestamp) {
    throw new UnsupportedOperationException();
  }

  @Override
  public TimestampReport.Builder withTimestamps(List<TaskTimestamp> taskTimestamps) {
    this.status = new ArrayList<>(taskTimestamps);
    return _this();
  }

  @Override
  public Report<TimestampQueryItem, TimeIntervalColumnHeader> buildReport(TaskTimestamp timestamp)
      throws InvalidArgumentException, MismatchedRoleException {
    return buildReport();
  }

  @Override
  public TimestampReport buildReport() throws InvalidArgumentException, MismatchedRoleException {
    this.taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR, TaskanaRole.ADMIN);
    try {
      this.taskanaEngine.openConnection();
      TimestampReport report = new TimestampReport(columnHeaders);
      List<TimestampQueryItem> items =
          status.stream()
              // This can also be implemented into a single sql query which combines all statuses
              // with the union operator. That would reduce the readability of the sql template.
              // That's why "the loop" is done outside mybatis.
              .map(this::getTasksCountForStatusGroupedByOrgLevel)
              .flatMap(Collection::stream)
              .collect(Collectors.toList());

      report.addItems(
          items,
          new DaysToWorkingDaysReportPreProcessor<>(
              columnHeaders, workingTimeCalculator, inWorkingDays));
      return report;
    } finally {
      this.taskanaEngine.returnConnection();
    }
  }

  @Override
  protected TimestampReport.Builder _this() {
    return this;
  }

  @Override
  protected String determineGroupedBy() {
    throw new UnsupportedOperationException();
  }

  private List<TimestampQueryItem> getTasksCountForStatusGroupedByOrgLevel(TaskTimestamp s) {
    return monitorMapper.getTasksCountForStatusGroupedByOrgLevel(Instant.now(), s, this);
  }
}
