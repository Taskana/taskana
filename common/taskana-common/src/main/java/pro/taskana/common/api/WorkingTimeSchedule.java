package pro.taskana.common.api;

import java.time.DayOfWeek;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

class WorkingTimeSchedule {

  private final Map<DayOfWeek, SortedSet<LocalTimeInterval>> workingTimeByDayOfWeek =
      new EnumMap<>(DayOfWeek.class);

  WorkingTimeSchedule(Map<DayOfWeek, Set<LocalTimeInterval>> workingTimeByDayOfWeek) {
    for (Entry<DayOfWeek, Set<LocalTimeInterval>> dayOfWeekSetEntry :
        workingTimeByDayOfWeek.entrySet()) {
      Set<LocalTimeInterval> workSlots = dayOfWeekSetEntry.getValue();
      for (LocalTimeInterval interval : workSlots) {
        if (interval != null && !interval.isValid()) {
          throw new IllegalArgumentException(
              "The work period doesn't have two LocalTimes for start and end.");
        }
      }
      this.workingTimeByDayOfWeek.put(
          dayOfWeekSetEntry.getKey(),
          sortedSetOf(workSlots, LocalTimeIntervalComparator.ASCENDING));
    }
  }

  /**
   * Returns all LocalTimeIntervals for <code>dayOfWeek</code> sorted ascending by their beginning.
   *
   * @param dayOfWeek The DayOfWeek to get LocalTimeIntervals for.
   * @return All LocalTimeIntervals sorted ascending by their beginning. May be empty.
   */
  public SortedSet<LocalTimeInterval> workSlotsFor(DayOfWeek dayOfWeek) {
    return workingTimeByDayOfWeek.getOrDefault(dayOfWeek, Collections.emptySortedSet());
  }

  /**
   * Returns all LocalTimeIntervals for <code>dayOfWeek</code> sorted descending by their beginning.
   *
   * @param dayOfWeek The DayOfWeek to get LocalTimeIntervals for.
   * @return All LocalTimeIntervals sorted descending by their beginning. May be empty.
   */
  public SortedSet<LocalTimeInterval> workSlotsForReversed(DayOfWeek dayOfWeek) {
    Set<LocalTimeInterval> localTimeIntervals = workSlotsFor(dayOfWeek);
    return sortedSetOf(localTimeIntervals, LocalTimeIntervalComparator.DESCENDING);
  }

  private SortedSet<LocalTimeInterval> sortedSetOf(
      Set<LocalTimeInterval> original, Comparator<LocalTimeInterval> comparator) {
    SortedSet<LocalTimeInterval> sorted = new TreeSet<>(comparator);
    sorted.addAll(original);

    return sorted;
  }

  private static class LocalTimeIntervalComparator implements Comparator<LocalTimeInterval> {

    public static final Comparator<LocalTimeInterval> ASCENDING = new LocalTimeIntervalComparator();

    public static final Comparator<LocalTimeInterval> DESCENDING = ASCENDING.reversed();

    @Override
    public int compare(LocalTimeInterval o1, LocalTimeInterval o2) {
      return o1.getBegin().compareTo(o2.getBegin());
    }
  }
}
