package io.kadai.workbasket.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.kadai.common.rest.QueryParameter;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketQuery;
import io.kadai.workbasket.api.WorkbasketType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.beans.ConstructorProperties;
import java.util.Optional;

public class WorkbasketQueryFilterParameter implements QueryParameter<WorkbasketQuery, Void> {

  @Schema(
      name = "name",
      description = "Filter by the name of the Workbasket. This is an exact match.")
  @JsonProperty("name")
  private final String[] name;

  @Schema(
      name = "name-like",
      description =
          "Filter by the name of the Workbasket. This results in a substring search. (% is "
              + "appended to beginning and end of the requested value). Further SQL \"LIKE\" "
              + "wildcard characters will be resolved correctly.")
  @JsonProperty("name-like")
  private final String[] nameLike;

  @Schema(
      name = "key",
      description = "Filter by the key of the Workbasket. This is an exact match.")
  @JsonProperty("key")
  private final String[] key;

  @Schema(
      name = "key-like",
      description =
          "Filter by the key of the Workbasket. This results in a substring search.. (% is "
              + "appended to the beginning and end of the requested value). Further SQL \"LIKE\""
              + " wildcard characters will be resolved correctly.")
  @JsonProperty("key-like")
  private final String[] keyLike;

  @Schema(
      name = "owner",
      description = "Filter by the owner of the Workbasket. This is an exact match.")
  @JsonProperty("owner")
  private final String[] owner;

  @Schema(
      name = "owner-like",
      description =
          "Filter by the owner of the Workbasket. This results in a substring search.. (% is "
              + "appended to the beginning and end of the requested value). Further SQL \"LIKE\" "
              + "wildcard characters will be resolved correctly.")
  @JsonProperty("owner-like")
  private final String[] ownerLike;

  @Schema(
      name = "description-like",
      description =
          "Filter by the description of the Workbasket. This results in a substring search.. (% "
              + "is appended to the beginning and end of the requested value). Further SQL "
              + "\"LIKE\" wildcard characters will be resolved correctly.")
  @JsonProperty("description-like")
  private final String[] descriptionLike;

  @Schema(
      name = "domain",
      description = "Filter by the domain of the Workbasket. This is an exact match.")
  @JsonProperty("domain")
  private final String[] domain;

  @Schema(
      name = "type",
      description = "Filter by the type of the Workbasket. This is an exact match.")
  @JsonProperty("type")
  private final WorkbasketType[] type;

  @Schema(
      name = "required-permission",
      description = "Filter by the required permission for the Workbasket.")
  @JsonProperty("required-permission")
  private final WorkbasketPermission[] requiredPermissions;

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
      WorkbasketPermission[] requiredPermissions) {
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

  public String[] getName() {
    return name;
  }

  public String[] getNameLike() {
    return nameLike;
  }

  public String[] getKey() {
    return key;
  }

  public String[] getKeyLike() {
    return keyLike;
  }

  public String[] getOwner() {
    return owner;
  }

  public String[] getOwnerLike() {
    return ownerLike;
  }

  public String[] getDescriptionLike() {
    return descriptionLike;
  }

  public String[] getDomain() {
    return domain;
  }

  public WorkbasketType[] getType() {
    return type;
  }

  public WorkbasketPermission[] getRequiredPermissions() {
    return requiredPermissions;
  }

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
