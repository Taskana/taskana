package io.kadai.common.api;

import io.kadai.common.internal.Interval;

/**
 * IntInterval captures an Integer interval. A fixed interval has defined begin and end. An open
 * ended interval has either begin == null or end ==null.
 */
public class IntInterval extends Interval<Integer> {

  public IntInterval(Integer begin, Integer end) {
    super(begin, end);
  }
}
