/*-
 * #%L
 * pro.taskana:taskana-common
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package pro.taskana.common.api.exceptions;

import pro.taskana.common.api.CustomHoliday;
import pro.taskana.common.internal.util.MapCreator;

/** This exception is thrown when an entry for the {@linkplain CustomHoliday} has a wrong format. */
public class WrongCustomHolidayFormatException extends TaskanaException {

  public static final String ERROR_KEY = "CUSTOM_HOLIDAY_WRONG_FORMAT";
  private final String customHoliday;

  public WrongCustomHolidayFormatException(String customHoliday) {
    super(
        String.format(
            "Wrong format for custom holiday entry '%s'! The format should be 'dd.MM' "
                + "i.e. 01.05 for the first of May.",
            customHoliday),
        ErrorCode.of(ERROR_KEY, MapCreator.of("customHoliday", customHoliday)));
    this.customHoliday = customHoliday;
  }

  public String getCustomHoliday() {
    return customHoliday;
  }
}
