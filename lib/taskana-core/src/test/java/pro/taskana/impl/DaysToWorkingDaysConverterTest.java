package pro.taskana.impl;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static pro.taskana.impl.DaysToWorkingDaysConverter.getEasterSunday;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.impl.report.header.TimeIntervalColumnHeader;

/**
 * Test for the DaysToWorkingDaysConverter.
 */
class DaysToWorkingDaysConverterTest {

    @BeforeAll
    static void setup() {
        DaysToWorkingDaysConverter.setGermanPublicHolidaysEnabled(true);
        LocalDate dayOfReformation = LocalDate.of(2018, 10, 31);
        LocalDate allSaintsDays = LocalDate.of(2018, 11, 1);
        DaysToWorkingDaysConverter.setCustomHolidays(Arrays.asList(dayOfReformation, allSaintsDays));
    }

    @Test
    void testInitializeForDifferentReportLineItemDefinitions() throws InvalidArgumentException {
        DaysToWorkingDaysConverter instance1 = DaysToWorkingDaysConverter
            .initialize(getShortListOfColumnHeaders(), Instant.parse("2018-02-03T00:00:00.000Z"));
        DaysToWorkingDaysConverter instance2 = DaysToWorkingDaysConverter
            .initialize(getShortListOfColumnHeaders(), Instant.parse("2018-02-03T00:00:00.000Z"));
        DaysToWorkingDaysConverter instance3 = DaysToWorkingDaysConverter
            .initialize(getLargeListOfColumnHeaders(), Instant.parse("2018-02-03T00:00:00.000Z"));

        assertEquals(instance1, instance2);
        assertNotEquals(instance1, instance3);
    }

    @Test
    void testConvertWorkingDaysToDaysForTasks() throws InvalidArgumentException {
        List<TimeIntervalColumnHeader> reportItems = singletonList(new TimeIntervalColumnHeader(0));
        Instant thursday0201 = Instant.parse("2018-02-01T07:00:00.000Z");
        DaysToWorkingDaysConverter converter = DaysToWorkingDaysConverter.initialize(reportItems, thursday0201);

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
        List<TimeIntervalColumnHeader> reportItems = singletonList(new TimeIntervalColumnHeader(0));
        Instant thursday0201 = Instant.parse("2018-02-01T07:00:00.000Z");
        DaysToWorkingDaysConverter converter = DaysToWorkingDaysConverter.initialize(reportItems, thursday0201);
        Instant gruenDonnerstag2018 = Instant.parse("2018-03-29T01:00:00.000Z");
        long days = converter.convertWorkingDaysToDays(gruenDonnerstag2018, 0);
        assertEquals(0, days);
        days = converter.convertWorkingDaysToDays(gruenDonnerstag2018, 1); // Karfreitag
        assertEquals(5, days);  // osterdienstag
        days = converter.convertWorkingDaysToDays(gruenDonnerstag2018, 2); // Karfreitag
        assertEquals(6, days);  // ostermittwoch
    }

    @Test
    void testConvertWorkingDaysToDaysForHolidays() throws InvalidArgumentException {
        List<TimeIntervalColumnHeader> reportItems = singletonList(new TimeIntervalColumnHeader(0));
        Instant thursday0201 = Instant.parse("2018-02-01T07:00:00.000Z");
        DaysToWorkingDaysConverter converter = DaysToWorkingDaysConverter.initialize(reportItems, thursday0201);

        Instant freitag0427 = Instant.parse("2018-04-27T19:00:00.000Z");
        long days = converter.convertWorkingDaysToDays(freitag0427, 0);
        assertEquals(0, days);
        days = converter.convertWorkingDaysToDays(freitag0427, 1);
        assertEquals(3, days); // 30.4.
        days = converter.convertWorkingDaysToDays(freitag0427, 2);
        assertEquals(5, days); // 2.5.
    }

    @Test
    void testInitializeForDifferentDates() throws InvalidArgumentException {
        DaysToWorkingDaysConverter instance1 = DaysToWorkingDaysConverter
            .initialize(getShortListOfColumnHeaders(), Instant.parse("2018-02-04T00:00:00.000Z"));
        DaysToWorkingDaysConverter instance2 = DaysToWorkingDaysConverter
            .initialize(getShortListOfColumnHeaders(), Instant.parse("2018-02-05T00:00:00.000Z"));

        assertNotEquals(instance1, instance2);
    }

    @Test
    void testConvertDaysToWorkingDays() throws InvalidArgumentException {
        DaysToWorkingDaysConverter instance = DaysToWorkingDaysConverter
            .initialize(getLargeListOfColumnHeaders(), Instant.parse("2018-02-06T00:00:00.000Z"));

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
        DaysToWorkingDaysConverter instance = DaysToWorkingDaysConverter
            .initialize(getLargeListOfColumnHeaders(), Instant.parse("2018-02-27T00:00:00.000Z"));

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
        DaysToWorkingDaysConverter instance = DaysToWorkingDaysConverter
            .initialize(getLargeListOfColumnHeaders(), Instant.parse("2018-03-10T00:00:00.000Z"));

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
        DaysToWorkingDaysConverter instance = DaysToWorkingDaysConverter
            .initialize(getLargeListOfColumnHeaders(), Instant.parse("2018-04-01T00:00:00.000Z"));

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
        DaysToWorkingDaysConverter instance = DaysToWorkingDaysConverter
            .initialize(getLargeListOfColumnHeaders(), Instant.parse("2018-03-28T00:00:00.000Z"));

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
        DaysToWorkingDaysConverter instance = DaysToWorkingDaysConverter
            .initialize(getLargeListOfColumnHeaders(), Instant.parse("2018-05-16T00:00:00.000Z"));

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
        DaysToWorkingDaysConverter instance = DaysToWorkingDaysConverter
            .initialize(getLargeListOfColumnHeaders(), Instant.parse("2018-04-26T00:00:00.000Z"));

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
        DaysToWorkingDaysConverter instance = DaysToWorkingDaysConverter
            .initialize(getLargeListOfColumnHeaders(), Instant.parse("2018-05-07T00:00:00.000Z"));

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
        DaysToWorkingDaysConverter instance = DaysToWorkingDaysConverter
            .initialize(getLargeListOfColumnHeaders(), Instant.parse("2018-10-01T00:00:00.000Z"));

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
        DaysToWorkingDaysConverter instance = DaysToWorkingDaysConverter
            .initialize(getLargeListOfColumnHeaders(), Instant.parse("2018-12-20T00:00:00.000Z"));

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
        DaysToWorkingDaysConverter instance = DaysToWorkingDaysConverter
            .initialize(getLargeListOfColumnHeaders(), Instant.parse("2018-10-26T00:00:00.000Z"));

        assertEquals(0, instance.convertDaysToWorkingDays(0));
        assertEquals(0, instance.convertDaysToWorkingDays(1));
        assertEquals(0, instance.convertDaysToWorkingDays(2));
        assertEquals(1, instance.convertDaysToWorkingDays(3));
        assertEquals(2, instance.convertDaysToWorkingDays(4));
        assertEquals(2, instance.convertDaysToWorkingDays(5));
        assertEquals(2, instance.convertDaysToWorkingDays(6));
        assertEquals(3, instance.convertDaysToWorkingDays(7));

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
