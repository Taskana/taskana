package pro.taskana.common.api;

import java.time.Instant;
import java.util.Objects;

/**
 * The TimeInterval captures a time interval. A fixed interval has defined begin and end Instant. An
 * open ended interval has either begin == null or end ==null.
 */
public class TimeInterval {

  private Instant begin;
  private Instant end;

  public TimeInterval(Instant begin, Instant end) {
    this.begin = begin;
    this.end = end;
  }

  public boolean contains(Instant i) {
    if (i == null) {
      return false;
    }
    boolean isAfterBegin = begin == null || !i.isBefore(begin);
    boolean isBeforeEnd = end == null || !i.isAfter(end);
    return (isAfterBegin && isBeforeEnd);
  }

  public boolean isValid() {
    boolean isValid = begin != null || end != null;
    if (begin != null && end != null && begin.isAfter(end)) {
      isValid = false;
    }
    return isValid;
  }

  public Instant getBegin() {
    return begin;
  }

  public void setBegin(Instant begin) {
    this.begin = begin;
  }

  public Instant getEnd() {
    return end;
  }

  public void setEnd(Instant end) {
    this.end = end;
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
    if (!(obj instanceof TimeInterval)) {
      return false;
    }
    TimeInterval other = (TimeInterval) obj;
    return Objects.equals(begin, other.begin) && Objects.equals(end, other.end);
  }

  @Override
  public String toString() {
    return "TimeInterval [" + "begin=" + this.begin + ", end=" + this.end + "]";
  }
}
