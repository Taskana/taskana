package pro.taskana.workbasket.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.beans.ConstructorProperties;
import java.util.Optional;

import pro.taskana.common.rest.QueryParameter;
import pro.taskana.workbasket.api.WorkbasketAccessItemQuery;

public class WorkbasketAccessItemQueryFilterParameter
    implements QueryParameter<WorkbasketAccessItemQuery, Void> {

  /** Filter by the key of the workbasket. This is an exact match. */
  @JsonProperty("workbasket-key")
  private final String[] workbasketKey;

  /**
   * Filter by the key of the workbasket. This results in a substring search.. (% is appended to the
   * beginning and end of the requested value). Further SQL "Like" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("workbasket-key-like")
  private final String[] workbasketKeyLike;

  /** Filter by the name of the access id. This is an exact match. */
  @JsonProperty("access-id")
  private final String[] accessId;

  /**
   * Filter by the name of the access id. This results in a substring search.. (% is appended to the
   * beginning and end of the requested value). Further SQL "Like" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("access-id-like")
  private final String[] accessIdLike;

  @ConstructorProperties({"workbasket-key", "workbasket-key-like", "access-id", "access-id-like"})
  public WorkbasketAccessItemQueryFilterParameter(
      String[] workbasketKey,
      String[] workbasketKeyLike,
      String[] accessId,
      String[] accessIdLike) {
    this.workbasketKey = workbasketKey;
    this.workbasketKeyLike = workbasketKeyLike;
    this.accessId = accessId;
    this.accessIdLike = accessIdLike;
  }

  @Override
  public Void applyToQuery(WorkbasketAccessItemQuery query) {
    Optional.ofNullable(workbasketKey).ifPresent(query::workbasketKeyIn);
    Optional.ofNullable(workbasketKeyLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::workbasketKeyLike);
    Optional.ofNullable(accessId).ifPresent(query::accessIdIn);
    Optional.ofNullable(accessIdLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::accessIdLike);
    return null;
  }
}
