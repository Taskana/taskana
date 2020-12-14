package pro.taskana.common.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static pro.taskana.common.rest.QueryHelper.applyAndRemoveSortingParams;

import java.util.AbstractMap.SimpleEntry;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.ThrowingConsumer;
import org.mockito.InOrder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.internal.util.CheckedBiConsumer;

class QueryHelperTest {

  @Test
  void should_RemoveSortByAndOrderDirection_When_ApplyingSortingParams() throws Exception {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.put(QueryHelper.SORT_BY, List.of("sort-by"));
    map.put(QueryHelper.ORDER_DIRECTION, List.of("order"));

    applyAndRemoveSortingParams(map, mock(MockBiConsumer.class));
    assertThat(map).isEmpty();
  }

  @Test
  void should_IgnoreMapContent_When_ApplyingSortingParams() throws Exception {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    String key = "unknown";
    List<String> value = List.of("sort-by");
    map.put(key, value);
    map.put(QueryHelper.SORT_BY, List.of("sort-by"));

    applyAndRemoveSortingParams(map, mock(MockBiConsumer.class));
    assertThat(map).containsExactly(new SimpleEntry<>(key, value));
  }

  @Test
  void should_NotCallConsumer_When_MapDoesNotContainSortBy() throws Exception {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    MockBiConsumer consumer = mock(MockBiConsumer.class);

    applyAndRemoveSortingParams(map, consumer);

    verifyNoInteractions(consumer);
  }

  @Test
  void should_CallConsumerWithSortByValue_When_MapContainsOneSortBy() throws Exception {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.put(QueryHelper.SORT_BY, List.of("sort-by-value"));
    MockBiConsumer consumer = mock(MockBiConsumer.class);

    applyAndRemoveSortingParams(map, consumer);
    verify(consumer).accept(eq("sort-by-value"), any());
    verifyNoMoreInteractions(consumer);
  }

  @Test
  void should_CallConsumerWithAscSortDirection_When_MapDoesNotContainSortDirection()
      throws Exception {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.put(QueryHelper.SORT_BY, List.of("sort-by-value"));
    MockBiConsumer consumer = mock(MockBiConsumer.class);

    applyAndRemoveSortingParams(map, consumer);
    verify(consumer).accept(any(), eq(SortDirection.ASCENDING));
    verifyNoMoreInteractions(consumer);
  }

  @TestFactory
  Stream<DynamicTest>
      should_CallConsumerWithDescSortDirection_When_MapDoesContainsDescSortDirection() {
    Iterator<String> testCases = List.of("desc", "DESC", "Desc", "desC", "DeSc").iterator();
    ThrowingConsumer<String> test =
        desc -> {
          MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
          map.put(QueryHelper.SORT_BY, List.of("sort-by-value"));
          map.put(QueryHelper.ORDER_DIRECTION, List.of(desc));
          MockBiConsumer consumer = mock(MockBiConsumer.class);

          applyAndRemoveSortingParams(map, consumer);
          verify(consumer).accept(any(), eq(SortDirection.DESCENDING));
          verifyNoMoreInteractions(consumer);
        };

    return DynamicTest.stream(testCases, s -> "Order by: " + s, test);
  }

  @Test
  void should_CallConsumerMultipleTimes_When_MapContainsMultipleSortBy() throws Exception {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.put(QueryHelper.SORT_BY, List.of("sort-by-value1", "sort-by-value2"));
    MockBiConsumer consumer = mock(MockBiConsumer.class);

    applyAndRemoveSortingParams(map, consumer);
    InOrder inOrder = inOrder(consumer);
    inOrder.verify(consumer).accept(eq("sort-by-value1"), any());
    inOrder.verify(consumer).accept(eq("sort-by-value2"), any());
    verifyNoMoreInteractions(consumer);
  }

  @Test
  void should_MatchSortDirectionForEachSortBy_When_MapContainsMultipleSortByAndOrderBy()
      throws Exception {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.put(QueryHelper.SORT_BY, List.of("sort-by-value1", "sort-by-value2"));
    map.put(QueryHelper.ORDER_DIRECTION, List.of("desc", "asc"));
    MockBiConsumer consumer = mock(MockBiConsumer.class);

    applyAndRemoveSortingParams(map, consumer);
    verify(consumer).accept("sort-by-value1", SortDirection.DESCENDING);
    verify(consumer).accept("sort-by-value2", SortDirection.ASCENDING);
    verifyNoMoreInteractions(consumer);
  }

  @Test
  void should_ThrowError_When_MapContainsOrderByButNoSortBy() {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.put(QueryHelper.ORDER_DIRECTION, List.of("desc"));
    assertThatThrownBy(() -> applyAndRemoveSortingParams(map, mock(MockBiConsumer.class)))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @Test
  void should_ThrowError_When_SortByAndOrderByCountDoesNotMatch() {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.put(QueryHelper.SORT_BY, List.of("1", "2"));
    map.put(QueryHelper.ORDER_DIRECTION, List.of("desc"));
    assertThatThrownBy(() -> applyAndRemoveSortingParams(map, mock(MockBiConsumer.class)))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @Test
  void should_ThrowError_When_ConsumerRaisesException() throws Exception {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.put(QueryHelper.SORT_BY, List.of("1"));
    MockBiConsumer consumer = mock(MockBiConsumer.class);
    doThrow(new InvalidArgumentException("")).when(consumer).accept(any(), any());
    assertThatThrownBy(() -> applyAndRemoveSortingParams(map, consumer))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @Test
  void should_ThrowError_When_ConsumerIsNull() {
    assertThatThrownBy(() -> applyAndRemoveSortingParams(new LinkedMultiValueMap<>(), null))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @Test
  void should_ThrowError_When_MapIsNull() {
    assertThatThrownBy(() -> applyAndRemoveSortingParams(null, mock(MockBiConsumer.class)))
        .isInstanceOf(InvalidArgumentException.class);
  }

  private abstract static class MockBiConsumer
      implements CheckedBiConsumer<String, SortDirection, InvalidArgumentException> {}
}
