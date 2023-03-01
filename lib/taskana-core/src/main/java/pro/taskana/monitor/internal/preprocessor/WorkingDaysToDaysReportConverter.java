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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.WorkingTimeCalculator;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;

/**
 * The DaysToWorkingDaysReportConverter provides a method to convert an age in days into an age in
 * working days. Before the method convertDaysToWorkingDays() can be used, the
 * WorkingDaysToDaysReportConverter has to be initialized. For a list of {@linkplain
 * TimeIntervalColumnHeader}s the converter creates a "table" with integer that represents the age
 * in days from the largest lower limit until the smallest upper limit of the
 * timeIntervalColumnHeaders. This table is valid for a whole day until the converter is initialized
 * with bigger limits.
 */
public class WorkingDaysToDaysReportConverter {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(WorkingDaysToDaysReportConverter.class);

  private final WorkingTimeCalculator workingTimeCalculator;
  private final Map<Integer, Integer> cacheDaysToWorkingDays;

  WorkingDaysToDaysReportConverter(
      List<? extends TimeIntervalColumnHeader> columnHeaders,
      WorkingTimeCalculator workingTimeCalculator,
      Instant referenceDate) {
    this.workingTimeCalculator = workingTimeCalculator;
    cacheDaysToWorkingDays = generateDaysToWorkingDays(columnHeaders, referenceDate);
  }

  public static WorkingDaysToDaysReportConverter initialize(
      List<? extends TimeIntervalColumnHeader> columnHeaders,
      WorkingTimeCalculator workingTimeCalculator)
      throws InvalidArgumentException {
    return initialize(columnHeaders, workingTimeCalculator, Instant.now());
  }

  /**
   * Initializes the WorkingDaysToDaysConverter for a list of {@linkplain TimeIntervalColumnHeader}s
   * and a referenceDate. A new table is only created if there are bigger limits or the date has
   * changed.
   *
   * @param columnHeaders a list of {@linkplain TimeIntervalColumnHeader}s that determines the size
   *     of the table
   * @param workingTimeCalculator the workingTimeCalculator used by taskana to determine if a
   *     specific day is a working day.
   * @param referenceDate a {@linkplain Instant} that represents the current day of the table
   * @return an instance of the WorkingDaysToDaysConverter
   * @throws InvalidArgumentException thrown if columnHeaders or referenceDate is null
   */
  public static WorkingDaysToDaysReportConverter initialize(
      List<? extends TimeIntervalColumnHeader> columnHeaders,
      WorkingTimeCalculator workingTimeCalculator,
      Instant referenceDate)
      throws InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Initialize WorkingDaysToDaysConverter with columnHeaders: {}", columnHeaders);
    }

    if (workingTimeCalculator == null) {
      throw new InvalidArgumentException("WorkingDaysToDaysConverter can't be null");
    }
    if (columnHeaders == null) {
      throw new InvalidArgumentException("TimeIntervalColumnHeaders can't be null");
    }
    if (referenceDate == null) {
      throw new InvalidArgumentException("ReferenceDate can't be null");
    }

    return new WorkingDaysToDaysReportConverter(
        columnHeaders, workingTimeCalculator, referenceDate);
  }

  public int convertDaysToWorkingDays(int amountOfDays) {
    return cacheDaysToWorkingDays.getOrDefault(amountOfDays, amountOfDays);
  }

  public List<Integer> convertWorkingDaysToDays(int amountOfWorkdays) {
    List<Integer> listOfAllMatchingDays =
        cacheDaysToWorkingDays.entrySet().stream()
            .filter(entry -> entry.getValue() == amountOfWorkdays)
            .map(Entry::getKey)
            .collect(Collectors.toList());
    if (listOfAllMatchingDays.isEmpty()) {
      return Collections.singletonList(amountOfWorkdays);
    }
    return listOfAllMatchingDays;
  }

  protected Map<Integer, Integer> generateDaysToWorkingDays(
      List<? extends TimeIntervalColumnHeader> columnHeaders, final Instant referenceDate) {
    HashMap<Integer, Integer> daysToWorkingDaysMap = new HashMap<>();
    daysToWorkingDaysMap.put(0, 0);

    int positiveWorkdayLimit = TimeIntervalColumnHeader.getLargestLowerLimit(columnHeaders);
    calculateFutureDaysToWorkingDays(daysToWorkingDaysMap, referenceDate, positiveWorkdayLimit);

    int negativeWorkdayLimit = TimeIntervalColumnHeader.getSmallestUpperLimit(columnHeaders);
    calculateNegativeDaysToWorkingDays(daysToWorkingDaysMap, referenceDate, negativeWorkdayLimit);

    return daysToWorkingDaysMap;
  }

  private void calculateFutureDaysToWorkingDays(
      HashMap<Integer, Integer> daysToWorkingDaysMap, Instant referenceDate, int workdayLimit) {
    calculateDaysToWorkingDays(daysToWorkingDaysMap, referenceDate, workdayLimit, 1);
  }

  private void calculateNegativeDaysToWorkingDays(
      HashMap<Integer, Integer> daysToWorkingDaysMap, Instant referenceDate, int workdayLimit) {
    calculateDaysToWorkingDays(daysToWorkingDaysMap, referenceDate, workdayLimit, -1);
  }

  private void calculateDaysToWorkingDays(
      HashMap<Integer, Integer> daysToWorkingDaysMap,
      Instant referenceDate,
      int workdayLimit,
      int direction) {
    int amountOfDays = 0;
    int amountOfWorkdays = 0;
    while (Math.abs(amountOfWorkdays) < Math.abs(workdayLimit)) {
      amountOfDays += direction;
      if (workingTimeCalculator.isWorkingDay(referenceDate.plus(amountOfDays, ChronoUnit.DAYS))) {
        amountOfWorkdays += direction;
      }
      daysToWorkingDaysMap.put(amountOfDays, amountOfWorkdays);
    }
  }

  @Override
  public String toString() {
    return "DaysToWorkingDaysReportConverter [cacheDaysToWorkingDays="
        + cacheDaysToWorkingDays
        + ", daysToWorkingDaysConverter="
        + workingTimeCalculator
        + "]";
  }
}
