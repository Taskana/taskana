package io.kadai.common.api;

import io.kadai.common.internal.Interval;
import java.time.LocalTime;
import java.util.Objects;

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
