package pro.taskana.monitor.internal.preprocessor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.LoggerUtils;
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
public class DaysToWorkingDaysReportConverter {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DaysToWorkingDaysReportConverter.class);
  private List<Integer> positiveDaysToWorkingDays;
  private List<Integer> negativeDaysToWorkingDays;
  private WorkingDaysToDaysConverter workingDaysToDaysConverter;

  DaysToWorkingDaysReportConverter(
      List<? extends TimeIntervalColumnHeader> columnHeaders,
      WorkingDaysToDaysConverter workingDaysToDaysConverter) {

    this.workingDaysToDaysConverter = workingDaysToDaysConverter;
    positiveDaysToWorkingDays =
        generatePositiveDaysToWorkingDays(
            columnHeaders, workingDaysToDaysConverter.getReferenceDate());
    negativeDaysToWorkingDays =
        generateNegativeDaysToWorkingDays(
            columnHeaders, workingDaysToDaysConverter.getReferenceDate());
  }

  public static DaysToWorkingDaysReportConverter initialize(
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
  public static DaysToWorkingDaysReportConverter initialize(
      List<? extends TimeIntervalColumnHeader> columnHeaders, Instant referenceDate)
      throws InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Initialize WorkingDaysToDaysConverter with columnHeaders: {}",
          LoggerUtils.listToString(columnHeaders));
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

    return new DaysToWorkingDaysReportConverter(columnHeaders, workingDaysToDaysConverter);
  }

  /**
   * Converts an integer, that represents the age in days, to the age in working days by using the
   * table that was created by initialization. If the age in days is beyond the limits of the table,
   * the integer will be returned unchanged.
   *
   * @param ageInDays represents the age in days
   * @return the age in working days
   */
  public int convertDaysToWorkingDays(int ageInDays) {

    int minDay = -(negativeDaysToWorkingDays.size() - 1);
    int maxDay = positiveDaysToWorkingDays.size() - 1;

    if (ageInDays >= minDay && ageInDays <= 0) {
      return negativeDaysToWorkingDays.get(-ageInDays);
    }
    if (ageInDays > 0 && ageInDays <= maxDay) {
      return positiveDaysToWorkingDays.get(ageInDays);
    }

    return ageInDays;
  }

  /**
   * Converts an integer, that represents the age in working days, to the age in days by using the
   * table that was created by initialization. Because one age in working days could match to more
   * than one age in days, the return value is a list of all days that match to the input parameter.
   * If the age in working days is beyond the limits of the table, the integer will be returned
   * unchanged.
   *
   * @param ageInWorkingDays represents the age in working days
   * @return a list of age in days
   */
  public ArrayList<Integer> convertWorkingDaysToDays(int ageInWorkingDays) {

    ArrayList<Integer> list = new ArrayList<>();

    int minWorkingDay = negativeDaysToWorkingDays.get(negativeDaysToWorkingDays.size() - 1);
    int maxWorkingDay = positiveDaysToWorkingDays.get(positiveDaysToWorkingDays.size() - 1);

    if (ageInWorkingDays >= minWorkingDay && ageInWorkingDays < 0) {
      for (int ageInDays = 0; ageInDays < negativeDaysToWorkingDays.size(); ageInDays++) {
        if (negativeDaysToWorkingDays.get(ageInDays) == ageInWorkingDays) {
          list.add(-ageInDays);
        }
      }
      return list;
    }
    if (ageInWorkingDays > 0 && ageInWorkingDays <= maxWorkingDay) {
      for (int ageInDays = 0; ageInDays < positiveDaysToWorkingDays.size(); ageInDays++) {
        if (positiveDaysToWorkingDays.get(ageInDays) == ageInWorkingDays) {
          list.add(ageInDays);
        }
      }
      return list;
    }

    if (ageInWorkingDays == 0) {
      list.add(0);
      for (int ageInDays = 1; ageInDays < positiveDaysToWorkingDays.size(); ageInDays++) {
        if (positiveDaysToWorkingDays.get(ageInDays) == ageInWorkingDays) {
          list.add(ageInDays);
        }
      }
      for (int ageInDays = 1; ageInDays < negativeDaysToWorkingDays.size(); ageInDays++) {
        if (negativeDaysToWorkingDays.get(ageInDays) == ageInWorkingDays) {
          list.add(-ageInDays);
        }
      }
      return list;
    }

    // If ageInWorkingDays is beyond the limits of the table, the value is returned unchanged.
    list.add(ageInWorkingDays);
    return list;
  }

  protected List<Integer> generateNegativeDaysToWorkingDays(
      List<? extends TimeIntervalColumnHeader> columnHeaders, Instant referenceDate) {
    int minUpperLimit = TimeIntervalColumnHeader.getSmallestUpperLimit(columnHeaders);

    List<Integer> daysToWorkingDays = new ArrayList<>();
    daysToWorkingDays.add(0);
    int day = -1;
    int workingDay = 0;
    while (workingDay > minUpperLimit) {
      workingDay -= (workingDaysToDaysConverter.isWorkingDay(day--, referenceDate)) ? 1 : 0;
      daysToWorkingDays.add(workingDay);
    }
    return daysToWorkingDays;
  }

  protected List<Integer> generatePositiveDaysToWorkingDays(
      List<? extends TimeIntervalColumnHeader> columnHeaders, Instant referenceDate) {
    int maxLowerLimit = TimeIntervalColumnHeader.getLargestLowerLimit(columnHeaders);
    ArrayList<Integer> daysToWorkingDays = new ArrayList<>();
    daysToWorkingDays.add(0);

    int day = 1;
    int workingDay = 0;
    while (workingDay < maxLowerLimit) {
      workingDay += (workingDaysToDaysConverter.isWorkingDay(day++, referenceDate)) ? 1 : 0;
      daysToWorkingDays.add(workingDay);
    }
    return daysToWorkingDays;
  }

  @Override
  public String toString() {
    return "DaysToWorkingDaysReportConverter [positiveDaysToWorkingDays="
        + positiveDaysToWorkingDays
        + ", negativeDaysToWorkingDays="
        + negativeDaysToWorkingDays
        + ", workingDaysToDaysConverter="
        + workingDaysToDaysConverter
        + "]";
  }
}
