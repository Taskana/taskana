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

import java.time.LocalTime;
import java.util.Objects;

import pro.taskana.common.internal.Interval;

/**
 * LocalTimeInterval provides a closed interval using {@link LocalTime}.
 *
 * <p>That means both begin and end must not be <code>null</code>.
 *
 * <p>Note: this class has a natural ordering that is inconsistent with equals.
 */
public class LocalTimeInterval extends Interval<LocalTime>
    implements Comparable<LocalTimeInterval> {

  public LocalTimeInterval(LocalTime begin, LocalTime end) {
    super(Objects.requireNonNull(begin), Objects.requireNonNull(end));
  }

  /**
   * Compares two LocalTimeInterval objects in regard to their {@link #getBegin() begin}.
   *
   * @param o the LocalTimeInterval to be compared.
   * @return a negative value if <code>o</code> begins before <code>this</code>, 0 if both have the
   *     same begin and a positive value if <code>o</code> begins after <code>this</code>.
   */
  @Override
  public int compareTo(LocalTimeInterval o) {
    return begin.compareTo(o.getBegin());
  }
}
