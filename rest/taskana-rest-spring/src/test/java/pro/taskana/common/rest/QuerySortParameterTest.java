package pro.taskana.common.rest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.Test;

import pro.taskana.common.api.BaseQuery;
import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.QueryColumnName;
import pro.taskana.common.api.exceptions.InvalidArgumentException;

class QuerySortParameterTest {

  @Test
  void should_ApplySortBy_When_SortByParameterIsCalled() throws Exception {
    MockQuery query = mock(MockQuery.class);
    MockSortBy sortBy = mock(MockSortBy.class);

    QuerySortParameter<MockQuery, MockSortBy> sortByParameter = new QuerySortParameter<>(
        List.of(sortBy), List.of(SortDirection.ASCENDING));

    sortByParameter.applyToQuery(query);

    verify(sortBy).applySortByForQuery(query, SortDirection.ASCENDING);
  }

  @Test
  void should_ApplySortDirectionAsc_When_OrderByIsNull() throws Exception {
    MockQuery query = mock(MockQuery.class);
    MockSortBy sortBy = mock(MockSortBy.class);

    QuerySortParameter<MockQuery, MockSortBy> sortByParameter =
        new QuerySortParameter<>(List.of(sortBy), null);

    sortByParameter.applyToQuery(query);

    verify(sortBy).applySortByForQuery(query, SortDirection.ASCENDING);
  }

  @Test
  void should_ApplySortDirectionAsc_When_OrderByIsEmpty() throws Exception {
    MockQuery query = mock(MockQuery.class);
    MockSortBy sortBy = mock(MockSortBy.class);

    QuerySortParameter<MockQuery, MockSortBy> sortByParameter =
        new QuerySortParameter<>(List.of(sortBy), List.of());

    sortByParameter.applyToQuery(query);

    verify(sortBy).applySortByForQuery(query, SortDirection.ASCENDING);
  }

  @Test
  void should_ApplySortByDesc_When_OrderByIsDesc() throws Exception {
    MockQuery query = mock(MockQuery.class);
    MockSortBy sortBy = mock(MockSortBy.class);

    QuerySortParameter<MockQuery, MockSortBy> sortByParameter =
        new QuerySortParameter<>(List.of(sortBy), List.of(SortDirection.DESCENDING));

    sortByParameter.applyToQuery(query);

    verify(sortBy).applySortByForQuery(query, SortDirection.DESCENDING);
  }

  @Test
  void should_ApplySortByMultipleTimes_When_SortByListContainsMultipleElements() throws Exception {
    MockQuery query = mock(MockQuery.class);
    MockSortBy sortBy1 = mock(MockSortBy.class);
    MockSortBy sortBy2 = mock(MockSortBy.class);

    QuerySortParameter<MockQuery, MockSortBy> sortByParameter =
        new QuerySortParameter<>(List.of(sortBy1, sortBy2),
            List.of(SortDirection.ASCENDING, SortDirection.ASCENDING));

    sortByParameter.applyToQuery(query);

    verify(sortBy1).applySortByForQuery(query, SortDirection.ASCENDING);
    verify(sortBy2).applySortByForQuery(query, SortDirection.ASCENDING);
  }

  @Test
  void should_ThrowException_When_SortByAndOrderByLengthDoesNotMatch() {
    MockSortBy sortBy = mock(MockSortBy.class);
    SortDirection sortDirection = SortDirection.ASCENDING;
    assertThatThrownBy(
        () -> new QuerySortParameter<>(List.of(sortBy, sortBy), List.of(sortDirection)))
        .isInstanceOf(InvalidArgumentException.class);
    assertThatThrownBy(
        () -> new QuerySortParameter<>(List.of(sortBy), List.of(sortDirection, sortDirection)))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @Test
  void should_ThrowException_When_SortByIsNull() {
    assertThatThrownBy(() -> new QuerySortParameter<>(null, List.of()))
        .isInstanceOf(InvalidArgumentException.class);
  }

  private enum MockColumnNames implements QueryColumnName {}

  private abstract static class MockSortBy implements QuerySortBy<MockQuery> {

  }

  private abstract static class MockQuery implements BaseQuery<Void, MockColumnNames> {

  }
}
