package io.kadai.common.api;

import io.kadai.common.api.exceptions.InvalidArgumentException;
import java.time.Duration;
import java.time.Instant;

@SuppressWarnings("unused")
public interface WorkingTimeCalculator {

  /**
   * Subtracts {@code workingTime} from {@code workStart}. Respects the configured working time
   * schedule and Holidays.
   *
   * <p>The returned Instant denotes the first time in point the work time starts or in short it is
   * inclusive.
   *
   * <p>E.g can be used for planned date calculation.
   *
   * @param workStart The Instant {@code workingTime} is subtracted from.
   * @param workingTime The Duration to subtract from {@code workStart}. May have any resolution
   *     Duration supports, e.g. minutes or seconds.
   * @return A new Instant which represents the subtraction of working time.
   * @throws InvalidArgumentException If {@code workingTime} is negative.
   */
  Instant subtractWorkingTime(Instant workStart, Duration workingTime)
      throws InvalidArgumentException;

  /**
   * Adds {@code workingTime} from {@code workStart}. Respects the configured working time schedule
   * and Holidays.
   *
   * <p>The returned Instant denotes the first time in point the work time has ended or in short it
   * is exclusive.
   *
   * <p>E.g can be used for due date calculation.
   *
   * @param workStart The Instant {@code workingTime} is added to.
   * @param workingTime The Duration to add to {@code workStart}. May have any resolution Duration
   *     supports, e.g. minutes or seconds.
   * @return A new Instant which represents the addition of working time.
   * @throws InvalidArgumentException If {@code workingTime} is negative.
   */
  Instant addWorkingTime(Instant workStart, Duration workingTime) throws InvalidArgumentException;

  /**
   * Calculates the working time between {@code first} and {@code second} according to the
   * configured working time schedule. The returned Duration is precise to nanoseconds.
   *
   * <p>This method does not impose any ordering on {@code first} or {@code second}.
   *
   * @param first An Instant denoting the start or end of the considered time frame.
   * @param second An Instant denoting the start or end of the considered time frame.
   * @return The Duration representing the working time between {@code first} and {@code to }.
   * @throws InvalidArgumentException If either {@code first} or {@code second} is {@code null}.
   */
  Duration workingTimeBetween(Instant first, Instant second) throws InvalidArgumentException;

  /**
   * Decides whether there is any working time between {@code first} and {@code second}.
   *
   * @see #workingTimeBetween(Instant, Instant)
   */
  @SuppressWarnings("checkstyle:JavadocMethod")
  default boolean isWorkingTimeBetween(Instant first, Instant second)
      throws InvalidArgumentException {
    return !Duration.ZERO.equals(workingTimeBetween(first, second));
  }

  /**
   * Decides whether {@code instant} is a working day.
   *
   * @param instant The Instant to check. May not be {@code null}.
   * @return {@code true} if {@code instant} is a working day. {@code false} otherwise.
   */
  boolean isWorkingDay(Instant instant);

  /**
   * Decides whether {@code instant} is a weekend day.
   *
   * @param instant The Instant to check. May not be {@code null}.
   * @return {@code true} if {@code instant} is a weekend day. {@code false} otherwise.
   */
  boolean isWeekend(Instant instant);

  /**
   * Decides whether { @code instant} is a holiday.
   *
   * @param instant The Instant to check. May not be {@code null}.
   * @return {@code true} if {@code instant} is a holiday. {@code false} otherwise.
   */
  boolean isHoliday(Instant instant);

  /**
   * Decides whether {@code instant} is a holiday in Germany.
   *
   * @param instant The Instant to check. May not be {@code null}.
   * @return {@code true} if {@code instant} is a holiday in Germany. {@code false} otherwise.
   */
  boolean isGermanHoliday(Instant instant);
}
