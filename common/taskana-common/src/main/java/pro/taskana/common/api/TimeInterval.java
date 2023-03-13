package pro.taskana.common.api;

import java.time.Instant;
import pro.taskana.common.internal.Interval;

/**
 * Capture a time interval. A fixed interval has defined begin and end Instant. An open ended
 * interval has either begin == null or end ==null.
 */
public class TimeInterval extends Interval<Instant> {

  public TimeInterval(Instant begin, Instant end) {
    super(begin, end);
  }
}
