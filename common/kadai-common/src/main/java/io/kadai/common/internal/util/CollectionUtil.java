package io.kadai.common.internal.util;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CollectionUtil {

  private CollectionUtil() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Splits a collection with objects of type T into chunks of a certain size.
   *
   * @param <T> type of elements inside collection
   * @param inputCollection collection to be divided
   * @param size maximal number of elements inside chunk
   * @return list containing the chunks
   */
  public static <T> Collection<List<T>> partitionBasedOnSize(
      Collection<T> inputCollection, int size) {
    final AtomicInteger counter = new AtomicInteger(0);
    return inputCollection.stream()
        .collect(Collectors.groupingBy(s -> counter.getAndIncrement() / size))
        .values();
  }
}
