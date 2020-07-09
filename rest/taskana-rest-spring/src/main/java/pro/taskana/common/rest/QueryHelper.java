package pro.taskana.common.rest;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;

import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.internal.util.CheckedBiConsumer;

public class QueryHelper {

  public static final String SORT_BY = "sort-by";
  public static final String ORDER_DIRECTION = "order";
  private static final Logger LOGGER = LoggerFactory.getLogger(QueryHelper.class);

  private QueryHelper() {
    // no op
  }

  public static void applyAndRemoveSortingParams(
      MultiValueMap<String, String> params,
      CheckedBiConsumer<String, SortDirection, InvalidArgumentException> consumer)
      throws InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to applyAndRemoveSortingParams(params= {})", params);
    }

    if (params == null || consumer == null) {
      throw new InvalidArgumentException("params or consumer can't be null!");
    }
    List<String> allSortBy = params.remove(SORT_BY);
    List<String> allOrderBy = params.remove(ORDER_DIRECTION);

    verifyNotOnlyOrderByExists(allSortBy, allOrderBy);
    verifyAmountOfSortByAndOrderByMatches(allSortBy, allOrderBy);

    if (allSortBy != null) {
      for (int i = 0; i < allSortBy.size(); i++) {
        consumer.accept(allSortBy.get(i), getSortDirectionForIndex(allOrderBy, i));
      }
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from applyAndRemoveSortingParams()");
    }
  }

  private static SortDirection getSortDirectionForIndex(List<String> allOrderBy, int i) {
    SortDirection sortDirection = SortDirection.ASCENDING;
    if (allOrderBy != null && !allOrderBy.isEmpty() && "desc".equalsIgnoreCase(allOrderBy.get(i))) {
      sortDirection = SortDirection.DESCENDING;
    }
    return sortDirection;
  }

  private static void verifyNotOnlyOrderByExists(List<String> allSortBy, List<String> allOrderBy)
      throws InvalidArgumentException {
    if (allSortBy == null && allOrderBy != null) {
      throw new InvalidArgumentException(
          String.format(
              "Only '%s' were provided. Please also provide '%s' parameter(s)",
              ORDER_DIRECTION, SORT_BY));
    }
  }

  private static void verifyAmountOfSortByAndOrderByMatches(
      List<String> allSortBy, List<String> allOrderBy) throws InvalidArgumentException {
    if (allSortBy != null
        && allOrderBy != null
        && allSortBy.size() != allOrderBy.size()
        && !allOrderBy.isEmpty()) {
      throw new InvalidArgumentException(
          String.format(
              "The amount of '%s' and '%s' does not match. "
                  + "Please specify an '%s' for each '%s' or no '%s' parameters at all.",
              SORT_BY, ORDER_DIRECTION, ORDER_DIRECTION, SORT_BY, ORDER_DIRECTION));
    }
  }
}
