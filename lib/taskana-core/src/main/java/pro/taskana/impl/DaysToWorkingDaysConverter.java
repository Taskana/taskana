package pro.taskana.impl;

import static java.time.temporal.ChronoUnit.DAYS;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.impl.report.header.TimeIntervalColumnHeader;
import pro.taskana.impl.util.LoggerUtils;

/**
 * The DaysToWorkingDaysConverter provides a method to convert an age in days into an age in working days. Before the
 * method convertDaysToWorkingDays() can be used, the DaysToWorkingDaysConverter has to be initialized. For a list of
 * {@link TimeIntervalColumnHeader}s the converter creates a "table" with integer that represents the age in days from
 * the largest lower limit until the smallest upper limit of the timeIntervalColumnHeaders. This table is valid for a
 * whole day until the converter is initialized with bigger limits.
 */
public final class DaysToWorkingDaysConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskMonitorServiceImpl.class);
    private static DaysToWorkingDaysConverter instance;
    private ArrayList<Integer> positiveDaysToWorkingDays;
    private ArrayList<Integer> negativeDaysToWorkingDays;
    private Instant dateCreated;
    private LocalDate easterSunday;
    private static boolean germanHolidaysEnabled;
    private static Set<LocalDate> customHolidays = new HashSet<>();

    private DaysToWorkingDaysConverter(List<? extends TimeIntervalColumnHeader> columnHeaders,
        Instant referenceDate) {
        easterSunday = getEasterSunday(LocalDateTime.ofInstant(referenceDate, ZoneId.systemDefault()).getYear());
        dateCreated = referenceDate;
        positiveDaysToWorkingDays = generatePositiveDaysToWorkingDays(columnHeaders, referenceDate);
        negativeDaysToWorkingDays = generateNegativeDaysToWorkingDays(columnHeaders, referenceDate);
    }

    /**
     * Initializes the DaysToWorkingDaysConverter for a list of {@link TimeIntervalColumnHeader}s and the current day. A
     * new table is only created if there are bigger limits or the date has changed.
     *
     * @param columnHeaders a list of {@link TimeIntervalColumnHeader}s that determines the size of the table
     * @return an instance of the DaysToWorkingDaysConverter
     * @throws InvalidArgumentException thrown if columnHeaders is null
     */
    public static DaysToWorkingDaysConverter initialize(List<? extends TimeIntervalColumnHeader> columnHeaders)
        throws InvalidArgumentException {
        return initialize(columnHeaders, Instant.now());
    }

    /**
     * Initializes the DaysToWorkingDaysConverter for a list of {@link TimeIntervalColumnHeader}s and a referenceDate. A
     * new table is only created if there are bigger limits or the date has changed.
     *
     * @param columnHeaders a list of {@link TimeIntervalColumnHeader}s that determines the size of the table
     * @param referenceDate a {@link Instant} that represents the current day of the table
     * @return an instance of the DaysToWorkingDaysConverter
     * @throws InvalidArgumentException thrown if columnHeaders or referenceDate is null
     */
    public static DaysToWorkingDaysConverter initialize(List<? extends TimeIntervalColumnHeader> columnHeaders,
        Instant referenceDate) throws InvalidArgumentException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialize DaysToWorkingDaysConverter with columnHeaders: {}",
                LoggerUtils.listToString(columnHeaders));
        }
        if (columnHeaders == null) {
            throw new InvalidArgumentException("TimeIntervalColumnHeaders can´t be used as NULL-Parameter");
        }
        if (referenceDate == null) {
            throw new InvalidArgumentException("ReferenceDate can´t be used as NULL-Parameter");
        }
        int largesLowerLimit = TimeIntervalColumnHeader.getLargestLowerLimit(columnHeaders);
        int smallestUpperLimit = TimeIntervalColumnHeader.getSmallestUpperLimit(columnHeaders);
        if (instance == null
            || !instance.positiveDaysToWorkingDays.contains(largesLowerLimit)
            || !instance.negativeDaysToWorkingDays.contains(smallestUpperLimit)
            || !instance.dateCreated.truncatedTo(DAYS).equals(referenceDate.truncatedTo(DAYS))) {

            instance = new DaysToWorkingDaysConverter(columnHeaders, referenceDate);
            LOGGER.debug("Create new converter for the values from {} until {} for the date: {}.", largesLowerLimit,
                smallestUpperLimit, instance.dateCreated);
        }
        return instance;
    }

    public static Optional<DaysToWorkingDaysConverter> getLastCreatedInstance() {
        return Optional.ofNullable(instance);
    }

    public static void setGermanPublicHolidaysEnabled(boolean germanPublicHolidaysEnabled) {
        germanHolidaysEnabled = germanPublicHolidaysEnabled;
    }

    /**
     * Converts an integer, that represents the age in days, to the age in working days by using the table that was
     * created by initialization. If the age in days is beyond the limits of the table, the integer will be returned
     * unchanged.
     *
     * @param ageInDays represents the age in days
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
     * created by initialization. Because one age in working days could match to more than one age in days, the return
     * value is a list of all days that match to the input parameter. If the age in working days is beyond the limits of
     * the table, the integer will be returned unchanged.
     *
     * @param ageInWorkingDays represents the age in working days
     * @return a list of age in days
     */
    public ArrayList<Integer> convertWorkingDaysToDays(int ageInWorkingDays) {

        ArrayList<Integer> list = new ArrayList<>();

        int minWorkingDay = negativeDaysToWorkingDays.get(negativeDaysToWorkingDays.size() - 1);
        int maxWorkingDay = positiveDaysToWorkingDays.get(positiveDaysToWorkingDays.size() - 1);

        if (ageInWorkingDays >= minWorkingDay && ageInWorkingDays < 0) {
            for (int ageInDays = 0; ageInDays < negativeDaysToWorkingDays.size(); ageInDays++) {
                if (negativeDaysToWorkingDays.get(ageInDays) == ageInWorkingDays) {
                    list.add(-ageInDays);
                }
            }
            return list;
        }
        if (ageInWorkingDays > 0 && ageInWorkingDays <= maxWorkingDay) {
            for (int ageInDays = 0; ageInDays < positiveDaysToWorkingDays.size(); ageInDays++) {
                if (positiveDaysToWorkingDays.get(ageInDays) == ageInWorkingDays) {
                    list.add(ageInDays);
                }
            }
            return list;
        }

        if (ageInWorkingDays == 0) {
            list.add(0);
            for (int ageInDays = 1; ageInDays < positiveDaysToWorkingDays.size(); ageInDays++) {
                if (positiveDaysToWorkingDays.get(ageInDays) == ageInWorkingDays) {
                    list.add(ageInDays);
                }
            }
            for (int ageInDays = 1; ageInDays < negativeDaysToWorkingDays.size(); ageInDays++) {
                if (negativeDaysToWorkingDays.get(ageInDays) == ageInWorkingDays) {
                    list.add(-ageInDays);
                }
            }
            return list;
        }

        // If ageInWorkingDays is beyond the limits of the table, the value is returned unchanged.
        list.add(ageInWorkingDays);
        return list;
    }

    public long convertWorkingDaysToDays(Instant startTime, long numberOfDays) {
        int days = 0;
        int workingDays = 0;
        int direction = numberOfDays > 0 ? 1 : -1;
        while (workingDays < numberOfDays * direction) {
            days += direction;
            workingDays += isWorkingDay(days, startTime) ? 1 : 0;
        }
        return days;
    }

    private ArrayList<Integer> generateNegativeDaysToWorkingDays(
        List<? extends TimeIntervalColumnHeader> columnHeaders, Instant referenceDate) {
        int minUpperLimit = TimeIntervalColumnHeader.getSmallestUpperLimit(columnHeaders);
        ArrayList<Integer> daysToWorkingDays = new ArrayList<>();
        daysToWorkingDays.add(0);
        int day = -1;
        int workingDay = 0;
        while (workingDay > minUpperLimit) {
            workingDay -= (isWorkingDay(day--, referenceDate)) ? 1 : 0;
            daysToWorkingDays.add(workingDay);
        }
        return daysToWorkingDays;
    }

    private ArrayList<Integer> generatePositiveDaysToWorkingDays(
        List<? extends TimeIntervalColumnHeader> columnHeaders, Instant referenceDate) {
        int maxLowerLimit = TimeIntervalColumnHeader.getLargestLowerLimit(columnHeaders);
        ArrayList<Integer> daysToWorkingDays = new ArrayList<>();
        daysToWorkingDays.add(0);

        int day = 1;
        int workingDay = 0;
        while (workingDay < maxLowerLimit) {
            workingDay += (isWorkingDay(day++, referenceDate)) ? 1 : 0;
            daysToWorkingDays.add(workingDay);
        }
        return daysToWorkingDays;
    }

    private boolean isWorkingDay(int day, Instant referenceDate) {
        LocalDateTime dateToCheck = LocalDateTime.ofInstant(referenceDate, ZoneId.systemDefault()).plusDays(day);

        return !isWeekend(dateToCheck)
            && !isHoliday(dateToCheck.toLocalDate());
    }

    private boolean isWeekend(LocalDateTime dateToCheck) {
        return dateToCheck.getDayOfWeek().equals(DayOfWeek.SATURDAY)
            || dateToCheck.getDayOfWeek().equals(DayOfWeek.SUNDAY);
    }

    private boolean isHoliday(LocalDate date) {
        if (germanHolidaysEnabled && isGermanHoliday(date)) {
            return true;
        }
        // Custom holidays that can be configured in the TaskanaEngineConfiguration
        return customHolidays.contains(date);
    }

    private boolean isGermanHoliday(LocalDate date) {
        // Fix and movable holidays that are valid throughout Germany: New years day, Labour Day, Day of German
        // Unity, Christmas,
        if (Stream.of(GermanFixHolidays.values()).anyMatch(day -> day.matches(date))) {
            return true;
        }

        // Easter holidays Good Friday, Easter Monday, Ascension Day, Whit Monday.
        long diffFromEasterSunday = DAYS.between(easterSunday, date);
        long goodFriday = -2;
        long easterMonday = 1;
        long ascensionDay = 39;
        long whitMonday = 50;

        return LongStream.of(goodFriday, easterMonday, ascensionDay, whitMonday)
            .anyMatch(diff -> diff == diffFromEasterSunday);
    }

    /**
     * Computes the date of Easter Sunday for a given year.
     *
     * @param year for which the date of Easter Sunday should be calculated
     * @return the date of Easter Sunday for the given year
     */
    static LocalDate getEasterSunday(int year) {
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
        customHolidays = new HashSet<>(holidays == null ? Collections.emptyList() : holidays);
    }

    @Override
    public String toString() {
        return "DaysToWorkingDaysConverter [instance= " + instance
            + ", positiveDaysToWorkingDays= " + positiveDaysToWorkingDays
            + ", negativeDaysToWorkingDays= " + negativeDaysToWorkingDays
            + ", dateCreated= " + dateCreated + ", easterSunday= " + easterSunday
            + ", germanHolidaysEnabled= " + germanHolidaysEnabled
            + ", customHolidays= " + LoggerUtils.setToString(customHolidays) + "]";
    }

    /**
     * Enumeration of German holidays.
     */
    private enum GermanFixHolidays {
        NEWYEAR(1, 1),
        LABOURDAY(5, 1),
        GERMANUNITY(10, 3),
        CHRISTMAS1(12, 25),
        CHRISTMAS2(12, 26);

        private int month;
        private int day;

        GermanFixHolidays(int month, int day) {
            this.month = month;
            this.day = day;
        }

        public boolean matches(LocalDate date) {
            return date.getDayOfMonth() == day && date.getMonthValue() == month;
        }
    }
}
