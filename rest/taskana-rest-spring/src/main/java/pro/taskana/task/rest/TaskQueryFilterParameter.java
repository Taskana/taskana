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
import pro.taskana.task.api.CallbackState;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskQuery;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.WildcardSearchField;
import pro.taskana.task.api.models.ObjectReference;

public class TaskQueryFilterParameter implements QueryParameter<TaskQuery, Void> {

  /** Filter by the name of the task. This is an exact match. */
  @JsonProperty("name")
  private final String[] name;
  /**
   * Filter by the name of the task. This results in a substring search (% is appended to the front
   * and end of the requested value). Further SQL "LIKE" wildcard characters will be resolved
   * correctly.
   */
  @JsonProperty("name-like")
  private final String[] nameLike;

  /** Filter by the priority of the task. This is an exact match. */
  @JsonProperty("priority")
  private final int[] priority;

  /** Filter by the task state. This is an exact match. */
  @JsonProperty("state")
  private final TaskState[] state;

  /** Filter by the classification id of the task. This is an exact match. */
  @JsonProperty("classification-id")
  private final String[] classificationId;

  /** Filter by the classification key of the task. This is an exact match. */
  @JsonProperty("classification.key")
  private final String[] classificationKeys;

  /**
   * Filter by the classification key of the task. This results in a substring search (% is appended
   * to the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("classification-key-like")
  private final String[] classificationKeysLike;

  /** Filter by the classification key of the task. This is an exact match. */
  @JsonProperty("classification-key-not-in")
  private final String[] classificationKeysNotIn;

  /** Filter by the is read flag of the task. This is an exact match. */
  @JsonProperty("is-read")
  private final Boolean isRead;

  /** Filter by the is transferred flag of the task. This is an exact match. */
  @JsonProperty("is-transferred")
  private final Boolean isTransferred;

  /** Filter by the primary object reference of the task. This is an exact match. */
  @JsonProperty("object-reference")
  private final ObjectReference[] objectReferences;

  /** Filter by the callback state of the task. This is an exact match. */
  @JsonProperty("callback-state")
  private final CallbackState[] callbackStates;

  /** Filter by the attachment classification key of the task. This is an exact match. */
  @JsonProperty("attachment-classification-key")
  private final String[] attachmentClassificationKeys;

  /**
   * Filter by the attachment classification key of the task. This results in a substring search (%
   * is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("attachment-classification-key-like")
  private final String[] attachmentClassificationKeysLike;

  /** Filter by the attachment classification id of the task. This is an exact match. */
  @JsonProperty("attachment-classification-id")
  private final String[] attachmentClassificationId;

  /**
   * Filter by the attachment classification id of the task. This results in a substring search (%
   * is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("attachment-classification-id-like")
  private final String[] attachmentClassificationIdLike;

  /** Filter by the attachment channel of the task. This is an exact match. */
  @JsonProperty("attachment-channel")
  private final String[] attachmentChannel;

  /**
   * Filter by the attachment channel of the task. This results in a substring search (% is appended
   * to the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("attachment-channel-like")
  private final String[] attachmentChannelLike;

  /** Filter by the attachment reference of the task. This is an exact match. */
  @JsonProperty("attachment-reference")
  private final String[] attachmentReference;

  /**
   * Filter by the attachment reference of the task. This results in a substring search (% is
   * appended to the front and end of the requested value). Further SQL "LIKE" wildcard characters
   * will be resolved correctly.
   */
  @JsonProperty("attachment-reference-like")
  private final String[] attachmentReferenceLike;

  /**
   * Filter by a time interval within which the attachment of the task was received. To create an
   * open interval you can just leave it blank.
   *
   * <p>The format is ISO-8601.
   */
  @JsonProperty("attachment-received")
  private final Instant[] attachmentReceived;

  /**
   * Filter by a time interval within which the task was created. To create an open interval you can
   * just leave it blank.
   *
   * <p>The format is ISO-8601.
   */
  @JsonProperty("created")
  private final Instant[] created;

  /**
   * Filter since a given created timestamp.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'created'.
   */
  @JsonProperty("created-from")
  private final Instant createdFrom;

  /**
   * Filter until a given created timestamp.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'created'.
   */
  @JsonProperty("created-until")
  private final Instant createdUntil;

  /**
   * Filter by a time interval within which the task was claimed. To create an open interval you can
   * just leave it blank.
   *
   * <p>The format is ISO-8601.
   */
  @JsonProperty("claimed")
  private final Instant[] claimed;

  /**
   * Filter by a time interval within which the task was completed. To create an open interval you
   * can just leave it blank.
   *
   * <p>The format is ISO-8601.
   */
  @JsonProperty("completed")
  private final Instant[] completed;

  /**
   * Filter since a given completed timestamp.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'completed'.
   */
  @JsonProperty("completed-from")
  private final Instant completedFrom;

  /**
   * Filter until a given completed timestamp.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'completed'.
   */
  @JsonProperty("completed-until")
  private final Instant completedUntil;

  /**
   * Filter by a time interval within which the task was modified. To create an open interval you
   * can just leave it blank.
   *
   * <p>The format is ISO-8601.
   */
  @JsonProperty("modified")
  private final Instant[] modified;

  /** Filter by the classification category of the task. This is an exact match. */
  @JsonProperty("classification-category")
  private final String[] classificationCategories;

  /**
   * Filter by the classification category of the task. This results in a substring search (% is
   * appended to the front and end of the requested value). Further SQL "LIKE" wildcard characters
   * will be resolved correctly.
   */
  @JsonProperty("classification-category-like")
  private final String[] classificationCategoriesLike;

  /** Filter by the classification name of the task. This is an exact match. */
  @JsonProperty("classification-name")
  private final String[] classificationNames;

  /**
   * Filter by the classification name of the task. This results in a substring search (% is
   * appended to the front and end of the requested value). Further SQL "LIKE" wildcard characters
   * will be resolved correctly.
   */
  @JsonProperty("classification-name-like")
  private final String[] classificationNamesLike;

  /** Filter by the attachment classification name of the task. This is an exact match. */
  @JsonProperty("attachment-classification-name")
  private final String[] attachmentClassificationNames;

  /**
   * Filter by the attachment classification name of the task. This results in a substring search (%
   * is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("attachment-classification-name-like")
  private final String[] attachmentClassificationNamesLike;

  /** Filter by the parent business process id of the task. This is an exact match. */
  @JsonProperty("parent-business-process-id")
  private final String[] parentBusinessProcessIds;

  /**
   * Filter by the parent business process id of the task. This results in a substring search (% is
   * appended to the front and end of the requested value). Further SQL "LIKE" wildcard characters
   * will be resolved correctly.
   */
  @JsonProperty("parent-business-process-id-like")
  private final String[] parentBusinessProcessIdsLike;

  /** Filter by the business process id of the task. This is an exact match. */
  @JsonProperty("business-process-id")
  private final String[] businessProcessIds;

  /**
   * Filter by the business process id of the task. This results in a substring search (% is
   * appended to the front and end of the requested value). Further SQL "LIKE" wildcard characters
   * will be resolved correctly.
   */
  @JsonProperty("business-process-id-like")
  private final String[] businessProcessIdsLike;

  /** Filter by task id. This is an exact match. */
  @JsonProperty("task-id")
  private final String[] taskIds;

  /** Filter by workbasket id of the task. This is an exact match. */
  @JsonProperty("workbasket-id")
  private final String[] workbasketIds;

  /**
   * Filter by workbasket keys of the task. This parameter can only be used in combination with
   * 'domain'
   */
  @JsonProperty("workbasket-key")
  private final String[] workbasketKeys;

  /** Filter by domain of the task. This is an exact match. */
  @JsonProperty("domain")
  private final String domain;

  /** Filter by owner of the task. This is an exact match. */
  @JsonProperty("owner")
  private final String[] owner;

  /**
   * Filter by the owner of the task. This results in a substring search (% is appended to the front
   * and end of the requested value). Further SQL "LIKE" wildcard characters will be resolved
   * correctly.
   */
  @JsonProperty("owner-like")
  private final String[] ownerLike;

  /** Filter by creator of the task. This is an exact match. */
  @JsonProperty("creator")
  private final String[] creator;

  /**
   * Filter by the creator of the task. This results in a substring search (% is appended to the
   * front and end of the requested value). Further SQL "LIKE" wildcard characters will be resolved
   * correctly.
   */
  @JsonProperty("creator-like")
  private final String[] creatorLike;

  /**
   * Filter by the note of the task. This results in a substring search (% is appended to the front
   * and end of the requested value). Further SQL "LIKE" wildcard characters will be resolved
   * correctly.
   */
  @JsonProperty("note-like")
  private final String[] noteLike;

  /** Filter by the company of the primary object reference of the task. This is an exact match. */
  @JsonProperty("por.company")
  private final String[] porCompany;

  /**
   * Filter by the company of the primary object reference of the task. This results in a substring
   * search (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("por-company-like")
  private final String[] porCompanyLike;

  /** Filter by the system of the primary object reference of the task. This is an exact match. */
  @JsonProperty("por.system")
  private final String[] porSystem;

  /**
   * Filter by the he system of the primary object reference of the task. This results in a
   * substring search (% is appended to the front and end of the requested value). Further SQL
   * "LIKE" wildcard characters will be resolved correctly.
   */
  @JsonProperty("por-system-like")
  private final String[] porSystemLike;

  /**
   * Filter by the system instance of the primary object reference of the task. This is an exact
   * match.
   */
  @JsonProperty("por.instance")
  private final String[] porInstance;

  /**
   * Filter by the system instance of the primary object reference of the task. This results in a
   * substring search (% is appended to the front and end of the requested value). Further SQL
   * "LIKE" wildcard characters will be resolved correctly.
   */
  @JsonProperty("por-instance-like")
  private final String[] porInstanceLike;

  /** Filter by the type of the primary object reference of the task. This is an exact match. */
  @JsonProperty("por.type")
  private final String[] porType;

  /**
   * Filter by the type of the primary object reference of the task. This results in a substring
   * search (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("por-type-like")
  private final String[] porTypeLike;

  /** Filter by the value of the primary object reference of the task. This is an exact match. */
  @JsonProperty("por.value")
  private final String[] porValue;

  /**
   * Filter by the value of the primary object reference of the task. This results in a substring
   * search (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("por-value-like")
  private final String[] porValueLike;

  /**
   * Filter by a time interval within which the task was planned. To create an open interval you can
   * just leave it blank.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'planned-from' or 'planned-until'.
   */
  @JsonProperty("planned")
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
   * Filter by a time interval within which the task was received. To create an open interval you
   * can just leave it blank.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'received-from' or 'received-until'.
   */
  private final Instant[] received;

  /**
   * Filter since a given received timestamp.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'received'.
   */
  @JsonProperty("received-from")
  private final Instant receivedFrom;

  /**
   * Filter until a given received timestamp.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'received'.
   */
  @JsonProperty("received-until")
  private final Instant receivedUntil;

  /**
   * Filter by a time interval within which the task was due. To create an open interval you can
   * just leave it blank.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'due-from' or 'due-until'.
   */
  @JsonProperty("due")
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
   * Filter by wildcard search field of the task.
   *
   * <p>This must be used in combination with 'wildcard-search-value'
   */
  @JsonProperty("wildcard-search-fields")
  private final WildcardSearchField[] wildcardSearchFields;

  /**
   * Filter by wildcard search field of the task. This is an exact match.
   *
   * <p>This must be used in combination with 'wildcard-search-value'
   */
  @JsonProperty("wildcard-search-value")
  private final String wildcardSearchValue;

  /** Filter by the external id of the task. This is an exact match. */
  @JsonProperty("external-id")
  private final String[] externalIds;
  /**
   * Filter by the externalId of the task. This results in a substring search (% is appended to the
   * front and end of the requested value). Further SQL "LIKE" wildcard characters will be resolved
   * correctly.
   */
  @JsonProperty("external-id-like")
  private final String[] externalIdsLike;

  /** Filter by the value of the field custom1 of the task. This is an exact match. */
  @JsonProperty("custom-1")
  private final String[] custom1;

  /** Exclude values of the field custom1 of the task. */
  @JsonProperty("custom-1-not-in")
  private final String[] custom1NotIn;

  /**
   * Filter by the custom1 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-1-like")
  private final String[] custom1Like;

  /** Filter by the value of the field custom2 of the task. This is an exact match. */
  @JsonProperty("custom-2")
  private final String[] custom2;

  /** Filter out by values of the field custom2 of the task. This is an exact match. */
  @JsonProperty("custom-2-not-in")
  private final String[] custom2NotIn;

  /**
   * Filter by the custom2 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-2-like")
  private final String[] custom2Like;

  /** Filter by the value of the field custom3 of the task. This is an exact match. */
  @JsonProperty("custom-3")
  private final String[] custom3;

  /** Filter out by values of the field custom3 of the task. This is an exact match. */
  @JsonProperty("custom-3-not-in")
  private final String[] custom3NotIn;

  /**
   * Filter by the custom3 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-3-like")
  private final String[] custom3Like;

  /** Filter by the value of the field custom4 of the task. This is an exact match. */
  @JsonProperty("custom-4")
  private final String[] custom4;

  /** Filter out by values of the field custom4 of the task. This is an exact match. */
  @JsonProperty("custom-4-not-in")
  private final String[] custom4NotIn;

  /**
   * Filter by the custom4 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-4-like")
  private final String[] custom4Like;

  /** Filter by the value of the field custom5 of the task. This is an exact match. */
  @JsonProperty("custom-5")
  private final String[] custom5;

  /** Filter out by values of the field custom5 of the task. This is an exact match. */
  @JsonProperty("custom-5-not-in")
  private final String[] custom5NotIn;

  /**
   * Filter by the custom5 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-5-like")
  private final String[] custom5Like;

  /** Filter by the value of the field custom6 of the task. This is an exact match. */
  @JsonProperty("custom-6")
  private final String[] custom6;

  /** Filter out by values of the field custom6 of the task. This is an exact match. */
  @JsonProperty("custom-6-not-in")
  private final String[] custom6NotIn;

  /**
   * Filter by the custom6 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-6-like")
  private final String[] custom6Like;

  /** Filter by the value of the field custom7 of the task. This is an exact match. */
  @JsonProperty("custom-7")
  private final String[] custom7;

  /** Filter out by values of the field custom7 of the task. This is an exact match. */
  @JsonProperty("custom-7-not-in")
  private final String[] custom7NotIn;

  /**
   * Filter by the custom7 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-7-like")
  private final String[] custom7Like;

  /** Filter by the value of the field custom8 of the task. This is an exact match. */
  @JsonProperty("custom-8")
  private final String[] custom8;

  /** Filter out by values of the field custom8 of the task. This is an exact match. */
  @JsonProperty("custom-8-not-in")
  private final String[] custom8NotIn;

  /**
   * Filter by the custom8 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-8-like")
  private final String[] custom8Like;

  /** Filter by the value of the field custom9 of the task. This is an exact match. */
  @JsonProperty("custom-9")
  private final String[] custom9;

  /** Filter out by values of the field custom9 of the task. This is an exact match. */
  @JsonProperty("custom-9-not-in")
  private final String[] custom9NotIn;

  /**
   * Filter by the custom9 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-9-like")
  private final String[] custom9Like;

  /** Filter by the value of the field custom10 of the task. This is an exact match. */
  @JsonProperty("custom-10")
  private final String[] custom10;

  /** Filter out by values of the field custom10 of the task. This is an exact match. */
  @JsonProperty("custom-10-not-in")
  private final String[] custom10NotIn;

  /**
   * Filter by the custom10 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-10-like")
  private final String[] custom10Like;

  /** Filter by the value of the field custom11 of the task. This is an exact match. */
  @JsonProperty("custom-11")
  private final String[] custom11;

  /** Filter out by values of the field custom11 of the task. This is an exact match. */
  @JsonProperty("custom-11-not-in")
  private final String[] custom11NotIn;

  /**
   * Filter by the custom11 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-11-like")
  private final String[] custom11Like;

  /** Filter by the value of the field custom12 of the task. This is an exact match. */
  @JsonProperty("custom-12")
  private final String[] custom12;

  /** Filter out by values of the field custom12 of the task. This is an exact match. */
  @JsonProperty("custom-12-not-in")
  private final String[] custom12NotIn;

  /**
   * Filter by the custom12 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-12-like")
  private final String[] custom12Like;

  /** Filter by the value of the field custom13 of the task. This is an exact match. */
  @JsonProperty("custom-13")
  private final String[] custom13;

  /** Filter out by values of the field custom13 of the task. This is an exact match. */
  @JsonProperty("custom-13-not-in")
  private final String[] custom13NotIn;

  /**
   * Filter by the custom13 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-13-like")
  private final String[] custom13Like;

  /** Filter by the value of the field custom14 of the task. This is an exact match. */
  @JsonProperty("custom-14")
  private final String[] custom14;

  /** Filter out by values of the field custom14 of the task. This is an exact match. */
  @JsonProperty("custom-14-not-in")
  private final String[] custom14NotIn;

  /**
   * Filter by the custom14 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-14-like")
  private final String[] custom14Like;

  /** Filter by the value of the field custom15 of the task. This is an exact match. */
  @JsonProperty("custom-15")
  private final String[] custom15;

  /** Filter out by values of the field custom15 of the task. This is an exact match. */
  @JsonProperty("custom-15-not-in")
  private final String[] custom15NotIn;

  /**
   * Filter by the custom15 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-15-like")
  private final String[] custom15Like;
  /** Filter by the value of the field custom16 of the task. This is an exact match. */
  @JsonProperty("custom-16")
  private final String[] custom16;
  /** Filter out by values of the field custom16 of the task. This is an exact match. */
  @JsonProperty("custom-16-not-in")
  private final String[] custom16NotIn;
  /**
   * Filter by the custom16 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
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
    "classification-id",
    "classification.key",
    "classification-key-like",
    "classification-key-not-in",
    "is-read",
    "is-transferred",
    "object-reference",
    "callback-state",
    "attachment-classification-key",
    "attachment-classification-key-like",
    "attachment-classification-id",
    "attachment-classification-id-like",
    "attachment-channel",
    "attachment-channel-like",
    "attachment-reference",
    "attachment-reference-like",
    "attachment-received",
    "created",
    "created-from",
    "created-until",
    "claimed",
    "completed",
    "completed-from",
    "complete-until",
    "modified",
    "classification-category",
    "classification-category-like",
    "classification-name",
    "classification-name-like",
    "attachment-classification-name",
    "attachment-classification-name-like",
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
    "por-company-like",
    "por.system",
    "por-system-like",
    "por.instance",
    "por-instance-like",
    "por.type",
    "por-type-like",
    "por.value",
    "por-value-like",
    "planned",
    "planned-from",
    "planned-until",
    "received",
    "received-from",
    "received-until",
    "due",
    "due-from",
    "due-until",
    "wildcard-search-fields",
    "wildcard-search-value",
    "external-id",
    "external-id-like",
    "custom-1",
    "custom-1-not-in",
    "custom-1-like",
    "custom-2",
    "custom-2-not-in",
    "custom-2-like",
    "custom-3",
    "custom-3-not-in",
    "custom-3-like",
    "custom-4",
    "custom-4-not-in",
    "custom-4-like",
    "custom-5",
    "custom-5-not-in",
    "custom-5-like",
    "custom-6",
    "custom-6-not-in",
    "custom-6-like",
    "custom-7",
    "custom-7-not-in",
    "custom-7-like",
    "custom-8",
    "custom-8-not-in",
    "custom-8-like",
    "custom-9",
    "custom-9-not-in",
    "custom-9-like",
    "custom-10",
    "custom-10-not-in",
    "custom-10-like",
    "custom-11",
    "custom-11-not-in",
    "custom-11-like",
    "custom-12",
    "custom-12-not-in",
    "custom-12-like",
    "custom-13",
    "custom-13-not-in",
    "custom-13-like",
    "custom-14",
    "custom-14-not-in",
    "custom-14-like",
    "custom-15",
    "custom-15-not-in",
    "custom-15-like",
    "custom-16",
    "custom-16-not-in",
    "custom-16-like"
  })
  public TaskQueryFilterParameter(
      String[] name,
      String[] nameLike,
      int[] priority,
      TaskState[] state,
      String[] classificationId,
      String[] classificationKeys,
      String[] classificationKeysLike,
      String[] classificationKeysNotIn,
      Boolean isRead,
      Boolean isTransferred,
      ObjectReference[] objectReferences,
      CallbackState[] callbackStates,
      String[] attachmentClassificationKeys,
      String[] attachmentClassificationKeysLike,
      String[] attachmentClassificationId,
      String[] attachmentClassificationIdLike,
      String[] attachmentChannel,
      String[] attachmentChannelLike,
      String[] attachmentReference,
      String[] attachmentReferenceLike,
      Instant[] attachmentReceived,
      Instant[] created,
      Instant createdFrom,
      Instant createdUntil,
      Instant[] claimed,
      Instant[] completed,
      Instant completedFrom,
      Instant completedUntil,
      Instant[] modified,
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
      Instant[] received,
      Instant receivedFrom,
      Instant receivedUntil,
      Instant[] due,
      Instant dueFrom,
      Instant dueUntil,
      WildcardSearchField[] wildcardSearchFields,
      String wildcardSearchValue,
      String[] externalIds,
      String[] externalIdsLike,
      String[] custom1,
      String[] custom1NotIn,
      String[] custom1Like,
      String[] custom2,
      String[] custom2NotIn,
      String[] custom2Like,
      String[] custom3,
      String[] custom3NotIn,
      String[] custom3Like,
      String[] custom4,
      String[] custom4NotIn,
      String[] custom4Like,
      String[] custom5,
      String[] custom5NotIn,
      String[] custom5Like,
      String[] custom6,
      String[] custom6NotIn,
      String[] custom6Like,
      String[] custom7,
      String[] custom7NotIn,
      String[] custom7Like,
      String[] custom8,
      String[] custom8NotIn,
      String[] custom8Like,
      String[] custom9,
      String[] custom9NotIn,
      String[] custom9Like,
      String[] custom10,
      String[] custom10NotIn,
      String[] custom10Like,
      String[] custom11,
      String[] custom11NotIn,
      String[] custom11Like,
      String[] custom12,
      String[] custom12NotIn,
      String[] custom12Like,
      String[] custom13,
      String[] custom13NotIn,
      String[] custom13Like,
      String[] custom14,
      String[] custom14NotIn,
      String[] custom14Like,
      String[] custom15,
      String[] custom15NotIn,
      String[] custom15Like,
      String[] custom16,
      String[] custom16NotIn,
      String[] custom16Like)
      throws InvalidArgumentException {
    this.name = name;
    this.nameLike = nameLike;
    this.priority = priority;
    this.state = state;
    this.classificationId = classificationId;
    this.classificationKeys = classificationKeys;
    this.classificationKeysLike = classificationKeysLike;
    this.classificationKeysNotIn = classificationKeysNotIn;
    this.isRead = isRead;
    this.isTransferred = isTransferred;
    this.objectReferences = objectReferences;
    this.callbackStates = callbackStates;
    this.attachmentClassificationKeys = attachmentClassificationKeys;
    this.attachmentClassificationKeysLike = attachmentClassificationKeysLike;
    this.attachmentClassificationId = attachmentClassificationId;
    this.attachmentClassificationIdLike = attachmentClassificationIdLike;
    this.attachmentChannel = attachmentChannel;
    this.attachmentChannelLike = attachmentChannelLike;
    this.attachmentReference = attachmentReference;
    this.attachmentReferenceLike = attachmentReferenceLike;
    this.attachmentReceived = attachmentReceived;
    this.created = created;
    this.createdFrom = createdFrom;
    this.createdUntil = createdUntil;
    this.claimed = claimed;
    this.completed = completed;
    this.completedFrom = completedFrom;
    this.completedUntil = completedUntil;
    this.modified = modified;
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
    this.received = received;
    this.receivedFrom = receivedFrom;
    this.receivedUntil = receivedUntil;
    this.due = due;
    this.dueFrom = dueFrom;
    this.dueUntil = dueUntil;
    this.wildcardSearchFields = wildcardSearchFields;
    this.wildcardSearchValue = wildcardSearchValue;
    this.externalIds = externalIds;
    this.externalIdsLike = externalIdsLike;
    this.custom1 = custom1;
    this.custom1NotIn = custom1NotIn;
    this.custom1Like = custom1Like;
    this.custom2 = custom2;
    this.custom2NotIn = custom2NotIn;
    this.custom2Like = custom2Like;
    this.custom3 = custom3;
    this.custom3NotIn = custom3NotIn;
    this.custom3Like = custom3Like;
    this.custom4 = custom4;
    this.custom4NotIn = custom4NotIn;
    this.custom4Like = custom4Like;
    this.custom5 = custom5;
    this.custom5NotIn = custom5NotIn;
    this.custom5Like = custom5Like;
    this.custom6 = custom6;
    this.custom6NotIn = custom6NotIn;
    this.custom6Like = custom6Like;
    this.custom7 = custom7;
    this.custom7NotIn = custom7NotIn;
    this.custom7Like = custom7Like;
    this.custom8 = custom8;
    this.custom8NotIn = custom8NotIn;
    this.custom8Like = custom8Like;
    this.custom9 = custom9;
    this.custom9NotIn = custom9NotIn;
    this.custom9Like = custom9Like;
    this.custom10 = custom10;
    this.custom10NotIn = custom10NotIn;
    this.custom10Like = custom10Like;
    this.custom11 = custom11;
    this.custom11NotIn = custom11NotIn;
    this.custom11Like = custom11Like;
    this.custom12 = custom12;
    this.custom12NotIn = custom12NotIn;
    this.custom12Like = custom12Like;
    this.custom13 = custom13;
    this.custom13NotIn = custom13NotIn;
    this.custom13Like = custom13Like;
    this.custom14 = custom14;
    this.custom14NotIn = custom14NotIn;
    this.custom14Like = custom14Like;
    this.custom15 = custom15;
    this.custom15NotIn = custom15NotIn;
    this.custom15Like = custom15Like;
    this.custom16 = custom16;
    this.custom16NotIn = custom16NotIn;
    this.custom16Like = custom16Like;

    validateFilterParameters();
  }

  @Override
  public Void applyToQuery(TaskQuery query) {
    Optional.ofNullable(name).ifPresent(query::nameIn);
    Optional.ofNullable(nameLike).map(this::wrapElementsInLikeStatement).ifPresent(query::nameLike);
    Optional.ofNullable(priority).ifPresent(query::priorityIn);
    Optional.ofNullable(state).ifPresent(query::stateIn);
    Optional.ofNullable(classificationId).ifPresent(query::classificationIdIn);
    Optional.ofNullable(classificationKeys).ifPresent(query::classificationKeyIn);
    Optional.ofNullable(classificationKeysLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::classificationKeyLike);
    Optional.ofNullable(classificationKeysNotIn).ifPresent(query::classificationKeyNotIn);
    Optional.ofNullable(isRead).ifPresent(query::readEquals);
    Optional.ofNullable(isTransferred).ifPresent(query::transferredEquals);
    Optional.ofNullable(objectReferences).ifPresent(query::primaryObjectReferenceIn);
    Optional.ofNullable(callbackStates).ifPresent(query::callbackStateIn);
    Optional.ofNullable(attachmentClassificationKeys)
        .ifPresent(query::attachmentClassificationKeyIn);
    Optional.ofNullable(attachmentClassificationKeysLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::attachmentClassificationKeyLike);
    Optional.ofNullable(attachmentClassificationId).ifPresent(query::attachmentClassificationIdIn);
    Optional.ofNullable(attachmentClassificationIdLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::attachmentClassificationIdLike);
    Optional.ofNullable(attachmentChannel).ifPresent(query::attachmentChannelIn);
    Optional.ofNullable(attachmentChannelLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::attachmentChannelLike);
    Optional.ofNullable(attachmentReference).ifPresent(query::attachmentReferenceValueIn);
    Optional.ofNullable(attachmentReferenceLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::attachmentReferenceValueLike);
    Optional.ofNullable(attachmentReceived)
        .map(this::extractTimeIntervals)
        .ifPresent(query::attachmentReceivedWithin);
    Optional.ofNullable(created).map(this::extractTimeIntervals).ifPresent(query::createdWithin);
    if (createdFrom != null || createdUntil != null) {
      query.createdWithin(new TimeInterval(createdFrom, createdUntil));
    }
    Optional.ofNullable(claimed).map(this::extractTimeIntervals).ifPresent(query::claimedWithin);
    Optional.ofNullable(completed)
        .map(this::extractTimeIntervals)
        .ifPresent(query::completedWithin);
    if (completedFrom != null || completedUntil != null) {
      query.completedWithin(new TimeInterval(completedFrom, completedUntil));
    }
    Optional.ofNullable(modified).map(this::extractTimeIntervals).ifPresent(query::modifiedWithin);
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
    if (plannedFrom != null || plannedUntil != null) {
      query.plannedWithin(new TimeInterval(plannedFrom, plannedUntil));
    }
    Optional.ofNullable(received).map(this::extractTimeIntervals).ifPresent(query::receivedWithin);
    if (receivedFrom != null || receivedUntil != null) {
      query.receivedWithin(new TimeInterval(receivedFrom, receivedUntil));
    }
    Optional.ofNullable(due).map(this::extractTimeIntervals).ifPresent(query::dueWithin);
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
            Pair.of(TaskCustomField.CUSTOM_1, custom1NotIn),
            Pair.of(TaskCustomField.CUSTOM_2, custom2NotIn),
            Pair.of(TaskCustomField.CUSTOM_3, custom3NotIn),
            Pair.of(TaskCustomField.CUSTOM_4, custom4NotIn),
            Pair.of(TaskCustomField.CUSTOM_5, custom5NotIn),
            Pair.of(TaskCustomField.CUSTOM_6, custom6NotIn),
            Pair.of(TaskCustomField.CUSTOM_7, custom7NotIn),
            Pair.of(TaskCustomField.CUSTOM_8, custom8NotIn),
            Pair.of(TaskCustomField.CUSTOM_9, custom9NotIn),
            Pair.of(TaskCustomField.CUSTOM_10, custom10NotIn),
            Pair.of(TaskCustomField.CUSTOM_11, custom11NotIn),
            Pair.of(TaskCustomField.CUSTOM_12, custom12NotIn),
            Pair.of(TaskCustomField.CUSTOM_13, custom13NotIn),
            Pair.of(TaskCustomField.CUSTOM_14, custom14NotIn),
            Pair.of(TaskCustomField.CUSTOM_15, custom15NotIn),
            Pair.of(TaskCustomField.CUSTOM_16, custom16NotIn))
        .forEach(
            pair ->
                Optional.ofNullable(pair.getRight())
                    .ifPresent(wrap(l -> query.customAttributeNotIn(pair.getLeft(), l))));
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
                    .map(this::wrapElementsInLikeStatement)
                    .ifPresent(wrap(l -> query.customAttributeLike(pair.getLeft(), l))));
    return null;
  }

  private void validateFilterParameters() throws InvalidArgumentException {
    if (planned != null && (plannedFrom != null || plannedUntil != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'planned' in combination "
              + "with the params 'planned-from'  and / or 'planned-until'");
    }

    if (received != null && (receivedFrom != null || receivedUntil != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'received' in combination "
              + "with the params 'received-from'  and / or 'received-until'");
    }

    if (due != null && (dueFrom != null || dueUntil != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'due' in combination with the params "
              + "'due-from'  and / or 'due-until'");
    }

    if (created != null && (createdFrom != null || createdUntil != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'created' in combination with the params "
              + "'created-from'  and / or 'created-until'");
    }

    if (completed != null && (completedFrom != null || completedUntil != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'completed' in combination with the params "
              + "'completed-from'  and / or 'completed-until'");
    }

    if (wildcardSearchFields == null ^ wildcardSearchValue == null) {
      throw new InvalidArgumentException(
          "The params 'wildcard-search-field' and 'wildcard-search-value' must be used together");
    }

    if (workbasketKeys != null && domain == null) {
      throw new InvalidArgumentException("'workbasket-key' requires exactly one domain.");
    }

    if (planned != null && planned.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'planned' is not dividable by 2");
    }

    if (received != null && received.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'received' is not dividable by 2");
    }

    if (due != null && due.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'due' is not dividable by 2");
    }

    if (modified != null && modified.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'modified' is not dividable by 2");
    }

    if (created != null && created.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'created' is not dividable by 2");
    }

    if (completed != null && completed.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'completed' is not dividable by 2");
    }

    if (claimed != null && claimed.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'claimed' is not dividable by 2");
    }

    if (attachmentReceived != null && attachmentReceived.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'attachmentReceived' is not dividable by 2");
    }
  }
}
