package io.kadai.common.internal.configuration.parser;

import io.kadai.common.api.CustomHoliday;
import io.kadai.common.api.exceptions.WrongCustomHolidayFormatException;
import java.util.List;

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
