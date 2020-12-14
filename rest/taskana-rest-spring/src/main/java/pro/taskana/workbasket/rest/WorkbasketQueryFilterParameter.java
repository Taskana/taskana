package pro.taskana.workbasket.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.beans.ConstructorProperties;
import java.util.Optional;

import pro.taskana.common.rest.QueryParameter;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketQuery;
import pro.taskana.workbasket.api.WorkbasketType;

public class WorkbasketQueryFilterParameter implements QueryParameter<WorkbasketQuery, Void> {

  /** Filter by the name of the workbasket. This is an exact match. */
  private final String[] name;

  /**
   * Filter by the name of the workbasket. This results into a substring search. (% is appended to
   * the front and end of the requested value). Further SQL "Like" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("name-like")
  private final String[] nameLike;

  /** Filter by the key of the workbasket. This is an exact match. */
  private final String[] key;

  /**
   * Filter by the key of the workbasket. This results into a substring search. (% is appended to
   * the front and end of the requested value). Further SQL "Like" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("key-like")
  private final String[] keyLike;

  /** Filter by the owner of the workbasket. This is an exact match. */
  private final String[] owner;

  /**
   * Filter by the owner of the workbasket. This results into a substring search. (% is appended to
   * the front and end of the requested value). Further SQL "Like" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("owner-like")
  private final String[] ownerLike;

  /**
   * Filter by the description of the workbasket. This results into a substring search. (% is
   * appended to the front and end of the requested value). Further SQL "Like" wildcard characters
   * will be resolved correctly.
   */
  @JsonProperty("description-like")
  private final String[] descriptionLike;

  /** Filter by the name of the workbasket. This is an exact match. */
  private final String[] domain;

  /** Filter by the type of the workbasket. This is an exact match. */
  private final WorkbasketType[] type;

  /** Filter by the required permission for the workbasket. */
  @JsonProperty("required-permission")
  private final WorkbasketPermission requiredPermissions;

  @SuppressWarnings("indentation")
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
  public WorkbasketQueryFilterParameter(
      String[] name,
      String[] nameLike,
      String[] key,
      String[] keyLike,
      String[] owner,
      String[] ownerLike,
      String[] descriptionLike,
      String[] domain,
      WorkbasketType[] type,
      WorkbasketPermission requiredPermissions) {
    this.name = name;
    this.nameLike = nameLike;
    this.key = key;
    this.keyLike = keyLike;
    this.owner = owner;
    this.ownerLike = ownerLike;
    this.descriptionLike = descriptionLike;
    this.domain = domain;
    this.type = type;
    this.requiredPermissions = requiredPermissions;
  }

  @Override
  public Void applyToQuery(WorkbasketQuery query) {
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
    Optional.ofNullable(requiredPermissions).ifPresent(query::callerHasPermission);
    return null;
  }
}
