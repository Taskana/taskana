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
package pro.taskana.monitor.api.reports.row;

import pro.taskana.monitor.api.reports.ClassificationReport;
import pro.taskana.monitor.api.reports.item.DetailedMonitorQueryItem;

/**
 * Represents a single Row inside {@linkplain ClassificationReport.DetailedClassificationReport}.
 * The collapsing criteria is the attachement key of each {@linkplain DetailedMonitorQueryItem}.
 */
public class DetailedClassificationRow extends FoldableRow<DetailedMonitorQueryItem> {

  public DetailedClassificationRow(String key, int columnSize) {
    super(
        key, columnSize, item -> item.getAttachmentKey() != null ? item.getAttachmentKey() : "N/A");
  }

  @Override
  public SingleRow<DetailedMonitorQueryItem> getFoldableRow(String key) {
    return (SingleRow<DetailedMonitorQueryItem>) super.getFoldableRow(key);
  }

  @Override
  protected Row<DetailedMonitorQueryItem> buildRow(String key, int columnSize) {
    return new SingleRow<>(key, columnSize);
  }
}
