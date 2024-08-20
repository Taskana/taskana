package io.kadai.common.api;

import io.kadai.common.internal.Interval;
import java.time.Instant;

/**
 * Capture a time interval. A fixed interval has defined begin and end Instant. An open ended
 * interval has either begin == null or end ==null.
 */
public class TimeInterval extends Interval<Instant> {

  public TimeInterval(Instant begin, Instant end) {
    super(begin, end);
  }
}
