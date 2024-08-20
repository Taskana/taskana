package io.kadai.common.rest;

import io.kadai.common.api.BaseQuery;
import io.kadai.common.api.BaseQuery.SortDirection;

public interface QuerySortBy<Q extends BaseQuery<?, ?>> {

  void applySortByForQuery(Q query, SortDirection sortDirection);
}
