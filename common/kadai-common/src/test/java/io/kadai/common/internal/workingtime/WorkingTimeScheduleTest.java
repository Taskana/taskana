package io.kadai.common.internal.workingtime;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import io.kadai.common.api.LocalTimeInterval;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class WorkingTimeScheduleTest {

  @Test
  void creationFailsIfWorkingTimesOverlap() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () ->
                new WorkingTimeSchedule(
                    Map.of(
                        DayOfWeek.MONDAY,
                        Set.of(
                            new LocalTimeInterval(LocalTime.MIN, LocalTime.MAX),
                            new LocalTimeInterval(LocalTime.NOON, LocalTime.MAX)))));
  }

  @Test
  void workSlotsForReturnsUnmodifiableSets() {
    WorkingTimeSchedule cut =
        new WorkingTimeSchedule(
            Map.of(DayOfWeek.MONDAY, Set.of(new LocalTimeInterval(LocalTime.MIN, LocalTime.MAX))));

    assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(
            () ->
                cut.workSlotsFor(DayOfWeek.MONDAY)
                    .add(new LocalTimeInterval(LocalTime.NOON, LocalTime.MIDNIGHT)));
  }

  @Test
  void workSlotsForReversedReturnsUnmodifiableSets() {
    WorkingTimeSchedule cut =
        new WorkingTimeSchedule(
            Map.of(DayOfWeek.MONDAY, Set.of(new LocalTimeInterval(LocalTime.MIN, LocalTime.MAX))));

    assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(
            () ->
                cut.workSlotsForReversed(DayOfWeek.MONDAY)
                    .add(new LocalTimeInterval(LocalTime.NOON, LocalTime.MIDNIGHT)));
  }
}
