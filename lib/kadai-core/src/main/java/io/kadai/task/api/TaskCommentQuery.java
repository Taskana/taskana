package io.kadai.task.api;

import io.kadai.common.api.BaseQuery;
import io.kadai.common.api.TimeInterval;
import io.kadai.task.api.models.Task;
import io.kadai.task.api.models.TaskComment;

/** TaskCommentQuery for generating dynamic sql. */
public interface TaskCommentQuery extends BaseQuery<TaskComment, TaskCommentQueryColumnName> {

  /**
   * Filter for {@linkplain TaskComment}s which are containing one of the given taskCommentIds.
   *
   * @param taskCommentIds The ids of the searched-for {@linkplain TaskComment}s.
   * @return the query
   */
  TaskCommentQuery idIn(String... taskCommentIds);

  /**
   * Filter for {@linkplain TaskComment}s which are containing non of the given taskCommentIds.
   *
   * @param taskCommentIds The ids of the searched-for {@linkplain TaskComment}s.
   * @return the query
   */
  TaskCommentQuery idNotIn(String... taskCommentIds);

  /**
   * Add your taskCommentIds for pattern matching to your query. It will be compared in SQL with a
   * LIKE. If you use a wildcard like % then it will be transmitted to the database.
   *
   * @param taskCommentIds The ids of the searched-for {@linkplain TaskComment}s.
   * @return the query
   */
  TaskCommentQuery idLike(String... taskCommentIds);

  /**
   * Add your taskCommentIds for pattern matching to your query, which should not be contained. It
   * will be compared in SQL with a LIKE. If you use a wildcard like % then it will be transmitted
   * to the database.
   *
   * @param taskCommentIds The ids of the searched-for {@linkplain TaskComment}s.
   * @return the query
   */
  TaskCommentQuery idNotLike(String... taskCommentIds);

  /**
   * Add your {@linkplain Task} ids to your query.
   *
   * @param taskIds the {@linkplain Task} ids as Strings
   * @return the query
   */
  TaskCommentQuery taskIdIn(String... taskIds);

  /**
   * Add your texts for pattern matching to your query. It will be compared in SQL with the LIKE
   * operator. You may use a wildcard like % to specify the pattern. If you specify multiple
   * arguments they are combined with the OR keyword.
   *
   * @param texts your texts of the {@linkplain TaskComment}
   * @return the query
   */
  TaskCommentQuery textFieldLike(String... texts);

  /**
   * Add your texts for pattern matching to your query, which should not be contained. It will be
   * compared in SQL with the LIKE operator. You may use a wildcard like % to specify the pattern.
   * If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param texts your texts of the {@linkplain TaskComment}
   * @return the query
   */
  TaskCommentQuery textFieldNotLike(String... texts);

  /**
   * Add the UserIds of the creator to your query.
   *
   * @param creators of the queried {@linkplain TaskComment}s
   * @return the query
   */
  TaskCommentQuery creatorIn(String... creators);

  /**
   * Add the UserIds of the creator to your query, which should not be contained.
   *
   * @param creators of the queried {@linkplain TaskComment}s
   * @return the query
   */
  TaskCommentQuery creatorNotIn(String... creators);

  /**
   * Add the UserIds of the creator for pattern matching to your query. It will be compared in SQL
   * with the LIKE operator. You may use a wildcard like % to specify the pattern. If you specify
   * multiple arguments they are combined with the OR keyword.
   *
   * @param creators of the queried {@linkplain TaskComment}s
   * @return the query
   */
  TaskCommentQuery creatorLike(String... creators);

  /**
   * Add the UserIds of the creator for pattern matching to your query, which should not be
   * contained. It will be compared in SQL with the LIKE operator. You may use a wildcard like % to
   * specify the pattern. If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param creators of the queried {@linkplain TaskComment}s
   * @return the query
   */
  TaskCommentQuery creatorNotLike(String... creators);

  /**
   * Add the time intervals within which the {@linkplain TaskComment} was created to your query. For
   * each time interval, the database query will search for tasks whose created timestamp is after
   * or at the interval's begin and before or at the interval's end. If more than one interval is
   * specified, the query will connect them with the OR keyword. If either begin or end of an
   * interval are null, these values will not be specified in the query.
   *
   * @param intervals - the TimeIntervals within which the {@linkplain TaskComment} was created
   * @return the query
   */
  TaskCommentQuery createdWithin(TimeInterval... intervals);

  /**
   * Add the time intervals within which the {@linkplain TaskComment} was not created to your query.
   * For each time interval, the database query will search for tasks whose created timestamp is
   * after or at the interval's begin and before or at the interval's end. If more than one interval
   * is specified, the query will connect them with the OR keyword. If either begin or end of an
   * interval are null, these values will not be specified in the query.
   *
   * @param intervals - the TimeIntervals within which the {@linkplain TaskComment} was created
   * @return the query
   */
  TaskCommentQuery createdNotWithin(TimeInterval... intervals);

  /**
   * Add the time intervals within which the {@linkplain TaskComment} was modified to your query.
   * For each time interval, the database query will search for tasks whose modified timestamp is
   * after or at the interval's begin and before or at the interval's end. If more than one interval
   * is specified, the query will connect them with the OR keyword. If either begin or end of an
   * interval are null, these values will not be specified in the query.
   *
   * @param intervals - the TimeIntervals within which the {@linkplain TaskComment} was modified
   * @return the query
   */
  TaskCommentQuery modifiedWithin(TimeInterval... intervals);

  /**
   * Add the time intervals within which the {@linkplain TaskComment} was not modified to your
   * query. For each time interval, the database query will search for tasks whose modified
   * timestamp is after or at the interval's begin and before or at the interval's end. If more than
   * one interval is specified, the query will connect them with the OR keyword. If either begin or
   * end of an interval are null, these values will not be specified in the query.
   *
   * @param intervals - the TimeIntervals within which the {@linkplain TaskComment} was modified
   * @return the query
   */
  TaskCommentQuery modifiedNotWithin(TimeInterval... intervals);

  /**
   * This method sorts the query result according to the created timestamp.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskCommentQuery orderByCreated(SortDirection sortDirection);

  /**
   * This method sorts the query result according to the modified timestamp.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskCommentQuery orderByModified(SortDirection sortDirection);
}
