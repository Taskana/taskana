package pro.taskana.common.internal.configuration.parser;

import java.util.List;
import pro.taskana.common.api.CustomHoliday;
import pro.taskana.common.api.exceptions.WrongCustomHolidayFormatException;

public class CustomHolidayParser extends SimpleParser<CustomHoliday> {
  public CustomHolidayParser() {
    super(CustomHoliday.class, CustomHolidayParser::parse);
  }

  private static CustomHoliday parse(String value) {
    List<String> parts = splitStringAndTrimElements(value, ".");
    if (parts.size() == 2) {
      return CustomHoliday.of(Integer.valueOf(parts.get(0)), Integer.valueOf(parts.get(1)));
    }
    throw new WrongCustomHolidayFormatException(value);
  }
}
