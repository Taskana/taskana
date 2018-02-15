package pro.taskana.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Test for the DaysToWorkingDaysConverter.
 */
public class DaysToWorkingDaysConverterTest {

    @Test
    public void testInitializeForDifferentReportLineItemDefinitions() {
        DaysToWorkingDaysConverter instance1 = DaysToWorkingDaysConverter
            .initialize(getShortListOfReportLineItemDefinitions(), LocalDate.of(2018, 02, 03));
        DaysToWorkingDaysConverter instance2 = DaysToWorkingDaysConverter
            .initialize(getShortListOfReportLineItemDefinitions(), LocalDate.of(2018, 02, 03));
        DaysToWorkingDaysConverter instance3 = DaysToWorkingDaysConverter
            .initialize(getLargeListOfReportLineItemDefinitions(), LocalDate.of(2018, 02, 03));

        assertEquals(instance1, instance2);
        assertNotEquals(instance1, instance3);
    }

    @Test
    public void testInitializeForDifferentDates() {
        DaysToWorkingDaysConverter instance1 = DaysToWorkingDaysConverter
            .initialize(getShortListOfReportLineItemDefinitions(), LocalDate.of(2018, 02, 04));
        DaysToWorkingDaysConverter instance2 = DaysToWorkingDaysConverter
            .initialize(getShortListOfReportLineItemDefinitions(), LocalDate.of(2018, 02, 05));

        assertNotEquals(instance1, instance2);
    }

    @Test
    public void testConvertDaysToWorkingDays() {
        DaysToWorkingDaysConverter instance = DaysToWorkingDaysConverter
            .initialize(getLargeListOfReportLineItemDefinitions(), LocalDate.of(2018, 02, 06));

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
