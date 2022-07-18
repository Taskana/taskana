package acceptance.report;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import pro.taskana.common.api.CustomHoliday;
import pro.taskana.common.api.WorkingDaysToDaysConverter;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.internal.preprocessor.WorkingDaysToDaysReportConverter;

class WorkingDaysToDaysReportConverterTest {

  private final WorkingDaysToDaysConverter converter;

  public WorkingDaysToDaysReportConverterTest() {
    CustomHoliday dayOfReformation = CustomHoliday.of(31, 10);
    CustomHoliday allSaintsDays = CustomHoliday.of(1, 11);
    converter =
        new WorkingDaysToDaysConverter(true, false, List.of(dayOfReformation, allSaintsDays));
  }

  @Test
  void should_AssertNotEqual_When_InitializingDifferentDates() throws Exception {
    WorkingDaysToDaysReportConverter instance1 =
        WorkingDaysToDaysReportConverter.initialize(
            getShortListOfColumnHeaders(), converter, Instant.parse("2018-02-04T00:00:00.000Z"));
    WorkingDaysToDaysReportConverter instance2 =
        WorkingDaysToDaysReportConverter.initialize(
            getShortListOfColumnHeaders(), converter, Instant.parse("2018-02-05T00:00:00.000Z"));

    assertThat(instance1).isNotEqualTo(instance2);
  }

  @Test
  void should_ReturnWorkingDays_When_ConvertingDaysToWorkingDays() throws Exception {
    WorkingDaysToDaysReportConverter instance =
        WorkingDaysToDaysReportConverter.initialize(
            getLargeListOfColumnHeaders(), converter, Instant.parse("2018-02-06T00:00:00.000Z"));

    int oneBelowLimit = -16;
    int oneAboveLimit = 16;

    assertThat(instance.convertDaysToWorkingDays(oneBelowLimit)).isEqualTo(oneBelowLimit);

    assertThat(instance.convertDaysToWorkingDays(-15)).isEqualTo(-11);
    assertThat(instance.convertDaysToWorkingDays(-4)).isEqualTo(-2);
    assertThat(instance.convertDaysToWorkingDays(-3)).isEqualTo(-1);
    assertThat(instance.convertDaysToWorkingDays(-2)).isEqualTo(-1);
    assertThat(instance.convertDaysToWorkingDays(-1)).isEqualTo(-1);
    assertThat(instance.convertDaysToWorkingDays(0)).isZero();
    assertThat(instance.convertDaysToWorkingDays(1)).isEqualTo(1);
    assertThat(instance.convertDaysToWorkingDays(2)).isEqualTo(2);
    assertThat(instance.convertDaysToWorkingDays(3)).isEqualTo(3);
    assertThat(instance.convertDaysToWorkingDays(4)).isEqualTo(3);
    assertThat(instance.convertDaysToWorkingDays(5)).isEqualTo(3);
    assertThat(instance.convertDaysToWorkingDays(6)).isEqualTo(4);

    assertThat(instance.convertDaysToWorkingDays(15)).isEqualTo(11);
    assertThat(instance.convertDaysToWorkingDays(oneAboveLimit)).isEqualTo(oneAboveLimit);
  }

  @Test
  void should_ReturnWorkingDaysUnchanged_When_ConvertingWorkingDaysOutOfNegativeLimit()
      throws Exception {
    WorkingDaysToDaysReportConverter instance =
        WorkingDaysToDaysReportConverter.initialize(
            getLargeListOfColumnHeaders(), converter, Instant.parse("2018-02-06T00:00:00.000Z"));

    assertThat(instance.convertWorkingDaysToDays(-999)).containsExactlyInAnyOrder(-999);
  }

  @Test
  void should_ReturnWorkingDaysUnchanged_When_ConvertingWorkingDaysOutOfPositiveLimit()
      throws Exception {
    WorkingDaysToDaysReportConverter instance =
        WorkingDaysToDaysReportConverter.initialize(
            getLargeListOfColumnHeaders(), converter, Instant.parse("2018-02-06T00:00:00.000Z"));

    assertThat(instance.convertWorkingDaysToDays(999)).containsExactlyInAnyOrder(999);
  }

  @Test
  void should_ReturnAllMatchingDays_When_ConvertingWorkingDaysToDays() throws Exception {
    WorkingDaysToDaysReportConverter instance =
        WorkingDaysToDaysReportConverter.initialize(
            getLargeListOfColumnHeaders(), converter, Instant.parse("2018-02-27T00:00:00.000Z"));

    assertThat(instance.convertWorkingDaysToDays(-13)).containsExactlyInAnyOrder(-13);
    assertThat(instance.convertWorkingDaysToDays(-12)).containsExactlyInAnyOrder(-12);

    assertThat(instance.convertWorkingDaysToDays(-8)).containsExactlyInAnyOrder(-12);
    assertThat(instance.convertWorkingDaysToDays(-7)).containsExactlyInAnyOrder(-11);
    assertThat(instance.convertWorkingDaysToDays(-6)).containsExactlyInAnyOrder(-10, -9, -8);
    assertThat(instance.convertWorkingDaysToDays(-5)).containsExactlyInAnyOrder(-7);
    assertThat(instance.convertWorkingDaysToDays(-4)).containsExactlyInAnyOrder(-6);
    assertThat(instance.convertWorkingDaysToDays(-3)).containsExactlyInAnyOrder(-5);
    assertThat(instance.convertWorkingDaysToDays(-2)).containsExactlyInAnyOrder(-4);
    assertThat(instance.convertWorkingDaysToDays(-1)).containsExactlyInAnyOrder(-1, -2, -3);
    assertThat(instance.convertWorkingDaysToDays(0)).containsExactlyInAnyOrder(0);
    assertThat(instance.convertWorkingDaysToDays(1)).containsExactlyInAnyOrder(1);
    assertThat(instance.convertWorkingDaysToDays(2)).containsExactlyInAnyOrder(2);
    assertThat(instance.convertWorkingDaysToDays(3)).containsExactlyInAnyOrder(3, 4, 5);
    assertThat(instance.convertWorkingDaysToDays(4)).containsExactlyInAnyOrder(6);
    assertThat(instance.convertWorkingDaysToDays(5)).containsExactlyInAnyOrder(7);
    assertThat(instance.convertWorkingDaysToDays(6)).containsExactlyInAnyOrder(8);
    assertThat(instance.convertWorkingDaysToDays(7)).containsExactlyInAnyOrder(9);
    assertThat(instance.convertWorkingDaysToDays(8)).containsExactlyInAnyOrder(10, 11, 12);
    assertThat(instance.convertWorkingDaysToDays(9)).containsExactlyInAnyOrder(13);
    assertThat(instance.convertWorkingDaysToDays(10)).containsExactlyInAnyOrder(14);
    assertThat(instance.convertWorkingDaysToDays(11)).containsExactlyInAnyOrder(15);

    assertThat(instance.convertWorkingDaysToDays(12)).containsExactlyInAnyOrder(12);
    assertThat(instance.convertWorkingDaysToDays(13)).containsExactlyInAnyOrder(13);
  }

  @Test
  void should_ReturnAllMatchingDays_When_ConvertingWorkingDaysToDaysAtWeekend() throws Exception {
    WorkingDaysToDaysReportConverter instance =
        WorkingDaysToDaysReportConverter.initialize(
            getLargeListOfColumnHeaders(), converter, Instant.parse("2018-03-10T00:00:00.000Z"));

    assertThat(instance.convertWorkingDaysToDays(-13)).containsExactlyInAnyOrder(-13);
    assertThat(instance.convertWorkingDaysToDays(-12)).containsExactlyInAnyOrder(-12);

    assertThat(instance.convertWorkingDaysToDays(-8)).containsExactlyInAnyOrder(-10);
    assertThat(instance.convertWorkingDaysToDays(-7)).containsExactlyInAnyOrder(-9);
    assertThat(instance.convertWorkingDaysToDays(-6)).containsExactlyInAnyOrder(-8);
    assertThat(instance.convertWorkingDaysToDays(-5)).containsExactlyInAnyOrder(-5, -6, -7);
    assertThat(instance.convertWorkingDaysToDays(-4)).containsExactlyInAnyOrder(-4);
    assertThat(instance.convertWorkingDaysToDays(-3)).containsExactlyInAnyOrder(-3);
    assertThat(instance.convertWorkingDaysToDays(-2)).containsExactlyInAnyOrder(-2);
    assertThat(instance.convertWorkingDaysToDays(-1)).containsExactlyInAnyOrder(-1);
    assertThat(instance.convertWorkingDaysToDays(0)).containsExactlyInAnyOrder(0, 1);
    assertThat(instance.convertWorkingDaysToDays(1)).containsExactlyInAnyOrder(2);
    assertThat(instance.convertWorkingDaysToDays(2)).containsExactlyInAnyOrder(3);
    assertThat(instance.convertWorkingDaysToDays(3)).containsExactlyInAnyOrder(4);
    assertThat(instance.convertWorkingDaysToDays(4)).containsExactlyInAnyOrder(5);
    assertThat(instance.convertWorkingDaysToDays(5)).containsExactlyInAnyOrder(6, 7, 8);
    assertThat(instance.convertWorkingDaysToDays(6)).containsExactlyInAnyOrder(9);
    assertThat(instance.convertWorkingDaysToDays(7)).containsExactlyInAnyOrder(10);
    assertThat(instance.convertWorkingDaysToDays(8)).containsExactlyInAnyOrder(11);
    assertThat(instance.convertWorkingDaysToDays(9)).containsExactlyInAnyOrder(12);
    assertThat(instance.convertWorkingDaysToDays(10)).containsExactlyInAnyOrder(13, 14, 15);
    assertThat(instance.convertWorkingDaysToDays(11)).containsExactlyInAnyOrder(16);

    assertThat(instance.convertWorkingDaysToDays(12)).containsExactlyInAnyOrder(12);
    assertThat(instance.convertWorkingDaysToDays(13)).containsExactlyInAnyOrder(13);
  }

  @Test
  void should_ReturnAllMatchingDays_When_ConvertingWorkingDaysToDaysOnEasterSunday()
      throws Exception {
    WorkingDaysToDaysReportConverter instance =
        WorkingDaysToDaysReportConverter.initialize(
            getLargeListOfColumnHeaders(), converter, Instant.parse("2018-04-01T00:00:00.000Z"));

    assertThat(instance.convertWorkingDaysToDays(-13)).containsExactlyInAnyOrder(-13);
    assertThat(instance.convertWorkingDaysToDays(-12)).containsExactlyInAnyOrder(-12);

    assertThat(instance.convertWorkingDaysToDays(-8)).containsExactlyInAnyOrder(-12);
    assertThat(instance.convertWorkingDaysToDays(-7)).containsExactlyInAnyOrder(-11);
    assertThat(instance.convertWorkingDaysToDays(-6)).containsExactlyInAnyOrder(-10);
    assertThat(instance.convertWorkingDaysToDays(-5)).containsExactlyInAnyOrder(-9);
    assertThat(instance.convertWorkingDaysToDays(-4)).containsExactlyInAnyOrder(-6, -7, -8);
    assertThat(instance.convertWorkingDaysToDays(-3)).containsExactlyInAnyOrder(-5);
    assertThat(instance.convertWorkingDaysToDays(-2)).containsExactlyInAnyOrder(-4);
    assertThat(instance.convertWorkingDaysToDays(-1)).containsExactlyInAnyOrder(-3);
    assertThat(instance.convertWorkingDaysToDays(0)).containsExactlyInAnyOrder(0, 1, -1, -2);
    assertThat(instance.convertWorkingDaysToDays(1)).containsExactlyInAnyOrder(2);
    assertThat(instance.convertWorkingDaysToDays(2)).containsExactlyInAnyOrder(3);
    assertThat(instance.convertWorkingDaysToDays(3)).containsExactlyInAnyOrder(4);
    assertThat(instance.convertWorkingDaysToDays(4)).containsExactlyInAnyOrder(5, 6, 7);
    assertThat(instance.convertWorkingDaysToDays(5)).containsExactlyInAnyOrder(8);
    assertThat(instance.convertWorkingDaysToDays(6)).containsExactlyInAnyOrder(9);
    assertThat(instance.convertWorkingDaysToDays(7)).containsExactlyInAnyOrder(10);
    assertThat(instance.convertWorkingDaysToDays(8)).containsExactlyInAnyOrder(11);
    assertThat(instance.convertWorkingDaysToDays(9)).containsExactlyInAnyOrder(12, 13, 14);
    assertThat(instance.convertWorkingDaysToDays(10)).containsExactlyInAnyOrder(15);
    assertThat(instance.convertWorkingDaysToDays(11)).containsExactlyInAnyOrder(16);

    assertThat(instance.convertWorkingDaysToDays(12)).containsExactlyInAnyOrder(12);
    assertThat(instance.convertWorkingDaysToDays(13)).containsExactlyInAnyOrder(13);
  }

  @Test
  void should_ReturnWorkingDays_When_ConvertingDaysToWorkingDaysOnEasterHolidays()
      throws Exception {
    WorkingDaysToDaysReportConverter instance =
        WorkingDaysToDaysReportConverter.initialize(
            getLargeListOfColumnHeaders(), converter, Instant.parse("2018-03-28T00:00:00.000Z"));

    assertThat(instance.convertDaysToWorkingDays(0)).isZero();
    assertThat(instance.convertDaysToWorkingDays(1)).isEqualTo(1);
    assertThat(instance.convertDaysToWorkingDays(2)).isEqualTo(1);
    assertThat(instance.convertDaysToWorkingDays(3)).isEqualTo(1);
    assertThat(instance.convertDaysToWorkingDays(4)).isEqualTo(1);
    assertThat(instance.convertDaysToWorkingDays(5)).isEqualTo(1);
    assertThat(instance.convertDaysToWorkingDays(6)).isEqualTo(2);
  }

  @Test
  void should_ReturnWorkingDays_When_ConvertingDaysToWorkingDaysOnWhitsunHolidays()
      throws Exception {
    WorkingDaysToDaysReportConverter instance =
        WorkingDaysToDaysReportConverter.initialize(
            getLargeListOfColumnHeaders(), converter, Instant.parse("2018-05-16T00:00:00.000Z"));

    assertThat(instance.convertDaysToWorkingDays(0)).isZero();
    assertThat(instance.convertDaysToWorkingDays(1)).isEqualTo(1);
    assertThat(instance.convertDaysToWorkingDays(2)).isEqualTo(2);
    assertThat(instance.convertDaysToWorkingDays(3)).isEqualTo(2);
    assertThat(instance.convertDaysToWorkingDays(4)).isEqualTo(2);
    assertThat(instance.convertDaysToWorkingDays(5)).isEqualTo(2);
    assertThat(instance.convertDaysToWorkingDays(6)).isEqualTo(3);
  }

  @Test
  void should_ReturnWorkingDays_When_ConvertingDaysToWorkingDaysOnLabourDay() throws Exception {
    WorkingDaysToDaysReportConverter instance =
        WorkingDaysToDaysReportConverter.initialize(
            getLargeListOfColumnHeaders(), converter, Instant.parse("2018-04-26T00:00:00.000Z"));

    assertThat(instance.convertDaysToWorkingDays(0)).isZero();
    assertThat(instance.convertDaysToWorkingDays(1)).isEqualTo(1);
    assertThat(instance.convertDaysToWorkingDays(2)).isEqualTo(1);
    assertThat(instance.convertDaysToWorkingDays(3)).isEqualTo(1);
    assertThat(instance.convertDaysToWorkingDays(4)).isEqualTo(2);
    assertThat(instance.convertDaysToWorkingDays(5)).isEqualTo(2);
    assertThat(instance.convertDaysToWorkingDays(6)).isEqualTo(3);
    assertThat(instance.convertDaysToWorkingDays(7)).isEqualTo(4);
  }

  @Test
  void should_ReturnWorkingDays_When_ConvertingDaysToWorkingDaysOnAscensionDay() throws Exception {
    WorkingDaysToDaysReportConverter instance =
        WorkingDaysToDaysReportConverter.initialize(
            getLargeListOfColumnHeaders(), converter, Instant.parse("2018-05-07T00:00:00.000Z"));

    assertThat(instance.convertDaysToWorkingDays(0)).isZero();
    assertThat(instance.convertDaysToWorkingDays(1)).isEqualTo(1);
    assertThat(instance.convertDaysToWorkingDays(2)).isEqualTo(2);
    assertThat(instance.convertDaysToWorkingDays(3)).isEqualTo(2);
    assertThat(instance.convertDaysToWorkingDays(4)).isEqualTo(3);
    assertThat(instance.convertDaysToWorkingDays(5)).isEqualTo(3);
    assertThat(instance.convertDaysToWorkingDays(6)).isEqualTo(3);
    assertThat(instance.convertDaysToWorkingDays(7)).isEqualTo(4);
  }

  @Test
  void should_ReturnWorkingDays_When_ConvertingDaysToWorkingDaysOnDayOfGermanUnity()
      throws Exception {
    WorkingDaysToDaysReportConverter instance =
        WorkingDaysToDaysReportConverter.initialize(
            getLargeListOfColumnHeaders(), converter, Instant.parse("2018-10-01T00:00:00.000Z"));

    assertThat(instance.convertDaysToWorkingDays(0)).isZero();
    assertThat(instance.convertDaysToWorkingDays(1)).isEqualTo(1);
    assertThat(instance.convertDaysToWorkingDays(2)).isEqualTo(1);
    assertThat(instance.convertDaysToWorkingDays(3)).isEqualTo(2);
    assertThat(instance.convertDaysToWorkingDays(4)).isEqualTo(3);
    assertThat(instance.convertDaysToWorkingDays(5)).isEqualTo(3);
    assertThat(instance.convertDaysToWorkingDays(6)).isEqualTo(3);
    assertThat(instance.convertDaysToWorkingDays(7)).isEqualTo(4);
  }

  @Test
  void should_ReturnWorkingDays_When_ConvertingDaysToWorkingDaysOnChristmasAndNewYearHolidays()
      throws Exception {
    WorkingDaysToDaysReportConverter instance =
        WorkingDaysToDaysReportConverter.initialize(
            getLargeListOfColumnHeaders(), converter, Instant.parse("2018-12-20T00:00:00.000Z"));

    assertThat(instance.convertDaysToWorkingDays(0)).isZero();
    assertThat(instance.convertDaysToWorkingDays(1)).isEqualTo(1);
    assertThat(instance.convertDaysToWorkingDays(2)).isEqualTo(1);
    assertThat(instance.convertDaysToWorkingDays(3)).isEqualTo(1);
    assertThat(instance.convertDaysToWorkingDays(4)).isEqualTo(2);
    assertThat(instance.convertDaysToWorkingDays(5)).isEqualTo(2);
    assertThat(instance.convertDaysToWorkingDays(6)).isEqualTo(2);
    assertThat(instance.convertDaysToWorkingDays(7)).isEqualTo(3);
    assertThat(instance.convertDaysToWorkingDays(8)).isEqualTo(4);
    assertThat(instance.convertDaysToWorkingDays(9)).isEqualTo(4);
    assertThat(instance.convertDaysToWorkingDays(10)).isEqualTo(4);
    assertThat(instance.convertDaysToWorkingDays(11)).isEqualTo(5);
    assertThat(instance.convertDaysToWorkingDays(12)).isEqualTo(5);
    assertThat(instance.convertDaysToWorkingDays(13)).isEqualTo(6);
    assertThat(instance.convertDaysToWorkingDays(14)).isEqualTo(7);
  }

  @Test
  void should_ReturnWorkingDays_When_ConvertingDaysToWorkingDaysOnCustomHoliday() throws Exception {
    WorkingDaysToDaysReportConverter instance =
        WorkingDaysToDaysReportConverter.initialize(
            getLargeListOfColumnHeaders(), converter, Instant.parse("2018-10-26T00:00:00.000Z"));

    assertThat(instance.convertDaysToWorkingDays(0)).isZero();
    assertThat(instance.convertDaysToWorkingDays(1)).isZero();
    assertThat(instance.convertDaysToWorkingDays(2)).isZero();
    assertThat(instance.convertDaysToWorkingDays(3)).isEqualTo(1);
    assertThat(instance.convertDaysToWorkingDays(4)).isEqualTo(2);
    assertThat(instance.convertDaysToWorkingDays(5)).isEqualTo(2);
    assertThat(instance.convertDaysToWorkingDays(6)).isEqualTo(2);
    assertThat(instance.convertDaysToWorkingDays(7)).isEqualTo(3);
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
