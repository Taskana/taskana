package pro.taskana.common.internal.util;

import static java.time.temporal.ChronoUnit.DAYS;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.SystemException;

/**
 * The WorkingDaysToDaysConverter provides a method to convert an age in working days into an age in
 * days.
 */
public final class WorkingDaysToDaysConverter {

  private static boolean germanHolidaysEnabled;
  private static Set<CustomHoliday> customHolidays = new HashSet<>();
  private Instant referenceDate;
  private LocalDate easterSunday;

  private WorkingDaysToDaysConverter(Instant referenceDate) {
    easterSunday =
        getEasterSunday(LocalDateTime.ofInstant(referenceDate, ZoneId.systemDefault()).getYear());
    this.referenceDate = referenceDate;
  }

  public Instant getReferenceDate() {
    return referenceDate;
  }

  /**
   * Initializes the WorkingDaysToDaysConverter for the current day.
   *
   * @return an instance of the WorkingDaysToDaysConverter
   * @throws SystemException is thrown when the {@link WorkingDaysToDaysConverter} cannot be
   *     initialized with the current Instant. Should never occur.
   */
  public static WorkingDaysToDaysConverter initialize() {
    try {
      return initialize(Instant.now());
    } catch (InvalidArgumentException ex) {
      throw new SystemException(
          "Internal error. Cannot initialize WorkingDaysToDaysConverter. This should not happen",
          ex);
    }
  }

  /**
   * Initializes the WorkingDaysToDaysConverter for a referenceDate.
   *
   * @param referenceDate a {@link Instant} that represents the current day of the table
   * @return an instance of the WorkingDaysToDaysConverter
   * @throws InvalidArgumentException thrown if columnHeaders or referenceDate is null
   */
  public static WorkingDaysToDaysConverter initialize(Instant referenceDate)
      throws InvalidArgumentException {

    if (referenceDate == null) {
      throw new InvalidArgumentException("ReferenceDate cannot be used as NULL-Parameter");
    }

    return new WorkingDaysToDaysConverter(referenceDate);
  }

  public static void setGermanPublicHolidaysEnabled(boolean germanPublicHolidaysEnabled) {
    germanHolidaysEnabled = germanPublicHolidaysEnabled;
  }

  public static void setCustomHolidays(List<CustomHoliday> holidays) {
    customHolidays = new HashSet<>(holidays == null ? Collections.emptyList() : holidays);
  }

  public long convertWorkingDaysToDays(Instant startTime, long numberOfDays) {
    if (startTime == null) {
      throw new SystemException(
          "Internal Error: convertWorkingDasToDays was called with a null startTime");
    } else if (!startTime.equals(referenceDate)) {
      refreshReferenceDate(referenceDate);
    }
    int direction = numberOfDays >= 0 ? 1 : -1;
    long limit = Math.abs(numberOfDays);
    final Instant finalStartTime = startTime;
    return LongStream.iterate(0, i -> i + direction)
        .filter(day -> isWorkingDay(day, finalStartTime))
        .skip(limit)
        .findFirst()
        .orElse(0);
  }

  public Instant addWorkingDaysToInstant(Instant instant, Duration workingDays) {
    long days = convertWorkingDaysToDays(instant, workingDays.toDays());
    return instant.plus(Duration.ofDays(days));
  }

  public Instant subtractWorkingDaysFromInstant(Instant instant, Duration workingDays) {
    long days = convertWorkingDaysToDays(instant, -workingDays.toDays());
    return instant.plus(Duration.ofDays(days));
  }

  public boolean isWorkingDay(long day, Instant referenceDate) {
    LocalDateTime dateToCheck =
        LocalDateTime.ofInstant(referenceDate, ZoneId.systemDefault()).plusDays(day);

    return !isWeekend(dateToCheck) && !isHoliday(dateToCheck.toLocalDate());
  }

  public boolean isWeekend(LocalDateTime dateToCheck) {
    return dateToCheck.getDayOfWeek().equals(DayOfWeek.SATURDAY)
        || dateToCheck.getDayOfWeek().equals(DayOfWeek.SUNDAY);
  }

  public boolean isHoliday(LocalDate date) {
    if (germanHolidaysEnabled && isGermanHoliday(date)) {
      return true;
    }
    // Custom holidays that can be configured in the TaskanaEngineConfiguration
    return customHolidays.contains(CustomHoliday.of(date.getDayOfMonth(), date.getMonthValue()));
  }

  public boolean isGermanHoliday(LocalDate date) {
    // Fix and movable holidays that are valid throughout Germany: New years day, Labour Day, Day of
    // German
    // Unity, Christmas,
    if (Stream.of(GermanFixHolidays.values()).anyMatch(day -> day.matches(date))) {
      return true;
    }

    // Easter holidays Good Friday, Easter Monday, Ascension Day, Whit Monday.
    long diffFromEasterSunday = DAYS.between(easterSunday, date);
    long goodFriday = -2;
    long easterMonday = 1;
    long ascensionDay = 39;
    long whitMonday = 50;

    return LongStream.of(goodFriday, easterMonday, ascensionDay, whitMonday)
        .anyMatch(diff -> diff == diffFromEasterSunday);
  }

  /**
   * Computes the date of Easter Sunday for a given year.
   *
   * @param year for which the date of Easter Sunday should be calculated
   * @return the date of Easter Sunday for the given year
   */
  static LocalDate getEasterSunday(int year) {
    // Formula to compute Easter Sunday by Gauss.
    int a = year % 19;
    int b = year % 4;
    int c = year % 7;
    int k = year / 100;
    int p = (13 + 8 * k) / 25;
    int q = k / 4;
    int m = (15 - p + k - q) % 30;
    int n = (4 + k - q) % 7;
    int d = (19 * a + m) % 30;

    int e = (2 * b + 4 * c + 6 * d + n) % 7;

    if (d == 29 && e == 6) {
      return LocalDate.of(year, 3, 15).plusDays((long) d + e);
    }
    if (d == 28 && e == 6 && (11 * m + 11) % 30 < 19) {
      return LocalDate.of(year, 3, 15).plusDays((long) d + e);
    }
    return LocalDate.of(year, 3, 22).plusDays((long) d + e);
  }

  private void refreshReferenceDate(Instant newReferenceDate) {
    int yearOfReferenceDate =
        LocalDateTime.ofInstant(referenceDate, ZoneId.systemDefault()).getYear();
    int yearOfNewReferenceDate =
        LocalDateTime.ofInstant(newReferenceDate, ZoneId.systemDefault()).getYear();
    if (yearOfReferenceDate != yearOfNewReferenceDate) {
      easterSunday = getEasterSunday(yearOfNewReferenceDate);
    }
    this.referenceDate = newReferenceDate;
  }

  @Override
  public String toString() {
    return "WorkingDaysToDaysConverter{"
        + "dateCreated="
        + referenceDate
        + ", easterSunday="
        + easterSunday
        + '}';
  }

  /** Enumeration of German holidays. */
  private enum GermanFixHolidays {
    NEWYEAR(1, 1),
    LABOURDAY(5, 1),
    GERMANUNITY(10, 3),
    CHRISTMAS1(12, 25),
    CHRISTMAS2(12, 26);

    private int month;
    private int day;

    GermanFixHolidays(int month, int day) {
      this.month = month;
      this.day = day;
    }

    public boolean matches(LocalDate date) {
      return date.getDayOfMonth() == day && date.getMonthValue() == month;
    }
  }
}
