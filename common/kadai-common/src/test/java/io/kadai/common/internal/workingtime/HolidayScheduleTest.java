package io.kadai.common.internal.workingtime;

import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.common.api.CustomHoliday;
import io.kadai.common.internal.workingtime.HolidaySchedule.EasterCalculator;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

/** Test for the HolidaySchedule. */
class HolidayScheduleTest {

  @TestFactory
  Stream<DynamicTest> should_NotDetectCorpusChristiAsHoliday_When_CorpusChristiIsDisabled() {
    CustomHoliday dayOfReformation = CustomHoliday.of(31, 10);
    CustomHoliday allSaintsDays = CustomHoliday.of(1, 11);
    HolidaySchedule schedule =
        new HolidaySchedule(true, false, List.of(dayOfReformation, allSaintsDays));
    DynamicTest year1980 =
        DynamicTest.dynamicTest(
            "year 1980",
            () -> assertThat(schedule.isGermanHoliday(LocalDate.parse("1980-06-05"))).isFalse());
    DynamicTest year2020 =
        DynamicTest.dynamicTest(
            "year 2020",
            () -> assertThat(schedule.isGermanHoliday(LocalDate.parse("2020-06-11"))).isFalse());
    return Stream.of(year1980, year2020);
  }

  @TestFactory
  Stream<DynamicNode> should_DetectCorpusChristiAsHoliday_When_CorpusChristiIsEnabled() {
    HolidaySchedule schedule = new HolidaySchedule(true, true);
    DynamicTest year1980 =
        DynamicTest.dynamicTest(
            "year 1980",
            () -> assertThat(schedule.isGermanHoliday(LocalDate.parse("1980-06-05"))).isTrue());
    DynamicTest year2020 =
        DynamicTest.dynamicTest(
            "year 2020",
            () -> assertThat(schedule.isGermanHoliday(LocalDate.parse("2020-06-11"))).isTrue());
    return Stream.of(year1980, year2020);
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
