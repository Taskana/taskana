package pro.taskana.common.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import pro.taskana.common.api.WorkingDaysToDaysConverter.EasterCalculator;

/** Test for the WorkingDaysToDaysConverter. */
class WorkingDaysToDaysConverterTest {

  private final WorkingDaysToDaysConverter converter;

  WorkingDaysToDaysConverterTest() {
    CustomHoliday dayOfReformation = CustomHoliday.of(31, 10);
    CustomHoliday allSaintsDays = CustomHoliday.of(1, 11);
    converter =
        new WorkingDaysToDaysConverter(true, false, List.of(dayOfReformation, allSaintsDays));
  }

  @TestFactory
  Stream<DynamicTest> should_NotDetectCorpusChristiAsHoliday_When_CorpusChristiIsDisabled() {
    DynamicTest year1980 =
        DynamicTest.dynamicTest(
            "year 1980",
            () -> assertThat(converter.isGermanHoliday(LocalDate.parse("1980-06-05"))).isFalse());
    DynamicTest year2020 =
        DynamicTest.dynamicTest(
            "year 2020",
            () -> assertThat(converter.isGermanHoliday(LocalDate.parse("2020-06-11"))).isFalse());
    return Stream.of(year1980, year2020);
  }

  @TestFactory
  Stream<DynamicNode> should_DetectCorpusChristiAsHoliday_When_CorpusChristiIsEnabled() {
    WorkingDaysToDaysConverter converter = new WorkingDaysToDaysConverter(true, true);
    DynamicTest year1980 =
        DynamicTest.dynamicTest(
            "year 1980",
            () -> assertThat(converter.isGermanHoliday(LocalDate.parse("1980-06-05"))).isTrue());
    DynamicTest year2020 =
        DynamicTest.dynamicTest(
            "year 2020",
            () -> assertThat(converter.isGermanHoliday(LocalDate.parse("2020-06-11"))).isTrue());
    return Stream.of(year1980, year2020);
  }

  @TestFactory
  Stream<DynamicNode> testHasWorkingInBetween() {
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
  void testConvertWorkingDaysToDaysForTasks() {
    Instant thursday0201 = Instant.parse("2018-02-01T07:00:00.000Z");
    Instant days =
        converter.subtractWorkingDaysFromInstant(
            thursday0201, Duration.ofDays(7)); // = tuesday (sat + sun)
    assertThat(days).isEqualTo(thursday0201.minus(9, ChronoUnit.DAYS));
    days =
        converter.subtractWorkingDaysFromInstant(
            thursday0201, Duration.ofDays(6)); // = wednesday (sat + sun)
    assertThat(days).isEqualTo(thursday0201.minus(8, ChronoUnit.DAYS));
    days =
        converter.subtractWorkingDaysFromInstant(
            thursday0201, Duration.ofDays(5)); // = thursday (sat + sun)
    assertThat(days).isEqualTo(thursday0201.minus(7, ChronoUnit.DAYS));
    days = converter.subtractWorkingDaysFromInstant(thursday0201, Duration.ofDays(4)); // = friday
    assertThat(days).isEqualTo(thursday0201.minus(6, ChronoUnit.DAYS));
    days = converter.subtractWorkingDaysFromInstant(thursday0201, Duration.ofDays(3)); // monday
    assertThat(days).isEqualTo(thursday0201.minus(3, ChronoUnit.DAYS));
    days = converter.subtractWorkingDaysFromInstant(thursday0201, Duration.ofDays(2)); // tuesday
    assertThat(days).isEqualTo(thursday0201.minus(2, ChronoUnit.DAYS));
    days = converter.subtractWorkingDaysFromInstant(thursday0201, Duration.ofDays(1)); // wednesday
    assertThat(days).isEqualTo(thursday0201.minus(1, ChronoUnit.DAYS));
    days = converter.addWorkingDaysToInstant(thursday0201, Duration.ofDays(0)); // = thursday
    assertThat(days).isEqualTo(thursday0201.plus(0, ChronoUnit.DAYS));
    days = converter.addWorkingDaysToInstant(thursday0201, Duration.ofDays(1)); // fri
    assertThat(days).isEqualTo(thursday0201.plus(1, ChronoUnit.DAYS));
    days = converter.addWorkingDaysToInstant(thursday0201, Duration.ofDays(2)); // mon
    assertThat(days).isEqualTo(thursday0201.plus(4, ChronoUnit.DAYS));
    days = converter.addWorkingDaysToInstant(thursday0201, Duration.ofDays(3)); // tues
    assertThat(days).isEqualTo(thursday0201.plus(5, ChronoUnit.DAYS));
    days = converter.addWorkingDaysToInstant(thursday0201, Duration.ofDays(4)); // we
    assertThat(days).isEqualTo(thursday0201.plus(6, ChronoUnit.DAYS));
    days = converter.addWorkingDaysToInstant(thursday0201, Duration.ofDays(5)); // thurs
    assertThat(days).isEqualTo(thursday0201.plus(7, ChronoUnit.DAYS));
    days = converter.addWorkingDaysToInstant(thursday0201, Duration.ofDays(6)); // fri
    assertThat(days).isEqualTo(thursday0201.plus(8, ChronoUnit.DAYS));
    days = converter.addWorkingDaysToInstant(thursday0201, Duration.ofDays(7)); // mon
    assertThat(days).isEqualTo(thursday0201.plus(11, ChronoUnit.DAYS));
    days = converter.addWorkingDaysToInstant(thursday0201, Duration.ofDays(8)); // tue
    assertThat(days).isEqualTo(thursday0201.plus(12, ChronoUnit.DAYS));
    days = converter.addWorkingDaysToInstant(thursday0201, Duration.ofDays(9)); // we
    assertThat(days).isEqualTo(thursday0201.plus(13, ChronoUnit.DAYS));
    days = converter.addWorkingDaysToInstant(thursday0201, Duration.ofDays(10)); // thu
    assertThat(days).isEqualTo(thursday0201.plus(14, ChronoUnit.DAYS));
    days = converter.addWorkingDaysToInstant(thursday0201, Duration.ofDays(11)); // fri
    assertThat(days).isEqualTo(thursday0201.plus(15, ChronoUnit.DAYS));
  }

  @Test
  void testConvertWorkingDaysToDaysForKarFreitag() {
    Instant gruenDonnerstag2018 = Instant.parse("2018-03-29T01:00:00.000Z");
    Instant days = converter.addWorkingDaysToInstant(gruenDonnerstag2018, Duration.ofDays(0));
    assertThat(days).isEqualTo(gruenDonnerstag2018.plus(0, ChronoUnit.DAYS));
    days = converter.addWorkingDaysToInstant(gruenDonnerstag2018, Duration.ofDays(1)); // Karfreitag
    assertThat(days).isEqualTo(gruenDonnerstag2018.plus(5, ChronoUnit.DAYS)); // osterdienstag
    days = converter.addWorkingDaysToInstant(gruenDonnerstag2018, Duration.ofDays(2)); // Karfreitag
    assertThat(days).isEqualTo(gruenDonnerstag2018.plus(6, ChronoUnit.DAYS)); // ostermittwoch
  }

  @Test
  void testConvertWorkingDaysToDaysForHolidays() {
    Instant freitag0427 = Instant.parse("2018-04-27T19:00:00.000Z");
    Instant days = converter.addWorkingDaysToInstant(freitag0427, Duration.ofDays(0));
    assertThat(days).isEqualTo(freitag0427.plus(0, ChronoUnit.DAYS));
    days = converter.addWorkingDaysToInstant(freitag0427, Duration.ofDays(1));
    assertThat(days).isEqualTo(freitag0427.plus(3, ChronoUnit.DAYS)); // 30.4.
    days = converter.addWorkingDaysToInstant(freitag0427, Duration.ofDays(2));
    assertThat(days).isEqualTo(freitag0427.plus(5, ChronoUnit.DAYS)); // 2.5.
  }

  @Test
  void testGetEasterSunday() {
    EasterCalculator easterCalculator = new EasterCalculator();
    assertThat(easterCalculator.getEasterSunday(2018)).isEqualTo(LocalDate.of(2018, 4, 1));
    assertThat(easterCalculator.getEasterSunday(2019)).isEqualTo(LocalDate.of(2019, 4, 21));
    assertThat(easterCalculator.getEasterSunday(2020)).isEqualTo(LocalDate.of(2020, 4, 12));
    assertThat(easterCalculator.getEasterSunday(2021)).isEqualTo(LocalDate.of(2021, 4, 4));
    assertThat(easterCalculator.getEasterSunday(2022)).isEqualTo(LocalDate.of(2022, 4, 17));
    assertThat(easterCalculator.getEasterSunday(2023)).isEqualTo(LocalDate.of(2023, 4, 9));
    assertThat(easterCalculator.getEasterSunday(2024)).isEqualTo(LocalDate.of(2024, 3, 31));
    assertThat(easterCalculator.getEasterSunday(2025)).isEqualTo(LocalDate.of(2025, 4, 20));
    assertThat(easterCalculator.getEasterSunday(2026)).isEqualTo(LocalDate.of(2026, 4, 5));
    assertThat(easterCalculator.getEasterSunday(2027)).isEqualTo(LocalDate.of(2027, 3, 28));
    assertThat(easterCalculator.getEasterSunday(2028)).isEqualTo(LocalDate.of(2028, 4, 16));
    assertThat(easterCalculator.getEasterSunday(2029)).isEqualTo(LocalDate.of(2029, 4, 1));
    assertThat(easterCalculator.getEasterSunday(2030)).isEqualTo(LocalDate.of(2030, 4, 21));
    assertThat(easterCalculator.getEasterSunday(2031)).isEqualTo(LocalDate.of(2031, 4, 13));
    assertThat(easterCalculator.getEasterSunday(2032)).isEqualTo(LocalDate.of(2032, 3, 28));
    assertThat(easterCalculator.getEasterSunday(2033)).isEqualTo(LocalDate.of(2033, 4, 17));
    assertThat(easterCalculator.getEasterSunday(2034)).isEqualTo(LocalDate.of(2034, 4, 9));
    assertThat(easterCalculator.getEasterSunday(2035)).isEqualTo(LocalDate.of(2035, 3, 25));
    assertThat(easterCalculator.getEasterSunday(2040)).isEqualTo(LocalDate.of(2040, 4, 1));
    assertThat(easterCalculator.getEasterSunday(2050)).isEqualTo(LocalDate.of(2050, 4, 10));
    assertThat(easterCalculator.getEasterSunday(2100)).isEqualTo(LocalDate.of(2100, 3, 28));
  }
}
