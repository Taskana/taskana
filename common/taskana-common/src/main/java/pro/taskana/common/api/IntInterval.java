package pro.taskana.common.api;

import java.util.Objects;

/**
 * IntInterval captures an Integer interval. A fixed interval has defined begin and end. An open
 * ended interval has either begin == null or end ==null.
 */
public class IntInterval {

  private final Integer begin;
  private final Integer end;

  public IntInterval(Integer begin, Integer end) {
    this.begin = begin;
    this.end = end;
  }

  public boolean isValid() {
    boolean isValid = begin != null || end != null;
    if (begin != null && end != null && begin > end) {
      isValid = false;
    }
    return isValid;
  }

  public Integer getBegin() {
    return begin;
  }

  public Integer getEnd() {
    return end;
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
    if (!(obj instanceof IntInterval)) {
      return false;
    }
    IntInterval other = (IntInterval) obj;
    return Objects.equals(begin, other.begin) && Objects.equals(end, other.end);
  }

  @Override
  public String toString() {
    return "IntInterval [" + "begin=" + this.begin + ", end=" + this.end + "]";
  }
}
