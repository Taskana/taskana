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

  /**
   * Filter by the classification key of the task. This results in a substring search.. (% is
   * appended to the front and end of the requested value). Further SQL "LIKE" wildcard characters
   * will be resolved correctly.
   */
  @JsonProperty("classification.key-like")
  private final String[] classificationKeysLike;

  /**
   * Filter by the classification category of the task. This results in a substring search.. (% is
   * appended to the front and end of the requested value). Further SQL "LIKE" wildcard characters
   * will be resolved correctly.
   */
  @JsonProperty("classification.category-like")
  private final String[] classificationCategoriesLike;

  /** Filter by the classification category. This is an exact match. */
  @JsonProperty("classification.category")
  private final String[] classificationCategories;

  /**
   * Filter by the classification name of the task. This results in a substring search.. (% is
   * appended to the front and end of the requested value). Further SQL "LIKE" wildcard characters
   * will be resolved correctly.
   */
  @JsonProperty("classification.name-like")
  private final String[] classificationNamesLike;

  /** Filter by the classification name. This is an exact match. */
  @JsonProperty("classification.name")
  private final String[] classificationNames;

  /**
   * Filter by the attachment classification name of the task. This results in a substring search..
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("attachment.classification.name-like")
  private final String[] attachmentClassificationNamesLike;

  /** Filter by the attachment classification name. This is an exact match. */
  @JsonProperty("attachment.classification.name")
  private final String[] attachmentClassificationNames;

  /**
   * Filter by the parent business process id of the task. This results in a substring search.. (%
   * is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("parent-business-process-id-like")
  private final String[] parentBusinessProcessIdsLike;

  /** Filter by the parent business process id. This is an exact match. */
  @JsonProperty("parent-business-process-id")
  private final String[] parentBusinessProcessIds;

  /**
   * Filter by the business process id of the task. This results in a substring search.. (% is
   * appended to the front and end of the requested value). Further SQL "LIKE" wildcard characters
   * will be resolved correctly.
   */
  @JsonProperty("business-process-id-like")
  private final String[] businessProcessIdsLike;

  /** Filter by the business process id. This is an exact match. */
  @JsonProperty("business-process-id")
  private final String[] businessProcessIds;

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

  /** Filter by creator. This is an exact match. */
  private final String[] creator;

  /**
   * Filter by the creator of the task. This results in a substring search.. (% is appended to the
   * front and end of the requested value). Further SQL "LIKE" wildcard characters will be resolved
   * correctly.
   */
  @JsonProperty("creator-like")
  private final String[] creatorLike;

  /**
   * Filter by the note of the task. This results in a substring search.. (% is appended to the
   * front and end of the requested value). Further SQL "LIKE" wildcard characters will be resolved
   * correctly.
   */
  @JsonProperty("note-like")
  private final String[] noteLike;

  /** Filter by the company of the primary object reference. This is an exact match. */
  @JsonProperty("por.company")
  private final String[] porCompany;

  /**
   * Filter by the porCompany of the task. This results in a substring search.. (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("por.company-like")
  private final String[] porCompanyLike;

  /** Filter by the system of the primary object reference. This is an exact match. */
  @JsonProperty("por.system")
  private final String[] porSystem;

  /**
   * Filter by the porSystem of the task. This results in a substring search.. (% is appended to the
   * front and end of the requested value). Further SQL "LIKE" wildcard characters will be resolved
   * correctly.
   */
  @JsonProperty("por.system-like")
  private final String[] porSystemLike;

  /** Filter by the system instance of the primary object reference. This is an exact match. */
  @JsonProperty("por.instance")
  private final String[] porInstance;

  /**
   * Filter by the porInstance of the task. This results in a substring search.. (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("por.instance-like")
  private final String[] porInstanceLike;

  /** Filter by the type of the primary object reference. This is an exact match. */
  @JsonProperty("por.type")
  private final String[] porType;

  /**
   * Filter by the porType of the task. This results in a substring search.. (% is appended to the
   * front and end of the requested value). Further SQL "LIKE" wildcard characters will be resolved
   * correctly.
   */
  @JsonProperty("por.type-like")
  private final String[] porTypeLike;

  /** Filter by the value of the primary object reference. This is an exact match. */
  @JsonProperty("por.value")
  private final String[] porValue;

  /**
   * Filter by the porValue of the task. This results in a substring search.. (% is appended to the
   * front and end of the requested value). Further SQL "LIKE" wildcard characters will be resolved
   * correctly.
   */
  @JsonProperty("por.value-like")
  private final String[] porValueLike;

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
  /**
   * Filter by the externalId of the task. This results in a substring search.. (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("external-id-like")
  private final String[] externalIdsLike;

  /** Filter by the value of the field custom1. This is an exact match. */
  @JsonProperty("custom-1")
  private final String[] custom1;

  /**
   * Filter by the custom1 field of the task. This results in a substring search.. (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-1-like")
  private final String[] custom1Like;

  /** Filter by the value of the field custom2. This is an exact match. */
  @JsonProperty("custom-2")
  private final String[] custom2;

  /**
   * Filter by the custom2 field of the task. This results in a substring search.. (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-2-like")
  private final String[] custom2Like;

  /** Filter by the value of the field custom3. This is an exact match. */
  @JsonProperty("custom-3")
  private final String[] custom3;

  /**
   * Filter by the custom3 field of the task. This results in a substring search.. (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-3-like")
  private final String[] custom3Like;

  /** Filter by the value of the field custom4. This is an exact match. */
  @JsonProperty("custom-4")
  private final String[] custom4;

  /**
   * Filter by the custom4 field of the task. This results in a substring search.. (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-4-like")
  private final String[] custom4Like;

  /** Filter by the value of the field custom5. This is an exact match. */
  @JsonProperty("custom-5")
  private final String[] custom5;

  /**
   * Filter by the custom5 field of the task. This results in a substring search.. (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-5-like")
  private final String[] custom5Like;

  /** Filter by the value of the field custom6. This is an exact match. */
  @JsonProperty("custom-6")
  private final String[] custom6;

  /**
   * Filter by the custom6 field of the task. This results in a substring search.. (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-6-like")
  private final String[] custom6Like;

  /** Filter by the value of the field custom7. This is an exact match. */
  @JsonProperty("custom-7")
  private final String[] custom7;

  /**
   * Filter by the custom7 field of the task. This results in a substring search.. (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-7-like")
  private final String[] custom7Like;

  /** Filter by the value of the field custom8. This is an exact match. */
  @JsonProperty("custom-8")
  private final String[] custom8;

  /**
   * Filter by the custom8 field of the task. This results in a substring search.. (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-8-like")
  private final String[] custom8Like;

  /** Filter by the value of the field custom9. This is an exact match. */
  @JsonProperty("custom-9")
  private final String[] custom9;

  /**
   * Filter by the custom9 field of the task. This results in a substring search.. (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-9-like")
  private final String[] custom9Like;

  /** Filter by the value of the field custom10. This is an exact match. */
  @JsonProperty("custom-10")
  private final String[] custom10;

  /**
   * Filter by the custom10 field of the task. This results in a substring search.. (% is appended
   * to the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-10-like")
  private final String[] custom10Like;

  /** Filter by the value of the field custom11. This is an exact match. */
  @JsonProperty("custom-11")
  private final String[] custom11;

  /**
   * Filter by the custom11 field of the task. This results in a substring search.. (% is appended
   * to the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-11-like")
  private final String[] custom11Like;

  /** Filter by the value of the field custom12. This is an exact match. */
  @JsonProperty("custom-12")
  private final String[] custom12;

  /**
   * Filter by the custom12 field of the task. This results in a substring search.. (% is appended
   * to the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-12-like")
  private final String[] custom12Like;

  /** Filter by the value of the field custom13. This is an exact match. */
  @JsonProperty("custom-13")
  private final String[] custom13;

  /**
   * Filter by the custom13 field of the task. This results in a substring search.. (% is appended
   * to the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-13-like")
  private final String[] custom13Like;

  /** Filter by the value of the field custom14. This is an exact match. */
  @JsonProperty("custom-14")
  private final String[] custom14;

  /**
   * Filter by the custom14 field of the task. This results in a substring search.. (% is appended
   * to the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-14-like")
  private final String[] custom14Like;

  /** Filter by the value of the field custom15. This is an exact match. */
  @JsonProperty("custom-15")
  private final String[] custom15;

  /**
   * Filter by the custom15 field of the task. This results in a substring search.. (% is appended
   * to the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-15-like")
  private final String[] custom15Like;

  /** Filter by the value of the field custom16. This is an exact match. */
  @JsonProperty("custom-16")
  private final String[] custom16;

  /**
   * Filter by the custom16 field of the task. This results in a substring search.. (% is appended
   * to the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-16-like")
  private final String[] custom16Like;

  @SuppressWarnings("indentation")
  @ConstructorProperties({
    "name",
    "name-like",
    "priority",
    "state",
    "classification.key",
    "classification.key-like",
    "classification.category",
    "classification.category-like",
    "classification.name",
    "classification.name-like",
    "attachment.classification.name",
    "attachment.classification.name-like",
    "parent-business-process-id",
    "parent-business-process-id-like",
    "business-process-id",
    "business-process-id-like",
    "task-id",
    "workbasket-id",
    "workbasket-key",
    "domain",
    "owner",
    "owner-like",
    "creator",
    "creator-like",
    "note-like",
    "por.company",
    "por.company-like",
    "por.system",
    "por.system-like",
    "por.instance",
    "por.instance-like",
    "por.type",
    "por.type-like",
    "por.value",
    "por.value-like",
    "planned",
    "planned-from",
    "planned-until",
    "due",
    "due-from",
    "due-until",
    "wildcard-search-fields",
    "wildcard-search-value",
    "external-id",
    "external-id-like",
    "custom-1",
    "custom-1-like",
    "custom-2",
    "custom-2-like",
    "custom-3",
    "custom-3-like",
    "custom-4",
    "custom-4-like",
    "custom-5",
    "custom-5-like",
    "custom-6",
    "custom-6-like",
    "custom-7",
    "custom-7-like",
    "custom-8",
    "custom-8-like",
    "custom-9",
    "custom-9-like",
    "custom-10",
    "custom-10-like",
    "custom-11",
    "custom-11-like",
    "custom-12",
    "custom-12-like",
    "custom-13",
    "custom-13-like",
    "custom-14",
    "custom-14-like",
    "custom-15",
    "custom-15-like",
    "custom-16",
    "custom-16-like"
  })
  public TaskQueryFilterParameter(
      String[] name,
      String[] nameLike,
      int[] priority,
      TaskState[] state,
      String[] classificationKeys,
      String[] classificationKeysLike,
      String[] classificationCategories,
      String[] classificationCategoriesLike,
      String[] classificationNames,
      String[] classificationNamesLike,
      String[] attachmentClassificationNames,
      String[] attachmentClassificationNamesLike,
      String[] parentBusinessProcessIds,
      String[] parentBusinessProcessIdsLike,
      String[] businessProcessIds,
      String[] businessProcessIdsLike,
      String[] taskIds,
      String[] workbasketIds,
      String[] workbasketKeys,
      String domain,
      String[] owner,
      String[] ownerLike,
      String[] creator,
      String[] creatorLike,
      String[] noteLike,
      String[] porCompany,
      String[] porCompanyLike,
      String[] porSystem,
      String[] porSystemLike,
      String[] porInstance,
      String[] porInstanceLike,
      String[] porType,
      String[] porTypeLike,
      String[] porValue,
      String[] porValueLike,
      Instant[] planned,
      Instant plannedFrom,
      Instant plannedUntil,
      Instant[] due,
      Instant dueFrom,
      Instant dueUntil,
      WildcardSearchField[] wildcardSearchFields,
      String wildcardSearchValue,
      String[] externalIds,
      String[] externalIdsLike,
      String[] custom1,
      String[] custom1Like,
      String[] custom2,
      String[] custom2Like,
      String[] custom3,
      String[] custom3Like,
      String[] custom4,
      String[] custom4Like,
      String[] custom5,
      String[] custom5Like,
      String[] custom6,
      String[] custom6Like,
      String[] custom7,
      String[] custom7Like,
      String[] custom8,
      String[] custom8Like,
      String[] custom9,
      String[] custom9Like,
      String[] custom10,
      String[] custom10Like,
      String[] custom11,
      String[] custom11Like,
      String[] custom12,
      String[] custom12Like,
      String[] custom13,
      String[] custom13Like,
      String[] custom14,
      String[] custom14Like,
      String[] custom15,
      String[] custom15Like,
      String[] custom16,
      String[] custom16Like)
      throws InvalidArgumentException {
    this.name = name;
    this.nameLike = nameLike;
    this.priority = priority;
    this.state = state;
    this.classificationKeys = classificationKeys;
    this.classificationKeysLike = classificationKeysLike;
    this.classificationCategories = classificationCategories;
    this.classificationCategoriesLike = classificationCategoriesLike;
    this.classificationNames = classificationNames;
    this.classificationNamesLike = classificationNamesLike;
    this.attachmentClassificationNames = attachmentClassificationNames;
    this.attachmentClassificationNamesLike = attachmentClassificationNamesLike;
    this.parentBusinessProcessIds = parentBusinessProcessIds;
    this.parentBusinessProcessIdsLike = parentBusinessProcessIdsLike;
    this.businessProcessIds = businessProcessIds;
    this.businessProcessIdsLike = businessProcessIdsLike;
    this.taskIds = taskIds;
    this.workbasketIds = workbasketIds;
    this.workbasketKeys = workbasketKeys;
    this.domain = domain;
    this.owner = owner;
    this.ownerLike = ownerLike;
    this.creator = creator;
    this.creatorLike = creatorLike;
    this.noteLike = noteLike;
    this.porCompany = porCompany;
    this.porCompanyLike = porCompanyLike;
    this.porSystem = porSystem;
    this.porSystemLike = porSystemLike;
    this.porInstance = porInstance;
    this.porInstanceLike = porInstanceLike;
    this.porType = porType;
    this.porTypeLike = porTypeLike;
    this.porValue = porValue;
    this.porValueLike = porValueLike;
    this.planned = planned;
    this.plannedFrom = plannedFrom;
    this.plannedUntil = plannedUntil;
    this.due = due;
    this.dueFrom = dueFrom;
    this.dueUntil = dueUntil;
    this.wildcardSearchFields = wildcardSearchFields;
    this.wildcardSearchValue = wildcardSearchValue;
    this.externalIds = externalIds;
    this.externalIdsLike = externalIdsLike;
    this.custom1 = custom1;
    this.custom1Like = custom1Like;
    this.custom2 = custom2;
    this.custom2Like = custom2Like;
    this.custom3 = custom3;
    this.custom3Like = custom3Like;
    this.custom4 = custom4;
    this.custom4Like = custom4Like;
    this.custom5 = custom5;
    this.custom5Like = custom5Like;
    this.custom6 = custom6;
    this.custom6Like = custom6Like;
    this.custom7 = custom7;
    this.custom7Like = custom7Like;
    this.custom8 = custom8;
    this.custom8Like = custom8Like;
    this.custom9 = custom9;
    this.custom9Like = custom9Like;
    this.custom10 = custom10;
    this.custom10Like = custom10Like;
    this.custom11 = custom11;
    this.custom11Like = custom11Like;
    this.custom12 = custom12;
    this.custom12Like = custom12Like;
    this.custom13 = custom13;
    this.custom13Like = custom13Like;
    this.custom14 = custom14;
    this.custom14Like = custom14Like;
    this.custom15 = custom15;
    this.custom15Like = custom15Like;
    this.custom16 = custom16;
    this.custom16Like = custom16Like;

    validateFilterParameters();
  }

  @Override
  public Void applyToQuery(TaskQuery query) {
    Optional.ofNullable(name).ifPresent(query::nameIn);
    Optional.ofNullable(nameLike).map(this::wrapElementsInLikeStatement).ifPresent(query::nameLike);
    Optional.ofNullable(priority).ifPresent(query::priorityIn);
    Optional.ofNullable(state).ifPresent(query::stateIn);
    Optional.ofNullable(classificationKeys).ifPresent(query::classificationKeyIn);
    Optional.ofNullable(classificationKeysLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::classificationKeyLike);
    Optional.ofNullable(classificationCategories).ifPresent(query::classificationCategoryIn);
    Optional.ofNullable(classificationCategoriesLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::classificationCategoryLike);
    Optional.ofNullable(classificationNames).ifPresent(query::classificationNameIn);
    Optional.ofNullable(classificationNamesLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::classificationNameLike);
    Optional.ofNullable(attachmentClassificationNames)
        .ifPresent(query::attachmentClassificationNameIn);
    Optional.ofNullable(attachmentClassificationNamesLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::attachmentClassificationNameLike);
    Optional.ofNullable(parentBusinessProcessIds).ifPresent(query::parentBusinessProcessIdIn);
    Optional.ofNullable(parentBusinessProcessIdsLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::parentBusinessProcessIdLike);
    Optional.ofNullable(businessProcessIds).ifPresent(query::businessProcessIdIn);
    Optional.ofNullable(businessProcessIdsLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::businessProcessIdLike);
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
    Optional.ofNullable(creator).ifPresent(query::creatorIn);
    Optional.ofNullable(creatorLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::creatorLike);
    Optional.ofNullable(noteLike).map(this::wrapElementsInLikeStatement).ifPresent(query::noteLike);
    Optional.ofNullable(porCompany).ifPresent(query::primaryObjectReferenceCompanyIn);
    Optional.ofNullable(porCompanyLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::primaryObjectReferenceCompanyLike);
    Optional.ofNullable(porSystem).ifPresent(query::primaryObjectReferenceSystemIn);
    Optional.ofNullable(porSystemLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::primaryObjectReferenceSystemLike);
    Optional.ofNullable(porInstance).ifPresent(query::primaryObjectReferenceSystemInstanceIn);
    Optional.ofNullable(porInstanceLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::primaryObjectReferenceSystemInstanceLike);
    Optional.ofNullable(porType).ifPresent(query::primaryObjectReferenceTypeIn);
    Optional.ofNullable(porTypeLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::primaryObjectReferenceTypeLike);
    Optional.ofNullable(porValue).ifPresent(query::primaryObjectReferenceValueIn);
    Optional.ofNullable(porValueLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::primaryObjectReferenceValueLike);
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
    Optional.ofNullable(externalIdsLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::externalIdLike);
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
    Stream.of(
            Pair.of(TaskCustomField.CUSTOM_1, custom1Like),
            Pair.of(TaskCustomField.CUSTOM_2, custom2Like),
            Pair.of(TaskCustomField.CUSTOM_3, custom3Like),
            Pair.of(TaskCustomField.CUSTOM_4, custom4Like),
            Pair.of(TaskCustomField.CUSTOM_5, custom5Like),
            Pair.of(TaskCustomField.CUSTOM_6, custom6Like),
            Pair.of(TaskCustomField.CUSTOM_7, custom7Like),
            Pair.of(TaskCustomField.CUSTOM_8, custom8Like),
            Pair.of(TaskCustomField.CUSTOM_9, custom9Like),
            Pair.of(TaskCustomField.CUSTOM_10, custom10Like),
            Pair.of(TaskCustomField.CUSTOM_11, custom11Like),
            Pair.of(TaskCustomField.CUSTOM_12, custom12Like),
            Pair.of(TaskCustomField.CUSTOM_13, custom13Like),
            Pair.of(TaskCustomField.CUSTOM_14, custom14Like),
            Pair.of(TaskCustomField.CUSTOM_15, custom15Like),
            Pair.of(TaskCustomField.CUSTOM_16, custom16Like))
        .forEach(
            pair ->
                Optional.ofNullable(pair.getRight())
                    .ifPresent(
                        wrap(
                            l ->
                                query.customAttributeLike(
                                    pair.getLeft(), wrapElementsInLikeStatement(l)))));
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
