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
package pro.taskana.common.api;

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
