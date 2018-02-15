package pro.taskana.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.impl.util.LoggerUtils;

/**
 * The DaysToWorkingDaysConverter provides a method to convert an age in days into an age in working days. Before the
 * method convertDaysToWorkingDays() can be used, the DaysToWorkingDaysConverter has to be initialized. For a list of
 * {@link ReportLineItemDefinition}s the converter creates a "table" with integer that represents the age in days from
 * the largest lower limit until the smallest upper limit of the reportLineItemDefinitions. This table is valid for a
 * whole day until the converter is initialized with bigger limits.
 */
public final class DaysToWorkingDaysConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskMonitorServiceImpl.class);
    private static DaysToWorkingDaysConverter instance;
    private static ArrayList<Integer> positiveDaysToWorkingDays;
    private static ArrayList<Integer> negativeDaysToWorkingDays;
    private static LocalDate dateCreated;

    private DaysToWorkingDaysConverter(List<ReportLineItemDefinition> reportLineItemDefinitions,
        LocalDate referenceDate) {
        positiveDaysToWorkingDays = generatePositiveDaysToWorkingDays(reportLineItemDefinitions, referenceDate);
        negativeDaysToWorkingDays = generateNegativeDaysToWorkingDays(reportLineItemDefinitions, referenceDate);
        dateCreated = referenceDate;
    }

    /**
     * Initializes the DaysToWorkingDaysConverter for a list of {@link ReportLineItemDefinition}s and the current day. A
     * new table is only created if there are bigger limits or the date has changed.
     *
     * @param reportLineItemDefinitions
     *            a list of {@link ReportLineItemDefinition}s that determines the size of the table
     * @return an instance of the DaysToWorkingDaysConverter
     */
    public static DaysToWorkingDaysConverter initialize(List<ReportLineItemDefinition> reportLineItemDefinitions) {
        return initialize(reportLineItemDefinitions, LocalDate.now());
    }

    /**
     * Initializes the DaysToWorkingDaysConverter for a list of {@link ReportLineItemDefinition}s and a referenceDate. A
     * new table is only created if there are bigger limits or the date has changed.
     *
     * @param reportLineItemDefinitions
     *            a list of {@link ReportLineItemDefinition}s that determines the size of the table
     * @param referenceDate
     *            a {@link LocalDate} that represents the current day of the table
     * @return an instance of the DaysToWorkingDaysConverter
     */
    public static DaysToWorkingDaysConverter initialize(List<ReportLineItemDefinition> reportLineItemDefinitions,
        LocalDate referenceDate) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialize DaysToWorkingDaysConverter with reportLineItemDefinitions: {}",
                LoggerUtils.listToString(reportLineItemDefinitions));
        }
        int largesLowerLimit = getLargestLowerLimit(reportLineItemDefinitions);
        int smallestUpperLimit = getSmallestUpperLimit(reportLineItemDefinitions);
        if (instance == null
            || !positiveDaysToWorkingDays.contains(largesLowerLimit)
            || !negativeDaysToWorkingDays.contains(smallestUpperLimit)
            || !dateCreated.isEqual(referenceDate)) {

            instance = new DaysToWorkingDaysConverter(reportLineItemDefinitions, referenceDate);
            LOGGER.debug("Create new converter for the values from {} until {} for the date: {}.", largesLowerLimit,
                smallestUpperLimit, dateCreated);
        }
        return instance;
    }

    /**
     * Converts an integer, that represents the age in days, to the age in working days by using the table that was
     * created by initialization. If the age in days is beyond the limits of the table, the integer will be returned
     * unchanged.
     *
     * @param ageInDays
     *            represents the age in days
     * @return the age in working days
     */
    public int convertDaysToWorkingDays(int ageInDays) {

        int minWorkingDay = -(negativeDaysToWorkingDays.size() - 1);
        int maxWorkingDay = positiveDaysToWorkingDays.size() - 1;

        if (ageInDays >= minWorkingDay && ageInDays <= 0) {
            return negativeDaysToWorkingDays.get(-ageInDays);
        }
        if (ageInDays > 0 && ageInDays <= maxWorkingDay) {
            return positiveDaysToWorkingDays.get(ageInDays);
        }

        return ageInDays;
    }

    private ArrayList<Integer> generateNegativeDaysToWorkingDays(
        List<ReportLineItemDefinition> reportLineItemDefinitions, LocalDate referenceDate) {
        int minUpperLimit = getSmallestUpperLimit(reportLineItemDefinitions);
        ArrayList<Integer> daysToWorkingDays = new ArrayList<>();
        daysToWorkingDays.add(0);
        int day = -1;
        int workingDay = 0;
        while (workingDay > minUpperLimit) {
            if (isWorkingDay(day, referenceDate)) {
                workingDay--;
            }
            daysToWorkingDays.add(workingDay);
            day--;
        }
        return daysToWorkingDays;
    }

    private ArrayList<Integer> generatePositiveDaysToWorkingDays(
        List<ReportLineItemDefinition> reportLineItemDefinitions, LocalDate referenceDate) {
        int maxLowerLimit = getLargestLowerLimit(reportLineItemDefinitions);
        ArrayList<Integer> daysToWorkingDays = new ArrayList<>();
        daysToWorkingDays.add(0);

        int day = 1;
        int workingDay = 0;
        while (workingDay < maxLowerLimit) {
            if (isWorkingDay(day, referenceDate)) {
                workingDay++;
            }
            daysToWorkingDays.add(workingDay);
            day++;
        }
        return daysToWorkingDays;
    }

    private static int getSmallestUpperLimit(List<ReportLineItemDefinition> reportLineItemDefinitions) {
        int smallestUpperLimit = 0;
        for (ReportLineItemDefinition reportLineItemDefinition : reportLineItemDefinitions) {
            if (reportLineItemDefinition.getUpperAgeLimit() < smallestUpperLimit) {
                smallestUpperLimit = reportLineItemDefinition.getUpperAgeLimit();
            }
        }
        return smallestUpperLimit;
    }

    private static int getLargestLowerLimit(List<ReportLineItemDefinition> reportLineItemDefinitions) {
        int greatestLowerLimit = 0;
        for (ReportLineItemDefinition reportLineItemDefinition : reportLineItemDefinitions) {
            if (reportLineItemDefinition.getUpperAgeLimit() > greatestLowerLimit) {
                greatestLowerLimit = reportLineItemDefinition.getLowerAgeLimit();
            }
        }
        return greatestLowerLimit;
    }

    private boolean isWorkingDay(int day, LocalDate referenceDate) {
        if (referenceDate.plusDays(day).getDayOfWeek().equals(DayOfWeek.SATURDAY)
            || referenceDate.plusDays(day).getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            return false;
        }
        return true;
    }

}
