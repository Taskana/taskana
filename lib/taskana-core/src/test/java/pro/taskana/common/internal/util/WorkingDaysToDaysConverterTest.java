package pro.taskana.common.internal.util;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static pro.taskana.common.internal.util.WorkingDaysToDaysConverter.getEasterSunday;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;

/** Test for the WorkingDaysToDaysConverter. */
class WorkingDaysToDaysConverterTest {

  @BeforeAll
  static void setup() {
    WorkingDaysToDaysConverter.setGermanPublicHolidaysEnabled(true);
    LocalDate dayOfReformation = LocalDate.of(2018, 10, 31);
    LocalDate allSaintsDays = LocalDate.of(2018, 11, 1);
    WorkingDaysToDaysConverter.setCustomHolidays(Arrays.asList(dayOfReformation, allSaintsDays));
  }

  @Test
  void testConvertWorkingDaysToDaysForTasks() throws InvalidArgumentException {
    Instant thursday0201 = Instant.parse("2018-02-01T07:00:00.000Z");
    WorkingDaysToDaysConverter converter = WorkingDaysToDaysConverter.initialize(thursday0201);

    long days = converter.convertWorkingDaysToDays(thursday0201, -7); // = tuesday (sat + sun)
    assertEquals(-9, days);
    days = converter.convertWorkingDaysToDays(thursday0201, -6); // = wednesday (sat + sun)
    assertEquals(-8, days);
    days = converter.convertWorkingDaysToDays(thursday0201, -5); // = thursday (sat + sun)
    assertEquals(-7, days);
    days = converter.convertWorkingDaysToDays(thursday0201, -4); // = friday
    assertEquals(-6, days);
    days = converter.convertWorkingDaysToDays(thursday0201, -3); // monday
    assertEquals(-3, days);
    days = converter.convertWorkingDaysToDays(thursday0201, -2); // tuesday
    assertEquals(-2, days);
    days = converter.convertWorkingDaysToDays(thursday0201, -1); // wednesday
    assertEquals(-1, days);
    days = converter.convertWorkingDaysToDays(thursday0201, 0); // = thursday
    assertEquals(0, days);
    days = converter.convertWorkingDaysToDays(thursday0201, 1); // fri
    assertEquals(1, days);
    days = converter.convertWorkingDaysToDays(thursday0201, 2); // mon
    assertEquals(4, days);
    days = converter.convertWorkingDaysToDays(thursday0201, 3); // tues
    assertEquals(5, days);
    days = converter.convertWorkingDaysToDays(thursday0201, 4); // we
    assertEquals(6, days);
    days = converter.convertWorkingDaysToDays(thursday0201, 5); // thurs
    assertEquals(7, days);
    days = converter.convertWorkingDaysToDays(thursday0201, 6); // fri
    assertEquals(8, days);
    days = converter.convertWorkingDaysToDays(thursday0201, 7); // mon
    assertEquals(11, days);
    days = converter.convertWorkingDaysToDays(thursday0201, 8); // tue
    assertEquals(12, days);
    days = converter.convertWorkingDaysToDays(thursday0201, 9); // we
    assertEquals(13, days);
    days = converter.convertWorkingDaysToDays(thursday0201, 10); // thu
    assertEquals(14, days);
    days = converter.convertWorkingDaysToDays(thursday0201, 11); // fri
    assertEquals(15, days);
  }

  @Test
  void testConvertWorkingDaysToDaysForKarFreitag() throws InvalidArgumentException {
    Instant thursday0201 = Instant.parse("2018-02-01T07:00:00.000Z");
    WorkingDaysToDaysConverter converter = WorkingDaysToDaysConverter.initialize(thursday0201);
    Instant gruenDonnerstag2018 = Instant.parse("2018-03-29T01:00:00.000Z");
    long days = converter.convertWorkingDaysToDays(gruenDonnerstag2018, 0);
    assertEquals(0, days);
    days = converter.convertWorkingDaysToDays(gruenDonnerstag2018, 1); // Karfreitag
    assertEquals(5, days); // osterdienstag
    days = converter.convertWorkingDaysToDays(gruenDonnerstag2018, 2); // Karfreitag
    assertEquals(6, days); // ostermittwoch
  }

  @Test
  void testConvertWorkingDaysToDaysForHolidays() throws InvalidArgumentException {
    List<TimeIntervalColumnHeader> reportItems = singletonList(new TimeIntervalColumnHeader(0));
    Instant thursday0201 = Instant.parse("2018-02-01T07:00:00.000Z");
    WorkingDaysToDaysConverter converter = WorkingDaysToDaysConverter.initialize(thursday0201);

    Instant freitag0427 = Instant.parse("2018-04-27T19:00:00.000Z");
    long days = converter.convertWorkingDaysToDays(freitag0427, 0);
    assertEquals(0, days);
    days = converter.convertWorkingDaysToDays(freitag0427, 1);
    assertEquals(3, days); // 30.4.
    days = converter.convertWorkingDaysToDays(freitag0427, 2);
    assertEquals(5, days); // 2.5.
  }

  @Test
  void testGetEasterSunday() {

    assertEquals(LocalDate.of(2018, 4, 1), getEasterSunday(2018));
    assertEquals(LocalDate.of(2019, 4, 21), getEasterSunday(2019));
    assertEquals(LocalDate.of(2020, 4, 12), getEasterSunday(2020));
    assertEquals(LocalDate.of(2021, 4, 4), getEasterSunday(2021));
    assertEquals(LocalDate.of(2022, 4, 17), getEasterSunday(2022));
    assertEquals(LocalDate.of(2023, 4, 9), getEasterSunday(2023));
    assertEquals(LocalDate.of(2024, 3, 31), getEasterSunday(2024));
    assertEquals(LocalDate.of(2025, 4, 20), getEasterSunday(2025));
    assertEquals(LocalDate.of(2026, 4, 5), getEasterSunday(2026));
    assertEquals(LocalDate.of(2027, 3, 28), getEasterSunday(2027));
    assertEquals(LocalDate.of(2028, 4, 16), getEasterSunday(2028));
    assertEquals(LocalDate.of(2029, 4, 1), getEasterSunday(2029));
    assertEquals(LocalDate.of(2030, 4, 21), getEasterSunday(2030));
    assertEquals(LocalDate.of(2031, 4, 13), getEasterSunday(2031));
    assertEquals(LocalDate.of(2032, 3, 28), getEasterSunday(2032));
    assertEquals(LocalDate.of(2033, 4, 17), getEasterSunday(2033));
    assertEquals(LocalDate.of(2034, 4, 9), getEasterSunday(2034));
    assertEquals(LocalDate.of(2035, 3, 25), getEasterSunday(2035));
    assertEquals(LocalDate.of(2040, 4, 1), getEasterSunday(2040));
    assertEquals(LocalDate.of(2050, 4, 10), getEasterSunday(2050));
    assertEquals(LocalDate.of(2100, 3, 28), getEasterSunday(2100));
  }
}
