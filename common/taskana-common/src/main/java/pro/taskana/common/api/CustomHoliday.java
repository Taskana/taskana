package pro.taskana.common.api;

import java.util.Objects;

public final class CustomHoliday {

  private final Integer day;
  private final Integer month;

  public CustomHoliday(Integer day, Integer month) {
    this.day = day;
    this.month = month;
  }

  public Integer getDay() {
    return day;
  }

  public Integer getMonth() {
    return month;
  }

  public static CustomHoliday of(Integer day, Integer month) {
    return new CustomHoliday(day, month);
  }

  @Override
  public int hashCode() {
    return Objects.hash(day, month);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof CustomHoliday)) {
      return false;
    }
    CustomHoliday other = (CustomHoliday) obj;
    return Objects.equals(day, other.day) && Objects.equals(month, other.month);
  }

  @Override
  public String toString() {
    return "CustomHoliday [day=" + day + ", month=" + month + "]";
  }
}
