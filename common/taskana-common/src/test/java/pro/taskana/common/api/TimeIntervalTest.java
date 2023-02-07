package pro.taskana.common.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TimeIntervalTest {

  private static final ZoneId UTC = ZoneId.of("UTC");
  private final Instant date1 = LocalDate.of(2023, 2, 10).atStartOfDay(UTC).toInstant();
  private final Instant date2 = LocalDate.of(2023, 2, 13).atStartOfDay(UTC).toInstant();

  @Test
  void should_BeAValidIntervall_when_BeginIsBeforEnd() {
    Interval<Instant> timeInterval = new Interval<>(date1, date2);

    assertThat(timeInterval.isValid()).isTrue();
  }

  @Test
  void should_BeAValidIntervall_when_BeginAndEndAreEqual() {
    Interval<Instant> timeInterval = new Interval<>(date1, date1);

    assertThat(timeInterval.isValid()).isTrue();
  }

  @Test
  void should_NotBeAValidIntervall_when_BeginIsAfterEnd() {
    Interval<Instant> timeInterval = new Interval<>(date2, date1);

    assertThat(timeInterval.isValid()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(ints = {10, 11, 12, 13})
  void should_ContainDateInIntervall(int day) {
    Interval<Instant> timeInterval = new Interval<>(date1, date2);

    Instant actualInstant = LocalDate.of(2023, 2, day).atStartOfDay(UTC).toInstant();

    assertThat(timeInterval.contains(actualInstant)).isTrue();
  }

  @ParameterizedTest
  @ValueSource(ints = {8, 9, 14, 15})
  void should_NotContainDateInIntervall_when_InstantIsBeforeOrAfter(int day) {
    Interval<Instant> timeInterval = new Interval<>(date1, date2);

    Instant actualInstant = LocalDate.of(2023, 2, day).atStartOfDay(UTC).toInstant();

    assertThat(timeInterval.contains(actualInstant)).isFalse();
  }

  @Test
  void should_CalculateCorrectHashCode_when_HashCodeIsCalled() {
    int expectedHashCode = Objects.hash(date1, date2);

    int actualHashCode = new Interval<>(date1, date2).hashCode();

    assertThat(actualHashCode).isEqualTo(expectedHashCode);
  }

  @Test
  void should_TwoIntervallsAreEqual_when_BothHaveSameBeginAndEnd() {
    Interval<Instant> timeInterval1 = new Interval<>(date1, date2);
    Interval<Instant> timeInterval2 = new Interval<>(date1, date2);

    assertThat(timeInterval1.equals(timeInterval2)).isTrue();
    assertThat(timeInterval2.equals(timeInterval1)).isTrue();
  }

  @Test
  void should_TwoIntervallsAreNotEqual_when_BeginAndEndAreDifferent() {
    Interval<Instant> timeInterval1 = new Interval<>(date1, date2);
    Interval<Instant> timeInterval2 =
        new Interval<>(date1, LocalDate.of(2023, 2, 14).atStartOfDay(UTC).toInstant());

    assertThat(timeInterval1.equals(timeInterval2)).isFalse();
    assertThat(timeInterval2.equals(timeInterval1)).isFalse();
  }

  @Test
  void should_ReturnIntervalAsString_when_ToStringMethodIsCalled() {
    Interval<Instant> timeInterval = new Interval<>(date1, date2);

    assertThat(timeInterval.toString())
        .hasToString("Interval [begin=2023-02-10T00:00:00Z, end=2023-02-13T00:00:00Z]");
  }
}
