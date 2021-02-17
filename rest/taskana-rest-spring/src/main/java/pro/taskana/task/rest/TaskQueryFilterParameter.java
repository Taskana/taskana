package pro.taskana.task.rest;

import static pro.taskana.common.internal.util.CheckedConsumer.wrap;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.beans.ConstructorProperties;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import pro.taskana.common.api.KeyDomain;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.internal.util.Pair;
import pro.taskana.common.rest.QueryParameter;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskQuery;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.WildcardSearchField;

public class TaskQueryFilterParameter implements QueryParameter<TaskQuery, Void> {

  /** Filter by the name of the task. This is an exact match. */
  private final String[] name;
  /**
   * Filter by the name of the task. This results in a substring search.. (% is appended to the
   * front and end of the requested value). Further SQL "LIKE" wildcard characters will be resolved
   * correctly.
   */
  @JsonProperty("name-like")
  private final String[] nameLike;
  /** Filter by the priority of the task. This is an exact match. */
  private final int[] priority;
  /** Filter by the task state. This is an exact match. */
  private final TaskState[] state;
  /** Filter by the classification key. This is an exact match. */
  @JsonProperty("classification.key")
  private final String[] classificationKeys;
  /** Filter by task id. This is an exact match. */
  @JsonProperty("task-id")
  private final String[] taskIds;
  /** Filter by workbasket id. This is an exact match. */
  @JsonProperty("workbasket-id")
  private final String[] workbasketIds;
  /** Filter by workbasket keys. This parameter can only be used in combination with 'domain' */
  @JsonProperty("workbasket-key")
  private final String[] workbasketKeys;
  /** Filter by domain. This is an exact match. */
  private final String domain;
  /** Filter by owner. This is an exact match. */
  private final String[] owner;
  /**
   * Filter by the owner of the task. This results in a substring search.. (% is appended to the
   * front and end of the requested value). Further SQL "LIKE" wildcard characters will be resolved
   * correctly.
   */
  @JsonProperty("owner-like")
  private final String[] ownerLike;
  /** Filter by the company of the primary object reference. This is an exact match. */
  @JsonProperty("por.company")
  private final String[] porCompany;
  /** Filter by the system of the primary object reference. This is an exact match. */
  @JsonProperty("por.system")
  private final String[] porSystem;
  /** Filter by the system instance of the primary object reference. This is an exact match. */
  @JsonProperty("por.instance")
  private final String[] porInstance;
  /** Filter by the type of the primary object reference. This is an exact match. */
  @JsonProperty("por.type")
  private final String[] porType;
  /** Filter by the value of the primary object reference. This is an exact match. */
  @JsonProperty("por.value")
  private final String[] porValue;
  /**
   * Filter by a planned time interval. The length of the provided values has to be even. To create
   * an open interval you can either use 'null' or just leave it blank.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'planned-from' or 'planned-until'.
   */
  private final Instant[] planned;

  /**
   * Filter since a given planned timestamp.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'planned'.
   */
  @JsonProperty("planned-from")
  private final Instant plannedFrom;

  /**
   * Filter until a given planned timestamp.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'planned'.
   */
  @JsonProperty("planned-until")
  private final Instant plannedUntil;

  /**
   * Filter by a due time interval. The length of the provided values has to be even. To create an
   * open interval you can either use 'null' or just leave it blank.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'due-from' or 'due-until'.
   */
  private final Instant[] due;

  /**
   * Filter since a given due timestamp.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'due'.
   */
  @JsonProperty("due-from")
  private final Instant dueFrom;

  /**
   * Filter until a given due timestamp.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'due'.
   */
  @JsonProperty("due-until")
  private final Instant dueUntil;

  /**
   * Filter by wildcard search field.
   *
   * <p>This must be used in combination with 'wildcard-search-value'
   */
  @JsonProperty("wildcard-search-fields")
  private final WildcardSearchField[] wildcardSearchFields;

  /**
   * Filter by wildcard search field. This is an exact match.
   *
   * <p>This must be used in combination with 'wildcard-search-value'
   */
  @JsonProperty("wildcard-search-value")
  private final String wildcardSearchValue;

  /** Filter by the external id. This is an exact match. */
  @JsonProperty("external-id")
  private final String[] externalIds;

  /** Filter by the value of the field custom1. This is an exact match. */
  @JsonProperty("custom-1")
  private final String[] custom1;

  /** Filter by the value of the field custom2. This is an exact match. */
  @JsonProperty("custom-2")
  private final String[] custom2;

  /** Filter by the value of the field custom3. This is an exact match. */
  @JsonProperty("custom-3")
  private final String[] custom3;

  /** Filter by the value of the field custom4. This is an exact match. */
  @JsonProperty("custom-4")
  private final String[] custom4;

  /** Filter by the value of the field custom5. This is an exact match. */
  @JsonProperty("custom-5")
  private final String[] custom5;

  /** Filter by the value of the field custom6. This is an exact match. */
  @JsonProperty("custom-6")
  private final String[] custom6;

  /** Filter by the value of the field custom7. This is an exact match. */
  @JsonProperty("custom-7")
  private final String[] custom7;

  /** Filter by the value of the field custom8. This is an exact match. */
  @JsonProperty("custom-8")
  private final String[] custom8;

  /** Filter by the value of the field custom9. This is an exact match. */
  @JsonProperty("custom-9")
  private final String[] custom9;

  /** Filter by the value of the field custom10. This is an exact match. */
  @JsonProperty("custom-10")
  private final String[] custom10;

  /** Filter by the value of the field custom11. This is an exact match. */
  @JsonProperty("custom-11")
  private final String[] custom11;

  /** Filter by the value of the field custom12. This is an exact match. */
  @JsonProperty("custom-12")
  private final String[] custom12;

  /** Filter by the value of the field custom13. This is an exact match. */
  @JsonProperty("custom-13")
  private final String[] custom13;

  /** Filter by the value of the field custom14. This is an exact match. */
  @JsonProperty("custom-14")
  private final String[] custom14;

  /** Filter by the value of the field custom15. This is an exact match. */
  @JsonProperty("custom-15")
  private final String[] custom15;

  /** Filter by the value of the field custom16. This is an exact match. */
  @JsonProperty("custom-16")
  private final String[] custom16;

  @SuppressWarnings("indentation")
  @ConstructorProperties({
    "name",
    "name-like",
    "priority",
    "state",
    "classification.key",
    "task-id",
    "workbasket-id",
    "workbasket-key",
    "domain",
    "owner",
    "owner-like",
    "por.company",
    "por.system",
    "por.instance",
    "por.type",
    "por.value",
    "planned",
    "due",
    "planned-from",
    "planned-until",
    "due-from",
    "due-until",
    "wildcard-search-fields",
    "wildcard-search-value",
    "external-id",
    "custom-1",
    "custom-2",
    "custom-3",
    "custom-4",
    "custom-5",
    "custom-6",
    "custom-7",
    "custom-8",
    "custom-9",
    "custom-10",
    "custom-11",
    "custom-12",
    "custom-13",
    "custom-14",
    "custom-15",
    "custom-16"
  })
  public TaskQueryFilterParameter(
      String[] name,
      String[] nameLike,
      int[] priority,
      TaskState[] state,
      String[] classificationKeys,
      String[] taskIds,
      String[] workbasketIds,
      String[] workbasketKeys,
      String domain,
      String[] owner,
      String[] ownerLike,
      String[] porCompany,
      String[] porSystem,
      String[] porInstance,
      String[] porType,
      String[] porValue,
      Instant[] planned,
      Instant[] due,
      Instant plannedFrom,
      Instant plannedUntil,
      Instant dueFrom,
      Instant dueUntil,
      WildcardSearchField[] wildcardSearchFields,
      String wildcardSearchValue,
      String[] externalIds,
      String[] custom1,
      String[] custom2,
      String[] custom3,
      String[] custom4,
      String[] custom5,
      String[] custom6,
      String[] custom7,
      String[] custom8,
      String[] custom9,
      String[] custom10,
      String[] custom11,
      String[] custom12,
      String[] custom13,
      String[] custom14,
      String[] custom15,
      String[] custom16)
      throws InvalidArgumentException {
    this.name = name;
    this.nameLike = nameLike;
    this.priority = priority;
    this.state = state;
    this.classificationKeys = classificationKeys;
    this.taskIds = taskIds;
    this.workbasketIds = workbasketIds;
    this.workbasketKeys = workbasketKeys;
    this.domain = domain;
    this.owner = owner;
    this.ownerLike = ownerLike;
    this.porCompany = porCompany;
    this.porSystem = porSystem;
    this.porInstance = porInstance;
    this.porType = porType;
    this.porValue = porValue;
    this.planned = planned;
    this.due = due;
    this.plannedFrom = plannedFrom;
    this.plannedUntil = plannedUntil;
    this.dueFrom = dueFrom;
    this.dueUntil = dueUntil;
    this.wildcardSearchFields = wildcardSearchFields;
    this.wildcardSearchValue = wildcardSearchValue;
    this.externalIds = externalIds;
    this.custom1 = custom1;
    this.custom2 = custom2;
    this.custom3 = custom3;
    this.custom4 = custom4;
    this.custom5 = custom5;
    this.custom6 = custom6;
    this.custom7 = custom7;
    this.custom8 = custom8;
    this.custom9 = custom9;
    this.custom10 = custom10;
    this.custom11 = custom11;
    this.custom12 = custom12;
    this.custom13 = custom13;
    this.custom14 = custom14;
    this.custom15 = custom15;
    this.custom16 = custom16;

    validateFilterParameters();
  }

  @Override
  public Void applyToQuery(TaskQuery query) {
    Optional.ofNullable(name).ifPresent(query::nameIn);
    Optional.ofNullable(nameLike).map(this::wrapElementsInLikeStatement).ifPresent(query::nameLike);
    Optional.ofNullable(priority).ifPresent(query::priorityIn);
    Optional.ofNullable(state).ifPresent(query::stateIn);
    Optional.ofNullable(classificationKeys).ifPresent(query::classificationKeyIn);
    Optional.ofNullable(taskIds).ifPresent(query::idIn);
    Optional.ofNullable(workbasketIds).ifPresent(query::workbasketIdIn);
    Optional.ofNullable(workbasketKeys)
        .map(
            keys ->
                Arrays.stream(keys)
                    .map(key -> new KeyDomain(key, domain))
                    .toArray(KeyDomain[]::new))
        .ifPresent(query::workbasketKeyDomainIn);
    Optional.ofNullable(owner).ifPresent(query::ownerIn);
    Optional.ofNullable(ownerLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::ownerLike);
    Optional.ofNullable(porCompany).ifPresent(query::primaryObjectReferenceCompanyIn);
    Optional.ofNullable(porSystem).ifPresent(query::primaryObjectReferenceSystemIn);
    Optional.ofNullable(porInstance).ifPresent(query::primaryObjectReferenceSystemInstanceIn);
    Optional.ofNullable(porType).ifPresent(query::primaryObjectReferenceTypeIn);
    Optional.ofNullable(porValue).ifPresent(query::primaryObjectReferenceValueIn);
    Optional.ofNullable(planned).map(this::extractTimeIntervals).ifPresent(query::plannedWithin);
    Optional.ofNullable(due).map(this::extractTimeIntervals).ifPresent(query::dueWithin);
    if (plannedFrom != null || plannedUntil != null) {
      query.plannedWithin(new TimeInterval(plannedFrom, plannedUntil));
    }
    if (dueFrom != null || dueUntil != null) {
      query.dueWithin(new TimeInterval(dueFrom, dueUntil));
    }
    if (wildcardSearchFields != null) {
      query.wildcardSearchFieldsIn(wildcardSearchFields);
      query.wildcardSearchValueLike("%" + wildcardSearchValue + "%");
    }
    Optional.ofNullable(externalIds).ifPresent(query::externalIdIn);
    Stream.of(
            Pair.of(TaskCustomField.CUSTOM_1, custom1),
            Pair.of(TaskCustomField.CUSTOM_2, custom2),
            Pair.of(TaskCustomField.CUSTOM_3, custom3),
            Pair.of(TaskCustomField.CUSTOM_4, custom4),
            Pair.of(TaskCustomField.CUSTOM_5, custom5),
            Pair.of(TaskCustomField.CUSTOM_6, custom6),
            Pair.of(TaskCustomField.CUSTOM_7, custom7),
            Pair.of(TaskCustomField.CUSTOM_8, custom8),
            Pair.of(TaskCustomField.CUSTOM_9, custom9),
            Pair.of(TaskCustomField.CUSTOM_10, custom10),
            Pair.of(TaskCustomField.CUSTOM_11, custom11),
            Pair.of(TaskCustomField.CUSTOM_12, custom12),
            Pair.of(TaskCustomField.CUSTOM_13, custom13),
            Pair.of(TaskCustomField.CUSTOM_14, custom14),
            Pair.of(TaskCustomField.CUSTOM_15, custom15),
            Pair.of(TaskCustomField.CUSTOM_16, custom16))
        .forEach(
            pair ->
                Optional.ofNullable(pair.getRight())
                    .ifPresent(wrap(l -> query.customAttributeIn(pair.getLeft(), l))));
    return null;
  }

  private void validateFilterParameters() throws InvalidArgumentException {
    if (planned != null && (plannedFrom != null || plannedUntil != null)) {
      throw new IllegalArgumentException(
          "It is prohibited to use the param 'planned' in combination "
              + "with the params 'planned-from'  and / or 'planned-until'");
    }

    if (due != null && (dueFrom != null || dueUntil != null)) {
      throw new IllegalArgumentException(
          "It is prohibited to use the param 'due' in combination "
              + "with the params 'due-from'  and / or 'due-until'");
    }

    if (wildcardSearchFields == null ^ wildcardSearchValue == null) {
      throw new IllegalArgumentException(
          "The params 'wildcard-search-field' and 'wildcard-search-value' must be used together");
    }

    if (workbasketKeys != null && domain == null) {
      throw new InvalidArgumentException("'workbasket-key' requires exactly one domain.");
    }

    if (planned != null && planned.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'planned' is not dividable by 2");
    }

    if (due != null && due.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'due' is not dividable by 2");
    }
  }
}
