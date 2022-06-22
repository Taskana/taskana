package pro.taskana.common.api;

import java.util.Objects;

/**
 * IntInterval captures an Integer interval. A fixed interval has defined begin and end. An open
 * ended interval has either begin == null or end ==null.
 */
public class IntInterval {

  private Integer begin;
  private Integer end;

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

  public void setBegin(Integer begin) {
    this.begin = begin;
  }

  public Integer getEnd() {
    return end;
  }

  public void setEnd(Integer end) {
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
