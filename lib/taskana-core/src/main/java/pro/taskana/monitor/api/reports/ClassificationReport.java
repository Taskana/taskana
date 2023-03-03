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

import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.header.ColumnHeader;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.DetailedMonitorQueryItem;
import pro.taskana.monitor.api.reports.item.MonitorQueryItem;
import pro.taskana.monitor.api.reports.row.DetailedClassificationRow;
import pro.taskana.monitor.api.reports.row.FoldableRow;
import pro.taskana.monitor.api.reports.row.Row;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.Task;

/**
 * A ClassificationReport aggregates {@linkplain Task} related data.
 *
 * <p>Each {@linkplain Row} represents a {@linkplain Classification}.
 *
 * <p>Each {@linkplain ColumnHeader} represents a {@linkplain TimeInterval}.
 */
public class ClassificationReport extends Report<MonitorQueryItem, TimeIntervalColumnHeader> {

  public ClassificationReport(List<TimeIntervalColumnHeader> timeIntervalColumnHeaders) {
    super(timeIntervalColumnHeaders, new String[] {"CLASSIFICATION"});
  }

  /** Builder for {@linkplain ClassificationReport}. */
  public interface Builder
      extends TimeIntervalReportBuilder<Builder, MonitorQueryItem, TimeIntervalColumnHeader> {

    @Override
    ClassificationReport buildReport() throws InvalidArgumentException, MismatchedRoleException;

    @Override
    ClassificationReport buildReport(TaskTimestamp timestamp)
        throws InvalidArgumentException, MismatchedRoleException;

    /**
     * Returns a {@linkplain DetailedClassificationReport} containing all tasks after applying the
     * filters. If the column headers are set the report is subdivided into clusters. Its {@link
     * FoldableRow}s contain an additional list of {@linkplain Row}s for the classifications of the
     * attachments of the tasks.
     *
     * @return the DetailedClassificationReport
     * @throws InvalidArgumentException if the column headers are not initialized
     * @throws MismatchedRoleException if the current user is not member of {@linkplain
     *     TaskanaRole#MONITOR} or {@linkplain TaskanaRole#ADMIN}
     */
    DetailedClassificationReport buildDetailedReport()
        throws InvalidArgumentException, MismatchedRoleException;

    DetailedClassificationReport buildDetailedReport(TaskTimestamp timestamp)
        throws InvalidArgumentException, MismatchedRoleException;
  }

  /**
   * A DetailedClassificationReport aggregates {@linkplain Task} related data.
   *
   * <p>Each {@linkplain FoldableRow} represents a {@linkplain Classification} and can be expanded
   * to show the {@linkplain Classification} of {@linkplain Attachment}s.
   *
   * <p>Each {@linkplain ColumnHeader} represents a {@linkplain TimeInterval}.
   */
  public static class DetailedClassificationReport
      extends Report<DetailedMonitorQueryItem, TimeIntervalColumnHeader> {

    public DetailedClassificationReport(
        List<TimeIntervalColumnHeader> workbasketLevelReportColumnHeaders) {
      super(workbasketLevelReportColumnHeaders, new String[] {"TASK CLASSIFICATION", "ATTACHMENT"});
    }

    @Override
    public DetailedClassificationRow getRow(String key) {
      return (DetailedClassificationRow) super.getRow(key);
    }

    @Override
    protected DetailedClassificationRow createRow(String key, int columnSize) {
      return new DetailedClassificationRow(key, columnSize);
    }
  }
}
