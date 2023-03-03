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
package pro.taskana.common.internal.workingtime;

import static java.time.temporal.ChronoUnit.DAYS;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.LongStream;
import java.util.stream.LongStream.Builder;

import pro.taskana.common.api.CustomHoliday;

/**
 * The WorkingDaysToDaysConverter provides a method to convert an age in working days into an age in
 * days.
 */
public class HolidaySchedule {

  // offset in days from easter sunday
  private static final long OFFSET_EASTER_SUNDAY = 0;
  private static final long OFFSET_GOOD_FRIDAY = -2; // Good Friday
  private static final long OFFSET_EASTER_MONDAY = 1; // Easter Monday
  private static final long OFFSET_ASCENSION_DAY = 39; // Ascension Day
  private static final long OFFSET_WHIT_MONDAY = 50; // Whit Monday
  private static final long OFFSET_CORPUS_CHRISTI = 60; // Corpus Christi
  private static final Set<CustomHoliday> GERMAN_HOLIDAYS =
      new HashSet<>(
          Arrays.asList(
              CustomHoliday.of(1, 1), // New Year
              CustomHoliday.of(1, 5), // Labour Day
              CustomHoliday.of(3, 10), // German Unity Day
              CustomHoliday.of(25, 12), // Christmas Day
              CustomHoliday.of(26, 12) // Christmas Day
              ));

  private final boolean germanHolidaysEnabled;
  private final boolean corpusChristiEnabled;
  private final Set<CustomHoliday> customHolidays;
  private final EasterCalculator easterCalculator;

  public HolidaySchedule(boolean germanHolidaysEnabled, boolean corpusChristiEnabled) {
    this(germanHolidaysEnabled, corpusChristiEnabled, Collections.emptySet());
  }

  /**
   * Creates a WorkingDasToDaysConverter.
   *
   * @param germanHolidaysEnabled identifier for German holidays
   * @param corpusChristiEnabled identifier for Corpus Christi - dependent from
   *     germanHolidaysEnabled and thus only validated if German holidays are enabled.
   * @param customHolidays additional custom holidays
   */
  public HolidaySchedule(
      boolean germanHolidaysEnabled,
      boolean corpusChristiEnabled,
      Collection<CustomHoliday> customHolidays) {
    this.germanHolidaysEnabled = germanHolidaysEnabled;
    this.corpusChristiEnabled = corpusChristiEnabled;
    this.customHolidays = new HashSet<>(customHolidays);
    easterCalculator = new EasterCalculator();
  }

  public boolean isHoliday(LocalDate date) {
    if (germanHolidaysEnabled && isGermanHoliday(date)) {
      return true;
    }
    // Custom holidays that can be configured in the TaskanaEngineConfiguration
    return customHolidays.contains(CustomHoliday.of(date.getDayOfMonth(), date.getMonthValue()));
  }

  public boolean isGermanHoliday(LocalDate date) {
    if (GERMAN_HOLIDAYS.contains(CustomHoliday.of(date.getDayOfMonth(), date.getMonthValue()))) {
      return true;
    }

    // Easter holidays Good Friday, Easter Monday, Ascension Day, Whit Monday.
    long diffFromEasterSunday =
        DAYS.between(easterCalculator.getEasterSunday(date.getYear()), date);

    Builder builder =
        LongStream.builder()
            .add(OFFSET_EASTER_SUNDAY)
            .add(OFFSET_GOOD_FRIDAY)
            .add(OFFSET_EASTER_MONDAY)
            .add(OFFSET_ASCENSION_DAY)
            .add(OFFSET_WHIT_MONDAY);

    if (corpusChristiEnabled) {
      builder.add(OFFSET_CORPUS_CHRISTI);
    }

    return builder.build().anyMatch(c -> c == diffFromEasterSunday);
  }

  @Override
  public String toString() {
    return "WorkingDaysToDaysConverter [germanHolidaysEnabled="
        + germanHolidaysEnabled
        + ", corpusChristiEnabled="
        + corpusChristiEnabled
        + ", customHolidays="
        + customHolidays
        + ", easterCalculator="
        + easterCalculator
        + "]";
  }

  static class EasterCalculator {

    LocalDate cachedEasterDay;

    /**
     * Computes the date of Easter Sunday for a given year.
     *
     * @param year for which the date of Easter Sunday should be calculated
     * @return the date of Easter Sunday for the given year
     */
    LocalDate getEasterSunday(int year) {
      if (cachedEasterDay != null && cachedEasterDay.getYear() == year) {
        return cachedEasterDay;
      }

      // Algorithm for calculating the date of Easter Sunday
      // (Meeus/Jones/Butcher Gregorian algorithm)
      // see https://dzone.com/articles/algorithm-calculating-date
      int a = year % 19;
      int b = year / 100;
      int c = year % 100;
      int d = b / 4;
      int e = b % 4;
      int f = (b + 8) / 25;
      int g = (b - f + 1) / 3;
      int h = (19 * a + b - d - g + 15) % 30;
      int i = c / 4;
      int k = c % 4;
      int l = (32 + 2 * e + 2 * i - h - k) % 7;
      int m = (a + 11 * h + 22 * l) / 451;
      int n = h + l - 7 * m + 114;
      int month = n / 31;
      int day = (n % 31) + 1;

      cachedEasterDay = LocalDate.of(year, month, day);
      return cachedEasterDay;
    }
  }
}
