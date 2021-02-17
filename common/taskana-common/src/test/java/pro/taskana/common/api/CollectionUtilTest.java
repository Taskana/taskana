package pro.taskana.common.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

import pro.taskana.common.internal.util.CollectionUtil;

class CollectionUtilTest {

  @Test
  void should_SplitListIntoChunks_When_CallingPartitionBasedOnSize() {
    List<Integer> listWith1000Entries =
        IntStream.rangeClosed(1, 1000).boxed().collect(Collectors.toList());
    assertThat(listWith1000Entries).hasSize(1000);
    Collection<List<Integer>> partitions =
        CollectionUtil.partitionBasedOnSize(listWith1000Entries, 100);
    assertThat(partitions).hasSize(10);
  }
}
