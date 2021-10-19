package pro.taskana.common.api;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import pro.taskana.common.api.exceptions.InvalidArgumentException;

public class WorkingTimeCalculator {

  private static final Map<DayOfWeek, LocalTimeInterval> WORKING_TIME;

  static {
    WORKING_TIME = new HashMap<>();
    WORKING_TIME.put(
        DayOfWeek.MONDAY, new LocalTimeInterval(LocalTime.of(9, 0), LocalTime.of(17, 0)));
    WORKING_TIME.put(
        DayOfWeek.TUESDAY, new LocalTimeInterval(LocalTime.of(9, 0), LocalTime.of(17, 0)));
    WORKING_TIME.put(
        DayOfWeek.WEDNESDAY, new LocalTimeInterval(LocalTime.of(9, 0), LocalTime.of(17, 0)));
    WORKING_TIME.put(
        DayOfWeek.THURSDAY, new LocalTimeInterval(LocalTime.of(9, 0), LocalTime.of(17, 0)));
    WORKING_TIME.put(
        DayOfWeek.FRIDAY, new LocalTimeInterval(LocalTime.of(9, 0), LocalTime.of(17, 0)));
    WORKING_TIME.put(
        DayOfWeek.SATURDAY, new LocalTimeInterval(LocalTime.of(10, 0), LocalTime.of(15, 0)));
    WORKING_TIME.put(DayOfWeek.SUNDAY, null);
  }

  private final ZoneId zone;
  private final WorkingDaysToDaysConverter converter;

  public WorkingTimeCalculator(WorkingDaysToDaysConverter converter) {
    this.converter = converter;
    zone = ZoneId.of("UTC");
  }

  public Duration workingTimeBetweenTwoTimestamps(Instant from, Instant to)
      throws InvalidArgumentException {
    checkValidInput(from, to);
    Instant currentTime = from;
    LocalDate currentDate = LocalDateTime.ofInstant(from, zone).toLocalDate();
    LocalDate untilDate = LocalDateTime.ofInstant(to, zone).toLocalDate();
    DayOfWeek weekDay = currentDate.getDayOfWeek();

    if (currentDate.isEqual(untilDate)) {
      return calculateDurationWithinOneDay(from, to, weekDay, currentDate);
    }

    Duration duration = Duration.ZERO;
    duration = duration.plus(calculateDurationOfStartDay(currentTime, weekDay, currentDate));
    currentTime = currentTime.plus(1, ChronoUnit.DAYS);
    currentDate = currentDate.plusDays(1);
    weekDay = weekDay.plus(1);

    while (!currentDate.isEqual(untilDate)) {
      duration = duration.plus(calculateDurationOfOneWorkDay(weekDay, currentDate));
      weekDay = weekDay.plus(1);
      currentDate = currentDate.plusDays(1);
      currentTime = currentTime.plus(1, ChronoUnit.DAYS);
    }

    return duration.plus(calculateDurationOnEndDay(to, weekDay, currentDate));
  }

  private Duration calculateDurationWithinOneDay(
      Instant from, Instant to, DayOfWeek weekday, LocalDate currentDate) {
    LocalTimeInterval workHours = WORKING_TIME.get(weekday);
    if (WORKING_TIME.get(weekday) != null && !converter.isHoliday(currentDate)) {
      LocalTime start = workHours.getBegin();
      LocalTime end = workHours.getEnd();
      LocalTime fromTime = from.atZone(zone).toLocalTime();
      LocalTime toTime = to.atZone(zone).toLocalTime();

      if (!fromTime.isBefore(start) && toTime.isBefore(end)) {
        return Duration.between(from, to);
      } else if (fromTime.isBefore(start)) {
        if (toTime.isAfter(end)) {
          return addWorkingHoursOfOneDay(weekday);
        } else if (!toTime.isBefore(start)) {
          return Duration.between(start, toTime);
        }
      } else if (fromTime.isBefore(end)) {
        return Duration.between(fromTime, end);
      }
    }
    return Duration.ZERO;
  }

  private Duration calculateDurationOfOneWorkDay(DayOfWeek weekday, LocalDate date) {
    if (WORKING_TIME.get(weekday) != null && !converter.isHoliday(date)) {
      return addWorkingHoursOfOneDay(weekday);
    }
    return Duration.ZERO;
  }

  private Duration calculateDurationOfStartDay(
      Instant startDay, DayOfWeek weekday, LocalDate date) {
    LocalTimeInterval workHours = WORKING_TIME.get(weekday);
    if (WORKING_TIME.get(weekday) != null && !converter.isHoliday(date)) {
      LocalTime fromTime = startDay.atZone(zone).toLocalTime();
      LocalTime end = workHours.getEnd();
      if (fromTime.isBefore(workHours.getBegin())) {
        return addWorkingHoursOfOneDay(weekday);
      } else if (fromTime.isBefore(end)) {
        return Duration.between(fromTime, end);
      }
    }
    return Duration.ZERO;
  }

  private Duration calculateDurationOnEndDay(Instant endDate, DayOfWeek weekday, LocalDate date) {
    LocalTimeInterval workHours = WORKING_TIME.get(weekday);
    if (WORKING_TIME.get(weekday) != null && !converter.isHoliday(date)) {
      LocalTime start = workHours.getBegin();
      LocalTime toTime = endDate.atZone(zone).toLocalTime();
      if (toTime.isAfter(workHours.getEnd())) {
        return addWorkingHoursOfOneDay(weekday);
      } else if (!toTime.isBefore(start)) {
        return Duration.between(start, toTime);
      }
    }
    return Duration.ZERO;
  }

  private void checkValidInput(Instant from, Instant to) throws InvalidArgumentException {
    if (from == null || to == null || from.compareTo(to) > 0) {
      throw new InvalidArgumentException("Instants are invalid.");
    }

    for (LocalTimeInterval interval : WORKING_TIME.values()) {
      if (interval != null && !interval.isValid()) {
        throw new InvalidArgumentException(
            "The work period doesn't have two LocalTimes for start and end.");
      }
    }
  }

  private Duration addWorkingHoursOfOneDay(DayOfWeek weekday) {
    LocalTimeInterval workHours = WORKING_TIME.get(weekday);
    if (workHours.isValid()) {
      return Duration.between(workHours.getBegin(), workHours.getEnd());
    } else {
      return Duration.ZERO;
    }
  }
}
