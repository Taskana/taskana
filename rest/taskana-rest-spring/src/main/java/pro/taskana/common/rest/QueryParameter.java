package pro.taskana.common.rest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import pro.taskana.common.api.BaseQuery;
import pro.taskana.common.api.TimeInterval;

public interface QueryParameter<Q extends BaseQuery<?, ?>, R> {

  R applyToQuery(Q query);

  default String[] wrapElementsInLikeStatement(String[] list) {
    if (list != null) {
      for (int i = 0; i < list.length; i++) {
        list[i] = "%" + list[i] + "%";
      }
    }
    return list;
  }

  default TimeInterval[] extractTimeIntervals(Instant[] instants) {
    List<TimeInterval> timeIntervalsList = new ArrayList<>();
    for (int i = 0; i < instants.length - 1; i += 2) {
      Instant left = instants[i];
      Instant right = instants[i + 1];
      if (left != null || right != null) {
        timeIntervalsList.add(new TimeInterval(left, right));
      }
    }

    return timeIntervalsList.toArray(new TimeInterval[0]);
  }
}
