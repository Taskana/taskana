package pro.taskana.common.internal.util;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ListUtil {

  private ListUtil() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Splits a list with objects of type T into chunks of a certain size.
   *
   * @param <T> type of elements inside list
   * @param inputList list to be divided
   * @param size maximal number of elements inside chunk
   * @return list containing the chunks
   */
  public static <T> Collection<List<T>> partitionBasedOnSize(Collection<T> inputList, int size) {
    final AtomicInteger counter = new AtomicInteger(0);
    return inputList.stream()
        .collect(Collectors.groupingBy(s -> counter.getAndIncrement() / size))
        .values();
  }
}
