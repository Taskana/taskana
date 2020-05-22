package pro.taskana.common.internal.util;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.common.internal.util.WorkingDaysToDaysConverter.getEasterSunday;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import pro.taskana.common.api.CustomHoliday;
import pro.taskana.common.api.exceptions.InvalidArgumentException;

/** Test for the WorkingDaysToDaysConverter. */
class WorkingDaysToDaysConverterTest {

  @BeforeAll
  static void setup() {
    WorkingDaysToDaysConverter.setGermanPublicHolidaysEnabled(true);
    CustomHoliday dayOfReformation = CustomHoliday.of(31, 10);
    CustomHoliday allSaintsDays = CustomHoliday.of(1, 11);
    WorkingDaysToDaysConverter.setCustomHolidays(Arrays.asList(dayOfReformation, allSaintsDays));
  }

  void verifyCorpusChristiForDate(
      WorkingDaysToDaysConverter converter,
      String date,
      boolean enableCorpsChristi,
      boolean expected) {
    WorkingDaysToDaysConverter.setCorpusChristiEnabled(enableCorpsChristi);
    Instant referenceDay = Instant.parse(date);
    converter.refreshReferenceDate(referenceDay);
    assertThat(
            converter.isGermanHoliday(
                LocalDateTime.ofInstant(referenceDay, ZoneId.systemDefault()).toLocalDate()))
        .isEqualTo(expected);
    WorkingDaysToDaysConverter.setCorpusChristiEnabled(false);
  }

  @TestFactory
  Stream<DynamicNode> should_DetectCorpusChristiAsHoliday_When_CorpusChristiIsEnabled() {
    WorkingDaysToDaysConverter converter = WorkingDaysToDaysConverter.initialize();

    DynamicContainer enabledCorpusChristi =
        DynamicContainer.dynamicContainer(
            "corpus christi is enabled",
            Stream.of(
                DynamicTest.dynamicTest(
                    "year 1980",
                    () ->
                        verifyCorpusChristiForDate(
                            converter, "1980-06-05T12:00:00.000Z", true, true)),
                DynamicTest.dynamicTest(
                    "year 2020",
                    () ->
                        verifyCorpusChristiForDate(
                            converter, "2020-06-11T12:00:00.000Z", true, true))));
    DynamicContainer disabledCorpusChristi =
        DynamicContainer.dynamicContainer(
            "corpus christi is enabled",
            Stream.of(
                DynamicTest.dynamicTest(
                    "year 1980",
                    () ->
                        verifyCorpusChristiForDate(
                            converter, "1980-06-05T12:00:00.000Z", false, false)),
                DynamicTest.dynamicTest(
                    "year 2020",
                    () ->
                        verifyCorpusChristiForDate(
                            converter, "2020-06-11T12:00:00.000Z", false, false))));
    return Stream.of(enabledCorpusChristi, disabledCorpusChristi);
  }

  @TestFactory
  Stream<DynamicNode> testHasWorkingInBetween() throws InvalidArgumentException {
    Instant referenceDay = Instant.parse("2020-02-01T07:00:00.000Z");
    WorkingDaysToDaysConverter converter = WorkingDaysToDaysConverter.initialize(referenceDay);

    Instant thursday = Instant.parse("2020-04-30T07:12:00.000Z");
    Instant friday = Instant.parse("2020-05-01T07:12:00.000Z"); // german holiday
    Instant saturday = Instant.parse("2020-05-02T07:12:00.000Z");
    Instant sunday = Instant.parse("2020-05-03T07:12:00.000Z");
    Instant monday = Instant.parse("2020-05-04T07:12:00.000Z");
    Instant tuesday = Instant.parse("2020-05-05T07:12:00.000Z");
    DynamicContainer noWorkingDaysInBetween =
        DynamicContainer.dynamicContainer(
            "no working days in between",
            Stream.of(
                DynamicTest.dynamicTest(
                    "tuesday <-> tuesday",
                    () ->
                        assertThat(converter.hasWorkingDaysInBetween(tuesday, tuesday)).isFalse()),
                DynamicTest.dynamicTest(
                    "thursday <-> saturday (friday is holiday)",
                    () ->
                        assertThat(converter.hasWorkingDaysInBetween(thursday, saturday))
                            .isFalse()),
                DynamicTest.dynamicTest(
                    "friday <-> friday",
                    () -> assertThat(converter.hasWorkingDaysInBetween(friday, friday)).isFalse()),
                DynamicTest.dynamicTest(
                    "friday <-> monday",
                    () -> assertThat(converter.hasWorkingDaysInBetween(friday, monday)).isFalse()),
                DynamicTest.dynamicTest(
                    "saturday <-> monday",
                    () ->
                        assertThat(converter.hasWorkingDaysInBetween(saturday, monday)).isFalse()),
                DynamicTest.dynamicTest(
                    "sunday <-> monday",
                    () -> assertThat(converter.hasWorkingDaysInBetween(sunday, monday)).isFalse()),
                DynamicTest.dynamicTest(
                    "monday <-> monday",
                    () -> assertThat(converter.hasWorkingDaysInBetween(sunday, monday)).isFalse()),
                DynamicTest.dynamicTest(
                    "monday <-> sunday",
                    () -> assertThat(converter.hasWorkingDaysInBetween(monday, sunday)).isFalse()),
                DynamicTest.dynamicTest(
                    "monday <-> friday",
                    () ->
                        assertThat(converter.hasWorkingDaysInBetween(monday, friday)).isFalse())));

    DynamicContainer hasWorkingDaysInBetween =
        DynamicContainer.dynamicContainer(
            "has working days in between",
            Stream.of(
                DynamicTest.dynamicTest(
                    "friday <-> tuesday",
                    () -> assertThat(converter.hasWorkingDaysInBetween(friday, tuesday)).isTrue()),
                DynamicTest.dynamicTest(
                    "sunday <-> tuesday",
                    () ->
                        assertThat(converter.hasWorkingDaysInBetween(sunday, tuesday)).isTrue())));

    return Stream.of(noWorkingDaysInBetween, hasWorkingDaysInBetween);
  }

  @Test
  void testConvertWorkingDaysToDaysForTasks() throws InvalidArgumentException {
    Instant thursday0201 = Instant.parse("2018-02-01T07:00:00.000Z");
    WorkingDaysToDaysConverter converter = WorkingDaysToDaysConverter.initialize(thursday0201);

    long days = converter.convertWorkingDaysToDays(thursday0201, -7); // = tuesday (sat + sun)
    assertThat(days).isEqualTo(-9);
    days = converter.convertWorkingDaysToDays(thursday0201, -6); // = wednesday (sat + sun)
    assertThat(days).isEqualTo(-8);
    days = converter.convertWorkingDaysToDays(thursday0201, -5); // = thursday (sat + sun)
    assertThat(days).isEqualTo(-7);
    days = converter.convertWorkingDaysToDays(thursday0201, -4); // = friday
    assertThat(days).isEqualTo(-6);
    days = converter.convertWorkingDaysToDays(thursday0201, -3); // monday
    assertThat(days).isEqualTo(-3);
    days = converter.convertWorkingDaysToDays(thursday0201, -2); // tuesday
    assertThat(days).isEqualTo(-2);
    days = converter.convertWorkingDaysToDays(thursday0201, -1); // wednesday
    assertThat(days).isEqualTo(-1);
    days = converter.convertWorkingDaysToDays(thursday0201, 0); // = thursday
    assertThat(days).isEqualTo(0);
    days = converter.convertWorkingDaysToDays(thursday0201, 1); // fri
    assertThat(days).isEqualTo(1);
    days = converter.convertWorkingDaysToDays(thursday0201, 2); // mon
    assertThat(days).isEqualTo(4);
    days = converter.convertWorkingDaysToDays(thursday0201, 3); // tues
    assertThat(days).isEqualTo(5);
    days = converter.convertWorkingDaysToDays(thursday0201, 4); // we
    assertThat(days).isEqualTo(6);
    days = converter.convertWorkingDaysToDays(thursday0201, 5); // thurs
    assertThat(days).isEqualTo(7);
    days = converter.convertWorkingDaysToDays(thursday0201, 6); // fri
    assertThat(days).isEqualTo(8);
    days = converter.convertWorkingDaysToDays(thursday0201, 7); // mon
    assertThat(days).isEqualTo(11);
    days = converter.convertWorkingDaysToDays(thursday0201, 8); // tue
    assertThat(days).isEqualTo(12);
    days = converter.convertWorkingDaysToDays(thursday0201, 9); // we
    assertThat(days).isEqualTo(13);
    days = converter.convertWorkingDaysToDays(thursday0201, 10); // thu
    assertThat(days).isEqualTo(14);
    days = converter.convertWorkingDaysToDays(thursday0201, 11); // fri
    assertThat(days).isEqualTo(15);
  }

  @Test
  void testConvertWorkingDaysToDaysForKarFreitag() throws InvalidArgumentException {
    Instant thursday0201 = Instant.parse("2018-02-01T07:00:00.000Z");
    WorkingDaysToDaysConverter converter = WorkingDaysToDaysConverter.initialize(thursday0201);
    Instant gruenDonnerstag2018 = Instant.parse("2018-03-29T01:00:00.000Z");
    long days = converter.convertWorkingDaysToDays(gruenDonnerstag2018, 0);
    assertThat(days).isEqualTo(0);
    days = converter.convertWorkingDaysToDays(gruenDonnerstag2018, 1); // Karfreitag
    assertThat(days).isEqualTo(5); // osterdienstag
    days = converter.convertWorkingDaysToDays(gruenDonnerstag2018, 2); // Karfreitag
    assertThat(days).isEqualTo(6); // ostermittwoch
  }

  @Test
  void testConvertWorkingDaysToDaysForHolidays() throws InvalidArgumentException {
    Instant thursday0201 = Instant.parse("2018-02-01T07:00:00.000Z");
    WorkingDaysToDaysConverter converter = WorkingDaysToDaysConverter.initialize(thursday0201);

    Instant freitag0427 = Instant.parse("2018-04-27T19:00:00.000Z");
    long days = converter.convertWorkingDaysToDays(freitag0427, 0);
    assertThat(days).isEqualTo(0);
    days = converter.convertWorkingDaysToDays(freitag0427, 1);
    assertThat(days).isEqualTo(3); // 30.4.
    days = converter.convertWorkingDaysToDays(freitag0427, 2);
    assertThat(days).isEqualTo(5); // 2.5.
  }

  @Test
  void testGetEasterSunday() {
    assertThat(getEasterSunday(2018)).isEqualTo(LocalDate.of(2018, 4, 1));
    assertThat(getEasterSunday(2019)).isEqualTo(LocalDate.of(2019, 4, 21));
    assertThat(getEasterSunday(2020)).isEqualTo(LocalDate.of(2020, 4, 12));
    assertThat(getEasterSunday(2021)).isEqualTo(LocalDate.of(2021, 4, 4));
    assertThat(getEasterSunday(2022)).isEqualTo(LocalDate.of(2022, 4, 17));
    assertThat(getEasterSunday(2023)).isEqualTo(LocalDate.of(2023, 4, 9));
    assertThat(getEasterSunday(2024)).isEqualTo(LocalDate.of(2024, 3, 31));
    assertThat(getEasterSunday(2025)).isEqualTo(LocalDate.of(2025, 4, 20));
    assertThat(getEasterSunday(2026)).isEqualTo(LocalDate.of(2026, 4, 5));
    assertThat(getEasterSunday(2027)).isEqualTo(LocalDate.of(2027, 3, 28));
    assertThat(getEasterSunday(2028)).isEqualTo(LocalDate.of(2028, 4, 16));
    assertThat(getEasterSunday(2029)).isEqualTo(LocalDate.of(2029, 4, 1));
    assertThat(getEasterSunday(2030)).isEqualTo(LocalDate.of(2030, 4, 21));
    assertThat(getEasterSunday(2031)).isEqualTo(LocalDate.of(2031, 4, 13));
    assertThat(getEasterSunday(2032)).isEqualTo(LocalDate.of(2032, 3, 28));
    assertThat(getEasterSunday(2033)).isEqualTo(LocalDate.of(2033, 4, 17));
    assertThat(getEasterSunday(2034)).isEqualTo(LocalDate.of(2034, 4, 9));
    assertThat(getEasterSunday(2035)).isEqualTo(LocalDate.of(2035, 3, 25));
    assertThat(getEasterSunday(2040)).isEqualTo(LocalDate.of(2040, 4, 1));
    assertThat(getEasterSunday(2050)).isEqualTo(LocalDate.of(2050, 4, 10));
    assertThat(getEasterSunday(2100)).isEqualTo(LocalDate.of(2100, 3, 28));
  }
}
