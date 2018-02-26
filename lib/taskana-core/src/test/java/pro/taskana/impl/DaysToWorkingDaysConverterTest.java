package pro.taskana.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for the DaysToWorkingDaysConverter.
 */
public class DaysToWorkingDaysConverterTest {

    @BeforeClass
    public static void setup() {
        DaysToWorkingDaysConverter.setGermanPublicHolidaysEnabled(true);
        LocalDate dayOfReformation = LocalDate.of(2018, 10, 31);
        LocalDate allSaintsDays = LocalDate.of(2018, 11, 1);
        DaysToWorkingDaysConverter.setCustomHolidays(Arrays.asList(dayOfReformation, allSaintsDays));
    }

    @Test
    public void testInitializeForDifferentReportLineItemDefinitions() {
        DaysToWorkingDaysConverter instance1 = DaysToWorkingDaysConverter
            .initialize(getShortListOfReportLineItemDefinitions(), Instant.parse("2018-02-03T00:00:00.000Z"));
        DaysToWorkingDaysConverter instance2 = DaysToWorkingDaysConverter
            .initialize(getShortListOfReportLineItemDefinitions(), Instant.parse("2018-02-03T00:00:00.000Z"));
        DaysToWorkingDaysConverter instance3 = DaysToWorkingDaysConverter
            .initialize(getLargeListOfReportLineItemDefinitions(), Instant.parse("2018-02-03T00:00:00.000Z"));

        assertEquals(instance1, instance2);
        assertNotEquals(instance1, instance3);
    }

    @Test
    public void testInitializeForDifferentDates() {
        DaysToWorkingDaysConverter instance1 = DaysToWorkingDaysConverter
            .initialize(getShortListOfReportLineItemDefinitions(), Instant.parse("2018-02-04T00:00:00.000Z"));
        DaysToWorkingDaysConverter instance2 = DaysToWorkingDaysConverter
            .initialize(getShortListOfReportLineItemDefinitions(), Instant.parse("2018-02-05T00:00:00.000Z"));

        assertNotEquals(instance1, instance2);
    }

    @Test
    public void testConvertDaysToWorkingDays() {
        DaysToWorkingDaysConverter instance = DaysToWorkingDaysConverter
            .initialize(getLargeListOfReportLineItemDefinitions(), Instant.parse("2018-02-06T00:00:00.000Z"));

        assertEquals(16, instance.convertDaysToWorkingDays(16));
        assertEquals(11, instance.convertDaysToWorkingDays(15));

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
    public void testEasterHolidays() {
        DaysToWorkingDaysConverter instance = DaysToWorkingDaysConverter
            .initialize(getLargeListOfReportLineItemDefinitions(), Instant.parse("2018-03-28T00:00:00.000Z"));

        assertEquals(0, instance.convertDaysToWorkingDays(0));
        assertEquals(1, instance.convertDaysToWorkingDays(1));
        assertEquals(1, instance.convertDaysToWorkingDays(2));
        assertEquals(1, instance.convertDaysToWorkingDays(3));
        assertEquals(1, instance.convertDaysToWorkingDays(4));
        assertEquals(1, instance.convertDaysToWorkingDays(5));
        assertEquals(2, instance.convertDaysToWorkingDays(6));
    }

    @Test
    public void testWhitsunHolidays() {
        DaysToWorkingDaysConverter instance = DaysToWorkingDaysConverter
            .initialize(getLargeListOfReportLineItemDefinitions(), Instant.parse("2018-05-16T00:00:00.000Z"));

        assertEquals(0, instance.convertDaysToWorkingDays(0));
        assertEquals(1, instance.convertDaysToWorkingDays(1));
        assertEquals(2, instance.convertDaysToWorkingDays(2));
        assertEquals(2, instance.convertDaysToWorkingDays(3));
        assertEquals(2, instance.convertDaysToWorkingDays(4));
        assertEquals(2, instance.convertDaysToWorkingDays(5));
        assertEquals(3, instance.convertDaysToWorkingDays(6));
    }

    @Test
    public void testLabourDayHoliday() {
        DaysToWorkingDaysConverter instance = DaysToWorkingDaysConverter
            .initialize(getLargeListOfReportLineItemDefinitions(), Instant.parse("2018-04-26T00:00:00.000Z"));

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
    public void testAscensionDayHoliday() {
        DaysToWorkingDaysConverter instance = DaysToWorkingDaysConverter
            .initialize(getLargeListOfReportLineItemDefinitions(), Instant.parse("2018-05-07T00:00:00.000Z"));

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
    public void testDayOfGermanUnityHoliday() {
        DaysToWorkingDaysConverter instance = DaysToWorkingDaysConverter
            .initialize(getLargeListOfReportLineItemDefinitions(), Instant.parse("2018-10-01T00:00:00.000Z"));

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
    public void testChristmasAndNewYearHolidays() {
        DaysToWorkingDaysConverter instance = DaysToWorkingDaysConverter
            .initialize(getLargeListOfReportLineItemDefinitions(), Instant.parse("2018-12-20T00:00:00.000Z"));

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
    public void testCustomHolidaysWithDayOfReformationAndAllSaintsDay() {
        DaysToWorkingDaysConverter instance = DaysToWorkingDaysConverter
            .initialize(getLargeListOfReportLineItemDefinitions(), Instant.parse("2018-10-26T00:00:00.000Z"));

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
    public void testgetEasterSunday() {
        DaysToWorkingDaysConverter instance = DaysToWorkingDaysConverter
            .initialize(getShortListOfReportLineItemDefinitions(), Instant.parse("2018-02-27T00:00:00.000Z"));

        assertEquals(LocalDate.of(2018, 4, 1), instance.getEasterSunday(2018));
        assertEquals(LocalDate.of(2019, 4, 21), instance.getEasterSunday(2019));
        assertEquals(LocalDate.of(2020, 4, 12), instance.getEasterSunday(2020));
        assertEquals(LocalDate.of(2021, 4, 4), instance.getEasterSunday(2021));
        assertEquals(LocalDate.of(2022, 4, 17), instance.getEasterSunday(2022));
        assertEquals(LocalDate.of(2023, 4, 9), instance.getEasterSunday(2023));
        assertEquals(LocalDate.of(2024, 3, 31), instance.getEasterSunday(2024));
        assertEquals(LocalDate.of(2025, 4, 20), instance.getEasterSunday(2025));
        assertEquals(LocalDate.of(2026, 4, 5), instance.getEasterSunday(2026));
        assertEquals(LocalDate.of(2027, 3, 28), instance.getEasterSunday(2027));
        assertEquals(LocalDate.of(2028, 4, 16), instance.getEasterSunday(2028));
        assertEquals(LocalDate.of(2029, 4, 1), instance.getEasterSunday(2029));
        assertEquals(LocalDate.of(2030, 4, 21), instance.getEasterSunday(2030));
        assertEquals(LocalDate.of(2031, 4, 13), instance.getEasterSunday(2031));
        assertEquals(LocalDate.of(2032, 3, 28), instance.getEasterSunday(2032));
        assertEquals(LocalDate.of(2033, 4, 17), instance.getEasterSunday(2033));
        assertEquals(LocalDate.of(2034, 4, 9), instance.getEasterSunday(2034));
        assertEquals(LocalDate.of(2035, 3, 25), instance.getEasterSunday(2035));
        assertEquals(LocalDate.of(2040, 4, 1), instance.getEasterSunday(2040));
        assertEquals(LocalDate.of(2050, 4, 10), instance.getEasterSunday(2050));
        assertEquals(LocalDate.of(2100, 3, 28), instance.getEasterSunday(2100));

    }

    private List<ReportLineItemDefinition> getShortListOfReportLineItemDefinitions() {
        List<ReportLineItemDefinition> reportLineItemDefinitions = new ArrayList<>();
        reportLineItemDefinitions.add(new ReportLineItemDefinition(Integer.MIN_VALUE, -3));
        reportLineItemDefinitions.add(new ReportLineItemDefinition(-1, -2));
        reportLineItemDefinitions.add(new ReportLineItemDefinition(0));
        reportLineItemDefinitions.add(new ReportLineItemDefinition(1, 2));
        reportLineItemDefinitions.add(new ReportLineItemDefinition(3, Integer.MAX_VALUE));
        return reportLineItemDefinitions;
    }

    private List<ReportLineItemDefinition> getLargeListOfReportLineItemDefinitions() {
        List<ReportLineItemDefinition> reportLineItemDefinitions = new ArrayList<>();
        reportLineItemDefinitions.add(new ReportLineItemDefinition(Integer.MIN_VALUE, -11));
        reportLineItemDefinitions.add(new ReportLineItemDefinition(-10, -6));
        reportLineItemDefinitions.add(new ReportLineItemDefinition(-5, -2));
        reportLineItemDefinitions.add(new ReportLineItemDefinition(-1));
        reportLineItemDefinitions.add(new ReportLineItemDefinition(0));
        reportLineItemDefinitions.add(new ReportLineItemDefinition(1));
        reportLineItemDefinitions.add(new ReportLineItemDefinition(2, 5));
        reportLineItemDefinitions.add(new ReportLineItemDefinition(6, 10));
        reportLineItemDefinitions.add(new ReportLineItemDefinition(11, Integer.MAX_VALUE));
        return reportLineItemDefinitions;
    }
}
