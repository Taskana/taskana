package pro.taskana.monitor.internal;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.internal.util.DaysToWorkingDaysConverter;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.internal.preprocessor.DaysToWorkingDaysReportConverter;

/** Test for the DaysToWorkingDaysReportConverter. */
class DaysToWorkingDaysReportConverterTest {

  @BeforeAll
  static void setup() {
    DaysToWorkingDaysConverter.setGermanPublicHolidaysEnabled(true);
    LocalDate dayOfReformation = LocalDate.of(2018, 10, 31);
    LocalDate allSaintsDays = LocalDate.of(2018, 11, 1);
    DaysToWorkingDaysConverter.setCustomHolidays(Arrays.asList(dayOfReformation, allSaintsDays));
  }

  @Test
  void testInitializeForDifferentDates() throws InvalidArgumentException {
    DaysToWorkingDaysReportConverter instance1 =
        DaysToWorkingDaysReportConverter.initialize(
            getShortListOfColumnHeaders(), Instant.parse("2018-02-04T00:00:00.000Z"));
    DaysToWorkingDaysReportConverter instance2 =
        DaysToWorkingDaysReportConverter.initialize(
            getShortListOfColumnHeaders(), Instant.parse("2018-02-05T00:00:00.000Z"));

    assertNotEquals(instance1, instance2);
  }

  @Test
  void testConvertDaysToWorkingDays() throws InvalidArgumentException {
    DaysToWorkingDaysReportConverter instance =
        DaysToWorkingDaysReportConverter.initialize(
            getLargeListOfColumnHeaders(), Instant.parse("2018-02-06T00:00:00.000Z"));

    assertEquals(-16, instance.convertDaysToWorkingDays(-16));
    assertEquals(-11, instance.convertDaysToWorkingDays(-15));

    assertEquals(-2, instance.convertDaysToWorkingDays(-4));
    assertEquals(-1, instance.convertDaysToWorkingDays(-3));
    assertEquals(-1, instance.convertDaysToWorkingDays(-2));
    assertEquals(-1, instance.convertDaysToWorkingDays(-1));
    assertEquals(0, instance.convertDaysToWorkingDays(0));
    assertEquals(1, instance.convertDaysToWorkingDays(1));
    assertEquals(2, instance.convertDaysToWorkingDays(2));
    assertEquals(3, instance.convertDaysToWorkingDays(3));
    assertEquals(3, instance.convertDaysToWorkingDays(4));
    assertEquals(3, instance.convertDaysToWorkingDays(5));
    assertEquals(4, instance.convertDaysToWorkingDays(6));

    assertEquals(11, instance.convertDaysToWorkingDays(15));
    assertEquals(16, instance.convertDaysToWorkingDays(16));
  }

  @Test
  void testConvertWorkingDaysToDays() throws InvalidArgumentException {
    DaysToWorkingDaysReportConverter instance =
        DaysToWorkingDaysReportConverter.initialize(
            getLargeListOfColumnHeaders(), Instant.parse("2018-02-27T00:00:00.000Z"));

    assertEquals(singletonList(-13), instance.convertWorkingDaysToDays(-13));
    assertEquals(singletonList(-12), instance.convertWorkingDaysToDays(-12));

    assertEquals(singletonList(-12), instance.convertWorkingDaysToDays(-8));
    assertEquals(singletonList(-11), instance.convertWorkingDaysToDays(-7));
    assertEquals(Arrays.asList(-8, -9, -10), instance.convertWorkingDaysToDays(-6));
    assertEquals(singletonList(-7), instance.convertWorkingDaysToDays(-5));
    assertEquals(singletonList(-6), instance.convertWorkingDaysToDays(-4));
    assertEquals(singletonList(-5), instance.convertWorkingDaysToDays(-3));
    assertEquals(singletonList(-4), instance.convertWorkingDaysToDays(-2));
    assertEquals(Arrays.asList(-1, -2, -3), instance.convertWorkingDaysToDays(-1));
    assertEquals(singletonList(0), instance.convertWorkingDaysToDays(0));
    assertEquals(singletonList(1), instance.convertWorkingDaysToDays(1));
    assertEquals(singletonList(2), instance.convertWorkingDaysToDays(2));
    assertEquals(Arrays.asList(3, 4, 5), instance.convertWorkingDaysToDays(3));
    assertEquals(singletonList(6), instance.convertWorkingDaysToDays(4));
    assertEquals(singletonList(7), instance.convertWorkingDaysToDays(5));
    assertEquals(singletonList(8), instance.convertWorkingDaysToDays(6));
    assertEquals(singletonList(9), instance.convertWorkingDaysToDays(7));
    assertEquals(Arrays.asList(10, 11, 12), instance.convertWorkingDaysToDays(8));
    assertEquals(singletonList(13), instance.convertWorkingDaysToDays(9));
    assertEquals(singletonList(14), instance.convertWorkingDaysToDays(10));
    assertEquals(singletonList(15), instance.convertWorkingDaysToDays(11));

    assertEquals(singletonList(12), instance.convertWorkingDaysToDays(12));
    assertEquals(singletonList(13), instance.convertWorkingDaysToDays(13));
  }

  @Test
  void testConvertWorkingDaysToDaysAtWeekend() throws InvalidArgumentException {
    DaysToWorkingDaysReportConverter instance =
        DaysToWorkingDaysReportConverter.initialize(
            getLargeListOfColumnHeaders(), Instant.parse("2018-03-10T00:00:00.000Z"));

    assertEquals(singletonList(-13), instance.convertWorkingDaysToDays(-13));
    assertEquals(singletonList(-12), instance.convertWorkingDaysToDays(-12));

    assertEquals(singletonList(-10), instance.convertWorkingDaysToDays(-8));
    assertEquals(singletonList(-9), instance.convertWorkingDaysToDays(-7));
    assertEquals(singletonList(-8), instance.convertWorkingDaysToDays(-6));
    assertEquals(Arrays.asList(-5, -6, -7), instance.convertWorkingDaysToDays(-5));
    assertEquals(singletonList(-4), instance.convertWorkingDaysToDays(-4));
    assertEquals(singletonList(-3), instance.convertWorkingDaysToDays(-3));
    assertEquals(singletonList(-2), instance.convertWorkingDaysToDays(-2));
    assertEquals(singletonList(-1), instance.convertWorkingDaysToDays(-1));
    assertEquals(Arrays.asList(0, 1), instance.convertWorkingDaysToDays(0));
    assertEquals(singletonList(2), instance.convertWorkingDaysToDays(1));
    assertEquals(singletonList(3), instance.convertWorkingDaysToDays(2));
    assertEquals(singletonList(4), instance.convertWorkingDaysToDays(3));
    assertEquals(singletonList(5), instance.convertWorkingDaysToDays(4));
    assertEquals(Arrays.asList(6, 7, 8), instance.convertWorkingDaysToDays(5));
    assertEquals(singletonList(9), instance.convertWorkingDaysToDays(6));
    assertEquals(singletonList(10), instance.convertWorkingDaysToDays(7));
    assertEquals(singletonList(11), instance.convertWorkingDaysToDays(8));
    assertEquals(singletonList(12), instance.convertWorkingDaysToDays(9));
    assertEquals(Arrays.asList(13, 14, 15), instance.convertWorkingDaysToDays(10));
    assertEquals(singletonList(16), instance.convertWorkingDaysToDays(11));

    assertEquals(singletonList(12), instance.convertWorkingDaysToDays(12));
    assertEquals(singletonList(13), instance.convertWorkingDaysToDays(13));
  }

  @Test
  void testConvertWorkingDaysToDaysOnEasterSunday() throws InvalidArgumentException {
    DaysToWorkingDaysReportConverter instance =
        DaysToWorkingDaysReportConverter.initialize(
            getLargeListOfColumnHeaders(), Instant.parse("2018-04-01T00:00:00.000Z"));

    assertEquals(singletonList(-13), instance.convertWorkingDaysToDays(-13));
    assertEquals(singletonList(-12), instance.convertWorkingDaysToDays(-12));

    assertEquals(singletonList(-12), instance.convertWorkingDaysToDays(-8));
    assertEquals(singletonList(-11), instance.convertWorkingDaysToDays(-7));
    assertEquals(singletonList(-10), instance.convertWorkingDaysToDays(-6));
    assertEquals(singletonList(-9), instance.convertWorkingDaysToDays(-5));
    assertEquals(Arrays.asList(-6, -7, -8), instance.convertWorkingDaysToDays(-4));
    assertEquals(singletonList(-5), instance.convertWorkingDaysToDays(-3));
    assertEquals(singletonList(-4), instance.convertWorkingDaysToDays(-2));
    assertEquals(singletonList(-3), instance.convertWorkingDaysToDays(-1));
    assertEquals(Arrays.asList(0, 1, -1, -2), instance.convertWorkingDaysToDays(0));
    assertEquals(singletonList(2), instance.convertWorkingDaysToDays(1));
    assertEquals(singletonList(3), instance.convertWorkingDaysToDays(2));
    assertEquals(singletonList(4), instance.convertWorkingDaysToDays(3));
    assertEquals(Arrays.asList(5, 6, 7), instance.convertWorkingDaysToDays(4));
    assertEquals(singletonList(8), instance.convertWorkingDaysToDays(5));
    assertEquals(singletonList(9), instance.convertWorkingDaysToDays(6));
    assertEquals(singletonList(10), instance.convertWorkingDaysToDays(7));
    assertEquals(singletonList(11), instance.convertWorkingDaysToDays(8));
    assertEquals(Arrays.asList(12, 13, 14), instance.convertWorkingDaysToDays(9));
    assertEquals(singletonList(15), instance.convertWorkingDaysToDays(10));
    assertEquals(singletonList(16), instance.convertWorkingDaysToDays(11));

    assertEquals(singletonList(12), instance.convertWorkingDaysToDays(12));
    assertEquals(singletonList(13), instance.convertWorkingDaysToDays(13));
  }

  @Test
  void testEasterHolidays() throws InvalidArgumentException {
    DaysToWorkingDaysReportConverter instance =
        DaysToWorkingDaysReportConverter.initialize(
            getLargeListOfColumnHeaders(), Instant.parse("2018-03-28T00:00:00.000Z"));

    assertEquals(0, instance.convertDaysToWorkingDays(0));
    assertEquals(1, instance.convertDaysToWorkingDays(1));
    assertEquals(1, instance.convertDaysToWorkingDays(2));
    assertEquals(1, instance.convertDaysToWorkingDays(3));
    assertEquals(1, instance.convertDaysToWorkingDays(4));
    assertEquals(1, instance.convertDaysToWorkingDays(5));
    assertEquals(2, instance.convertDaysToWorkingDays(6));
  }

  @Test
  void testWhitsunHolidays() throws InvalidArgumentException {
    DaysToWorkingDaysReportConverter instance =
        DaysToWorkingDaysReportConverter.initialize(
            getLargeListOfColumnHeaders(), Instant.parse("2018-05-16T00:00:00.000Z"));

    assertEquals(0, instance.convertDaysToWorkingDays(0));
    assertEquals(1, instance.convertDaysToWorkingDays(1));
    assertEquals(2, instance.convertDaysToWorkingDays(2));
    assertEquals(2, instance.convertDaysToWorkingDays(3));
    assertEquals(2, instance.convertDaysToWorkingDays(4));
    assertEquals(2, instance.convertDaysToWorkingDays(5));
    assertEquals(3, instance.convertDaysToWorkingDays(6));
  }

  @Test
  void testLabourDayHoliday() throws InvalidArgumentException {
    DaysToWorkingDaysReportConverter instance =
        DaysToWorkingDaysReportConverter.initialize(
            getLargeListOfColumnHeaders(), Instant.parse("2018-04-26T00:00:00.000Z"));

    assertEquals(0, instance.convertDaysToWorkingDays(0));
    assertEquals(1, instance.convertDaysToWorkingDays(1));
    assertEquals(1, instance.convertDaysToWorkingDays(2));
    assertEquals(1, instance.convertDaysToWorkingDays(3));
    assertEquals(2, instance.convertDaysToWorkingDays(4));
    assertEquals(2, instance.convertDaysToWorkingDays(5));
    assertEquals(3, instance.convertDaysToWorkingDays(6));
    assertEquals(4, instance.convertDaysToWorkingDays(7));
  }

  @Test
  void testAscensionDayHoliday() throws InvalidArgumentException {
    DaysToWorkingDaysReportConverter instance =
        DaysToWorkingDaysReportConverter.initialize(
            getLargeListOfColumnHeaders(), Instant.parse("2018-05-07T00:00:00.000Z"));

    assertEquals(0, instance.convertDaysToWorkingDays(0));
    assertEquals(1, instance.convertDaysToWorkingDays(1));
    assertEquals(2, instance.convertDaysToWorkingDays(2));
    assertEquals(2, instance.convertDaysToWorkingDays(3));
    assertEquals(3, instance.convertDaysToWorkingDays(4));
    assertEquals(3, instance.convertDaysToWorkingDays(5));
    assertEquals(3, instance.convertDaysToWorkingDays(6));
    assertEquals(4, instance.convertDaysToWorkingDays(7));
  }

  @Test
  void testDayOfGermanUnityHoliday() throws InvalidArgumentException {
    DaysToWorkingDaysReportConverter instance =
        DaysToWorkingDaysReportConverter.initialize(
            getLargeListOfColumnHeaders(), Instant.parse("2018-10-01T00:00:00.000Z"));

    assertEquals(0, instance.convertDaysToWorkingDays(0));
    assertEquals(1, instance.convertDaysToWorkingDays(1));
    assertEquals(1, instance.convertDaysToWorkingDays(2));
    assertEquals(2, instance.convertDaysToWorkingDays(3));
    assertEquals(3, instance.convertDaysToWorkingDays(4));
    assertEquals(3, instance.convertDaysToWorkingDays(5));
    assertEquals(3, instance.convertDaysToWorkingDays(6));
    assertEquals(4, instance.convertDaysToWorkingDays(7));
  }

  @Test
  void testChristmasAndNewYearHolidays() throws InvalidArgumentException {
    DaysToWorkingDaysReportConverter instance =
        DaysToWorkingDaysReportConverter.initialize(
            getLargeListOfColumnHeaders(), Instant.parse("2018-12-20T00:00:00.000Z"));

    assertEquals(0, instance.convertDaysToWorkingDays(0));
    assertEquals(1, instance.convertDaysToWorkingDays(1));
    assertEquals(1, instance.convertDaysToWorkingDays(2));
    assertEquals(1, instance.convertDaysToWorkingDays(3));
    assertEquals(2, instance.convertDaysToWorkingDays(4));
    assertEquals(2, instance.convertDaysToWorkingDays(5));
    assertEquals(2, instance.convertDaysToWorkingDays(6));
    assertEquals(3, instance.convertDaysToWorkingDays(7));
    assertEquals(4, instance.convertDaysToWorkingDays(8));
    assertEquals(4, instance.convertDaysToWorkingDays(9));
    assertEquals(4, instance.convertDaysToWorkingDays(10));
    assertEquals(5, instance.convertDaysToWorkingDays(11));
    assertEquals(5, instance.convertDaysToWorkingDays(12));
    assertEquals(6, instance.convertDaysToWorkingDays(13));
    assertEquals(7, instance.convertDaysToWorkingDays(14));
  }

  @Test
  void testCustomHolidaysWithDayOfReformationAndAllSaintsDay() throws InvalidArgumentException {
    DaysToWorkingDaysReportConverter instance =
        DaysToWorkingDaysReportConverter.initialize(
            getLargeListOfColumnHeaders(), Instant.parse("2018-10-26T00:00:00.000Z"));

    assertEquals(0, instance.convertDaysToWorkingDays(0));
    assertEquals(0, instance.convertDaysToWorkingDays(1));
    assertEquals(0, instance.convertDaysToWorkingDays(2));
    assertEquals(1, instance.convertDaysToWorkingDays(3));
    assertEquals(2, instance.convertDaysToWorkingDays(4));
    assertEquals(2, instance.convertDaysToWorkingDays(5));
    assertEquals(2, instance.convertDaysToWorkingDays(6));
    assertEquals(3, instance.convertDaysToWorkingDays(7));
  }

  private List<TimeIntervalColumnHeader> getShortListOfColumnHeaders() {
    List<TimeIntervalColumnHeader> columnHeaders = new ArrayList<>();
    columnHeaders.add(new TimeIntervalColumnHeader(Integer.MIN_VALUE, -3));
    columnHeaders.add(new TimeIntervalColumnHeader(-1, -2));
    columnHeaders.add(new TimeIntervalColumnHeader(0));
    columnHeaders.add(new TimeIntervalColumnHeader(1, 2));
    columnHeaders.add(new TimeIntervalColumnHeader(3, Integer.MAX_VALUE));
    return columnHeaders;
  }

  private List<TimeIntervalColumnHeader> getLargeListOfColumnHeaders() {
    List<TimeIntervalColumnHeader> columnHeaders = new ArrayList<>();
    columnHeaders.add(new TimeIntervalColumnHeader(Integer.MIN_VALUE, -11));
    columnHeaders.add(new TimeIntervalColumnHeader(-10, -6));
    columnHeaders.add(new TimeIntervalColumnHeader(-5, -2));
    columnHeaders.add(new TimeIntervalColumnHeader(-1));
    columnHeaders.add(new TimeIntervalColumnHeader(0));
    columnHeaders.add(new TimeIntervalColumnHeader(1));
    columnHeaders.add(new TimeIntervalColumnHeader(2, 5));
    columnHeaders.add(new TimeIntervalColumnHeader(6, 10));
    columnHeaders.add(new TimeIntervalColumnHeader(11, Integer.MAX_VALUE));
    return columnHeaders;
  }
}
