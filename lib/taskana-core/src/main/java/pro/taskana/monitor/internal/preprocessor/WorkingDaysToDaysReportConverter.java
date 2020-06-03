package pro.taskana.monitor.internal.preprocessor;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.internal.util.WorkingDaysToDaysConverter;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;

/**
 * The DaysToWorkingDaysReportConverter provides a method to convert an age in days into an age in
 * working days. Before the method convertDaysToWorkingDays() can be used, the
 * WorkingDaysToDaysConverter has to be initialized. For a list of {@link TimeIntervalColumnHeader}s
 * the converter creates a "table" with integer that represents the age in days from the largest
 * lower limit until the smallest upper limit of the timeIntervalColumnHeaders. This table is valid
 * for a whole day until the converter is initialized with bigger limits.
 */
public class WorkingDaysToDaysReportConverter {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(WorkingDaysToDaysReportConverter.class);
  private final WorkingDaysToDaysConverter daysToWorkingDaysConverter;
  private final Map<Integer, Integer> cacheDaysToWorkingDays;

  WorkingDaysToDaysReportConverter(
      List<? extends TimeIntervalColumnHeader> columnHeaders,
      WorkingDaysToDaysConverter daysToWorkingDaysConverter) {
    this.daysToWorkingDaysConverter = daysToWorkingDaysConverter;
    cacheDaysToWorkingDays =
        generateDaysToWorkingDays(columnHeaders, daysToWorkingDaysConverter.getReferenceDate());
  }

  public static WorkingDaysToDaysReportConverter initialize(
      List<? extends TimeIntervalColumnHeader> columnHeaders) throws InvalidArgumentException {
    return initialize(columnHeaders, Instant.now());
  }

  /**
   * Initializes the WorkingDaysToDaysConverter for a list of {@link TimeIntervalColumnHeader}s and
   * a referenceDate. A new table is only created if there are bigger limits or the date has
   * changed.
   *
   * @param columnHeaders a list of {@link TimeIntervalColumnHeader}s that determines the size of
   *     the table
   * @param referenceDate a {@link Instant} that represents the current day of the table
   * @return an instance of the WorkingDaysToDaysConverter
   * @throws InvalidArgumentException thrown if columnHeaders or referenceDate is null
   */
  public static WorkingDaysToDaysReportConverter initialize(
      List<? extends TimeIntervalColumnHeader> columnHeaders, Instant referenceDate)
      throws InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Initialize WorkingDaysToDaysConverter with columnHeaders: {}",
          columnHeaders);
    }
    if (columnHeaders == null) {
      throw new InvalidArgumentException(
          "TimeIntervalColumnHeaders can´t be used as NULL-Parameter");
    }
    if (referenceDate == null) {
      throw new InvalidArgumentException("ReferenceDate can´t be used as NULL-Parameter");
    }
    WorkingDaysToDaysConverter workingDaysToDaysConverter =
        WorkingDaysToDaysConverter.initialize(referenceDate);

    return new WorkingDaysToDaysReportConverter(columnHeaders, workingDaysToDaysConverter);
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
      amountOfWorkdays +=
          (daysToWorkingDaysConverter.isWorkingDay(amountOfDays, referenceDate)) ? direction : 0;
      daysToWorkingDaysMap.put(amountOfDays, amountOfWorkdays);
    }
  }

  @Override
  public String toString() {
    return "DaysToWorkingDaysReportConverter [cacheDaysToWorkingDays="
        + cacheDaysToWorkingDays
        + ", daysToWorkingDaysConverter="
        + daysToWorkingDaysConverter
        + "]";
  }
}
