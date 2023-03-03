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

import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.ClassificationCategoryReport;
import pro.taskana.monitor.api.reports.ClassificationCategoryReport.Builder;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.MonitorQueryItem;
import pro.taskana.monitor.internal.MonitorMapper;
import pro.taskana.monitor.internal.preprocessor.DaysToWorkingDaysReportPreProcessor;

/** The implementation of CategoryReportBuilder. */
public class ClassificationCategoryReportBuilderImpl
    extends TimeIntervalReportBuilderImpl<Builder, MonitorQueryItem, TimeIntervalColumnHeader>
    implements ClassificationCategoryReport.Builder {

  public ClassificationCategoryReportBuilderImpl(
      InternalTaskanaEngine taskanaEngine, MonitorMapper monitorMapper) {
    super(taskanaEngine, monitorMapper);
  }

  @Override
  public ClassificationCategoryReport buildReport()
      throws InvalidArgumentException, MismatchedRoleException {
    return buildReport(TaskTimestamp.DUE);
  }

  @Override
  public ClassificationCategoryReport buildReport(TaskTimestamp timestamp)
      throws InvalidArgumentException, MismatchedRoleException {
    this.taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR, TaskanaRole.ADMIN);
    try {
      this.taskanaEngine.openConnection();
      ClassificationCategoryReport report = new ClassificationCategoryReport(this.columnHeaders);
      List<MonitorQueryItem> monitorQueryItems =
          this.monitorMapper.getTaskCountOfCategories(Instant.now(), timestamp, this);
      report.addItems(
          monitorQueryItems,
          new DaysToWorkingDaysReportPreProcessor<>(
              this.columnHeaders, workingTimeCalculator, this.inWorkingDays));
      return report;
    } finally {
      this.taskanaEngine.returnConnection();
    }
  }

  @Override
  protected ClassificationCategoryReport.Builder _this() {
    return this;
  }

  @Override
  protected String determineGroupedBy() {
    return "CLASSIFICATION_CATEGORY";
  }
}
