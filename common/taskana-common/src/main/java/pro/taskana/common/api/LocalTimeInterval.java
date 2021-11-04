package pro.taskana.common.api;

import java.time.LocalTime;

public class LocalTimeInterval {

  private LocalTime begin;
  private LocalTime end;

  public LocalTimeInterval(LocalTime begin, LocalTime end) {
    this.begin = begin;
    this.end = end;
  }

  public boolean isValid() {
    return begin != null && end != null;
  }

  public LocalTime getBegin() {
    return begin;
  }

  public void setBegin(LocalTime begin) {
    this.begin = begin;
  }

  public LocalTime getEnd() {
    return end;
  }

  public void setEnd(LocalTime end) {
    this.end = end;
  }

  @Override
  public String toString() {
    return "LocalTimeInterval [begin=" + begin + ", end=" + end + "]";
  }
}
