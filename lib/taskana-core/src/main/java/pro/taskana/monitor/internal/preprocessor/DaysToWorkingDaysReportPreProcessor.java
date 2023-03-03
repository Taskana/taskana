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
package pro.taskana.monitor.internal.preprocessor;

import java.util.List;

import pro.taskana.common.api.WorkingTimeCalculator;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.AgeQueryItem;
import pro.taskana.monitor.api.reports.item.QueryItemPreprocessor;

/**
 * Uses {@linkplain WorkingDaysToDaysReportConverter} to convert an &lt;I&gt;s age to working days.
 *
 * @param <I> QueryItem which is being processed
 */
public class DaysToWorkingDaysReportPreProcessor<I extends AgeQueryItem>
    implements QueryItemPreprocessor<I> {

  private WorkingDaysToDaysReportConverter instance;

  public DaysToWorkingDaysReportPreProcessor(
      List<? extends TimeIntervalColumnHeader> columnHeaders,
      WorkingTimeCalculator workingTimeCalculator,
      boolean activate)
      throws InvalidArgumentException {
    if (activate) {
      instance = WorkingDaysToDaysReportConverter.initialize(columnHeaders, workingTimeCalculator);
    }
  }

  @Override
  public I apply(I item) {
    if (instance != null) {
      item.setAgeInDays(instance.convertDaysToWorkingDays(item.getAgeInDays()));
    }
    return item;
  }
}
