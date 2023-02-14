package pro.taskana.common.api;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public final class CustomHoliday {

  private final Integer day;
  private final Integer month;

  public static CustomHoliday of(Integer day, Integer month) {
    return new CustomHoliday(day, month);
  }
}
