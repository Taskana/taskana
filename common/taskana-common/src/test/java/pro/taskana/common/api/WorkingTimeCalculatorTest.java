package pro.taskana.common.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.ThrowingConsumer;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.internal.util.Quadruple;

class WorkingTimeCalculatorTest {

  private final WorkingTimeCalculator calculator;

  WorkingTimeCalculatorTest() {
    WorkingDaysToDaysConverter converter = new WorkingDaysToDaysConverter(true, false);
    calculator = new WorkingTimeCalculator(converter, initWorkingTimeScheduleForTests());
  }

  @SuppressWarnings("deprecation")
  @Test
  void should_throwInvalidArgumentException_When_FromTimeIsAfterUntilTime() {
    Instant from = Instant.parse("2021-09-30T12:00:00.000Z");
    Instant to = Instant.parse("2021-09-30T09:00:00.000Z");

    assertThatThrownBy(() -> calculator.workingTimeBetweenTwoTimestamps(from, to))
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessage("Instants are invalid.");
  }

  @SuppressWarnings("deprecation")
  @Test
  void should_throwInvalidArgumentException_When_FromIsNull() {
    Instant to = Instant.parse("2021-09-30T09:00:00.000Z");

    assertThatThrownBy(() -> calculator.workingTimeBetweenTwoTimestamps(null, to))
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessage("Instants are invalid.");
  }

  @SuppressWarnings("deprecation")
  @Test
  void should_ReturnMultipleWorkingTimes_When_CalculatorUsedMultipleTimes() throws Exception {
    Instant from1 = Instant.parse("2021-09-30T10:02:00.000Z");
    Instant to1 = Instant.parse("2021-09-30T10:38:00.000Z");

    Duration duration1 = calculator.workingTimeBetweenTwoTimestamps(from1, to1);

    assertThat(duration1).isEqualTo(Duration.of(36, ChronoUnit.MINUTES));

    Instant from2 = Instant.parse("2021-09-27T10:00:00.000Z");
    Instant to2 = Instant.parse("2021-10-02T15:00:00.000Z");

    Duration duration2 = calculator.workingTimeBetweenTwoTimestamps(from2, to2);

    assertThat(duration2).isEqualTo(Duration.of(56, ChronoUnit.HOURS));
  }

  @SuppressWarnings("deprecation")
  @TestFactory
  Stream<DynamicTest> should_ReturnWorkingTime() {
    List<Quadruple<String, Instant, Instant, Duration>> valuesForTests =
        List.of(
            // Test instants that are within same day
            Quadruple.of(
                "Delta in hours",
                Instant.parse("2021-09-30T09:00:00.000Z"),
                Instant.parse("2021-09-30T14:00:00.000Z"),
                Duration.of(5, ChronoUnit.HOURS)),
            Quadruple.of(
                "Delta in minutes",
                Instant.parse("2021-09-30T09:02:00.000Z"),
                Instant.parse("2021-09-30T09:38:00.000Z"),
                Duration.of(36, ChronoUnit.MINUTES)),
            Quadruple.of(
                "Delta in seconds",
                Instant.parse("2021-09-30T15:00:00.000Z"),
                Instant.parse("2021-09-30T15:00:01.000Z"),
                Duration.of(1, ChronoUnit.SECONDS)),
            Quadruple.of(
                "Delta in milliseconds",
                Instant.parse("2021-09-30T15:00:00.000Z"),
                Instant.parse("2021-09-30T15:00:00.111Z"),
                Duration.of(111, ChronoUnit.MILLIS)),
            Quadruple.of(
                "Delta in all time units",
                Instant.parse("2021-09-30T15:00:00.000Z"),
                Instant.parse("2021-09-30T16:01:01.001Z"),
                Duration.of(1, ChronoUnit.HOURS).plusMinutes(1).plusSeconds(1).plusMillis(1)),
            Quadruple.of(
                "Start time before working hours",
                Instant.parse("2021-09-30T05:00:00.000Z"),
                Instant.parse("2021-09-30T07:00:00.000Z"),
                Duration.of(1, ChronoUnit.HOURS)),
            Quadruple.of(
                "End time after working hours",
                Instant.parse("2021-09-30T17:00:00.000Z"),
                Instant.parse("2021-09-30T19:00:00.000Z"),
                Duration.of(1, ChronoUnit.HOURS)),
            Quadruple.of(
                "On holiday",
                Instant.parse("2021-01-01T11:00:00.000Z"),
                Instant.parse("2021-01-01T14:00:00.000Z"),
                Duration.ZERO),
            Quadruple.of(
                "Start and end after hours",
                Instant.parse("2021-09-30T19:00:00.000Z"),
                Instant.parse("2021-09-30T20:00:00.000Z"),
                Duration.ZERO),
            // Test instants that are over two days
            Quadruple.of(
                "Two days, start before working hours",
                Instant.parse("2021-09-30T05:00:00.000Z"),
                Instant.parse("2021-10-01T10:00:00.000Z"),
                Duration.of(12 + 4, ChronoUnit.HOURS)),
            Quadruple.of(
                "Two days, start after working hours",
                Instant.parse("2021-09-30T19:00:00.000Z"),
                Instant.parse("2021-10-01T10:00:00.000Z"),
                Duration.of(4, ChronoUnit.HOURS)),
            Quadruple.of(
                "Two days, end before working hours",
                Instant.parse("2021-09-30T17:00:00.000Z"),
                Instant.parse("2021-10-01T05:00:00.000Z"),
                Duration.of(1, ChronoUnit.HOURS)),
            Quadruple.of(
                "Two days, end after working hours",
                Instant.parse("2021-09-30T17:00:00.000Z"),
                Instant.parse("2021-10-01T19:00:00.000Z"),
                Duration.of(1 + 12, ChronoUnit.HOURS)),
            // Test instants that are over multiple days
            Quadruple.of(
                "Separated by weekend",
                Instant.parse("2021-09-24T15:00:00.000Z"),
                Instant.parse("2021-09-27T10:00:00.000Z"),
                Duration.of(3 + 4, ChronoUnit.HOURS)),
            Quadruple.of(
                "Separated by holiday",
                Instant.parse("2021-05-12T17:00:00.000Z"),
                Instant.parse("2021-05-14T07:00:00.000Z"),
                Duration.of(1 + 1, ChronoUnit.HOURS)),
            Quadruple.of(
                "From Monday to Saturday",
                Instant.parse("2021-09-27T09:00:00.000Z"),
                Instant.parse("2021-10-02T14:00:00.000Z"),
                Duration.of(9 + 12 + 12 + 12 + 12, ChronoUnit.HOURS)));

    ThrowingConsumer<Quadruple<String, Instant, Instant, Duration>> test =
        q -> {
          Duration duration =
              calculator.workingTimeBetweenTwoTimestamps(q.getSecond(), q.getThird());
          assertThat(duration).isEqualTo(q.getFourth());
        };
    return DynamicTest.stream(valuesForTests.iterator(), Quadruple::getFirst, test);
  }

  @Test
  void should_adhere_to_working_time_table_per_day() {
    // Change a working day on Mondays to three hours only
    WorkingTimeCalculator cut =
        new WorkingTimeCalculator(
            new WorkingDaysToDaysConverter(true, false),
            Map.of(
                DayOfWeek.MONDAY,
                Set.of(new LocalTimeInterval(LocalTime.of(9, 0), LocalTime.of(12, 0))),
                DayOfWeek.TUESDAY,
                Set.of(new LocalTimeInterval(LocalTime.of(9, 0), LocalTime.of(12, 0)))));

    Instant monday10oClock = Instant.parse("2022-11-14T10:00:00.000Z");

    Instant newInstant = cut.addWorkingTime(monday10oClock, Duration.ofHours(3));

    assertThat(newInstant).isEqualTo(Instant.parse("2022-11-15T10:00:00.000Z"));
  }

  private static Map<DayOfWeek, Set<LocalTimeInterval>> initWorkingTimeScheduleForTests() {
    Map<DayOfWeek, Set<LocalTimeInterval>> workingTime = new EnumMap<>(DayOfWeek.class);

    Set<LocalTimeInterval> standardWorkingSlots =
        Set.of(
            new LocalTimeInterval(LocalTime.of(6, 0), LocalTime.of(12, 0)),
            new LocalTimeInterval(LocalTime.of(13, 0), LocalTime.of(18, 0)));
    workingTime.put(DayOfWeek.MONDAY, standardWorkingSlots);
    workingTime.put(DayOfWeek.TUESDAY, standardWorkingSlots);
    workingTime.put(DayOfWeek.WEDNESDAY, standardWorkingSlots);
    workingTime.put(DayOfWeek.THURSDAY, standardWorkingSlots);
    workingTime.put(DayOfWeek.FRIDAY, standardWorkingSlots);
    return workingTime;
  }

  @Nested
  class WorkingTimeAddition {

    /*
     * Examples (assuming a working day is from 06:00 - 12:00 and 13:00 - 18:00):
     *  Start                | duration in minutes | Due Date
     *  Tue, 09:00           | 42                  | Tue, 09:42
     *  Wed, 09:00           | 3*60                | Wed, 12:00
     *  Thu, 09:00           | 3*60 + 1            | Thu, 13:01
     *  Fri, 09:00           | 11*60               | Mon, 09:00
     *  Holy Thursday, 09:00 | 8*60 + 1            | Tue, 08:01
     */
    @Test
    void withinWorkSlot() {
      Instant tuesday9oClock = Instant.parse("2022-11-15T09:00:00.000Z");

      Instant dueDate = calculator.addWorkingTime(tuesday9oClock, Duration.ofMinutes(42));

      assertThat(dueDate).isEqualTo(Instant.parse("2022-11-15T09:42:00.000Z"));
    }

    @Test
    void untilEndOfWorkSlot() {
      Instant wednesday9oClock = Instant.parse("2022-11-16T09:00:00.000Z");

      Instant dueDate = calculator.addWorkingTime(wednesday9oClock, Duration.ofHours(3));

      assertThat(dueDate).isEqualTo(Instant.parse("2022-11-16T12:00:00.000Z"));
    }

    @Test
    void spanningToNextWorkSlot() {
      Instant thursday9oClock = Instant.parse("2022-11-17T09:00:00.000Z");

      Instant dueDate = calculator.addWorkingTime(thursday9oClock, Duration.ofMinutes(3 * 60 + 1));

      assertThat(dueDate).isEqualTo(Instant.parse("2022-11-17T13:01:00.000Z"));
    }

    @Test
    void spanningOverWeekend() {
      Instant friday9oClock = Instant.parse("2022-11-18T09:00:00.000Z");

      Instant dueDate = calculator.addWorkingTime(friday9oClock, Duration.ofHours(11));

      assertThat(dueDate).isEqualTo(Instant.parse("2022-11-21T09:00:00.000Z"));
    }

    @Test
    void spanningOverHoliday() {
      Instant holyThursday = Instant.parse("2022-04-14T09:00:00.000Z");

      Instant dueDate = calculator.addWorkingTime(holyThursday, Duration.ofHours(11));

      Instant tuesDayAfterEaster = Instant.parse("2022-04-19T09:00:00.000Z");
      assertThat(dueDate).isEqualTo(tuesDayAfterEaster);
    }

    @Test
    void startOnHoliday() {
      Instant holyFriday = Instant.parse("2022-04-15T09:00:00.000Z");

      Instant dueDate = calculator.addWorkingTime(holyFriday, Duration.ofHours(2));

      Instant tuesDayAfterEaster = Instant.parse("2022-04-19T08:00:00.000Z");
      assertThat(dueDate).isEqualTo(tuesDayAfterEaster);
    }

    @Test
    void withDurationOfZero() {
      Instant mondayHalfPastNine = Instant.parse("2022-11-14T09:30:00.000Z");

      Instant dueDate = calculator.addWorkingTime(mondayHalfPastNine, Duration.ZERO);

      assertThat(dueDate).isEqualTo(mondayHalfPastNine);
    }

    @Test
    void withDurationOfZeroOnHolySaturday() {
      Instant holySaturdayHalfPastNine = Instant.parse("2022-04-16T09:30:00.000Z");

      Instant dueDate = calculator.addWorkingTime(holySaturdayHalfPastNine, Duration.ZERO);

      assertThat(dueDate).isEqualTo(Instant.parse("2022-04-19T06:00:00.000Z"));
    }

    @Test
    void currentTimeIsBeforeWorkingHours() {
      Instant wednesday5oClock = Instant.parse("2022-11-16T05:00:00.000Z");

      Instant dueDate = calculator.addWorkingTime(wednesday5oClock, Duration.ofHours(1));

      assertThat(dueDate).isEqualTo(Instant.parse("2022-11-16T07:00:00.000Z"));
    }

    @Test
    void currentTimeIsAfterWorkingHours() {
      Instant wednesday19oClock = Instant.parse("2022-11-16T19:00:00.000Z");

      Instant dueDate = calculator.addWorkingTime(wednesday19oClock, Duration.ofHours(1));

      assertThat(dueDate).isEqualTo(Instant.parse("2022-11-17T07:00:00.000Z"));
    }

    @Test
    void withNegativeDuration() {
      assertThatIllegalArgumentException()
          .isThrownBy(() -> calculator.addWorkingTime(Instant.now(), Duration.ofMillis(-1)));
    }
  }

  @Nested
  class WorkingTimeSubtraction {

    /*
     * Examples (assuming a working day is from 06:00 - 12:00 and 13:00 - 18:00):
     *  Start                       | duration in minutes | Due Date
     *  Tue, 09:00                  | 42                  | Tue, 08:18
     *  Wed, 09:00                  | 3*60                | Wed, 06:00
     *  Thu, 16:00                  | 3*60 + 1            | Thu, 11:59
     *  Mon, 17:00                  | 11*60               | Fri, 17:00
     *  Tuesday after Easter, 09:00 | 3*60 + 1            | Thu, 17:59
     */
    @Test
    void withinWorkSlot() {
      Instant tuesday9oClock = Instant.parse("2022-11-15T09:00:00.000Z");

      Instant dueDate = calculator.subtractWorkingTime(tuesday9oClock, Duration.ofMinutes(42));

      assertThat(dueDate).isEqualTo(Instant.parse("2022-11-15T08:18:00.000Z"));
    }

    @Test
    void untilEndOfWorkSlot() {
      Instant wednesday9oClock = Instant.parse("2022-11-16T09:00:00.000Z");

      Instant dueDate = calculator.subtractWorkingTime(wednesday9oClock, Duration.ofHours(3));

      assertThat(dueDate).isEqualTo(Instant.parse("2022-11-16T06:00:00.000Z"));
    }

    @Test
    void spanningToPreviousWorkSlot() {
      Instant thursday16oClock = Instant.parse("2022-11-17T16:00:00.000Z");

      Instant dueDate =
          calculator.subtractWorkingTime(thursday16oClock, Duration.ofMinutes(3 * 60 + 1));

      assertThat(dueDate).isEqualTo(Instant.parse("2022-11-17T11:59:00.000Z"));
    }

    @Test
    void spanningOverWeekend() {
      Instant monday17oClock = Instant.parse("2022-11-14T17:00:00.000Z");

      Instant dueDate = calculator.subtractWorkingTime(monday17oClock, Duration.ofHours(11));

      assertThat(dueDate).isEqualTo(Instant.parse("2022-11-11T17:00:00.000Z"));
    }

    @Test
    void spanningOverHoliday() {
      Instant tuesdayAfterEaster = Instant.parse("2022-04-19T09:00:00.000Z");

      Instant dueDate =
          calculator.subtractWorkingTime(tuesdayAfterEaster, Duration.ofMinutes(3 * 60 + 1));

      Instant holyThursday = Instant.parse("2022-04-14T17:59:00.000Z");
      assertThat(dueDate).isEqualTo(holyThursday);
    }

    @Test
    void startOnHoliday() {
      Instant holyFriday = Instant.parse("2022-04-15T09:00:00.000Z");

      Instant dueDate = calculator.subtractWorkingTime(holyFriday, Duration.ofHours(2));

      Instant tuesDayAfterEaster = Instant.parse("2022-04-14T16:00:00.000Z");
      assertThat(dueDate).isEqualTo(tuesDayAfterEaster);
    }

    @Test
    void withDurationOfZero() {
      Instant mondayHalfPastNine = Instant.parse("2022-11-14T09:30:00.000Z");

      Instant dueDate = calculator.subtractWorkingTime(mondayHalfPastNine, Duration.ZERO);

      assertThat(dueDate).isEqualTo(mondayHalfPastNine);
    }

    @Test
    void withDurationOfZeroOnHolySaturday() {
      Instant holySaturdayHalfPastNine = Instant.parse("2022-04-16T09:30:00.000Z");

      Instant dueDate = calculator.subtractWorkingTime(holySaturdayHalfPastNine, Duration.ZERO);

      assertThat(dueDate).isEqualTo(Instant.parse("2022-04-14T18:00:00.000Z"));
    }

    @Test
    void currentTimeIsBeforeWorkingHours() {
      Instant wednesday1230 = Instant.parse("2022-11-16T12:30:00.000Z");

      Instant dueDate = calculator.subtractWorkingTime(wednesday1230, Duration.ofHours(1));

      assertThat(dueDate).isEqualTo(Instant.parse("2022-11-16T11:00:00.000Z"));
    }

    @Test
    void currentTimeIsAfterWorkingHours() {
      Instant wednesday5oClock = Instant.parse("2022-11-16T05:00:00.000Z");

      Instant dueDate = calculator.subtractWorkingTime(wednesday5oClock, Duration.ofHours(1));

      assertThat(dueDate).isEqualTo(Instant.parse("2022-11-15T17:00:00.000Z"));
    }

    @Test
    void withNegativeDuration() {
      assertThatIllegalArgumentException()
          .isThrownBy(() -> calculator.subtractWorkingTime(Instant.now(), Duration.ofMillis(-1)));
    }
  }

  @Nested
  class WorkingTimeBetweenTwoInstants {

    /*
     * Examples (assuming a working day is from 06:00 - 12:00 and 13:00 - 18:00):
     *  Start                       | duration in minutes | Due Date
     *  Tue, 09:00                  | 42                  | Tue, 08:18
     *  Wed, 09:00                  | 3*60                | Wed, 06:00
     *  Thu, 16:00                  | 3*60 + 1            | Thu, 11:59
     *  Mon, 17:00                  | 11*60               | Fri, 17:00
     *  Tuesday after Easter, 09:00 | 3*60 + 1            | Thu, 17:59
     */

    @Test
    void precisionIsLowerThanDays() {
      Instant from = Instant.parse("2022-11-15T09:00:00.000Z");
      Instant to = Instant.parse("2022-11-15T09:00:00.001Z");

      Duration workingTime = calculator.workingTimeBetween(from, to);

      assertThat(workingTime).isEqualTo(Duration.ofMillis(1));
    }

    @Test
    void timestampsInSameWorkSlot() {
      Instant tuesday9oClock = Instant.parse("2022-11-15T09:00:00.000Z");
      Instant tuesday10oClock = Instant.parse("2022-11-15T10:00:00.000Z");

      Duration workingTime = calculator.workingTimeBetween(tuesday9oClock, tuesday10oClock);

      assertThat(workingTime).isEqualTo(Duration.ofHours(1));
    }

    @Test
    void timestampsInDifferentWorkSlot() {
      Instant tuesday9oClock = Instant.parse("2022-11-15T09:00:00.000Z");
      Instant tuesday10oClock = Instant.parse("2022-11-15T14:00:00.000Z");

      Duration workingTime = calculator.workingTimeBetween(tuesday9oClock, tuesday10oClock);

      assertThat(workingTime).isEqualTo(Duration.ofHours(4));
    }

    @Test
    void timestampsSpanningHoliday() {
      Instant holyThursday = Instant.parse("2022-04-14T17:00:00.000Z");
      Instant tuesdayAfterEaster = Instant.parse("2022-04-19T09:00:00.000Z");

      Duration workingTime = calculator.workingTimeBetween(holyThursday, tuesdayAfterEaster);

      assertThat(workingTime).isEqualTo(Duration.ofHours(4));
    }

    @Test
    void dropTimeIfFromIsOutsideWorkSlot() {
      Instant tuesday5oClock = Instant.parse("2022-11-15T05:00:00.000Z");
      Instant tuesday7oClock = Instant.parse("2022-11-15T07:00:00.000Z");

      Duration workingTime = calculator.workingTimeBetween(tuesday5oClock, tuesday7oClock);

      assertThat(workingTime).isEqualTo(Duration.ofHours(1));
    }

    @Test
    void dropRemainingTimeIfToIsOutsideWorkSlot() {
      Instant tuesday1100 = Instant.parse("2022-11-15T11:00:00.000Z");
      Instant tuesday1230 = Instant.parse("2022-11-15T12:30:00.000Z");

      Duration workingTime = calculator.workingTimeBetween(tuesday1100, tuesday1230);

      assertThat(workingTime).isEqualTo(Duration.ofHours(1));
    }

    @Test
    void failsIfFromIsNull() {
      assertThatIllegalArgumentException()
          .isThrownBy(() -> calculator.workingTimeBetween(null, Instant.now()));
    }

    @Test
    void failsIfToIsNull() {
      assertThatIllegalArgumentException()
          .isThrownBy(() -> calculator.workingTimeBetween(Instant.now(), null));
    }

    @Test
    void failsIfFromIsAfterTo() {
      Instant from = Instant.parse("2023-01-09T10:11:00.000Z");
      Instant to = from.minusMillis(1);

      assertThatIllegalArgumentException()
          .isThrownBy(() -> calculator.workingTimeBetween(from, to));
    }
  }

  @Nested
  class WorkSlotSpansCompleteDay {

    private WorkingTimeCalculator cut;

    @BeforeEach
    void setUp() {
      Set<LocalTimeInterval> completeWorkDay =
          Set.of(new LocalTimeInterval(LocalTime.MIN, LocalTime.MAX));
      cut =
          new WorkingTimeCalculator(
              new WorkingDaysToDaysConverter(true, false),
              Map.of(
                  DayOfWeek.MONDAY, completeWorkDay,
                  DayOfWeek.TUESDAY, completeWorkDay,
                  DayOfWeek.WEDNESDAY, completeWorkDay,
                  DayOfWeek.THURSDAY, completeWorkDay,
                  DayOfWeek.FRIDAY, completeWorkDay));
    }

    @Test
    void withDurationOfZeroOnHolySaturday() {
      Instant holySaturdayHalfPastNine = Instant.parse("2022-04-16T09:30:00.000Z");

      Instant dueDate = cut.subtractWorkingTime(holySaturdayHalfPastNine, Duration.ZERO);

      assertThat(dueDate).isEqualTo(Instant.parse("2022-04-15T00:00:00Z"));
    }

    @Test
    void calculatesWorkingTimeBetweenCorrectlySpanningWorkDays() {
      Instant start = Instant.parse("2023-01-09T23:00:00.000Z");
      Instant end = Instant.parse("2023-01-10T01:00:00.000Z");

      Duration duration = cut.workingTimeBetween(start, end);

      assertThat(duration).isEqualTo(Duration.ofHours(2));
    }

    @Test
    void addsWorkingTimeCorrectly() {
      Instant start = Instant.parse("2023-01-09T23:00:00.000Z");

      Instant end = cut.addWorkingTime(start, Duration.ofDays(1));

      assertThat(end).isEqualTo(Instant.parse("2023-01-10T23:00:00.000Z"));
    }

    @Test
    void subtractsWorkingTimeCorrectly() {
      Instant end = Instant.parse("2023-01-10T23:00:00.000Z");

      Instant start = cut.subtractWorkingTime(end, Duration.ofDays(1));

      assertThat(start).isEqualTo(Instant.parse("2023-01-09T23:00:00.000Z"));
    }
  }
}
