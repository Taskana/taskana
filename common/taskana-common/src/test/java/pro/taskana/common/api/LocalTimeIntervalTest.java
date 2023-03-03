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

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

public class LocalTimeIntervalTest {

  @Test
  void naturalOrderingIsDefinedByBegin() {
    LocalTimeInterval ltiOne = new LocalTimeInterval(LocalTime.MIN, LocalTime.MAX);
    LocalTimeInterval ltiTwo =
        new LocalTimeInterval(LocalTime.MIN.plus(1, ChronoUnit.MILLIS), LocalTime.MAX);

    assertThat(ltiOne).isLessThan(ltiTwo);
  }
}
