package pro.taskana.common.internal.workingtime;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;

import pro.taskana.common.api.LocalTimeInterval;
import pro.taskana.common.api.WorkingTimeCalculator;
import pro.taskana.common.api.exceptions.InvalidArgumentException;

public class WorkingTimeCalculatorImpl implements WorkingTimeCalculator {

  static final ZoneOffset UTC = ZoneOffset.UTC;

  private final HolidaySchedule holidaySchedule;
  private final WorkingTimeSchedule workingTimeSchedule;

  public WorkingTimeCalculatorImpl(
      HolidaySchedule holidaySchedule, Map<DayOfWeek, Set<LocalTimeInterval>> workingTimeSchedule) {
    this.holidaySchedule = holidaySchedule;
    this.workingTimeSchedule = new WorkingTimeSchedule(workingTimeSchedule);
  }

  @Override
  public Instant subtractWorkingTime(Instant workStart, Duration workingTime)
      throws InvalidArgumentException {
    validatePositiveDuration(workingTime);
    WorkSlot workSlot = getWorkSlotOrPrevious(toLocalDateTime(workStart));
    return workSlot.subtractWorkingTime(workStart, workingTime);
  }

  @Override
  public Instant addWorkingTime(Instant workStart, Duration workingTime)
      throws InvalidArgumentException {
    validatePositiveDuration(workingTime);
    WorkSlot bestMatchingWorkSlot = getWorkSlotOrNext(toLocalDateTime(workStart));
    return bestMatchingWorkSlot.addWorkingTime(workStart, workingTime);
  }

  @Override
  public Duration workingTimeBetween(Instant first, Instant second)
      throws InvalidArgumentException {
    validateNonNullInstants(first, second);

    Instant from;
    Instant to;
    if (first.isAfter(second)) {
      from = second;
      to = first;
    } else {
      from = first;
      to = second;
    }

    WorkSlot bestMatchingWorkSlot = getWorkSlotOrNext(toLocalDateTime(from));
    Instant earliestWorkStart = max(from, bestMatchingWorkSlot.start);
    Instant endOfWorkSlot = bestMatchingWorkSlot.end;

    if (endOfWorkSlot.compareTo(to) >= 0) {
      if (bestMatchingWorkSlot.start.compareTo(to) <= 0) {
        // easy part. _from_ and _to_ are in the same work slot
        return Duration.between(earliestWorkStart, to);
      } else {
        // _from_ and _to_ are before the bestMatchingWorkSlot aka between two work slots. We simply
        // drop it
        return Duration.ZERO;
      }
    } else {
      // Take the current duration and add the working time starting after this work slot.
      return Duration.between(earliestWorkStart, endOfWorkSlot)
          .plus(workingTimeBetween(endOfWorkSlot, to));
    }
  }

  @Override
  public boolean isWorkingDay(Instant instant) {
    return workingTimeSchedule.isWorkingDay(toDayOfWeek(instant)) && !isHoliday(instant);
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

  private void validateNonNullInstants(Instant first, Instant second) {
    if (first == null || second == null) {
      throw new InvalidArgumentException("Neither first nor second may be null.");
    }
  }

  private void validatePositiveDuration(Duration workingTime) {
    if (workingTime.isNegative()) {
      throw new InvalidArgumentException("Duration must be zero or positive.");
    }
  }

  /**
   * Returns the WorkSlot that matches best <code>currentDateTime</code>. If currentDateTime is
   * within a WorkSlot that WorkSlot is returned, if currentDateTime is not within a WorkSlot the
   * next WorkSlot is returned.
   *
   * @param currentDateTime The LocalDateTime we want the best matching WorkSlot for. May not be
   *     <code>null</code>.
   * @return The WorkSlot that matches best <code>currentDateTime</code> if we want to add.
   */
  private WorkSlot getWorkSlotOrNext(LocalDateTime currentDateTime) {
    LocalDate currentDate = currentDateTime.toLocalDate();
    // We do not work on Holidays
    if (holidaySchedule.isHoliday(currentDate)) {
      return getWorkSlotOrNext(getDayAfter(currentDateTime));
    }
    SortedSet<LocalTimeInterval> workSlotsOfWorkingDay =
        workingTimeSchedule.workSlotsFor(currentDate.getDayOfWeek());
    // We are looking for the first workingSlot whose end is after the current time
    Optional<LocalTimeInterval> workSlotEndingAfterCurrentTime =
        workSlotsOfWorkingDay.stream()
            .filter(it -> it.getEnd().isAfter(currentDateTime.toLocalTime()))
            .findFirst();
    return workSlotEndingAfterCurrentTime
        .map(it -> new WorkSlot(currentDate, it))
        .orElseGet(
            () ->
                // we started after the last working slot on that day, the next start time is the
                // first working slot of the next working day.
                getWorkSlotOrNext(getDayAfter(currentDateTime)));
  }

  /**
   * Returns the WorkSlot that matches best <code>currentDateTime</code>. If currentDateTime is
   * within a WorkSlot that WorkSlot is returned, if currentDateTime is not within a WorkSlot the
   * previous WorkSlot is returned.
   *
   * @param currentDateTime The LocalDateTime we want the best matching WorkSlot for. May not be
   *     <code>null</code>.
   * @return The WorkSlot that matches best <code>currentDateTime</code> if we want to subtract.
   */
  private WorkSlot getWorkSlotOrPrevious(LocalDateTime currentDateTime) {
    LocalDate currentDate = currentDateTime.toLocalDate();
    // We do not work on Holidays
    if (holidaySchedule.isHoliday(currentDate)) {
      return getWorkSlotOrPrevious(getDayBefore(currentDateTime));
    }

    SortedSet<LocalTimeInterval> workSlotsOfWorkingDay =
        workingTimeSchedule.workSlotsForReversed(currentDate.getDayOfWeek());
    // We are looking for the last workingSlot whose begin is before or equals the current time
    Optional<LocalTimeInterval> workSlotStartingBeforeCurrentTime =
        workSlotsOfWorkingDay.stream()
            // we use beforeOrEquals because begin is inclusive
            .filter(it -> isBeforeOrEquals(it.getBegin(), currentDateTime))
            .findFirst();
    return workSlotStartingBeforeCurrentTime
        .map(it -> new WorkSlot(currentDate, it))
        .orElseGet(
            () ->
                // we started before the first working slot on that day, the next start time is the
                // last working slot of the previous working day.
                getWorkSlotOrPrevious(getDayBefore(currentDateTime)));
  }

  private static boolean isBeforeOrEquals(LocalTime time, LocalDateTime currentDateTime) {
    return !time.isAfter(currentDateTime.toLocalTime());
  }

  private LocalDateTime getDayAfter(LocalDateTime current) {
    return LocalDateTime.of(current.toLocalDate().plusDays(1), LocalTime.MIN);
  }

  private LocalDateTime getDayBefore(LocalDateTime current) {
    return LocalDateTime.of(current.toLocalDate().minusDays(1), LocalTime.MAX);
  }

  private LocalDateTime toLocalDateTime(Instant instant) {
    return LocalDateTime.ofInstant(instant, UTC);
  }

  private LocalDate toLocalDate(Instant instant) {
    return LocalDate.ofInstant(instant, UTC);
  }

  private DayOfWeek toDayOfWeek(Instant instant) {
    return toLocalDate(instant).getDayOfWeek();
  }

  private static Instant max(Instant a, Instant b) {
    if (a.isAfter(b)) {
      return a;
    } else {
      return b;
    }
  }

  private static Instant min(Instant a, Instant b) {
    if (a.isBefore(b)) {
      return a;
    } else {
      return b;
    }
  }

  class WorkSlot {

    private final Instant start;
    private final Instant end;

    public WorkSlot(LocalDate day, LocalTimeInterval interval) {
      this.start = LocalDateTime.of(day, interval.getBegin()).toInstant(UTC);
      if (interval.getEnd().equals(LocalTime.MAX)) {
        this.end = day.plusDays(1).atStartOfDay().toInstant(UTC);
      } else {
        this.end = LocalDateTime.of(day, interval.getEnd()).toInstant(UTC);
      }
    }

    public Instant addWorkingTime(Instant workStart, Duration workingTime) {
      // _workStart_ might be outside the working hours. We need to adjust the start accordingly.
      Instant earliestWorkStart = max(workStart, start);
      Duration untilEndOfWorkSlot = Duration.between(earliestWorkStart, end);
      if (workingTime.compareTo(untilEndOfWorkSlot) <= 0) {
        // easy part. It is due within the same work slot
        return earliestWorkStart.plus(workingTime);
      } else {
        // we subtract the duration of the currentWorkingSlot
        Duration remainingWorkingTime = workingTime.minus(untilEndOfWorkSlot);
        // We continue to calculate the dueDate by starting from an workStart outside the current
        // working slot and the remainingWorkingTime
        return next().addWorkingTime(end, remainingWorkingTime);
      }
    }

    public Instant subtractWorkingTime(Instant workStart, Duration workingTime) {
      // _workStart_ might be outside the working hours. We need to adjust the end accordingly.
      Instant latestWorkEnd = min(workStart, end);
      Duration untilStartOfWorkSlot = Duration.between(start, latestWorkEnd);
      if (workingTime.compareTo(untilStartOfWorkSlot) <= 0) {
        // easy part. It is due within the same work slot
        return latestWorkEnd.minus(workingTime);
      } else {
        Duration remainingWorkingTime = workingTime.minus(untilStartOfWorkSlot);
        return previous().subtractWorkingTime(start, remainingWorkingTime);
      }
    }

    private WorkSlot previous() {
      // We need to subtract a nanosecond because start is inclusive
      return getWorkSlotOrPrevious(toLocalDateTime(start.minusNanos(1)));
    }

    private WorkSlot next() {
      return getWorkSlotOrNext(toLocalDateTime(end));
    }
  }
}
