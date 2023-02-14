package pro.taskana.workbasket.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.beans.ConstructorProperties;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

import pro.taskana.common.rest.QueryParameter;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketQuery;
import pro.taskana.workbasket.api.WorkbasketType;

@RequiredArgsConstructor(
    onConstructor =
        @__({
          @ConstructorProperties({
            "name",
            "name-like",
            "key",
            "key-like",
            "owner",
            "owner-like",
            "description-like",
            "domain",
            "type",
            "required-permission"
          })
        }))
public class WorkbasketQueryFilterParameter implements QueryParameter<WorkbasketQuery, Void> {

  /** Filter by the name of the Workbasket. This is an exact match. */
  @JsonProperty("name")
  private final String[] name;

  /**
   * Filter by the name of the Workbasket. This results in a substring search. (% is appended to the
   * beginning and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("name-like")
  private final String[] nameLike;

  /** Filter by the key of the Workbasket. This is an exact match. */
  @JsonProperty("key")
  private final String[] key;

  /**
   * Filter by the key of the Workbasket. This results in a substring search.. (% is appended to the
   * beginning and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("key-like")
  private final String[] keyLike;

  /** Filter by the owner of the Workbasket. This is an exact match. */
  @JsonProperty("owner")
  private final String[] owner;

  /**
   * Filter by the owner of the Workbasket. This results in a substring search.. (% is appended to
   * the beginning and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("owner-like")
  private final String[] ownerLike;

  /**
   * Filter by the description of the Workbasket. This results in a substring search.. (% is
   * appended to the beginning and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("description-like")
  private final String[] descriptionLike;

  /** Filter by the domain of the Workbasket. This is an exact match. */
  @JsonProperty("domain")
  private final String[] domain;

  /** Filter by the type of the Workbasket. This is an exact match. */
  @JsonProperty("type")
  private final WorkbasketType[] type;

  /** Filter by the required permission for the Workbasket. */
  @JsonProperty("required-permission")
  private final WorkbasketPermission[] requiredPermissions;

  @Override
  public Void apply(WorkbasketQuery query) {
    Optional.ofNullable(name).ifPresent(query::nameIn);
    Optional.ofNullable(nameLike).map(this::wrapElementsInLikeStatement).ifPresent(query::nameLike);
    Optional.ofNullable(key).ifPresent(query::keyIn);
    Optional.ofNullable(keyLike).map(this::wrapElementsInLikeStatement).ifPresent(query::keyLike);
    Optional.ofNullable(owner).ifPresent(query::ownerIn);
    Optional.ofNullable(ownerLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::ownerLike);
    Optional.ofNullable(descriptionLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::descriptionLike);
    Optional.ofNullable(domain).ifPresent(query::domainIn);
    Optional.ofNullable(type).ifPresent(query::typeIn);
    Optional.ofNullable(requiredPermissions).ifPresent(query::callerHasPermissions);
    return null;
  }
}
