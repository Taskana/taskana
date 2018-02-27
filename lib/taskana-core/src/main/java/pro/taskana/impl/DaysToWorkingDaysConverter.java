package pro.taskana.impl;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.exceptions.InvalidArgumentException;
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
    private static Instant dateCreated;
    private static LocalDate easterSunday;
    private static boolean germanHolidaysEnabled;
    private static List<LocalDate> customHolidays;

    private DaysToWorkingDaysConverter(List<ReportLineItemDefinition> reportLineItemDefinitions,
        Instant referenceDate) {
        easterSunday = getEasterSunday(LocalDateTime.ofInstant(referenceDate, ZoneId.systemDefault()).getYear());
        dateCreated = referenceDate;
        positiveDaysToWorkingDays = generatePositiveDaysToWorkingDays(reportLineItemDefinitions, referenceDate);
        negativeDaysToWorkingDays = generateNegativeDaysToWorkingDays(reportLineItemDefinitions, referenceDate);
    }

    /**
     * Initializes the DaysToWorkingDaysConverter for a list of {@link ReportLineItemDefinition}s and the current day. A
     * new table is only created if there are bigger limits or the date has changed.
     *
     * @param reportLineItemDefinitions
     *            a list of {@link ReportLineItemDefinition}s that determines the size of the table
     * @return an instance of the DaysToWorkingDaysConverter
     * @throws InvalidArgumentException
     *             thrown if reportLineItemDefinitions is null
     */
    public static DaysToWorkingDaysConverter initialize(List<ReportLineItemDefinition> reportLineItemDefinitions)
        throws InvalidArgumentException {
        return initialize(reportLineItemDefinitions, Instant.now());
    }

    /**
     * Initializes the DaysToWorkingDaysConverter for a list of {@link ReportLineItemDefinition}s and a referenceDate. A
     * new table is only created if there are bigger limits or the date has changed.
     *
     * @param reportLineItemDefinitions
     *            a list of {@link ReportLineItemDefinition}s that determines the size of the table
     * @param referenceDate
     *            a {@link Instant} that represents the current day of the table
     * @return an instance of the DaysToWorkingDaysConverter
     * @throws InvalidArgumentException
     *             thrown if reportLineItemDefinitions or referenceDate is null
     */
    public static DaysToWorkingDaysConverter initialize(List<ReportLineItemDefinition> reportLineItemDefinitions,
        Instant referenceDate) throws InvalidArgumentException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialize DaysToWorkingDaysConverter with reportLineItemDefinitions: {}",
                LoggerUtils.listToString(reportLineItemDefinitions));
        }
        if (reportLineItemDefinitions == null) {
            throw new InvalidArgumentException("ReportLineItemDefinitions can´t be used as NULL-Parameter");
        }
        if (referenceDate == null) {
            throw new InvalidArgumentException("ReferenceDate can´t be used as NULL-Parameter");
        }
        int largesLowerLimit = getLargestLowerLimit(reportLineItemDefinitions);
        int smallestUpperLimit = getSmallestUpperLimit(reportLineItemDefinitions);
        if (instance == null
            || !positiveDaysToWorkingDays.contains(largesLowerLimit)
            || !negativeDaysToWorkingDays.contains(smallestUpperLimit)
            || !dateCreated.truncatedTo(ChronoUnit.DAYS).equals(referenceDate.truncatedTo(ChronoUnit.DAYS))) {

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

        int minDay = -(negativeDaysToWorkingDays.size() - 1);
        int maxDay = positiveDaysToWorkingDays.size() - 1;

        if (ageInDays >= minDay && ageInDays <= 0) {
            return negativeDaysToWorkingDays.get(-ageInDays);
        }
        if (ageInDays > 0 && ageInDays <= maxDay) {
            return positiveDaysToWorkingDays.get(ageInDays);
        }

        return ageInDays;
    }

    /**
     * Converts an integer, that represents the age in working days, to the age in days by using the table that was
     * created by initialization. If the age in working days is beyond the limits of the table, the integer will be
     * returned unchanged.
     *
     * @param ageInWorkingDays
     *            represents the age in working days
     * @return the age in days
     */
    public int convertWorkingDaysToDays(int ageInWorkingDays) {

        int minWorkingDay = negativeDaysToWorkingDays.get(negativeDaysToWorkingDays.size() - 1);
        int maxWorkingDay = positiveDaysToWorkingDays.get(positiveDaysToWorkingDays.size() - 1);

        int ageInDays = 0;
        if (ageInWorkingDays >= minWorkingDay && ageInWorkingDays < 0) {
            while (negativeDaysToWorkingDays.get(ageInDays) > ageInWorkingDays) {
                ageInDays++;
            }
            return -ageInDays;
        }
        if (ageInWorkingDays > 0 && ageInWorkingDays <= maxWorkingDay) {
            while (positiveDaysToWorkingDays.get(ageInDays) < ageInWorkingDays) {
                ageInDays++;
            }
            return ageInDays;
        }

        return ageInWorkingDays;
    }

    private ArrayList<Integer> generateNegativeDaysToWorkingDays(
        List<ReportLineItemDefinition> reportLineItemDefinitions, Instant referenceDate) {
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
        List<ReportLineItemDefinition> reportLineItemDefinitions, Instant referenceDate) {
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

    private boolean isWorkingDay(int day, Instant referenceDate) {
        if (LocalDateTime.ofInstant(referenceDate, ZoneId.systemDefault()).plusDays(day).getDayOfWeek().equals(
            DayOfWeek.SATURDAY)
            || LocalDateTime.ofInstant(referenceDate, ZoneId.systemDefault()).plusDays(day).getDayOfWeek().equals(
                DayOfWeek.SUNDAY)
            || isHoliday(LocalDateTime.ofInstant(referenceDate, ZoneId.systemDefault()).plusDays(day).toLocalDate())) {
            return false;
        }
        return true;
    }

    private boolean isHoliday(LocalDate date) {
        if (germanHolidaysEnabled) {
            // Fix and movable holidays that are valid throughout Germany: New years day, Labour Day, Day of German
            // Unity, Christmas, Good Friday, Easter Monday, Ascension Day, Whit Monday.
            if (date.getDayOfMonth() == 1 && date.getMonthValue() == 1
                || date.getDayOfMonth() == 1 && date.getMonthValue() == 5
                || date.getDayOfMonth() == 3 && date.getMonthValue() == 10
                || date.getDayOfMonth() == 25 && date.getMonthValue() == 12
                || date.getDayOfMonth() == 26 && date.getMonthValue() == 12
                || easterSunday.minusDays(2).equals(date)
                || easterSunday.plusDays(1).equals(date)
                || easterSunday.plusDays(39).equals(date)
                || easterSunday.plusDays(50).equals(date)) {
                return true;
            }
        }
        if (customHolidays != null) {
            // Custom holidays that can be configured in the TaskanaEngineConfiguration
            for (LocalDate customHoliday : customHolidays) {
                if (date.equals(customHoliday)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Computes the date of Easter Sunday for a given year.
     *
     * @param year
     *            for which the date of Easter Sunday should be calculated
     * @return the date of Easter Sunday for the given year
     */
    public LocalDate getEasterSunday(int year) {
        // Formula to compute Easter Sunday by Gauss.
        int a = year % 19;
        int b = year % 4;
        int c = year % 7;
        int k = year / 100;
        int p = (13 + 8 * k) / 25;
        int q = k / 4;
        int m = (15 - p + k - q) % 30;
        int n = (4 + k - q) % 7;
        int d = (19 * a + m) % 30;

        int e = (2 * b + 4 * c + 6 * d + n) % 7;

        if (d == 29 && e == 6) {
            return LocalDate.of(year, 3, 15).plusDays(d + e);
        }
        if (d == 28 && e == 6 && (11 * m + 11) % 30 < 19) {
            return LocalDate.of(year, 3, 15).plusDays(d + e);
        }
        return LocalDate.of(year, 3, 22).plusDays(d + e);
    }

    public static void setCustomHolidays(List<LocalDate> holidays) {
        customHolidays = holidays;
    }

    public List<LocalDate> getCustomHolidays() {
        return customHolidays;
    }

    public static void setGermanPublicHolidaysEnabled(boolean germanPublicHolidaysEnabled) {
        germanHolidaysEnabled = germanPublicHolidaysEnabled;
    }

    public boolean isGermanPublicHolidayEnabled() {
        return germanHolidaysEnabled;
    }

}
