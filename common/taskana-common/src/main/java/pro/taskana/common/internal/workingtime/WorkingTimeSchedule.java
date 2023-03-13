package pro.taskana.common.internal.workingtime;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import pro.taskana.common.api.LocalTimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.internal.util.Pair;

class WorkingTimeSchedule {

  // Holds WorkSlots as SortedSet of LocalTimeInterval sorted ascending and descending by DayOfWeek
  private final Map<DayOfWeek, Pair<SortedSet<LocalTimeInterval>, SortedSet<LocalTimeInterval>>>
      workingTimeByDayOfWeek = new EnumMap<>(DayOfWeek.class);

  WorkingTimeSchedule(Map<DayOfWeek, Set<LocalTimeInterval>> workingTimeByDayOfWeek) {
    if (workingTimeByDayOfWeek.isEmpty()) {
      throw new InvalidArgumentException("At least one day of the week needs to have working time");
    }

    for (Entry<DayOfWeek, Set<LocalTimeInterval>> dayOfWeekSetEntry :
        workingTimeByDayOfWeek.entrySet()) {
      SortedSet<LocalTimeInterval> intervalsAscending =
          sortedSetOf(dayOfWeekSetEntry.getValue(), null);

      LocalTime previousEnd = null;
      for (LocalTimeInterval current : intervalsAscending) {
        if (previousEnd != null && current.getBegin().isBefore(previousEnd)) {
          throw new IllegalArgumentException(
              "Working time is overlapping for " + intervalsAscending);
        }
        previousEnd = current.getEnd();
      }

      SortedSet<LocalTimeInterval> intervalsDescending =
          sortedSetOf(intervalsAscending, Comparator.reverseOrder());

      this.workingTimeByDayOfWeek.put(
          dayOfWeekSetEntry.getKey(), Pair.of(intervalsAscending, intervalsDescending));
    }
  }

  /**
   * Determines whether <code>dayOfWeek</code> is a working day.
   *
   * @param dayOfWeek The dayOfWeek. May not be <code>null</code>>.
   * @return <code>true</code> if it is a working day, <code>false</code> otherwise.
   */
  public boolean isWorkingDay(DayOfWeek dayOfWeek) {
    return !workSlotsFor(dayOfWeek).isEmpty();
  }

  /**
   * Returns all LocalTimeIntervals for <code>dayOfWeek</code> sorted ascending by their beginning.
   *
   * @param dayOfWeek The DayOfWeek to get LocalTimeIntervals for.
   * @return All LocalTimeIntervals sorted ascending by their beginning. May be empty.
   */
  public SortedSet<LocalTimeInterval> workSlotsFor(DayOfWeek dayOfWeek) {
    return workSlotsForBySortOrder(dayOfWeek, Pair::getLeft);
  }

  /**
   * Returns all LocalTimeIntervals for <code>dayOfWeek</code> sorted descending by their beginning.
   *
   * @param dayOfWeek The DayOfWeek to get LocalTimeIntervals for.
   * @return All LocalTimeIntervals sorted descending by their beginning. May be empty.
   */
  public SortedSet<LocalTimeInterval> workSlotsForReversed(DayOfWeek dayOfWeek) {
    return workSlotsForBySortOrder(dayOfWeek, Pair::getRight);
  }

  private SortedSet<LocalTimeInterval> workSlotsForBySortOrder(
      DayOfWeek dayOfWeek,
      Function<
              Pair<SortedSet<LocalTimeInterval>, SortedSet<LocalTimeInterval>>,
              SortedSet<LocalTimeInterval>>
          pairChooser) {
    Pair<SortedSet<LocalTimeInterval>, SortedSet<LocalTimeInterval>> bothIntervalSets =
        workingTimeByDayOfWeek.get(dayOfWeek);
    if (bothIntervalSets == null) {
      return Collections.emptySortedSet();
    }
    return pairChooser.apply(bothIntervalSets);
  }

  private SortedSet<LocalTimeInterval> sortedSetOf(
      Set<LocalTimeInterval> original, Comparator<LocalTimeInterval> comparator) {
    SortedSet<LocalTimeInterval> sorted = new TreeSet<>(comparator);
    sorted.addAll(original);

    return Collections.unmodifiableSortedSet(sorted);
  }
}
