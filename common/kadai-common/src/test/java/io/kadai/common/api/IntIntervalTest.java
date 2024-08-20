package io.kadai.common.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class IntIntervalTest {

  @Test
  void should_BeAValidIntervall_when_BeginIsBeforEnd() {
    IntInterval interval = new IntInterval(1, 2);
    assertThat(interval.isValid()).isTrue();
  }

  @Test
  void should_BeAValidIntervall_when_BeginAndEndAreEqual() {
    IntInterval interval = new IntInterval(1, 1);
    assertThat(interval.isValid()).isTrue();
  }

  @Test
  void should_NotBeAValidIntervall_when_BeginIsAfterEnd() {
    IntInterval interval = new IntInterval(2, 1);
    assertThat(interval.isValid()).isFalse();
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3})
  void should_ContainIntegerInIntervall_when_BeginIsOneAndEndIsThree(int number) {
    IntInterval interval = new IntInterval(1, 3);
    assertThat(interval.contains(number)).isTrue();
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 4})
  void should_NotContainIntegerInIntervall_when_BeginIsOneAndEndIsThree(int number) {
    IntInterval interval = new IntInterval(1, 3);
    assertThat(interval.contains(number)).isFalse();
  }

  @Test
  void should_TwoIntervallsAreEqual_when_BothHaveSameBeginAndEnd() {
    IntInterval interval1 = new IntInterval(1, 2);
    IntInterval interval2 = new IntInterval(1, 2);

    assertThat(interval1).isEqualTo(interval2);
    assertThat(interval2).isEqualTo(interval1);
  }

  @Test
  void should_TwoIntervallsAreNotEqual_when_BeginAndEndAreDifferent() {
    IntInterval interval1 = new IntInterval(1, 2);
    IntInterval interval2 = new IntInterval(1, 3);

    assertThat(interval1).isNotEqualTo(interval2);
    assertThat(interval2).isNotEqualTo(interval1);
  }
}
