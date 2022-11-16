package pro.taskana.common.api;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;

import pro.taskana.common.api.exceptions.InvalidArgumentException;

public class WorkingTimeCalculator {

  static final ZoneOffset UTC = ZoneOffset.UTC;

  private final WorkingDaysToDaysConverter converter;
  private final WorkingTimeSchedule workingTimeSchedule;

  private final Map<DayOfWeek, LocalTimeInterval> legacyWorkingTimeSchedule;

  public WorkingTimeCalculator(
      WorkingDaysToDaysConverter converter,
      Map<DayOfWeek, Set<LocalTimeInterval>> workingTimeSchedule) {
    this.converter = converter;
    this.workingTimeSchedule = new WorkingTimeSchedule(workingTimeSchedule);
    this.legacyWorkingTimeSchedule = initLegacyDefaultWorkingTimeSchedule();
  }

  /**
   * Subtracts <code>workingTime</code> from <code>instant</code>. Respects the configured working
   * time schedule and Holidays.
   *
   * <p>E.g can be used for planned date calculation.
   *
   * @param workStart The Instant <code>workingTime</code> is subtracted from.
   * @param workingTime The Duration to subtract from <code>instant</code>. May have any resolution
   *     Duration supports, e.g. minutes or seconds.
   * @return A new Instant which represents the subtraction of working time (rounded to
   *     milliseconds).
   * @throws IllegalArgumentException If <code>workingTime</code> is negative.
   */
  public Instant subtractWorkingTime(Instant workStart, Duration workingTime)
      throws IllegalArgumentException {
    validatePositiveDuration(workingTime);
    WorkSlot workSlot = getWorkSlotOrPrevious(workStart.atZone(UTC).toLocalDateTime());
    return workSlot.subtractWorkingTime(workStart, workingTime);
  }

  /**
   * Adds <code>workingTime</code> from <code>workStart</code> Respects the configured working time
   * schedule and Holidays.
   *
   * <p>E.g can be used for due date calculation.
   *
   * @param workStart The Instant <code>workingTime</code> is added to.
   * @param workingTime The Duration to add to <code>workStart</code>. May have any resolution *
   *     Duration supports, e.g. minutes or seconds.
   * @return A new Instant which represents the addition of working time (rounded to milliseconds).
   * @throws IllegalArgumentException If <code>workingTime</code> is negative.
   */
  public Instant addWorkingTime(Instant workStart, Duration workingTime)
      throws IllegalArgumentException {
    validatePositiveDuration(workingTime);
    WorkSlot bestMatchingWorkSlot = getWorkSlotOrNext(workStart.atZone(UTC).toLocalDateTime());
    return bestMatchingWorkSlot.addWorkingTime(workStart, workingTime);
  }

  /**
   * Calculates the working time between <code>from</code> and <code>to</code> according to the
   * configured working time schedule. The returned Duration is precise to microseconds.
   *
   * @param from The Instant which denotes the beginn of the considered time frame. May not be
   *     <code>null</code>.
   * @param to The Instant which denotes the end of the considered time frame. May not be <code>null
   *             </code>.
   * @return The Duration representing the working time between <code>from</code> and <code>to
   *     </code>.
   * @throws IllegalArgumentException If either <code>from</code> or <code>to</code> is <code>null
   *                                  </code>. If <code>from</code> is after <code>to</code>.
   */
  public Duration workingTimeBetween(Instant from, Instant to) throws IllegalArgumentException {
    validateProperInterval(from, to);

    WorkSlot bestMatchingWorkSlot = getWorkSlotOrNext(from.atZone(UTC).toLocalDateTime());
    Instant earliestWorkStart = max(from, bestMatchingWorkSlot.getStart());
    Instant endOfWorkSlot = bestMatchingWorkSlot.getEnd();

    if (endOfWorkSlot.compareTo(to) >= 0) {
      if (bestMatchingWorkSlot.getStart().compareTo(to) <= 0) {
        // easy part. _from_ and _to_ are in the same work slot
        return Duration.between(earliestWorkStart, to);
      } else {
        // _to_ is before the bestMatchingWorkSlot aka between two work slots. We simply drop it
        return Duration.ZERO;
      }
    } else {
      // Take the current duration and add the working time starting after this work slot.
      return Duration.between(earliestWorkStart, endOfWorkSlot)
          .plus(workingTimeBetween(endOfWorkSlot, to));
    }
  }

  /**
   * Decides whether <code>instant</code> is a working day.
   *
   * @param instant The Instant to check. May not be <code>null</code>.
   * @return <code>true</code> if <code>instant</code> is a working day. <code>false</code>
   *     otherwise.
   */
  public boolean isWorkingDay(Instant instant) {
    return converter.isWorkingDay(instant);
  }

  /**
   * Decides whether there is any working time between <code>first</code> and <code>second</code>.
   *
   * <p><code>first</code> may be after <code>second</code>.
   *
   * @param first The first Instant to check. May not be <code>null</code>.
   * @param second The second Instant to check. May not be <code>null</code>.
   * @return <code>true</code> if there is working time between <code>first</code> and <code>second
   *     </code>. <code>false</code> otherwise.
   */
  public boolean isWorkingTimeBetween(Instant first, Instant second) {
    Duration workingTime;
    if (first.isAfter(second)) {
      workingTime = workingTimeBetween(second, first);
    } else {
      workingTime = workingTimeBetween(first, second);
    }

    return !Duration.ZERO.equals(workingTime);
  }

  /**
   * Calculates the working time between <code>from</code> and <code>to</code>.
   *
   * @param from The Instant which denotes the beginn of the considered time frame. May not be
   *     <code>null</code>.
   * @param to The Instant which denotes the end of the considered time frame. May not be <code>null
   *             </code>.
   * @return The Duration representing the working time between <code>from</code> and <code>to
   *     </code>.
   * @throws InvalidArgumentException If either <code>from</code> or <code>to</code> is <code>null
   *                                  *
   *                                  </code>. If <code>from</code> is after <code>to</code>.
   * @deprecated Use {@link #workingTimeBetween(Instant, Instant)} instead. The alternative supports
   *     all resolutions which Duration supports, not only days.
   */
  @Deprecated
  public Duration workingTimeBetweenTwoTimestamps(Instant from, Instant to)
      throws InvalidArgumentException {
    checkValidInput(from, to);
    Instant currentTime = from;
    LocalDate currentDate = from.atZone(UTC).toLocalDate();
    LocalDate untilDate = to.atZone(UTC).toLocalDate();
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

  private void validateProperInterval(Instant from, Instant to) {
    if (from == null || to == null || from.isAfter(to)) {
      throw new IllegalArgumentException("Instants are invalid.");
    }
  }

  private void validatePositiveDuration(Duration workingTime) {
    if (workingTime.isNegative()) {
      throw new IllegalArgumentException("Duration must be zero or positive.");
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
    if (converter.isHoliday(currentDate)) {
      return getWorkSlotOrNext(getDayAfter(currentDateTime));
    }
    SortedSet<LocalTimeInterval> workSlotsOfWorkingDay = getWorkSlotsOfWorkingDay(currentDate);
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
    if (converter.isHoliday(currentDate)) {
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

  private SortedSet<LocalTimeInterval> getWorkSlotsOfWorkingDay(LocalDate workingDay) {
    return workingTimeSchedule.workSlotsFor(workingDay.getDayOfWeek());
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

  private static Map<DayOfWeek, LocalTimeInterval> initLegacyDefaultWorkingTimeSchedule() {
    Map<DayOfWeek, LocalTimeInterval> workingTime = new EnumMap<>(DayOfWeek.class);
    workingTime.put(
        DayOfWeek.MONDAY, new LocalTimeInterval(LocalTime.of(6, 0), LocalTime.of(18, 0)));
    workingTime.put(
        DayOfWeek.TUESDAY, new LocalTimeInterval(LocalTime.of(6, 0), LocalTime.of(18, 0)));
    workingTime.put(
        DayOfWeek.WEDNESDAY, new LocalTimeInterval(LocalTime.of(6, 0), LocalTime.of(18, 0)));
    workingTime.put(
        DayOfWeek.THURSDAY, new LocalTimeInterval(LocalTime.of(6, 0), LocalTime.of(18, 0)));
    workingTime.put(
        DayOfWeek.FRIDAY, new LocalTimeInterval(LocalTime.of(6, 0), LocalTime.of(18, 0)));
    workingTime.put(DayOfWeek.SATURDAY, null);
    workingTime.put(DayOfWeek.SUNDAY, null);
    return workingTime;
  }

  private Duration calculateDurationWithinOneDay(
      Instant from, Instant to, DayOfWeek weekday, LocalDate currentDate) {
    if (converter.isWorkingDay(currentDate) && !converter.isHoliday(currentDate)) {
      LocalTimeInterval workHours = legacyWorkingTimeSchedule.get(weekday);
      LocalTime start = workHours.getBegin();
      LocalTime end = workHours.getEnd();
      LocalTime fromTime = from.atZone(UTC).toLocalTime();
      LocalTime toTime = to.atZone(UTC).toLocalTime();

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
    if (converter.isWorkingDay(date) && !converter.isHoliday(date)) {
      return addWorkingHoursOfOneDay(weekday);
    }
    return Duration.ZERO;
  }

  private Duration calculateDurationOfStartDay(
      Instant startDay, DayOfWeek weekday, LocalDate date) {
    if (converter.isWorkingDay(date) && !converter.isHoliday(date)) {
      LocalTimeInterval workHours = legacyWorkingTimeSchedule.get(weekday);
      LocalTime fromTime = startDay.atZone(UTC).toLocalTime();
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
    if (converter.isWorkingDay(date) && !converter.isHoliday(date)) {
      LocalTimeInterval workHours = legacyWorkingTimeSchedule.get(weekday);
      LocalTime start = workHours.getBegin();
      LocalTime toTime = endDate.atZone(UTC).toLocalTime();
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
  }

  private Duration addWorkingHoursOfOneDay(DayOfWeek weekday) {
    LocalTimeInterval workHours = legacyWorkingTimeSchedule.get(weekday);
    if (workHours.isValid()) {
      return Duration.between(workHours.getBegin(), workHours.getEnd());
    } else {
      return Duration.ZERO;
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

    public Instant getStart() {
      return start;
    }

    public Instant getEnd() {
      return end;
    }

    private WorkSlot previous() {
      // We need to subtract a nanosecond because start is inclusive
      return getWorkSlotOrPrevious(LocalDateTime.ofInstant(start.minusNanos(1), UTC));
    }

    private WorkSlot next() {
      return getWorkSlotOrNext(LocalDateTime.ofInstant(end, UTC));
    }
  }
}
