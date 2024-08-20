package io.kadai.common.api.exceptions;

import io.kadai.common.api.CustomHoliday;
import java.util.Map;

/** This exception is thrown when an entry for the {@linkplain CustomHoliday} has a wrong format. */
public class WrongCustomHolidayFormatException extends KadaiRuntimeException {

  public static final String ERROR_KEY = "CUSTOM_HOLIDAY_WRONG_FORMAT";
  private final String customHoliday;

  public WrongCustomHolidayFormatException(String customHoliday) {
    super(
        String.format(
            "Wrong format for custom holiday entry '%s'! The format should be 'dd.MM' "
                + "i.e. 01.05 for the first of May.",
            customHoliday),
        ErrorCode.of(ERROR_KEY, Map.of("customHoliday", ensureNullIsHandled(customHoliday))));
    this.customHoliday = customHoliday;
  }

  public String getCustomHoliday() {
    return customHoliday;
  }
}
