package pro.taskana.common.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class IntIntervalTest {

  @Test
  void should_BeAValidIntervall_when_BeginIsBeforEnd() {
    Interval<Integer> interval = new Interval<>(1, 2);
    assertThat(interval.isValid()).isTrue();
  }

  @Test
  void should_BeAValidIntervall_when_BeginAndEndAreEqual() {
    Interval<Integer> interval = new Interval<>(1, 1);
    assertThat(interval.isValid()).isTrue();
  }

  @Test
  void should_NotBeAValidIntervall_when_BeginIsAfterEnd() {
    Interval<Integer> interval = new Interval<>(2, 1);
    assertThat(interval.isValid()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3})
  void should_ContainIntegerInIntervall_when_BeginIsOneAndEndIsThree(int number) {
    Interval<Integer> interval = new Interval<>(1, 3);
    assertThat(interval.contains(number)).isTrue();
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 4})
  void should_NotContainIntegerInIntervall_when_BeginIsOneAndEndIsThree(int number) {
    Interval<Integer> interval = new Interval<>(1, 3);
    assertThat(interval.contains(number)).isFalse();
  }

  @Test
  void should_CalculateCorrectHashCode_when_HashCodeIsCalled() {
    int begin = 1;
    int end = 2;
    int expectedHashCode = Objects.hash(begin, end);

    int actualHashCode = new Interval<>(1, 2).hashCode();

    assertThat(actualHashCode).isEqualTo(expectedHashCode);
  }

  @Test
  void should_TwoIntervallsAreEqual_when_BothHaveSameBeginAndEnd() {
    Interval<Integer> interval1 = new Interval<>(1, 2);
    Interval<Integer> interval2 = new Interval<>(1, 2);

    assertThat(interval1.equals(interval2)).isTrue();
    assertThat(interval2.equals(interval1)).isTrue();
  }

  @Test
  void should_TwoIntervallsAreNotEqual_when_BeginAndEndAreDifferent() {
    Interval<Integer> interval1 = new Interval<>(1, 2);
    Interval<Integer> interval2 = new Interval<>(1, 3);

    assertThat(interval1.equals(interval2)).isFalse();
    assertThat(interval2.equals(interval1)).isFalse();
  }

  @Test
  void should_ReturnIntervalAsString_when_ToStringMethodIsCalled() {
    Interval<Integer> interval1 = new Interval<>(1, 2);

    assertThat(interval1.toString()).hasToString("Interval [begin=1, end=2]");
  }
}
