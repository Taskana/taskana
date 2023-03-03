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
package pro.taskana.monitor.api.reports;

import java.util.List;

import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.monitor.api.CombinedClassificationFilter;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.header.ColumnHeader;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.MonitorQueryItem;
import pro.taskana.monitor.api.reports.row.Row;
import pro.taskana.task.api.models.Task;
import pro.taskana.workbasket.api.models.Workbasket;

/**
 * A WorkbasketReport aggregates {@linkplain Task} related data.
 *
 * <p>Each {@linkplain Row} represents a {@linkplain Workbasket}.
 *
 * <p>Each {@linkplain ColumnHeader} represents a {@linkplain TimeInterval}.
 */
public class WorkbasketReport extends Report<MonitorQueryItem, TimeIntervalColumnHeader> {

  public WorkbasketReport(List<TimeIntervalColumnHeader> timeIntervalColumnHeaders) {
    super(timeIntervalColumnHeaders, new String[] {"WORKBASKET"});
  }

  /** Builder for {@linkplain WorkbasketReport}. */
  public interface Builder
      extends TimeIntervalReportBuilder<Builder, MonitorQueryItem, TimeIntervalColumnHeader> {

    @Override
    WorkbasketReport buildReport() throws InvalidArgumentException, MismatchedRoleException;

    @Override
    WorkbasketReport buildReport(TaskTimestamp timestamp)
        throws InvalidArgumentException, MismatchedRoleException;

    /**
     * Adds a list of {@linkplain CombinedClassificationFilter} to the builder. The created report
     * contains only tasks with a pair of a classificationId for a task and a classificationId for
     * the corresponding attachment in this list.
     *
     * @param combinedClassificationFilter a list of combinedClassificationFilter
     * @return the WorkbasketReportBuilder
     */
    WorkbasketReport.Builder combinedClassificationFilterIn(
        List<CombinedClassificationFilter> combinedClassificationFilter);
  }
}
