package pro.taskana.common.rest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pro.taskana.common.api.Interval;

public interface QueryParameter<E, R> {

  R apply(E entity);

  default String[] wrapElementsInLikeStatement(String[] list) {
    return Arrays.stream(list).map(item -> "%" + item + "%").toArray(String[]::new);
  }

  default Interval<Instant>[] extractTimeIntervals(Instant[] instants) {
    List<Interval<Instant>> timeIntervalsList = new ArrayList<>();
    for (int i = 0; i < instants.length - 1; i += 2) {
      Instant left = instants[i];
      Instant right = instants[i + 1];
      if (left != null || right != null) {
        timeIntervalsList.add(new Interval<>(left, right));
      }
    }

    return timeIntervalsList.toArray(new Interval[0]);
  }

  default Interval<Integer>[] extractIntervals(Integer[] boundaries) {
    List<Interval<Integer>> intervalsList = new ArrayList<>();
    for (int i = 0; i < boundaries.length - 1; i += 2) {
      Integer left = boundaries[i];
      Integer right = boundaries[i + 1];
      if (left != null || right != null) {
        intervalsList.add(new Interval<>(left, right));
      }
    }
    return intervalsList.toArray(new Interval[0]);
  }
}
