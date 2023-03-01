/*-
 * #%L
 * pro.taskana:taskana-common
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package pro.taskana.common.internal.workingtime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import pro.taskana.common.api.LocalTimeInterval;
import pro.taskana.common.api.WorkingTimeCalculator;
import pro.taskana.common.api.exceptions.InvalidArgumentException;

class WorkingTimeCalculatorImplTest {

  @Nested
  class UsualWorkingSlotsWithSingleBreak {

    private final Set<LocalTimeInterval> standardWorkingSlots =
        Set.of(
            new LocalTimeInterval(LocalTime.of(6, 0), LocalTime.of(12, 0)),
            new LocalTimeInterval(LocalTime.of(13, 0), LocalTime.of(18, 0)));
    private final WorkingTimeCalculator cut =
        new WorkingTimeCalculatorImpl(
            new HolidaySchedule(true, false),
            Map.of(
                DayOfWeek.MONDAY, standardWorkingSlots,
                DayOfWeek.TUESDAY, standardWorkingSlots,
                DayOfWeek.WEDNESDAY, standardWorkingSlots,
                DayOfWeek.THURSDAY, standardWorkingSlots,
                DayOfWeek.FRIDAY, standardWorkingSlots));

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

        Instant dueDate = cut.addWorkingTime(tuesday9oClock, Duration.ofMinutes(42));

        assertThat(dueDate).isEqualTo(Instant.parse("2022-11-15T09:42:00.000Z"));
      }

      @Test
      void untilEndOfWorkSlot() {
        Instant wednesday9oClock = Instant.parse("2022-11-16T09:00:00.000Z");

        Instant dueDate = cut.addWorkingTime(wednesday9oClock, Duration.ofHours(3));

        assertThat(dueDate).isEqualTo(Instant.parse("2022-11-16T12:00:00.000Z"));
      }

      @Test
      void spanningToNextWorkSlot() {
        Instant thursday9oClock = Instant.parse("2022-11-17T09:00:00.000Z");

        Instant dueDate = cut.addWorkingTime(thursday9oClock, Duration.ofMinutes(3 * 60 + 1));

        assertThat(dueDate).isEqualTo(Instant.parse("2022-11-17T13:01:00.000Z"));
      }

      @Test
      void spanningOverWeekend() {
        Instant friday9oClock = Instant.parse("2022-11-18T09:00:00.000Z");

        Instant dueDate = cut.addWorkingTime(friday9oClock, Duration.ofHours(11));

        assertThat(dueDate).isEqualTo(Instant.parse("2022-11-21T09:00:00.000Z"));
      }

      @Test
      void spanningOverHoliday() {
        Instant holyThursday = Instant.parse("2022-04-14T09:00:00.000Z");

        Instant dueDate = cut.addWorkingTime(holyThursday, Duration.ofHours(11));

        Instant tuesDayAfterEaster = Instant.parse("2022-04-19T09:00:00.000Z");
        assertThat(dueDate).isEqualTo(tuesDayAfterEaster);
      }

      @Test
      void startOnHoliday() {
        Instant holyFriday = Instant.parse("2022-04-15T09:00:00.000Z");

        Instant dueDate = cut.addWorkingTime(holyFriday, Duration.ofHours(2));

        Instant tuesDayAfterEaster = Instant.parse("2022-04-19T08:00:00.000Z");
        assertThat(dueDate).isEqualTo(tuesDayAfterEaster);
      }

      @Test
      void withDurationOfZero() {
        Instant mondayHalfPastNine = Instant.parse("2022-11-14T09:30:00.000Z");

        Instant dueDate = cut.addWorkingTime(mondayHalfPastNine, Duration.ZERO);

        assertThat(dueDate).isEqualTo(mondayHalfPastNine);
      }

      @Test
      void withDurationOfZeroOnHolySaturday() {
        Instant holySaturdayHalfPastNine = Instant.parse("2022-04-16T09:30:00.000Z");

        Instant dueDate = cut.addWorkingTime(holySaturdayHalfPastNine, Duration.ZERO);

        assertThat(dueDate).isEqualTo(Instant.parse("2022-04-19T06:00:00.000Z"));
      }

      @Test
      void currentTimeIsBeforeWorkingHours() {
        Instant wednesday5oClock = Instant.parse("2022-11-16T05:00:00.000Z");

        Instant dueDate = cut.addWorkingTime(wednesday5oClock, Duration.ofHours(1));

        assertThat(dueDate).isEqualTo(Instant.parse("2022-11-16T07:00:00.000Z"));
      }

      @Test
      void currentTimeIsAfterWorkingHours() {
        Instant wednesday19oClock = Instant.parse("2022-11-16T19:00:00.000Z");

        Instant dueDate = cut.addWorkingTime(wednesday19oClock, Duration.ofHours(1));

        assertThat(dueDate).isEqualTo(Instant.parse("2022-11-17T07:00:00.000Z"));
      }

      @Test
      void withNegativeDuration() {
        assertThatExceptionOfType(InvalidArgumentException.class)
            .isThrownBy(() -> cut.addWorkingTime(Instant.now(), Duration.ofMillis(-1)));
      }

      @Test
      void withNullInstant() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> cut.addWorkingTime(null, Duration.ofMillis(1)));
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

        Instant dueDate = cut.subtractWorkingTime(tuesday9oClock, Duration.ofMinutes(42));

        assertThat(dueDate).isEqualTo(Instant.parse("2022-11-15T08:18:00.000Z"));
      }

      @Test
      void untilEndOfWorkSlot() {
        Instant wednesday9oClock = Instant.parse("2022-11-16T09:00:00.000Z");

        Instant dueDate = cut.subtractWorkingTime(wednesday9oClock, Duration.ofHours(3));

        assertThat(dueDate).isEqualTo(Instant.parse("2022-11-16T06:00:00.000Z"));
      }

      @Test
      void spanningToPreviousWorkSlot() {
        Instant thursday16oClock = Instant.parse("2022-11-17T16:00:00.000Z");

        Instant dueDate = cut.subtractWorkingTime(thursday16oClock, Duration.ofMinutes(3 * 60 + 1));

        assertThat(dueDate).isEqualTo(Instant.parse("2022-11-17T11:59:00.000Z"));
      }

      @Test
      void spanningOverWeekend() {
        Instant monday17oClock = Instant.parse("2022-11-14T17:00:00.000Z");

        Instant dueDate = cut.subtractWorkingTime(monday17oClock, Duration.ofHours(11));

        assertThat(dueDate).isEqualTo(Instant.parse("2022-11-11T17:00:00.000Z"));
      }

      @Test
      void spanningOverHoliday() {
        Instant tuesdayAfterEaster = Instant.parse("2022-04-19T09:00:00.000Z");

        Instant dueDate =
            cut.subtractWorkingTime(tuesdayAfterEaster, Duration.ofMinutes(3 * 60 + 1));

        Instant holyThursday = Instant.parse("2022-04-14T17:59:00.000Z");
        assertThat(dueDate).isEqualTo(holyThursday);
      }

      @Test
      void startOnHoliday() {
        Instant holyFriday = Instant.parse("2022-04-15T09:00:00.000Z");

        Instant dueDate = cut.subtractWorkingTime(holyFriday, Duration.ofHours(2));

        Instant tuesDayAfterEaster = Instant.parse("2022-04-14T16:00:00.000Z");
        assertThat(dueDate).isEqualTo(tuesDayAfterEaster);
      }

      @Test
      void withDurationOfZero() {
        Instant mondayHalfPastNine = Instant.parse("2022-11-14T09:30:00.000Z");

        Instant dueDate = cut.subtractWorkingTime(mondayHalfPastNine, Duration.ZERO);

        assertThat(dueDate).isEqualTo(mondayHalfPastNine);
      }

      @Test
      void withDurationOfZeroOnHolySaturday() {
        Instant holySaturdayHalfPastNine = Instant.parse("2022-04-16T09:30:00.000Z");

        Instant dueDate = cut.subtractWorkingTime(holySaturdayHalfPastNine, Duration.ZERO);

        assertThat(dueDate).isEqualTo(Instant.parse("2022-04-14T18:00:00.000Z"));
      }

      @Test
      void currentTimeIsBeforeWorkingHours() {
        Instant wednesday1230 = Instant.parse("2022-11-16T12:30:00.000Z");

        Instant dueDate = cut.subtractWorkingTime(wednesday1230, Duration.ofHours(1));

        assertThat(dueDate).isEqualTo(Instant.parse("2022-11-16T11:00:00.000Z"));
      }

      @Test
      void currentTimeIsAfterWorkingHours() {
        Instant wednesday5oClock = Instant.parse("2022-11-16T05:00:00.000Z");

        Instant dueDate = cut.subtractWorkingTime(wednesday5oClock, Duration.ofHours(1));

        assertThat(dueDate).isEqualTo(Instant.parse("2022-11-15T17:00:00.000Z"));
      }

      @Test
      void withNegativeDuration() {
        assertThatExceptionOfType(InvalidArgumentException.class)
            .isThrownBy(() -> cut.subtractWorkingTime(Instant.now(), Duration.ofMillis(-1)));
      }

      @Test
      void withNullInstant() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> cut.subtractWorkingTime(null, Duration.ofMillis(1)));
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

        Duration workingTime = cut.workingTimeBetween(from, to);

        assertThat(workingTime).isEqualTo(Duration.ofMillis(1));
      }

      @Test
      void timestampsInSameWorkSlot() {
        Instant tuesday9oClock = Instant.parse("2022-11-15T09:00:00.000Z");
        Instant tuesday10oClock = Instant.parse("2022-11-15T10:00:00.000Z");

        Duration workingTime = cut.workingTimeBetween(tuesday9oClock, tuesday10oClock);

        assertThat(workingTime).isEqualTo(Duration.ofHours(1));
      }

      @Test
      void timestampsInDifferentWorkSlot() {
        Instant tuesday9oClock = Instant.parse("2022-11-15T09:00:00.000Z");
        Instant tuesday10oClock = Instant.parse("2022-11-15T14:00:00.000Z");

        Duration workingTime = cut.workingTimeBetween(tuesday9oClock, tuesday10oClock);

        assertThat(workingTime).isEqualTo(Duration.ofHours(4));
      }

      @Test
      void timestampsSpanningHoliday() {
        Instant holyThursday = Instant.parse("2022-04-14T17:00:00.000Z");
        Instant tuesdayAfterEaster = Instant.parse("2022-04-19T09:00:00.000Z");

        Duration workingTime = cut.workingTimeBetween(holyThursday, tuesdayAfterEaster);

        assertThat(workingTime).isEqualTo(Duration.ofHours(4));
      }

      @Test
      void dropTimeIfFromIsOutsideWorkSlot() {
        Instant tuesday5oClock = Instant.parse("2022-11-15T05:00:00.000Z");
        Instant tuesday7oClock = Instant.parse("2022-11-15T07:00:00.000Z");

        Duration workingTime = cut.workingTimeBetween(tuesday5oClock, tuesday7oClock);

        assertThat(workingTime).isEqualTo(Duration.ofHours(1));
      }

      @Test
      void dropRemainingTimeIfToIsOutsideWorkSlot() {
        Instant tuesday1100 = Instant.parse("2022-11-15T11:00:00.000Z");
        Instant tuesday1230 = Instant.parse("2022-11-15T12:30:00.000Z");

        Duration workingTime = cut.workingTimeBetween(tuesday1100, tuesday1230);

        assertThat(workingTime).isEqualTo(Duration.ofHours(1));
      }

      @Test
      void worksIfFromIsAfterTo() {
        Instant from = Instant.parse("2023-01-09T10:11:00.000Z");
        Instant to = Instant.parse("2023-01-09T10:10:00.000Z");

        Duration workingTime = cut.workingTimeBetween(from, to);

        assertThat(workingTime).isEqualTo(Duration.ofMinutes(1));
      }

      @Test
      void failsIfFromIsNull() {
        assertThatExceptionOfType(InvalidArgumentException.class)
            .isThrownBy(() -> cut.workingTimeBetween(null, Instant.now()));
      }

      @Test
      void failsIfToIsNull() {
        assertThatExceptionOfType(InvalidArgumentException.class)
            .isThrownBy(() -> cut.workingTimeBetween(Instant.now(), null));
      }
    }
  }

  @Nested
  class WorkSlotWithTimeIntervalsWithoutBreak {

    private final Set<LocalTimeInterval> standardWorkday =
        Set.of(
            new LocalTimeInterval(LocalTime.of(9, 0), LocalTime.of(12, 0)),
            new LocalTimeInterval(LocalTime.of(12, 0), LocalTime.of(17, 0)));
    private final WorkingTimeCalculator cut =
        new WorkingTimeCalculatorImpl(
            new HolidaySchedule(true, false),
            Map.of(
                DayOfWeek.MONDAY, standardWorkday,
                DayOfWeek.TUESDAY, standardWorkday,
                DayOfWeek.WEDNESDAY, standardWorkday,
                DayOfWeek.THURSDAY, standardWorkday,
                DayOfWeek.FRIDAY, standardWorkday));

    @Test
    void addTimeToMatchEndOfFirstAndStartOfSecondSlot() {
      Instant wednesday9oClock = Instant.parse("2022-11-16T09:00:00.000Z");

      Instant dueDate = cut.addWorkingTime(wednesday9oClock, Duration.ofHours(3));

      assertThat(dueDate).isEqualTo(Instant.parse("2022-11-16T12:00:00.000Z"));
    }

    @Test
    void subtractTimeToMatchEndOfFirstAndStartOfSecondSlot() {
      Instant wednesday15oClock = Instant.parse("2022-11-16T15:00:00.000Z");

      Instant dueDate = cut.subtractWorkingTime(wednesday15oClock, Duration.ofHours(3));

      assertThat(dueDate).isEqualTo(Instant.parse("2022-11-16T12:00:00.000Z"));
    }
  }

  @Nested
  class WorkSlotSpansCompleteDay {

    private final Set<LocalTimeInterval> completeWorkDay =
        Set.of(new LocalTimeInterval(LocalTime.MIN, LocalTime.MAX));

    private final WorkingTimeCalculator cut =
        new WorkingTimeCalculatorImpl(
            new HolidaySchedule(true, false),
            Map.of(
                DayOfWeek.MONDAY, completeWorkDay,
                DayOfWeek.TUESDAY, completeWorkDay,
                DayOfWeek.WEDNESDAY, completeWorkDay,
                DayOfWeek.THURSDAY, completeWorkDay,
                DayOfWeek.FRIDAY, completeWorkDay));

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

  @Nested
  class WorkingDayDetermination {

    private final WorkingTimeCalculator cut =
        new WorkingTimeCalculatorImpl(
            new HolidaySchedule(true, false),
            Map.of(DayOfWeek.SUNDAY, Set.of(new LocalTimeInterval(LocalTime.MIN, LocalTime.MAX))));

    @Test
    void returnsTrueIfWorkingTimeScheduleIsDefinedForDayOfWeek() {
      Instant sunday = Instant.parse("2023-02-26T12:30:00.000Z");

      boolean isWorkingDay = cut.isWorkingDay(sunday);

      assertThat(isWorkingDay).isTrue();
    }

    @Test
    void returnsFalseIfNoWorkingTimeScheduleIsDefinedForDayOfWeek() {
      Instant monday = Instant.parse("2023-02-27T12:30:00.000Z");

      boolean isWorkingDay = cut.isWorkingDay(monday);

      assertThat(isWorkingDay).isFalse();
    }

    @Test
    void returnsFalseForHolidays() {
      Instant easterSunday = Instant.parse("2023-04-09T12:30:00.000Z");

      boolean isWorkingDay = cut.isWorkingDay(easterSunday);

      assertThat(isWorkingDay).isFalse();
    }

    @Test
    void failForNull() {
      assertThatExceptionOfType(NullPointerException.class)
          .isThrownBy(() -> cut.isWorkingDay(null));
    }
  }
}
