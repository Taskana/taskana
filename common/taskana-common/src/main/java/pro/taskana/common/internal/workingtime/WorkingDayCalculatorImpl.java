package pro.taskana.common.internal.workingtime;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.stream.LongStream;
import pro.taskana.common.api.WorkingTimeCalculator;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.SystemException;

public class WorkingDayCalculatorImpl implements WorkingTimeCalculator {

  private final ZoneId zoneId;
  private final HolidaySchedule holidaySchedule;

  public WorkingDayCalculatorImpl(HolidaySchedule holidaySchedule, ZoneId zoneId) {
    this.holidaySchedule = holidaySchedule;
    this.zoneId = zoneId;
  }

  @Override
  public Instant subtractWorkingTime(Instant workStart, Duration workingTime)
      throws InvalidArgumentException {
    long days = convertWorkingDaysToDays(workStart, -workingTime.toDays(), ZeroDirection.SUB_DAYS);
    return workStart.plus(Duration.ofDays(days));
  }

  @Override
  public Instant addWorkingTime(Instant workStart, Duration workingTime)
      throws InvalidArgumentException {
    long days = convertWorkingDaysToDays(workStart, workingTime.toDays(), ZeroDirection.ADD_DAYS);
    return workStart.plus(Duration.ofDays(days));
  }

  @Override
  public Duration workingTimeBetween(Instant first, Instant second)
      throws InvalidArgumentException {
    long days = Duration.between(first, second).abs().toDays();
    Instant firstInstant = first.isBefore(second) ? first : second;

    long workingDaysBetween =
        LongStream.range(1, days)
            .mapToObj(day -> isWorkingDay(firstInstant.plus(day, ChronoUnit.DAYS)))
            .filter(t -> t)
            .count();
    return Duration.ofDays(workingDaysBetween);
  }

  @Override
  public boolean isWorkingDay(Instant instant) {
    return !isWeekend(instant) && !isHoliday(instant);
  }

  @Override
  public boolean isWeekend(Instant instant) {
    DayOfWeek dayOfWeek = toDayOfWeek(instant);
    return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
  }

  @Override
  public boolean isHoliday(Instant instant) {
    return holidaySchedule.isHoliday(toLocalDate(instant));
  }

  @Override
  public boolean isGermanHoliday(Instant instant) {
    return holidaySchedule.isGermanHoliday(toLocalDate(instant));
  }

  private long convertWorkingDaysToDays(
      final Instant startTime, long numberOfDays, ZeroDirection zeroDirection) {
    if (startTime == null) {
      throw new SystemException(
          "Internal Error: convertWorkingDaysToDays was called with a null startTime");
    }
    int direction = calculateDirection(numberOfDays, zeroDirection);
    long limit = Math.abs(numberOfDays);
    return LongStream.iterate(0, i -> i + direction)
        .filter(day -> isWorkingDay(startTime.plus(day, ChronoUnit.DAYS)))
        .skip(limit)
        .findFirst()
        .orElse(0);
  }

  private int calculateDirection(long numberOfDays, ZeroDirection zeroDirection) {
    if (numberOfDays == 0) {
      return zeroDirection.getDirection();
    } else {
      return numberOfDays >= 0 ? 1 : -1;
    }
  }

  private LocalDate toLocalDate(Instant instant) {
    return LocalDate.ofInstant(instant, zoneId);
  }

  private DayOfWeek toDayOfWeek(Instant instant) {
    return toLocalDate(instant).getDayOfWeek();
  }

  private enum ZeroDirection {
    SUB_DAYS(-1),
    ADD_DAYS(1);

    private final int direction;

    ZeroDirection(int direction) {
      this.direction = direction;
    }

    public int getDirection() {
      return direction;
    }
  }
}
