package pro.taskana.common.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

import pro.taskana.common.api.exceptions.InvalidArgumentException;

class WorkingTimeCalculatorTest {

  private final WorkingTimeCalculator calculator;

  WorkingTimeCalculatorTest() {
    WorkingDaysToDaysConverter converter = new WorkingDaysToDaysConverter(true, false);
    calculator = new WorkingTimeCalculator(converter);
  }

  @Test
  void should_throwInvalidArgumentException_WhenFromTimeIsAfterUntilTime() {
    Instant from = Instant.parse("2021-09-30T13:00:00.000Z");
    Instant to = Instant.parse("2021-09-30T10:00:00.000Z");

    assertThatThrownBy(() -> calculator.workingTimeBetweenTwoTimestamps(from, to))
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessage("Instants are invalid.");
  }

  @Test
  void should_throwInvalidArgumentException_WhenFromIsNull() {
    Instant to = Instant.parse("2021-09-30T10:00:00.000Z");

    assertThatThrownBy(() -> calculator.workingTimeBetweenTwoTimestamps(null, to))
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessage("Instants are invalid.");
  }

  @Test
  void should_ReturnWorkingTime_When_InstantsWithinSameHour() throws Exception {
    Instant from = Instant.parse("2021-09-30T10:02:00.000Z");
    Instant to = Instant.parse("2021-09-30T10:38:00.000Z");

    Duration duration = calculator.workingTimeBetweenTwoTimestamps(from, to);

    assertThat(duration).isEqualTo(Duration.of(36, ChronoUnit.MINUTES));
  }

  @Test
  void should_ReturnWorkingTime_When_InstantsWithinTheSameDay() throws Exception {
    Instant thursdayMorning = Instant.parse("2021-09-30T10:00:00.000Z");
    Instant thursdayEvening = Instant.parse("2021-09-30T15:00:00.000Z");

    Duration duration =
        calculator.workingTimeBetweenTwoTimestamps(thursdayMorning, thursdayEvening);

    assertThat(duration).isEqualTo(Duration.of(5, ChronoUnit.HOURS));
  }

  @Test
  void should_ReturnWorkingTime_When_InstantsWithinTheSameDayStartBeforeHours() throws Exception {
    Instant thursdayMorning = Instant.parse("2021-09-30T08:00:00.000Z");
    Instant thursdayEvening = Instant.parse("2021-09-30T15:00:00.000Z");

    Duration duration =
        calculator.workingTimeBetweenTwoTimestamps(thursdayMorning, thursdayEvening);

    assertThat(duration).isEqualTo(Duration.of(6, ChronoUnit.HOURS));
  }

  @Test
  void should_ReturnWorkingTime_When_InstantsWithinTheSameDayEndAfterHours() throws Exception {
    Instant thursdayMorning = Instant.parse("2021-09-30T10:00:00.000Z");
    Instant thursdayEvening = Instant.parse("2021-09-30T20:00:00.000Z");

    Duration duration =
        calculator.workingTimeBetweenTwoTimestamps(thursdayMorning, thursdayEvening);

    assertThat(duration).isEqualTo(Duration.of(7, ChronoUnit.HOURS));
  }

  @Test
  void should_ReturnWorkingTime_When_InstantsWithinTheSameDayStartAndEndAfterHours()
      throws Exception {
    Instant thursdayMorning = Instant.parse("2021-09-30T19:00:00.000Z");
    Instant thursdayEvening = Instant.parse("2021-09-30T20:00:00.000Z");

    Duration duration =
        calculator.workingTimeBetweenTwoTimestamps(thursdayMorning, thursdayEvening);

    assertThat(duration).isEqualTo(Duration.of(0, ChronoUnit.MINUTES));
  }

  @Test
  void should_ReturnWorkingTime_When_InstantsWithinTheSameDayStartAndEndBeforeHours()
      throws Exception {
    Instant thursdayMorning = Instant.parse("2021-09-30T03:00:00.000Z");
    Instant thursdayEvening = Instant.parse("2021-09-30T04:00:00.000Z");

    Duration duration =
        calculator.workingTimeBetweenTwoTimestamps(thursdayMorning, thursdayEvening);

    assertThat(duration).isEqualTo(Duration.of(0, ChronoUnit.MINUTES));
  }

  @Test
  void should_ReturnWorkingTime_When_InstantsSameTime() throws Exception {
    Instant thursdayMorning = Instant.parse("2021-09-30T15:00:00.000Z");
    Instant thursdayEvening = Instant.parse("2021-09-30T15:00:00.000Z");

    Duration duration =
        calculator.workingTimeBetweenTwoTimestamps(thursdayMorning, thursdayEvening);

    assertThat(duration).isEqualTo(Duration.of(0, ChronoUnit.MILLIS));
  }

  @Test
  void should_ReturnWorkingTime_When_InstantsWithinTheSameWeek() throws Exception {
    Instant fromMonday = Instant.parse("2021-09-27T10:00:00.000Z");
    Instant toSaturday = Instant.parse("2021-10-02T15:00:00.000Z");

    Duration duration = calculator.workingTimeBetweenTwoTimestamps(fromMonday, toSaturday);

    assertThat(duration).isEqualTo(Duration.of(44, ChronoUnit.HOURS));
  }

  @Test
  void should_ReturnWorkingTime_When_UntilInstantIsBeforeWorkingHours() throws Exception {
    Instant thursday = Instant.parse("2021-09-30T10:00:00.000Z");
    Instant friday = Instant.parse("2021-10-01T06:00:00.000Z");

    Duration duration = calculator.workingTimeBetweenTwoTimestamps(thursday, friday);

    assertThat(duration).isEqualTo(Duration.of(7, ChronoUnit.HOURS));
  }

  @Test
  void should_ReturnWorkingTime_When_UntilInstantIsAfterWorkingHours() throws Exception {
    Instant thursday = Instant.parse("2021-09-30T10:00:00.000Z");
    Instant friday = Instant.parse("2021-10-01T19:00:00.000Z");

    Duration duration = calculator.workingTimeBetweenTwoTimestamps(thursday, friday);

    assertThat(duration).isEqualTo(Duration.of(15, ChronoUnit.HOURS));
  }

  @Test
  void should_ReturnWorkingTime_When_FromInstantIsBeforeWorkingHours() throws Exception {
    Instant thursday = Instant.parse("2021-09-30T06:00:00.000Z");
    Instant friday = Instant.parse("2021-10-01T10:00:00.000Z");

    Duration duration = calculator.workingTimeBetweenTwoTimestamps(thursday, friday);

    assertThat(duration).isEqualTo(Duration.of(9, ChronoUnit.HOURS));
  }

  @Test
  void should_ReturnWorkingTime_When_FromInstantIsAfterWorkingHours() throws Exception {
    Instant thursday = Instant.parse("2021-09-30T19:00:00.000Z");
    Instant friday = Instant.parse("2021-10-01T10:00:00.000Z");

    Duration duration = calculator.workingTimeBetweenTwoTimestamps(thursday, friday);

    assertThat(duration).isEqualTo(Duration.of(1, ChronoUnit.HOURS));
  }

  @Test
  void should_ReturnWorkingTime_When_InstantsSeparatedByWeekend() throws Exception {
    Instant fromFriday = Instant.parse("2021-09-24T15:00:00.000Z");
    Instant toMonday = Instant.parse("2021-09-27T10:00:00.000Z");

    Duration duration = calculator.workingTimeBetweenTwoTimestamps(fromFriday, toMonday);

    assertThat(duration).isEqualTo(Duration.of(8, ChronoUnit.HOURS));
  }

  @Test
  void should_ReturnWorkingTime_When_InstantsSeparatedByNewYearHoliday() throws Exception {
    Instant fromThursday = Instant.parse("2020-12-31T14:00:00.000Z");
    Instant toSaturday = Instant.parse("2021-01-02T11:00:00.000Z");

    Duration duration = calculator.workingTimeBetweenTwoTimestamps(fromThursday, toSaturday);

    assertThat(duration).isEqualTo(Duration.of(4, ChronoUnit.HOURS));
  }

  @Test
  void should_ReturnZeroAsTime_WhenInstantsWithinSameHoliday() throws Exception {
    Instant fridayFrom = Instant.parse("2021-01-01T11:00:00.000Z");
    Instant fridayTo = Instant.parse("2021-01-01T14:00:00.000Z");

    Duration duration = calculator.workingTimeBetweenTwoTimestamps(fridayFrom, fridayTo);

    assertThat(duration).isEqualTo(Duration.of(0, ChronoUnit.HOURS));
  }

  @Test
  void should_ReturMultipleWorkingTimes_When_CalculatorUsedMultipleTimes() throws Exception {
    Instant from1 = Instant.parse("2021-09-30T10:02:00.000Z");
    Instant to1 = Instant.parse("2021-09-30T10:38:00.000Z");

    Duration duration1 = calculator.workingTimeBetweenTwoTimestamps(from1, to1);

    assertThat(duration1).isEqualTo(Duration.of(36, ChronoUnit.MINUTES));

    Instant from2 = Instant.parse("2021-09-27T10:00:00.000Z");
    Instant to2 = Instant.parse("2021-10-02T15:00:00.000Z");

    Duration duration2 = calculator.workingTimeBetweenTwoTimestamps(from2, to2);

    assertThat(duration2).isEqualTo(Duration.of(44, ChronoUnit.HOURS));
  }
}
