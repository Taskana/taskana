package pro.taskana.common.rest;

import pro.taskana.common.api.BaseQuery;
import pro.taskana.common.api.BaseQuery.SortDirection;

public interface QuerySortBy<Q extends BaseQuery<?, ?>> {

  void applySortByForQuery(Q query, SortDirection sortDirection);
}
