package pro.taskana.common.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.ThrowingConsumer;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.internal.util.Quadruple;

class WorkingTimeCalculatorTest {

  private final WorkingTimeCalculator calculator;

  WorkingTimeCalculatorTest() {
    WorkingDaysToDaysConverter converter = new WorkingDaysToDaysConverter(true, false);
    calculator = new WorkingTimeCalculator(converter);
  }

  @Test
  void should_throwInvalidArgumentException_WhenFromTimeIsAfterUntilTime() {
    Instant from = Instant.parse("2021-09-30T12:00:00.000Z");
    Instant to = Instant.parse("2021-09-30T09:00:00.000Z");

    assertThatThrownBy(() -> calculator.workingTimeBetweenTwoTimestamps(from, to))
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessage("Instants are invalid.");
  }

  @Test
  void should_throwInvalidArgumentException_WhenFromIsNull() {
    Instant to = Instant.parse("2021-09-30T09:00:00.000Z");

    assertThatThrownBy(() -> calculator.workingTimeBetweenTwoTimestamps(null, to))
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessage("Instants are invalid.");
  }

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
}
