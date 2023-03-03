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
package pro.taskana.common.internal;

import java.util.Objects;

/**
 * An Interval captures an interval of Type <code>T extends Comparable</code>. A fixed interval has
 * defined begin and end. An open ended interval has either begin == null or end ==null.
 *
 * <p>Example: <code>Interval&lt;Integer&gt; interval = new Interval&lt;&gt;(1, 2);</code> or <code>
 * Interval&lt;Instant&gt; timeInterval = new Interval&lt;&gt;(instant_1, instant_2)</code>
 */
public class Interval<T extends Comparable<? super T>> {

  protected final T begin;

  protected final T end;

  public Interval(T begin, T end) {
    this.begin = begin;
    this.end = end;
  }

  public T getBegin() {
    return begin;
  }

  public T getEnd() {
    return end;
  }

  public boolean contains(T i) {
    if (i == null) {
      return false;
    }
    boolean isAfterBegin = begin == null || i.compareTo(begin) >= 0;
    boolean isBeforeEnd = end == null || i.compareTo(end) <= 0;
    return (isAfterBegin && isBeforeEnd);
  }

  public boolean isValid() {
    boolean isValid = begin != null || end != null;
    if (begin != null && end != null && begin.compareTo(end) > 0) {
      isValid = false;
    }
    return isValid;
  }

  @Override
  public int hashCode() {
    return Objects.hash(begin, end);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Interval)) {
      return false;
    }
    Interval<?> other = (Interval<?>) obj;
    return Objects.equals(begin, other.begin) && Objects.equals(end, other.end);
  }

  @Override
  public String toString() {
    return "Interval [" + "begin=" + this.begin + ", end=" + this.end + "]";
  }
}
