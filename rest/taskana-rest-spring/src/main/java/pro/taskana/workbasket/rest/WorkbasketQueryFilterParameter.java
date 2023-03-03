/*-
 * #%L
 * pro.taskana:taskana-rest-spring
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package pro.taskana.workbasket.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.beans.ConstructorProperties;
import java.util.Optional;

import pro.taskana.common.rest.QueryParameter;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketQuery;
import pro.taskana.workbasket.api.WorkbasketType;

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
