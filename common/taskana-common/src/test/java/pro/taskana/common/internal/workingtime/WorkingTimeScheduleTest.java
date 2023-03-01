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

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

import pro.taskana.common.api.LocalTimeInterval;

public class WorkingTimeScheduleTest {

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
