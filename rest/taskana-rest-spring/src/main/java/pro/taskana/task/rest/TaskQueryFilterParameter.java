package pro.taskana.task.rest;

import static pro.taskana.common.internal.util.CheckedConsumer.wrap;
import static pro.taskana.common.internal.util.Quadruple.of;
import static pro.taskana.task.api.TaskCustomField.CUSTOM_1;
import static pro.taskana.task.api.TaskCustomField.CUSTOM_10;
import static pro.taskana.task.api.TaskCustomField.CUSTOM_11;
import static pro.taskana.task.api.TaskCustomField.CUSTOM_12;
import static pro.taskana.task.api.TaskCustomField.CUSTOM_13;
import static pro.taskana.task.api.TaskCustomField.CUSTOM_14;
import static pro.taskana.task.api.TaskCustomField.CUSTOM_15;
import static pro.taskana.task.api.TaskCustomField.CUSTOM_16;
import static pro.taskana.task.api.TaskCustomField.CUSTOM_2;
import static pro.taskana.task.api.TaskCustomField.CUSTOM_3;
import static pro.taskana.task.api.TaskCustomField.CUSTOM_4;
import static pro.taskana.task.api.TaskCustomField.CUSTOM_5;
import static pro.taskana.task.api.TaskCustomField.CUSTOM_6;
import static pro.taskana.task.api.TaskCustomField.CUSTOM_7;
import static pro.taskana.task.api.TaskCustomField.CUSTOM_8;
import static pro.taskana.task.api.TaskCustomField.CUSTOM_9;

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
import pro.taskana.task.api.TaskQuery;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.WildcardSearchField;
import pro.taskana.task.api.models.ObjectReference;

public class TaskQueryFilterParameter implements QueryParameter<TaskQuery, Void> {

  // region id
  /** Filter by task id. This is an exact match. */
  @JsonProperty("task-id")
  private final String[] taskIdIn;

  /** Filter by what the task id shouldn't be. This is an exact match. */
  @JsonProperty("task-id-not")
  private final String[] taskIdNotIn;
  // endregion
  // region externalId
  /** Filter by the external id of the task. This is an exact match. */
  @JsonProperty("external-id")
  private final String[] externalIdIn;

  /** Filter by what the external id of the task shouldn't be. This is an exact match. */
  @JsonProperty("external-id-not")
  private final String[] externalIdNotIn;
  // endregion
  // region received
  /**
   * Filter by a time interval within which the task was received. To create an open interval you
   * can just leave it blank.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'received-from' or 'received-until'.
   */
  @JsonProperty("received")
  private final Instant[] receivedWithin;

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
   * Filter by a time interval within which the task wasn't received. To create an open interval you
   * can just leave it blank.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'received-not-in-from' or
   * 'received-not-in-until'.
   */
  @JsonProperty("received-not")
  private final Instant[] receivedNotIn;

  /**
   * Filter since a given timestamp where it wasn't received.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'received-not-in'.
   */
  @JsonProperty("received-from-not")
  private final Instant receivedFromNot;

  /**
   * Filter until a given timestamp where it wasn't received.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'received-not-in'.
   */
  @JsonProperty("received-until-not")
  private final Instant receivedUntilNot;
  // endregion
  // region created
  /**
   * Filter by a time interval within which the task was created. To create an open interval you can
   * just leave it blank.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'created-from' or 'created-until'.
   */
  @JsonProperty("created")
  private final Instant[] createdWithin;

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
   * Filter by a time interval within which the task wasn't created. To create an open interval you
   * can just leave it blank.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'created-not-in-from' or 'created-not-in-until'.
   */
  @JsonProperty("created-not")
  private final Instant[] createdNotWithin;

  /**
   * Filter not since a given timestamp where it wasn't created.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'created-not-in'.
   */
  @JsonProperty("created-from-not")
  private final Instant createdFromNot;

  /**
   * Filter not until a given timestamp where it wasn't created.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'created-not-in'.
   */
  @JsonProperty("created-until-not")
  private final Instant createdUntilNot;
  // endregion
  // region claimed
  /**
   * Filter by a time interval within which the task was claimed. To create an open interval you can
   * just leave it blank.
   *
   * <p>The format is ISO-8601.
   */
  @JsonProperty("claimed")
  private final Instant[] claimedWithin;

  /**
   * Filter by a time interval within which the task wasn't claimed. To create an open interval you
   * can just leave it blank.
   *
   * <p>The format is ISO-8601.
   */
  @JsonProperty("claimed-not")
  private final Instant[] claimedNotWithin;
  // endregion
  // region modified
  /**
   * Filter by a time interval within which the task was modified. To create an open interval you
   * can just leave it blank.
   *
   * <p>The format is ISO-8601.
   */
  @JsonProperty("modified")
  private final Instant[] modifiedWithin;

  /**
   * Filter by a time interval within which the task wasn't modified. To create an open interval you
   * can just leave it blank.
   *
   * <p>The format is ISO-8601.
   */
  @JsonProperty("modified-not")
  private final Instant[] modifiedNotWithin;
  // endregion
  // region planned
  /**
   * Filter by a time interval within which the task was planned. To create an open interval you can
   * just leave it blank.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'planned-from' or 'planned-until'.
   */
  @JsonProperty("planned")
  private final Instant[] plannedWithin;

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
   * Filter by a time interval within which the task was planned. To create an open interval you can
   * just leave it blank.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'planned-not-in-from' or 'planned-not-in-until'.
   */
  @JsonProperty("planned-not")
  private final Instant[] plannedNotWithin;

  /**
   * Filter since a given timestamp where it wasn't planned.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'planned-not-in'.
   */
  @JsonProperty("planned-from-not")
  private final Instant plannedFromNot;

  /**
   * Filter until a given timestamp where it wasn't planned.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'planned-not-in'.
   */
  @JsonProperty("planned-until-not")
  private final Instant plannedUntilNot;
  // endregion
  // region due
  /**
   * Filter by a time interval within which the task was due. To create an open interval you can
   * just leave it blank.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'due-from' or 'due-until'.
   */
  @JsonProperty("due")
  private final Instant[] dueWithin;

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
   * Filter by a time interval within which the task wasn't due. To create an open interval you can
   * just leave it blank.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'due-not-in-from' or 'due-not-in-until'.
   */
  @JsonProperty("due-not")
  private final Instant[] dueNotWithin;

  /**
   * Filter since a given timestamp where it isn't due.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'due-not-in'.
   */
  @JsonProperty("due-from-not")
  private final Instant dueFromNot;

  /**
   * Filter until a given timestamp where it isn't due.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'due-not-in'.
   */
  @JsonProperty("due-until-not")
  private final Instant dueUntilNot;
  // endregion
  // region completed
  /**
   * Filter by a time interval within which the task was completed. To create an open interval you
   * can just leave it blank.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'completed-from' or 'completed-until'.
   */
  @JsonProperty("completed")
  private final Instant[] completedWithin;

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
   * Filter by a time interval within which the task wasn't completed. To create an open interval
   * you can just leave it blank.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'completed-not-in-from' or
   * 'completed-not-in-until'.
   */
  @JsonProperty("completed-not")
  private final Instant[] completedNotWithin;

  /**
   * Filter since a given timestamp where it wasn't completed.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'completed-not-in'.
   */
  @JsonProperty("completed-from-not")
  private final Instant completedFromNot;

  /**
   * Filter until a given timestamp where it wasn't completed.
   *
   * <p>The format is ISO-8601.
   *
   * <p>This parameter can't be used together with 'completed-not-in'.
   */
  @JsonProperty("completed-until-not")
  private final Instant completedUntilNot;
  // endregion
  // region name
  /** Filter by the name of the task. This is an exact match. */
  @JsonProperty("name")
  private final String[] nameIn;

  /** Filter by what the name of the task shouldn't be. This is an exact match. */
  @JsonProperty("name-not")
  private final String[] nameNotIn;

  /**
   * Filter by the name of the task. This results in a substring search (% is appended to the front
   * and end of the requested value). Further SQL "LIKE" wildcard characters will be resolved
   * correctly.
   */
  @JsonProperty("name-like")
  private final String[] nameLike;

  /**
   * Filter by what the name of the task shouldn't be. This results in a substring search (% is
   * appended to the front and end of the requested value). Further SQL "LIKE" wildcard characters
   * will be resolved correctly.
   */
  @JsonProperty("name-not-like")
  private final String[] nameNotLike;
  // endregion
  // region creator
  /** Filter by creator of the task. This is an exact match. */
  @JsonProperty("creator")
  private final String[] creatorIn;

  /** Filter by what the creator of the task shouldn't be. This is an exact match. */
  @JsonProperty("creator-not")
  private final String[] creatorNotIn;

  /**
   * Filter by the creator of the task. This results in a substring search (% is appended to the
   * front and end of the requested value). Further SQL "LIKE" wildcard characters will be resolved
   * correctly.
   */
  @JsonProperty("creator-like")
  private final String[] creatorLike;

  /**
   * Filter by what the creator of the task shouldn't be. This results in a substring search (% is
   * appended to the front and end of the requested value). Further SQL "LIKE" wildcard characters
   * will be resolved correctly.
   */
  @JsonProperty("creator-not-like")
  private final String[] creatorNotLike;
  // endregion
  // region note
  /**
   * Filter by the note of the task. This results in a substring search (% is appended to the front
   * and end of the requested value). Further SQL "LIKE" wildcard characters will be resolved
   * correctly.
   */
  @JsonProperty("note-like")
  private final String[] noteLike;

  /**
   * Filter by what the note of the task shouldn't be. This results in a substring search (% is
   * appended to the front and end of the requested value). Further SQL "LIKE" wildcard characters
   * will be resolved correctly.
   */
  @JsonProperty("note-not-like")
  private final String[] noteNotLike;
  // endregion
  // region description
  /**
   * Filter by the description of the task. This results in a substring search (% is appended to the
   * front and end of the requested value). Further SQL "LIKE" wildcard characters will be resolved
   * correctly.
   */
  @JsonProperty("description-like")
  private final String[] descriptionLike;

  /**
   * Filter by what the description of the task shouldn't be. This results in a substring search (%
   * is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("description-not-like")
  private final String[] descriptionNotLike;
  // endregion
  // region priority
  /** Filter by the priority of the task. This is an exact match. */
  @JsonProperty("priority")
  private final int[] priorityIn;

  /** Filter by what the priority of the task shouldn't be. This is an exact match. */
  @JsonProperty("priority-not")
  private final int[] priorityNotIn;
  // endregion
  // region state
  /** Filter by the task state. This is an exact match. */
  @JsonProperty("state")
  private final TaskState[] stateIn;

  /** Filter by what the task state shouldn't be. This is an exact match. */
  @JsonProperty("state-not")
  private final TaskState[] stateNotIn;
  // endregion
  // region classificationId
  /** Filter by the classification id of the task. This is an exact match. */
  @JsonProperty("classification-id")
  private final String[] classificationIdIn;

  /** Filter by what the classification id of the task shouldn't be. This is an exact match. */
  @JsonProperty("classification-id-not")
  private final String[] classificationIdNotIn;
  // endregion
  // region classificationKey
  /** Filter by the classification key of the task. This is an exact match. */
  @JsonProperty("classification.key")
  private final String[] classificationKeyIn;

  /** Filter by the classification key of the task. This is an exact match. */
  @JsonProperty("classification-key-not")
  private final String[] classificationKeyNotIn;

  /**
   * Filter by the classification key of the task. This results in a substring search (% is appended
   * to the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("classification-key-like")
  private final String[] classificationKeyLike;

  /**
   * Filter by what the classification key of the task shouldn't be. This results in a substring
   * search (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("classification-key-not-like")
  private final String[] classificationKeyNotLike;
  // endregion
  // region classificationCategory
  /** Filter by the classification category of the task. This is an exact match. */
  @JsonProperty("classification-category")
  private final String[] classificationCategoryIn;

  /**
   * Filter by what the classification category of the task shouldn't be. This is an exact match.
   */
  @JsonProperty("classification-category-not")
  private final String[] classificationCategoryNotIn;

  /**
   * Filter by the classification category of the task. This results in a substring search (% is
   * appended to the front and end of the requested value). Further SQL "LIKE" wildcard characters
   * will be resolved correctly.
   */
  @JsonProperty("classification-category-like")
  private final String[] classificationCategoryLike;

  /**
   * Filter by what the classification category of the task shouldn't be. This results in a
   * substring search (% is appended to the front and end of the requested value). Further SQL
   * "LIKE" wildcard characters will be resolved correctly.
   */
  @JsonProperty("classification-category-not-like")
  private final String[] classificationCategoryNotLike;
  // endregion
  // region classificationName
  /** Filter by the classification name of the task. This is an exact match. */
  @JsonProperty("classification-name")
  private final String[] classificationNameIn;

  /** Filter by what the classification name of the task shouldn't be. This is an exact match. */
  @JsonProperty("classification-name-not")
  private final String[] classificationNameNotIn;

  /**
   * Filter by the classification name of the task. This results in a substring search (% is
   * appended to the front and end of the requested value). Further SQL "LIKE" wildcard characters
   * will be resolved correctly.
   */
  @JsonProperty("classification-name-like")
  private final String[] classificationNameLike;

  /**
   * Filter by what the classification name of the task shouldn't be. This results in a substring
   * search (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("classification-name-not-like")
  private final String[] classificationNameNotLike;
  // endregion
  // region workbasketId
  /** Filter by workbasket id of the task. This is an exact match. */
  @JsonProperty("workbasket-id")
  private final String[] workbasketIdIn;

  /** Filter by what the workbasket id of the task shouldn't be. This is an exact match. */
  @JsonProperty("workbasket-id-not")
  private final String[] workbasketIdNotIn;
  // endregion
  // region workbasketKeyDomain
  /**
   * Filter by workbasket keys of the task. This parameter can only be used in combination with
   * 'domain'
   */
  @JsonProperty("workbasket-key")
  private final String[] workbasketKeyIn;

  /**
   * Filter by what the workbasket keys of the task aren't. This parameter can only be used in
   * combination with 'domain'
   */
  @JsonProperty("workbasket-key-not")
  private final String[] workbasketKeyNotIn;

  /** Filter by domain of the task. This is an exact match. */
  @JsonProperty("domain")
  private final String domain;
  // endregion
  // region businessProcessId
  /** Filter by the business process id of the task. This is an exact match. */
  @JsonProperty("business-process-id")
  private final String[] businessProcessIdIn;

  /** Filter by what the business process id of the task shouldn't be. This is an exact match. */
  @JsonProperty("business-process-id-not")
  private final String[] businessProcessIdNot;

  /**
   * Filter by the business process id of the task. This results in a substring search (% is
   * appended to the front and end of the requested value). Further SQL "LIKE" wildcard characters
   * will be resolved correctly.
   */
  @JsonProperty("business-process-id-like")
  private final String[] businessProcessIdLike;

  /**
   * Filter by the business process id of the task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("business-process-id-not-like")
  private final String[] businessProcessIdNotLike;

  // endregion
  // region parentBusinessProcessId
  /** Filter by the parent business process id of the task. This is an exact match. */
  @JsonProperty("parent-business-process-id")
  private final String[] parentBusinessProcessIdIn;

  /**
   * Filter by what the parent business process id of the task shouldn't be. This is an exact match.
   */
  @JsonProperty("parent-business-process-id-not")
  private final String[] parentBusinessProcessIdNotIn;

  /**
   * Filter by the parent business process id of the task. This results in a substring search (% is
   * appended to the front and end of the requested value). Further SQL "LIKE" wildcard characters
   * will be resolved correctly.
   */
  @JsonProperty("parent-business-process-id-like")
  private final String[] parentBusinessProcessIdLike;

  /**
   * Filter by the parent business process id of the task shouldn't be. This results in a substring
   * search (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("parent-business-process-id-not-like")
  private final String[] parentBusinessProcessIdNotLike;

  // endregion
  // region owner
  /** Filter by owner of the task. This is an exact match. */
  @JsonProperty("owner")
  private final String[] ownerIn;

  /** Filter by what the owner of the task shouldn't be. This is an exact match. */
  @JsonProperty("owner-not")
  private final String[] ownerNotIn;

  /**
   * Filter by the owner of the task. This results in a substring search (% is appended to the front
   * and end of the requested value). Further SQL "LIKE" wildcard characters will be resolved
   * correctly.
   */
  @JsonProperty("owner-like")
  private final String[] ownerLike;

  /**
   * Filter by what the owner of the task shouldn't be. This results in a substring search (% is
   * appended to the front and end of the requested value). Further SQL "LIKE" wildcard characters
   * will be resolved correctly.
   */
  @JsonProperty("owner-not-like")
  private final String[] ownerNotLike;
  // endregion
  // region primaryObjectReference
  /**
   * Filter by the primary object reference of the task. This is an exact match. "por" is a
   * parameter of complex type. Its following attributes from por[].id to por[].value can be
   * specified according to the description of complex parameters in the overview, e.g.
   * por={"value":"exampleValue"}
   */
  @JsonProperty("por")
  private final ObjectReference[] primaryObjectReferenceIn;
  // endregion
  // region primaryObjectReferenceCompany
  /** Filter by the company of the primary object reference of the task. This is an exact match. */
  @JsonProperty("por.company")
  private final String[] porCompanyIn;

  /**
   * Filter by what the company of the primary object reference of the task shouldn't be. This is an
   * exact match.
   */
  @JsonProperty("por-company-not")
  private final String[] porCompanyNotIn;

  /**
   * Filter by the company of the primary object reference of the task. This results in a substring
   * search (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("por-company-like")
  private final String[] porCompanyLike;

  /**
   * Filter by what the company of the primary object reference of the task shouldn't be. This
   * results in a substring search (% is appended to the front and end of the requested value).
   * Further SQL "LIKE" wildcard characters will be resolved correctly.
   */
  @JsonProperty("por-company-not-like")
  private final String[] porCompanyNotLike;
  // endregion
  // region primaryObjectReferenceSystem
  /** Filter by the system of the primary object reference of the task. This is an exact match. */
  @JsonProperty("por.system")
  private final String[] porSystemIn;

  /**
   * Filter by what the system of the primary object reference of the task shouldn't be. This is an
   * exact match.
   */
  @JsonProperty("por-system-not")
  private final String[] porSystemNotIn;

  /**
   * Filter by the system of the primary object reference of the task. This results in a substring
   * search (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("por-system-like")
  private final String[] porSystemLike;

  /**
   * Filter by what the system of the primary object reference of the task shouldn't be. This
   * results in a substring search (% is appended to the front and end of the requested value).
   * Further SQL "LIKE" wildcard characters will be resolved correctly.
   */
  @JsonProperty("por-system-not-like")
  private final String[] porSystemNotLike;
  // endregion
  // region primaryObjectReferenceSystemInstance
  /**
   * Filter by the system instance of the primary object reference of the task. This is an exact
   * match.
   */
  @JsonProperty("por.instance")
  private final String[] porInstanceIn;

  /**
   * Filter by what the system instance of the primary object reference of the task shouldn't be.
   * This is an exact match.
   */
  @JsonProperty("por-instance-not")
  private final String[] porInstanceNotIn;

  /**
   * Filter by the system instance of the primary object reference of the task. This results in a
   * substring search (% is appended to the front and end of the requested value). Further SQL
   * "LIKE" wildcard characters will be resolved correctly.
   */
  @JsonProperty("por-instance-like")
  private final String[] porInstanceLike;

  /**
   * Filter by what the system instance of the primary object reference of the task shouldn't be.
   * This results in a substring search (% is appended to the front and end of the requested value).
   * Further SQL "LIKE" wildcard characters will be resolved correctly.
   */
  @JsonProperty("por-instance-not-like")
  private final String[] porInstanceNotLike;
  // endregion
  // region primaryObjectReferenceSystemType
  /** Filter by the type of the primary object reference of the task. This is an exact match. */
  @JsonProperty("por.type")
  private final String[] porTypeIn;

  /**
   * Filter by what the type of the primary object reference of the task shouldn't be. This is an
   * exact match.
   */
  @JsonProperty("por-type-not")
  private final String[] porTypeNotIn;

  /**
   * Filter by the type of the primary object reference of the task. This results in a substring
   * search (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("por-type-like")
  private final String[] porTypeLike;

  /**
   * Filter by what the type of the primary object reference of the task shouldn't be. This results
   * in a substring search (% is appended to the front and end of the requested value). Further SQL
   * "LIKE" wildcard characters will be resolved correctly.
   */
  @JsonProperty("por-type-not-like")
  private final String[] porTypeNotLike;
  // endregion
  // region primaryObjectReferenceSystemValue
  /** Filter by the value of the primary object reference of the task. This is an exact match. */
  @JsonProperty("por.value")
  private final String[] porValueIn;

  /**
   * Filter by what the value of the primary object reference of the task shouldn't be. This is an
   * exact match.
   */
  @JsonProperty("por-value-not")
  private final String[] porValueNotIn;

  /**
   * Filter by the value of the primary object reference of the task. This results in a substring
   * search (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("por-value-like")
  private final String[] porValueLike;

  /**
   * Filter by what the value of the primary object reference of the task shouldn't be. This results
   * in a substring search (% is appended to the front and end of the requested value). Further SQL
   * "LIKE" wildcard characters will be resolved correctly.
   */
  @JsonProperty("por-value-not-like")
  private final String[] porValueNotLike;
  // endregion
  // region secondaryObjectReference
  /**
   * Filter by the primary object reference of the task. This is an exact match. "sor" is a
   * parameter of complex type. Its following attributes from sor[].id to sor[].value can be
   * specified according to the description of complex parameters in the overview, e.g.
   * sor={"value":"exampleValue"}
   */
  @JsonProperty("sor")
  private final ObjectReference[] secondaryObjectReferenceIn;
  // endregion
  // region secondaryObjectReferenceCompany
  /**
   * Filter by the company of the secondary object reference of the task. This is an exact match.
   */
  @JsonProperty("sor-company")
  private final String[] sorCompanyIn;

  /**
   * Filter by the company of the secondary object references of the task. This results in a
   * substring search (% is appended to the front and end of the requested value). Further SQL
   * "LIKE" wildcard characters will be resolved correctly.
   */
  @JsonProperty("sor-company-like")
  private final String[] sorCompanyLike;

  // endregion
  // region secondaryObjectReferenceSystem
  /** Filter by the system of the secondary object reference of the task. This is an exact match. */
  @JsonProperty("sor-system")
  private final String[] sorSystemIn;

  /**
   * Filter by the system of the secondary object reference of the task. This results in a substring
   * search (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("sor-system-like")
  private final String[] sorSystemLike;

  // endregion
  // region secondaryObjectReferenceSystemInstance
  /**
   * Filter by the system instance of the secondary object reference of the task. This is an exact
   * match.
   */
  @JsonProperty("sor-instance")
  private final String[] sorInstanceIn;

  /**
   * Filter by the system instance of the secondary object reference of the task. This results in a
   * substring search (% is appended to the front and end of the requested value). Further SQL
   * "LIKE" wildcard characters will be resolved correctly.
   */
  @JsonProperty("sor-instance-like")
  private final String[] sorInstanceLike;

  // endregion
  // region secondaryObjectReferenceSystemType
  /** Filter by the type of the secondary object reference of the task. This is an exact match. */
  @JsonProperty("sor-type")
  private final String[] sorTypeIn;

  /**
   * Filter by the type of the secondary object reference of the task. This results in a substring
   * search (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("sor-type-like")
  private final String[] sorTypeLike;

  // endregion
  // region primaryObjectReferenceSystemValue
  /** Filter by the value of the secondary object reference of the task. This is an exact match. */
  @JsonProperty("sor-value")
  private final String[] sorValueIn;

  /**
   * Filter by the value of the secondary object reference of the task. This results in a substring
   * search (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("sor-value-like")
  private final String[] sorValueLike;

  // endregion
  // region read
  /** Filter by the is read flag of the task. This is an exact match. */
  @JsonProperty("is-read")
  private final Boolean isRead;
  // endregion
  // region transferred
  /** Filter by the is transferred flag of the task. This is an exact match. */
  @JsonProperty("is-transferred")
  private final Boolean isTransferred;
  // endregion
  // region attachmentClassificationId
  /** Filter by the attachment classification id of the task. This is an exact match. */
  @JsonProperty("attachment-classification-id")
  private final String[] attachmentClassificationIdIn;

  /**
   * Filter by what the attachment classification id of the task shouldn't be. This is an exact
   * match.
   */
  @JsonProperty("attachment-classification-id-not")
  private final String[] attachmentClassificationIdNotIn;
  // endregion
  // region attachmentClassificationKey
  /** Filter by the attachment classification key of the task. This is an exact match. */
  @JsonProperty("attachment-classification-key")
  private final String[] attachmentClassificationKeyIn;

  /**
   * Filter by what the attachment classification key of the task shouldn't be. This is an exact
   * match.
   */
  @JsonProperty("attachment-classification-key-not")
  private final String[] attachmentClassificationKeyNotIn;

  /**
   * Filter by the attachment classification key of the task. This results in a substring search (%
   * is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("attachment-classification-key-like")
  private final String[] attachmentClassificationKeyLike;

  /**
   * Filter by what the attachment classification key of the task shouldn't be. This results in a
   * substring search (% is appended to the front and end of the requested value). Further SQL
   * "LIKE" wildcard characters will be resolved correctly.
   */
  @JsonProperty("attachment-classification-key-not-like")
  private final String[] attachmentClassificationKeyNotLike;
  // endregion
  // region attachmentClassificationName
  /** Filter by the attachment classification name of the task. This is an exact match. */
  @JsonProperty("attachment-classification-name")
  private final String[] attachmentClassificationNameIn;

  /**
   * Filter by what the attachment classification name of the task shouldn't be. This is an exact
   * match.
   */
  @JsonProperty("attachment-classification-name-not")
  private final String[] attachmentClassificationNameNotIn;

  /**
   * Filter by the attachment classification name of the task. This results in a substring search (%
   * is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("attachment-classification-name-like")
  private final String[] attachmentClassificationNameLike;

  /**
   * Filter by what the attachment classification name of the task shouldn't be. This results in a
   * substring search (% is appended to the front and end of the requested value). Further SQL
   * "LIKE" wildcard characters will be resolved correctly.
   */
  @JsonProperty("attachment-classification-name-not-like")
  private final String[] attachmentClassificationNameNotLike;
  // endregion
  // region attachmentChannel
  /** Filter by the attachment channel of the task. This is an exact match. */
  @JsonProperty("attachment-channel")
  private final String[] attachmentChannelIn;

  /** Filter by what the attachment channel of the task shouldn't be. This is an exact match. */
  @JsonProperty("attachment-channel-not")
  private final String[] attachmentChannelNotIn;

  /**
   * Filter by the attachment channel of the task. This results in a substring search (% is appended
   * to the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("attachment-channel-like")
  private final String[] attachmentChannelLike;

  /**
   * Filter by what the attachment channel of the task shouldn't be. This results in a substring
   * search (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("attachment-channel-not-like")
  private final String[] attachmentChannelNotLike;
  // endregion
  // region attachmentReferenceValue
  /** Filter by the attachment reference of the task. This is an exact match. */
  @JsonProperty("attachment-reference")
  private final String[] attachmentReferenceIn;

  /** Filter by what the attachment reference of the task shouldn't be. This is an exact match. */
  @JsonProperty("attachment-reference-not")
  private final String[] attachmentReferenceNotIn;

  /**
   * Filter by the attachment reference of the task. This results in a substring search (% is
   * appended to the front and end of the requested value). Further SQL "LIKE" wildcard characters
   * will be resolved correctly.
   */
  @JsonProperty("attachment-reference-like")
  private final String[] attachmentReferenceLike;

  /**
   * Filter by what the attachment reference of the task shouldn't be. This results in a substring
   * search (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("attachment-reference-not-like")
  private final String[] attachmentReferenceNotLike;
  // endregion
  // region attachmentReceived
  /**
   * Filter by a time interval within which the attachment of the task was received. To create an
   * open interval you can just leave it blank.
   *
   * <p>The format is ISO-8601.
   */
  @JsonProperty("attachment-received")
  private final Instant[] attachmentReceivedWithin;

  /**
   * Filter by a time interval within which the attachment of the task wasn't received. To create an
   * open interval you can just leave it blank.
   *
   * <p>The format is ISO-8601.
   */
  @JsonProperty("attachment-received-not")
  private final Instant[] attachmentReceivedNotWithin;
  // endregion
  // region customAttributes
  /** Filter by the value of the field custom1 of the task. This is an exact match. */
  @JsonProperty("custom-1")
  private final String[] custom1In;

  /** Exclude values of the field custom1 of the task. */
  @JsonProperty("custom-1-not")
  private final String[] custom1NotIn;

  /**
   * Filter by the custom1 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-1-like")
  private final String[] custom1Like;

  /**
   * Filter by what the custom1 field of the task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-1-not-like")
  private final String[] custom1NotLike;

  /** Filter by the value of the field custom2 of the task. This is an exact match. */
  @JsonProperty("custom-2")
  private final String[] custom2In;

  /** Filter out by values of the field custom2 of the task. This is an exact match. */
  @JsonProperty("custom-2-not")
  private final String[] custom2NotIn;

  /**
   * Filter by the custom2 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-2-like")
  private final String[] custom2Like;

  /**
   * Filter by what the custom2 field of the task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-2-not-like")
  private final String[] custom2NotLike;

  /** Filter by the value of the field custom3 of the task. This is an exact match. */
  @JsonProperty("custom-3")
  private final String[] custom3In;

  /** Filter out by values of the field custom3 of the task. This is an exact match. */
  @JsonProperty("custom-3-not")
  private final String[] custom3NotIn;

  /**
   * Filter by the custom3 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-3-like")
  private final String[] custom3Like;

  /**
   * Filter by what the custom3 field of the task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-3-not-like")
  private final String[] custom3NotLike;

  /** Filter by the value of the field custom4 of the task. This is an exact match. */
  @JsonProperty("custom-4")
  private final String[] custom4In;

  /** Filter out by values of the field custom4 of the task. This is an exact match. */
  @JsonProperty("custom-4-not")
  private final String[] custom4NotIn;

  /**
   * Filter by the custom4 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-4-like")
  private final String[] custom4Like;

  /**
   * Filter by what the custom4 field of the task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-4-not-like")
  private final String[] custom4NotLike;

  /** Filter by the value of the field custom5 of the task. This is an exact match. */
  @JsonProperty("custom-5")
  private final String[] custom5In;

  /** Filter out by values of the field custom5 of the task. This is an exact match. */
  @JsonProperty("custom-5-not")
  private final String[] custom5NotIn;

  /**
   * Filter by the custom5 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-5-like")
  private final String[] custom5Like;

  /**
   * Filter by what the custom5 field of the task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-5-not-like")
  private final String[] custom5NotLike;

  /** Filter by the value of the field custom6 of the task. This is an exact match. */
  @JsonProperty("custom-6")
  private final String[] custom6In;

  /** Filter out by values of the field custom6 of the task. This is an exact match. */
  @JsonProperty("custom-6-not")
  private final String[] custom6NotIn;

  /**
   * Filter by the custom6 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-6-like")
  private final String[] custom6Like;

  /**
   * Filter by what the custom6 field of the task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-6-not-like")
  private final String[] custom6NotLike;

  /** Filter by the value of the field custom7 of the task. This is an exact match. */
  @JsonProperty("custom-7")
  private final String[] custom7In;

  /** Filter out by values of the field custom7 of the task. This is an exact match. */
  @JsonProperty("custom-7-not")
  private final String[] custom7NotIn;

  /**
   * Filter by the custom7 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-7-like")
  private final String[] custom7Like;

  /**
   * Filter by what the custom7 field of the task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-7-not-like")
  private final String[] custom7NotLike;

  /** Filter by the value of the field custom8 of the task. This is an exact match. */
  @JsonProperty("custom-8")
  private final String[] custom8In;

  /** Filter out by values of the field custom8 of the task. This is an exact match. */
  @JsonProperty("custom-8-not")
  private final String[] custom8NotIn;

  /**
   * Filter by the custom8 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-8-like")
  private final String[] custom8Like;

  /**
   * Filter by what the custom8 field of the task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-8-not-like")
  private final String[] custom8NotLike;

  /** Filter by the value of the field custom9 of the task. This is an exact match. */
  @JsonProperty("custom-9")
  private final String[] custom9In;

  /** Filter out by values of the field custom9 of the task. This is an exact match. */
  @JsonProperty("custom-9-not")
  private final String[] custom9NotIn;

  /**
   * Filter by the custom9 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-9-like")
  private final String[] custom9Like;

  /**
   * Filter by what the custom9 field of the task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-9-not-like")
  private final String[] custom9NotLike;

  /** Filter by the value of the field custom10 of the task. This is an exact match. */
  @JsonProperty("custom-10")
  private final String[] custom10In;

  /** Filter out by values of the field custom10 of the task. This is an exact match. */
  @JsonProperty("custom-10-not")
  private final String[] custom10NotIn;

  /**
   * Filter by the custom10 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-10-like")
  private final String[] custom10Like;

  /**
   * Filter by what the custom10 field of the task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-10-not-like")
  private final String[] custom10NotLike;

  /** Filter by the value of the field custom11 of the task. This is an exact match. */
  @JsonProperty("custom-11")
  private final String[] custom11In;

  /** Filter out by values of the field custom11 of the task. This is an exact match. */
  @JsonProperty("custom-11-not")
  private final String[] custom11NotIn;

  /**
   * Filter by the custom11 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-11-like")
  private final String[] custom11Like;

  /**
   * Filter by what the custom11 field of the task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-11-not-like")
  private final String[] custom11NotLike;

  /** Filter by the value of the field custom12 of the task. This is an exact match. */
  @JsonProperty("custom-12")
  private final String[] custom12In;

  /** Filter out by values of the field custom12 of the task. This is an exact match. */
  @JsonProperty("custom-12-not")
  private final String[] custom12NotIn;

  /**
   * Filter by the custom12 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-12-like")
  private final String[] custom12Like;

  /**
   * Filter by what the custom12 field of the task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-12-not-like")
  private final String[] custom12NotLike;

  /** Filter by the value of the field custom13 of the task. This is an exact match. */
  @JsonProperty("custom-13")
  private final String[] custom13In;

  /** Filter out by values of the field custom13 of the task. This is an exact match. */
  @JsonProperty("custom-13-not")
  private final String[] custom13NotIn;

  /**
   * Filter by the custom13 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-13-like")
  private final String[] custom13Like;

  /**
   * Filter by what the custom13 field of the task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-13-not-like")
  private final String[] custom13NotLike;

  /** Filter by the value of the field custom14 of the task. This is an exact match. */
  @JsonProperty("custom-14")
  private final String[] custom14In;

  /** Filter out by values of the field custom14 of the task. This is an exact match. */
  @JsonProperty("custom-14-not")
  private final String[] custom14NotIn;

  /**
   * Filter by the custom14 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-14-like")
  private final String[] custom14Like;

  /**
   * Filter by what the custom14 field of the task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-14-not-like")
  private final String[] custom14NotLike;

  /** Filter by the value of the field custom15 of the task. This is an exact match. */
  @JsonProperty("custom-15")
  private final String[] custom15In;

  /** Filter out by values of the field custom15 of the task. This is an exact match. */
  @JsonProperty("custom-15-not")
  private final String[] custom15NotIn;

  /**
   * Filter by the custom15 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-15-like")
  private final String[] custom15Like;

  /**
   * Filter by what the custom15 field of the task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-15-not-like")
  private final String[] custom15NotLike;

  /** Filter by the value of the field custom16 of the task. This is an exact match. */
  @JsonProperty("custom-16")
  private final String[] custom16In;

  /** Filter out by values of the field custom16 of the task. This is an exact match. */
  @JsonProperty("custom-16-not")
  private final String[] custom16NotIn;
  /**
   * Filter by the custom16 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-16-like")
  private final String[] custom16Like;

  /**
   * Filter by what the custom16 field of the task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-16-not-like")
  private final String[] custom16NotLike;
  // endregion
  // region callbackState
  /** Filter by the callback state of the task. This is an exact match. */
  @JsonProperty("callback-state")
  private final CallbackState[] callbackStateIn;

  /** Filter by what the callback state of the task shouldn't be. This is an exact match. */
  @JsonProperty("callback-state-not")
  private final CallbackState[] callbackStateNotIn;
  // endregion
  // region wildcardSearchValue
  /**
   * Filter by wildcard search field of the task.
   *
   * <p>This must be used in combination with 'wildcard-search-value'
   */
  @JsonProperty("wildcard-search-fields")
  private final WildcardSearchField[] wildcardSearchFieldIn;

  /**
   * Filter by wildcard search field of the task. This is an exact match.
   *
   * <p>This must be used in combination with 'wildcard-search-fields'
   */
  @JsonProperty("wildcard-search-value")
  private final String wildcardSearchValue;
  // endregion

  // region constructor

  @ConstructorProperties({
    "task-id",
    "task-id-not",
    "external-id",
    "external-id-not",
    "received",
    "received-from",
    "received-until",
    "received-not",
    "received-from-not",
    "received-until-not",
    "created",
    "created-from",
    "created-until",
    "created-not",
    "created-from-not",
    "created-until-not",
    "claimed",
    "claimed-not",
    "modified",
    "modified-not",
    "planned",
    "planned-from",
    "planned-until",
    "planned-not",
    "planned-from-not",
    "planned-until-not",
    "due",
    "due-from",
    "due-until",
    "due-not",
    "due-from-not",
    "due-until-not",
    "completed",
    "completed-from",
    "completed-until",
    "completed-not",
    "completed-from-not",
    "completed-until-not",
    "name",
    "name-not",
    "name-like",
    "name-not-like",
    "creator",
    "creator-not",
    "creator-like",
    "creator-not-like",
    "note-like",
    "note-not-like",
    "description-like",
    "description-not-like",
    "priority",
    "priority-not",
    "state",
    "state-not",
    "classification-id",
    "classification-id-not",
    "classification.key",
    "classification-key-not",
    "classification-key-like",
    "classification-key-not-like",
    "classification-category",
    "classification-category-not",
    "classification-category-like",
    "classification-category-not-like",
    "classification-name",
    "classification-name-not",
    "classification-name-like",
    "classification-name-not-like",
    "workbasket-id",
    "workbasket-id-not",
    "workbasket-key",
    "workbasket-key-not",
    "domain",
    "business-process-id",
    "business-process-id-not",
    "business-process-id-like",
    "business-process-id-not-like",
    "parent-business-process-id",
    "parent-business-process-id-not",
    "parent-business-process-id-like",
    "parent-business-process-id-not-like",
    "owner",
    "owner-not",
    "owner-like",
    "owner-not-like",
    "por",
    "por.company",
    "por-company-not",
    "por-company-like",
    "por-company-not-like",
    "por.system",
    "por-system-not",
    "por-system-like",
    "por-system-not-like",
    "por.instance",
    "por-instance-not",
    "por-instance-like",
    "por-instance-not-like",
    "por.type",
    "por-type-not",
    "por-type-like",
    "por-type-not-like",
    "por.value",
    "por-value-not",
    "por-value-like",
    "por-value-not-like",
    "sor",
    "sor-company",
    "sor-company-like",
    "sor-system",
    "sor-system-like",
    "sor-instance",
    "sor-instance-like",
    "sor-type",
    "sor-type-like",
    "sor-value",
    "sor-value-like",
    "is-read",
    "is-transferred",
    "attachment-classification-id",
    "attachment-classification-id-not",
    "attachment-classification-key",
    "attachment-classification-key-not",
    "attachment-classification-key-like",
    "attachment-classification-key-not-like",
    "attachment-classification-name",
    "attachment-classification-name-not",
    "attachment-classification-name-like",
    "attachment-classification-name-not-like",
    "attachment-channel",
    "attachment-channel-not",
    "attachment-channel-like",
    "attachment-channel-not-like",
    "attachment-reference",
    "attachment-reference-not",
    "attachment-reference-like",
    "attachment-reference-not-like",
    "attachment-received",
    "attachment-received-not",
    "custom-1",
    "custom-1-not",
    "custom-1-like",
    "custom-1-not-like",
    "custom-2",
    "custom-2-not",
    "custom-2-like",
    "custom-2-not-like",
    "custom-3",
    "custom-3-not",
    "custom-3-like",
    "custom-3-not-like",
    "custom-4",
    "custom-4-not",
    "custom-4-like",
    "custom-4-not-like",
    "custom-5",
    "custom-5-not",
    "custom-5-like",
    "custom-5-not-like",
    "custom-6",
    "custom-6-not",
    "custom-6-like",
    "custom-6-not-like",
    "custom-7",
    "custom-7-not",
    "custom-7-like",
    "custom-7-not-like",
    "custom-8",
    "custom-8-not",
    "custom-8-like",
    "custom-8-not-like",
    "custom-9",
    "custom-9-not",
    "custom-9-like",
    "custom-9-not-like",
    "custom-10",
    "custom-10-not",
    "custom-10-like",
    "custom-10-not-like",
    "custom-11",
    "custom-11-not",
    "custom-11-like",
    "custom-11-not-like",
    "custom-12",
    "custom-12-not",
    "custom-12-like",
    "custom-12-not-like",
    "custom-13",
    "custom-13-not",
    "custom-13-like",
    "custom-13-not-like",
    "custom-14",
    "custom-14-not",
    "custom-14-like",
    "custom-14-not-like",
    "custom-15",
    "custom-15-not",
    "custom-15-like",
    "custom-15-not-like",
    "custom-16",
    "custom-16-not",
    "custom-16-like",
    "custom-16-not-like",
    "callback-state",
    "callback-state-not",
    "wildcard-search-fields",
    "wildcard-search-value"
  })
  public TaskQueryFilterParameter(
      String[] taskIdIn,
      String[] taskIdNotIn,
      String[] externalIdIn,
      String[] externalIdNotIn,
      Instant[] receivedWithin,
      Instant receivedFrom,
      Instant receivedUntil,
      Instant[] receivedNotIn,
      Instant receivedFromNot,
      Instant receivedUntilNot,
      Instant[] createdWithin,
      Instant createdFrom,
      Instant createdUntil,
      Instant[] createdNotWithin,
      Instant createdFromNot,
      Instant createdUntilNot,
      Instant[] claimedWithin,
      Instant[] claimedNotWithin,
      Instant[] modifiedWithin,
      Instant[] modifiedNotWithin,
      Instant[] plannedWithin,
      Instant plannedFrom,
      Instant plannedUntil,
      Instant[] plannedNotWithin,
      Instant plannedFromNot,
      Instant plannedUntilNot,
      Instant[] dueWithin,
      Instant dueFrom,
      Instant dueUntil,
      Instant[] dueNotWithin,
      Instant dueFromNot,
      Instant dueUntilNot,
      Instant[] completedWithin,
      Instant completedFrom,
      Instant completedUntil,
      Instant[] completedNotWithin,
      Instant completedFromNot,
      Instant completedUntilNot,
      String[] nameIn,
      String[] nameNotIn,
      String[] nameLike,
      String[] nameNotLike,
      String[] creatorIn,
      String[] creatorNotIn,
      String[] creatorLike,
      String[] creatorNotLike,
      String[] noteLike,
      String[] noteNotLike,
      String[] descriptionLike,
      String[] descriptionNotLike,
      int[] priorityIn,
      int[] priorityNotIn,
      TaskState[] stateIn,
      TaskState[] stateNotIn,
      String[] classificationIdIn,
      String[] classificationIdNotIn,
      String[] classificationKeyIn,
      String[] classificationKeyNotIn,
      String[] classificationKeyLike,
      String[] classificationKeyNotLike,
      String[] classificationCategoryIn,
      String[] classificationCategoryNotIn,
      String[] classificationCategoryLike,
      String[] classificationCategoryNotLike,
      String[] classificationNameIn,
      String[] classificationNameNotIn,
      String[] classificationNameLike,
      String[] classificationNameNotLike,
      String[] workbasketIdIn,
      String[] workbasketIdNotIn,
      String[] workbasketKeyIn,
      String[] workbasketKeyNotIn,
      String domain,
      String[] businessProcessIdIn,
      String[] businessProcessIdNot,
      String[] businessProcessIdLike,
      String[] businessProcessIdNotLike,
      String[] parentBusinessProcessIdIn,
      String[] parentBusinessProcessIdNotIn,
      String[] parentBusinessProcessIdLike,
      String[] parentBusinessProcessIdNotLike,
      String[] ownerIn,
      String[] ownerNotIn,
      String[] ownerLike,
      String[] ownerNotLike,
      ObjectReference[] primaryObjectReferenceIn,
      String[] porCompanyIn,
      String[] porCompanyNotIn,
      String[] porCompanyLike,
      String[] porCompanyNotLike,
      String[] porSystemIn,
      String[] porSystemNotIn,
      String[] porSystemLike,
      String[] porSystemNotLike,
      String[] porInstanceIn,
      String[] porInstanceNotIn,
      String[] porInstanceLike,
      String[] porInstanceNotLike,
      String[] porTypeIn,
      String[] porTypeNotIn,
      String[] porTypeLike,
      String[] porTypeNotLike,
      String[] porValueIn,
      String[] porValueNotIn,
      String[] porValueLike,
      String[] porValueNotLike,
      ObjectReference[] secondaryObjectReferenceIn,
      String[] sorCompanyIn,
      String[] sorCompanyLike,
      String[] sorSystemIn,
      String[] sorSystemLike,
      String[] sorInstanceIn,
      String[] sorInstanceLike,
      String[] sorTypeIn,
      String[] sorTypeLike,
      String[] sorValueIn,
      String[] sorValueLike,
      Boolean isRead,
      Boolean isTransferred,
      String[] attachmentClassificationIdIn,
      String[] attachmentClassificationIdNotIn,
      String[] attachmentClassificationKeyIn,
      String[] attachmentClassificationKeyNotIn,
      String[] attachmentClassificationKeyLike,
      String[] attachmentClassificationKeyNotLike,
      String[] attachmentClassificationNameIn,
      String[] attachmentClassificationNameNotIn,
      String[] attachmentClassificationNameLike,
      String[] attachmentClassificationNameNotLike,
      String[] attachmentChannelIn,
      String[] attachmentChannelNotIn,
      String[] attachmentChannelLike,
      String[] attachmentChannelNotLike,
      String[] attachmentReferenceIn,
      String[] attachmentReferenceNotIn,
      String[] attachmentReferenceLike,
      String[] attachmentReferenceNotLike,
      Instant[] attachmentReceivedWithin,
      Instant[] attachmentReceivedNotWithin,
      String[] custom1In,
      String[] custom1NotIn,
      String[] custom1Like,
      String[] custom1NotLike,
      String[] custom2In,
      String[] custom2NotIn,
      String[] custom2Like,
      String[] custom2NotLike,
      String[] custom3In,
      String[] custom3NotIn,
      String[] custom3Like,
      String[] custom3NotLike,
      String[] custom4In,
      String[] custom4NotIn,
      String[] custom4Like,
      String[] custom4NotLike,
      String[] custom5In,
      String[] custom5NotIn,
      String[] custom5Like,
      String[] custom5NotLike,
      String[] custom6In,
      String[] custom6NotIn,
      String[] custom6Like,
      String[] custom6NotLike,
      String[] custom7In,
      String[] custom7NotIn,
      String[] custom7Like,
      String[] custom7NotLike,
      String[] custom8In,
      String[] custom8NotIn,
      String[] custom8Like,
      String[] custom8NotLike,
      String[] custom9In,
      String[] custom9NotIn,
      String[] custom9Like,
      String[] custom9NotLike,
      String[] custom10In,
      String[] custom10NotIn,
      String[] custom10Like,
      String[] custom10NotLike,
      String[] custom11In,
      String[] custom11NotIn,
      String[] custom11Like,
      String[] custom11NotLike,
      String[] custom12In,
      String[] custom12NotIn,
      String[] custom12Like,
      String[] custom12NotLike,
      String[] custom13In,
      String[] custom13NotIn,
      String[] custom13Like,
      String[] custom13NotLike,
      String[] custom14In,
      String[] custom14NotIn,
      String[] custom14Like,
      String[] custom14NotLike,
      String[] custom15In,
      String[] custom15NotIn,
      String[] custom15Like,
      String[] custom15NotLike,
      String[] custom16In,
      String[] custom16NotIn,
      String[] custom16Like,
      String[] custom16NotLike,
      CallbackState[] callbackStateIn,
      CallbackState[] callbackStateNotIn,
      WildcardSearchField[] wildcardSearchFieldIn,
      String wildcardSearchValue)
      throws InvalidArgumentException {
    this.taskIdIn = taskIdIn;
    this.taskIdNotIn = taskIdNotIn;
    this.externalIdIn = externalIdIn;
    this.externalIdNotIn = externalIdNotIn;
    this.receivedWithin = receivedWithin;
    this.receivedFrom = receivedFrom;
    this.receivedUntil = receivedUntil;
    this.receivedNotIn = receivedNotIn;
    this.receivedFromNot = receivedFromNot;
    this.receivedUntilNot = receivedUntilNot;
    this.createdWithin = createdWithin;
    this.createdFrom = createdFrom;
    this.createdUntil = createdUntil;
    this.createdNotWithin = createdNotWithin;
    this.createdFromNot = createdFromNot;
    this.createdUntilNot = createdUntilNot;
    this.claimedWithin = claimedWithin;
    this.claimedNotWithin = claimedNotWithin;
    this.modifiedWithin = modifiedWithin;
    this.modifiedNotWithin = modifiedNotWithin;
    this.plannedWithin = plannedWithin;
    this.plannedFrom = plannedFrom;
    this.plannedUntil = plannedUntil;
    this.plannedNotWithin = plannedNotWithin;
    this.plannedFromNot = plannedFromNot;
    this.plannedUntilNot = plannedUntilNot;
    this.dueWithin = dueWithin;
    this.dueFrom = dueFrom;
    this.dueUntil = dueUntil;
    this.dueNotWithin = dueNotWithin;
    this.dueFromNot = dueFromNot;
    this.dueUntilNot = dueUntilNot;
    this.completedWithin = completedWithin;
    this.completedFrom = completedFrom;
    this.completedUntil = completedUntil;
    this.completedNotWithin = completedNotWithin;
    this.completedFromNot = completedFromNot;
    this.completedUntilNot = completedUntilNot;
    this.nameIn = nameIn;
    this.nameNotIn = nameNotIn;
    this.nameLike = nameLike;
    this.nameNotLike = nameNotLike;
    this.creatorIn = creatorIn;
    this.creatorNotIn = creatorNotIn;
    this.creatorLike = creatorLike;
    this.creatorNotLike = creatorNotLike;
    this.noteLike = noteLike;
    this.noteNotLike = noteNotLike;
    this.descriptionLike = descriptionLike;
    this.descriptionNotLike = descriptionNotLike;
    this.priorityIn = priorityIn;
    this.priorityNotIn = priorityNotIn;
    this.stateIn = stateIn;
    this.stateNotIn = stateNotIn;
    this.classificationIdIn = classificationIdIn;
    this.classificationIdNotIn = classificationIdNotIn;
    this.classificationKeyIn = classificationKeyIn;
    this.classificationKeyNotIn = classificationKeyNotIn;
    this.classificationKeyLike = classificationKeyLike;
    this.classificationKeyNotLike = classificationKeyNotLike;
    this.classificationCategoryIn = classificationCategoryIn;
    this.classificationCategoryNotIn = classificationCategoryNotIn;
    this.classificationCategoryLike = classificationCategoryLike;
    this.classificationCategoryNotLike = classificationCategoryNotLike;
    this.classificationNameIn = classificationNameIn;
    this.classificationNameNotIn = classificationNameNotIn;
    this.classificationNameLike = classificationNameLike;
    this.classificationNameNotLike = classificationNameNotLike;
    this.workbasketIdIn = workbasketIdIn;
    this.workbasketIdNotIn = workbasketIdNotIn;
    this.workbasketKeyIn = workbasketKeyIn;
    this.workbasketKeyNotIn = workbasketKeyNotIn;
    this.domain = domain;
    this.businessProcessIdIn = businessProcessIdIn;
    this.businessProcessIdNot = businessProcessIdNot;
    this.businessProcessIdLike = businessProcessIdLike;
    this.businessProcessIdNotLike = businessProcessIdNotLike;
    this.parentBusinessProcessIdIn = parentBusinessProcessIdIn;
    this.parentBusinessProcessIdNotIn = parentBusinessProcessIdNotIn;
    this.parentBusinessProcessIdLike = parentBusinessProcessIdLike;
    this.parentBusinessProcessIdNotLike = parentBusinessProcessIdNotLike;
    this.ownerIn = ownerIn;
    this.ownerNotIn = ownerNotIn;
    this.ownerLike = ownerLike;
    this.ownerNotLike = ownerNotLike;
    this.primaryObjectReferenceIn = primaryObjectReferenceIn;
    this.porCompanyIn = porCompanyIn;
    this.porCompanyNotIn = porCompanyNotIn;
    this.porCompanyLike = porCompanyLike;
    this.porCompanyNotLike = porCompanyNotLike;
    this.porSystemIn = porSystemIn;
    this.porSystemNotIn = porSystemNotIn;
    this.porSystemLike = porSystemLike;
    this.porSystemNotLike = porSystemNotLike;
    this.porInstanceIn = porInstanceIn;
    this.porInstanceNotIn = porInstanceNotIn;
    this.porInstanceLike = porInstanceLike;
    this.porInstanceNotLike = porInstanceNotLike;
    this.porTypeIn = porTypeIn;
    this.porTypeNotIn = porTypeNotIn;
    this.porTypeLike = porTypeLike;
    this.porTypeNotLike = porTypeNotLike;
    this.porValueIn = porValueIn;
    this.porValueNotIn = porValueNotIn;
    this.porValueLike = porValueLike;
    this.porValueNotLike = porValueNotLike;
    this.secondaryObjectReferenceIn = secondaryObjectReferenceIn;
    this.sorCompanyIn = sorCompanyIn;
    this.sorCompanyLike = sorCompanyLike;
    this.sorSystemIn = sorSystemIn;
    this.sorSystemLike = sorSystemLike;
    this.sorInstanceIn = sorInstanceIn;
    this.sorInstanceLike = sorInstanceLike;
    this.sorTypeIn = sorTypeIn;
    this.sorTypeLike = sorTypeLike;
    this.sorValueIn = sorValueIn;
    this.sorValueLike = sorValueLike;
    this.isRead = isRead;
    this.isTransferred = isTransferred;
    this.attachmentClassificationIdIn = attachmentClassificationIdIn;
    this.attachmentClassificationIdNotIn = attachmentClassificationIdNotIn;
    this.attachmentClassificationKeyIn = attachmentClassificationKeyIn;
    this.attachmentClassificationKeyNotIn = attachmentClassificationKeyNotIn;
    this.attachmentClassificationKeyLike = attachmentClassificationKeyLike;
    this.attachmentClassificationKeyNotLike = attachmentClassificationKeyNotLike;
    this.attachmentClassificationNameIn = attachmentClassificationNameIn;
    this.attachmentClassificationNameNotIn = attachmentClassificationNameNotIn;
    this.attachmentClassificationNameLike = attachmentClassificationNameLike;
    this.attachmentClassificationNameNotLike = attachmentClassificationNameNotLike;
    this.attachmentChannelIn = attachmentChannelIn;
    this.attachmentChannelNotIn = attachmentChannelNotIn;
    this.attachmentChannelLike = attachmentChannelLike;
    this.attachmentChannelNotLike = attachmentChannelNotLike;
    this.attachmentReferenceIn = attachmentReferenceIn;
    this.attachmentReferenceNotIn = attachmentReferenceNotIn;
    this.attachmentReferenceLike = attachmentReferenceLike;
    this.attachmentReferenceNotLike = attachmentReferenceNotLike;
    this.attachmentReceivedWithin = attachmentReceivedWithin;
    this.attachmentReceivedNotWithin = attachmentReceivedNotWithin;
    this.custom1In = custom1In;
    this.custom1NotIn = custom1NotIn;
    this.custom1Like = custom1Like;
    this.custom1NotLike = custom1NotLike;
    this.custom2In = custom2In;
    this.custom2NotIn = custom2NotIn;
    this.custom2Like = custom2Like;
    this.custom2NotLike = custom2NotLike;
    this.custom3In = custom3In;
    this.custom3NotIn = custom3NotIn;
    this.custom3Like = custom3Like;
    this.custom3NotLike = custom3NotLike;
    this.custom4In = custom4In;
    this.custom4NotIn = custom4NotIn;
    this.custom4Like = custom4Like;
    this.custom4NotLike = custom4NotLike;
    this.custom5In = custom5In;
    this.custom5NotIn = custom5NotIn;
    this.custom5Like = custom5Like;
    this.custom5NotLike = custom5NotLike;
    this.custom6In = custom6In;
    this.custom6NotIn = custom6NotIn;
    this.custom6Like = custom6Like;
    this.custom6NotLike = custom6NotLike;
    this.custom7In = custom7In;
    this.custom7NotIn = custom7NotIn;
    this.custom7Like = custom7Like;
    this.custom7NotLike = custom7NotLike;
    this.custom8In = custom8In;
    this.custom8NotIn = custom8NotIn;
    this.custom8Like = custom8Like;
    this.custom8NotLike = custom8NotLike;
    this.custom9In = custom9In;
    this.custom9NotIn = custom9NotIn;
    this.custom9Like = custom9Like;
    this.custom9NotLike = custom9NotLike;
    this.custom10In = custom10In;
    this.custom10NotIn = custom10NotIn;
    this.custom10Like = custom10Like;
    this.custom10NotLike = custom10NotLike;
    this.custom11In = custom11In;
    this.custom11NotIn = custom11NotIn;
    this.custom11Like = custom11Like;
    this.custom11NotLike = custom11NotLike;
    this.custom12In = custom12In;
    this.custom12NotIn = custom12NotIn;
    this.custom12Like = custom12Like;
    this.custom12NotLike = custom12NotLike;
    this.custom13In = custom13In;
    this.custom13NotIn = custom13NotIn;
    this.custom13Like = custom13Like;
    this.custom13NotLike = custom13NotLike;
    this.custom14In = custom14In;
    this.custom14NotIn = custom14NotIn;
    this.custom14Like = custom14Like;
    this.custom14NotLike = custom14NotLike;
    this.custom15In = custom15In;
    this.custom15NotIn = custom15NotIn;
    this.custom15Like = custom15Like;
    this.custom15NotLike = custom15NotLike;
    this.custom16In = custom16In;
    this.custom16NotIn = custom16NotIn;
    this.custom16Like = custom16Like;
    this.custom16NotLike = custom16NotLike;
    this.callbackStateIn = callbackStateIn;
    this.callbackStateNotIn = callbackStateNotIn;
    this.wildcardSearchFieldIn = wildcardSearchFieldIn;
    this.wildcardSearchValue = wildcardSearchValue;

    validateFilterParameters();
  }

  // endregion

  @Override
  public Void apply(TaskQuery query) {
    Optional.ofNullable(taskIdIn).ifPresent(query::idIn);
    Optional.ofNullable(taskIdNotIn).ifPresent(query::idNotIn);

    Optional.ofNullable(externalIdIn).ifPresent(query::externalIdIn);
    Optional.ofNullable(externalIdNotIn).ifPresent(query::externalIdNotIn);

    Optional.ofNullable(receivedWithin)
        .map(this::extractTimeIntervals)
        .ifPresent(query::receivedWithin);
    if (receivedFrom != null || receivedUntil != null) {
      query.receivedWithin(new TimeInterval(receivedFrom, receivedUntil));
    }
    Optional.ofNullable(receivedNotIn)
        .map(this::extractTimeIntervals)
        .ifPresent(query::receivedNotWithin);
    if (receivedFromNot != null || receivedUntilNot != null) {
      query.receivedNotWithin(new TimeInterval(receivedFromNot, receivedUntilNot));
    }

    Optional.ofNullable(createdWithin)
        .map(this::extractTimeIntervals)
        .ifPresent(query::createdWithin);
    if (createdFrom != null || createdUntil != null) {
      query.createdWithin(new TimeInterval(createdFrom, createdUntil));
    }
    Optional.ofNullable(createdNotWithin)
        .map(this::extractTimeIntervals)
        .ifPresent(query::createdNotWithin);
    if (createdFromNot != null || createdUntilNot != null) {
      query.createdNotWithin(new TimeInterval(createdFromNot, createdUntilNot));
    }

    Optional.ofNullable(claimedWithin)
        .map(this::extractTimeIntervals)
        .ifPresent(query::claimedWithin);
    Optional.ofNullable(claimedNotWithin)
        .map(this::extractTimeIntervals)
        .ifPresent(query::claimedNotWithin);

    Optional.ofNullable(modifiedWithin)
        .map(this::extractTimeIntervals)
        .ifPresent(query::modifiedWithin);
    Optional.ofNullable(modifiedNotWithin)
        .map(this::extractTimeIntervals)
        .ifPresent(query::modifiedNotWithin);

    Optional.ofNullable(plannedWithin)
        .map(this::extractTimeIntervals)
        .ifPresent(query::plannedWithin);
    if (plannedFrom != null || plannedUntil != null) {
      query.plannedWithin(new TimeInterval(plannedFrom, plannedUntil));
    }
    Optional.ofNullable(plannedNotWithin)
        .map(this::extractTimeIntervals)
        .ifPresent(query::plannedNotWithin);
    if (plannedFromNot != null || plannedUntilNot != null) {
      query.plannedNotWithin(new TimeInterval(plannedFromNot, plannedUntilNot));
    }

    Optional.ofNullable(dueWithin).map(this::extractTimeIntervals).ifPresent(query::dueWithin);
    if (dueFrom != null || dueUntil != null) {
      query.dueWithin(new TimeInterval(dueFrom, dueUntil));
    }
    Optional.ofNullable(dueNotWithin)
        .map(this::extractTimeIntervals)
        .ifPresent(query::dueNotWithin);
    if (dueFromNot != null || dueUntilNot != null) {
      query.dueNotWithin(new TimeInterval(dueFromNot, dueUntilNot));
    }

    Optional.ofNullable(completedWithin)
        .map(this::extractTimeIntervals)
        .ifPresent(query::completedWithin);
    if (completedFrom != null || completedUntil != null) {
      query.completedWithin(new TimeInterval(completedFrom, completedUntil));
    }
    Optional.ofNullable(completedNotWithin)
        .map(this::extractTimeIntervals)
        .ifPresent(query::completedNotWithin);
    if (completedFromNot != null || completedUntilNot != null) {
      query.completedNotWithin(new TimeInterval(completedFromNot, completedUntilNot));
    }

    Optional.ofNullable(nameIn).ifPresent(query::nameIn);
    Optional.ofNullable(nameNotIn).ifPresent(query::nameNotIn);
    Optional.ofNullable(nameLike).map(this::wrapElementsInLikeStatement).ifPresent(query::nameLike);
    Optional.ofNullable(nameNotLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::nameNotLike);

    Optional.ofNullable(creatorIn).ifPresent(query::creatorIn);
    Optional.ofNullable(creatorNotIn).ifPresent(query::creatorNotIn);
    Optional.ofNullable(creatorLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::creatorLike);
    Optional.ofNullable(creatorNotLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::creatorNotLike);

    Optional.ofNullable(noteLike).map(this::wrapElementsInLikeStatement).ifPresent(query::noteLike);
    Optional.ofNullable(noteNotLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::noteNotLike);

    Optional.ofNullable(descriptionLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::descriptionLike);
    Optional.ofNullable(descriptionNotLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::descriptionNotLike);

    Optional.ofNullable(priorityIn).ifPresent(query::priorityIn);
    Optional.ofNullable(priorityNotIn).ifPresent(query::priorityNotIn);

    Optional.ofNullable(stateIn).ifPresent(query::stateIn);
    Optional.ofNullable(stateNotIn).ifPresent(query::stateNotIn);

    Optional.ofNullable(classificationIdIn).ifPresent(query::classificationIdIn);
    Optional.ofNullable(classificationIdNotIn).ifPresent(query::classificationIdNotIn);

    Optional.ofNullable(classificationKeyIn).ifPresent(query::classificationKeyIn);
    Optional.ofNullable(classificationKeyNotIn).ifPresent(query::classificationKeyNotIn);
    Optional.ofNullable(classificationKeyLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::classificationKeyLike);
    Optional.ofNullable(classificationKeyNotLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::classificationKeyNotLike);

    Optional.ofNullable(classificationCategoryIn).ifPresent(query::classificationCategoryIn);
    Optional.ofNullable(classificationCategoryNotIn).ifPresent(query::classificationCategoryNotIn);
    Optional.ofNullable(classificationCategoryLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::classificationCategoryLike);
    Optional.ofNullable(classificationCategoryNotLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::classificationCategoryNotLike);

    Optional.ofNullable(classificationNameIn).ifPresent(query::classificationNameIn);
    Optional.ofNullable(classificationNameNotIn).ifPresent(query::classificationNameNotIn);
    Optional.ofNullable(classificationNameLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::classificationNameLike);
    Optional.ofNullable(classificationNameNotLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::classificationNameNotLike);

    Optional.ofNullable(workbasketIdIn).ifPresent(query::workbasketIdIn);
    Optional.ofNullable(workbasketIdNotIn).ifPresent(query::workbasketIdNotIn);

    Optional.ofNullable(workbasketKeyIn)
        .map(
            keys ->
                Arrays.stream(keys)
                    .map(key -> new KeyDomain(key, domain))
                    .toArray(KeyDomain[]::new))
        .ifPresent(query::workbasketKeyDomainIn);
    Optional.ofNullable(workbasketKeyNotIn)
        .map(
            keys ->
                Arrays.stream(keys)
                    .map(key -> new KeyDomain(key, domain))
                    .toArray(KeyDomain[]::new))
        .ifPresent(query::workbasketKeyDomainNotIn);

    Optional.ofNullable(businessProcessIdIn).ifPresent(query::businessProcessIdIn);
    Optional.ofNullable(businessProcessIdNot).ifPresent(query::businessProcessIdNotIn);
    Optional.ofNullable(businessProcessIdLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::businessProcessIdLike);
    Optional.ofNullable(businessProcessIdNotLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::businessProcessIdNotLike);

    Optional.ofNullable(parentBusinessProcessIdIn).ifPresent(query::parentBusinessProcessIdIn);
    Optional.ofNullable(parentBusinessProcessIdNotIn)
        .ifPresent(query::parentBusinessProcessIdNotIn);
    Optional.ofNullable(parentBusinessProcessIdLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::parentBusinessProcessIdLike);
    Optional.ofNullable(parentBusinessProcessIdNotLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::parentBusinessProcessIdNotLike);

    Optional.ofNullable(ownerIn).ifPresent(query::ownerIn);
    Optional.ofNullable(ownerNotIn).ifPresent(query::ownerNotIn);
    Optional.ofNullable(ownerLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::ownerLike);
    Optional.ofNullable(ownerNotLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::ownerNotLike);

    Optional.ofNullable(primaryObjectReferenceIn).ifPresent(query::primaryObjectReferenceIn);

    Optional.ofNullable(porCompanyIn).ifPresent(query::primaryObjectReferenceCompanyIn);
    Optional.ofNullable(porCompanyNotIn).ifPresent(query::primaryObjectReferenceCompanyNotIn);
    Optional.ofNullable(porCompanyLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::primaryObjectReferenceCompanyLike);
    Optional.ofNullable(porCompanyNotLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::primaryObjectReferenceCompanyNotLike);

    Optional.ofNullable(porSystemIn).ifPresent(query::primaryObjectReferenceSystemIn);
    Optional.ofNullable(porSystemNotIn).ifPresent(query::primaryObjectReferenceSystemNotIn);
    Optional.ofNullable(porSystemLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::primaryObjectReferenceSystemLike);
    Optional.ofNullable(porSystemNotLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::primaryObjectReferenceSystemNotLike);

    Optional.ofNullable(porInstanceIn).ifPresent(query::primaryObjectReferenceSystemInstanceIn);
    Optional.ofNullable(porInstanceNotIn)
        .ifPresent(query::primaryObjectReferenceSystemInstanceNotIn);
    Optional.ofNullable(porInstanceLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::primaryObjectReferenceSystemInstanceLike);
    Optional.ofNullable(porInstanceNotLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::primaryObjectReferenceSystemInstanceNotLike);

    Optional.ofNullable(porTypeIn).ifPresent(query::primaryObjectReferenceTypeIn);
    Optional.ofNullable(porTypeNotIn).ifPresent(query::primaryObjectReferenceTypeNotIn);
    Optional.ofNullable(porTypeLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::primaryObjectReferenceTypeLike);
    Optional.ofNullable(porTypeNotLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::primaryObjectReferenceTypeNotLike);

    Optional.ofNullable(porValueIn).ifPresent(query::primaryObjectReferenceValueIn);
    Optional.ofNullable(porValueNotIn).ifPresent(query::primaryObjectReferenceValueNotIn);
    Optional.ofNullable(porValueLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::primaryObjectReferenceValueLike);
    Optional.ofNullable(porValueNotLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::primaryObjectReferenceValueNotLike);

    Optional.ofNullable(secondaryObjectReferenceIn).ifPresent(query::secondaryObjectReferenceIn);

    Optional.ofNullable(sorCompanyIn).ifPresent(query::sorCompanyIn);
    Optional.ofNullable(sorCompanyLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::sorCompanyLike);

    Optional.ofNullable(sorSystemIn).ifPresent(query::sorSystemIn);
    Optional.ofNullable(sorSystemLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::sorSystemLike);

    Optional.ofNullable(sorInstanceIn).ifPresent(query::sorSystemInstanceIn);
    Optional.ofNullable(sorInstanceLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::sorSystemInstanceLike);
    Optional.ofNullable(sorTypeIn).ifPresent(query::sorTypeIn);
    Optional.ofNullable(sorTypeLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::sorTypeLike);
    Optional.ofNullable(sorValueIn).ifPresent(query::sorValueIn);
    Optional.ofNullable(sorValueLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::sorValueLike);

    Optional.ofNullable(isRead).ifPresent(query::readEquals);

    Optional.ofNullable(isTransferred).ifPresent(query::transferredEquals);

    Optional.ofNullable(attachmentClassificationIdIn)
        .ifPresent(query::attachmentClassificationIdIn);
    Optional.ofNullable(attachmentClassificationIdNotIn)
        .ifPresent(query::attachmentClassificationIdNotIn);

    Optional.ofNullable(attachmentClassificationKeyIn)
        .ifPresent(query::attachmentClassificationKeyIn);
    Optional.ofNullable(attachmentClassificationKeyNotIn)
        .ifPresent(query::attachmentClassificationKeyNotIn);
    Optional.ofNullable(attachmentClassificationKeyLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::attachmentClassificationKeyLike);
    Optional.ofNullable(attachmentClassificationKeyNotLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::attachmentClassificationKeyNotLike);

    Optional.ofNullable(attachmentClassificationNameIn)
        .ifPresent(query::attachmentClassificationNameIn);
    Optional.ofNullable(attachmentClassificationNameNotIn)
        .ifPresent(query::attachmentClassificationNameNotIn);
    Optional.ofNullable(attachmentClassificationNameLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::attachmentClassificationNameLike);
    Optional.ofNullable(attachmentClassificationNameNotLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::attachmentClassificationNameNotLike);

    Optional.ofNullable(attachmentChannelIn).ifPresent(query::attachmentChannelIn);
    Optional.ofNullable(attachmentChannelNotIn).ifPresent(query::attachmentChannelNotIn);
    Optional.ofNullable(attachmentChannelLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::attachmentChannelLike);
    Optional.ofNullable(attachmentChannelNotLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::attachmentChannelNotLike);

    Optional.ofNullable(attachmentReferenceIn).ifPresent(query::attachmentReferenceValueIn);
    Optional.ofNullable(attachmentReferenceNotIn).ifPresent(query::attachmentReferenceValueNotIn);
    Optional.ofNullable(attachmentReferenceLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::attachmentReferenceValueLike);
    Optional.ofNullable(attachmentReferenceNotLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::attachmentReferenceValueNotLike);

    Optional.ofNullable(attachmentReceivedWithin)
        .map(this::extractTimeIntervals)
        .ifPresent(query::attachmentReceivedWithin);
    Optional.ofNullable(attachmentReceivedNotWithin)
        .map(this::extractTimeIntervals)
        .ifPresent(query::attachmentNotReceivedWithin);

    Stream.of(
            Pair.of(CUSTOM_1, of(custom1In, custom1NotIn, custom1Like, custom1NotLike)),
            Pair.of(CUSTOM_2, of(custom2In, custom2NotIn, custom2Like, custom2NotLike)),
            Pair.of(CUSTOM_3, of(custom3In, custom3NotIn, custom3Like, custom3NotLike)),
            Pair.of(CUSTOM_4, of(custom4In, custom4NotIn, custom4Like, custom4NotLike)),
            Pair.of(CUSTOM_5, of(custom5In, custom5NotIn, custom5Like, custom5NotLike)),
            Pair.of(CUSTOM_6, of(custom6In, custom6NotIn, custom6Like, custom6NotLike)),
            Pair.of(CUSTOM_7, of(custom7In, custom7NotIn, custom7Like, custom7NotLike)),
            Pair.of(CUSTOM_8, of(custom8In, custom8NotIn, custom8Like, custom8NotLike)),
            Pair.of(CUSTOM_9, of(custom9In, custom9NotIn, custom9Like, custom9NotLike)),
            Pair.of(CUSTOM_10, of(custom10In, custom10NotIn, custom10Like, custom10NotLike)),
            Pair.of(CUSTOM_11, of(custom11In, custom11NotIn, custom11Like, custom11NotLike)),
            Pair.of(CUSTOM_12, of(custom12In, custom12NotIn, custom12Like, custom12NotLike)),
            Pair.of(CUSTOM_13, of(custom13In, custom13NotIn, custom13Like, custom13NotLike)),
            Pair.of(CUSTOM_14, of(custom14In, custom14NotIn, custom14Like, custom14NotLike)),
            Pair.of(CUSTOM_15, of(custom15In, custom15NotIn, custom15Like, custom15NotLike)),
            Pair.of(CUSTOM_16, of(custom16In, custom16NotIn, custom16Like, custom16NotLike)))
        .forEach(
            pair -> {
              Optional.ofNullable(pair.getRight().getFirst())
                  .ifPresent(wrap(l -> query.customAttributeIn(pair.getLeft(), l)));
              Optional.ofNullable(pair.getRight().getSecond())
                  .ifPresent(wrap(l -> query.customAttributeNotIn(pair.getLeft(), l)));
              Optional.ofNullable(pair.getRight().getThird())
                  .map(this::wrapElementsInLikeStatement)
                  .ifPresent(wrap(l -> query.customAttributeLike(pair.getLeft(), l)));
              Optional.ofNullable(pair.getRight().getFourth())
                  .map(this::wrapElementsInLikeStatement)
                  .ifPresent(wrap(l -> query.customAttributeNotLike(pair.getLeft(), l)));
            });

    Optional.ofNullable(callbackStateIn).ifPresent(query::callbackStateIn);
    Optional.ofNullable(callbackStateNotIn).ifPresent(query::callbackStateNotIn);

    if (wildcardSearchFieldIn != null) {
      query.wildcardSearchFieldsIn(wildcardSearchFieldIn);
      query.wildcardSearchValueLike("%" + wildcardSearchValue + "%");
    }

    return null;
  }

  private void validateFilterParameters() throws InvalidArgumentException {
    if (plannedWithin != null && (plannedFrom != null || plannedUntil != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'planned' in combination "
              + "with the params 'planned-from'  and / or 'planned-until'");
    }

    if (plannedNotWithin != null && (plannedFromNot != null || plannedUntilNot != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'planned-not-in' in combination "
              + "with the params 'planned-not-in-from'  and / or 'planned-not-in-until'");
    }

    if (receivedWithin != null && (receivedFrom != null || receivedUntil != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'received' in combination "
              + "with the params 'received-from'  and / or 'received-until'");
    }

    if (receivedNotIn != null && (receivedFromNot != null || receivedUntilNot != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'received-not-in' in combination "
              + "with the params 'received-not-in-from'  and / or 'received-not-in-until'");
    }

    if (dueWithin != null && (dueFrom != null || dueUntil != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'due' in combination with the params "
              + "'due-from'  and / or 'due-until'");
    }

    if (dueNotWithin != null && (dueFromNot != null || dueUntilNot != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'due-not-in' in combination with the params "
              + "'due-not-in-from'  and / or 'due-not-in-until'");
    }

    if (createdWithin != null && (createdFrom != null || createdUntil != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'created' in combination with the params "
              + "'created-from'  and / or 'created-until'");
    }
    if (createdNotWithin != null && (createdFromNot != null || createdUntilNot != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'created-not-in' in combination with the params "
              + "'created-not-in-from'  and / or 'created-not-in-until'");
    }

    if (completedWithin != null && (completedFrom != null || completedUntil != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'completed' in combination with the params "
              + "'completed-from'  and / or 'completed-until'");
    }
    if (completedNotWithin != null && (completedFromNot != null || completedUntilNot != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'completed-not-in' in combination with the params "
              + "'completed-not-in-from'  and / or 'completed-not-in-until'");
    }

    if (wildcardSearchFieldIn == null ^ wildcardSearchValue == null) {
      throw new InvalidArgumentException(
          "The params 'wildcard-search-field' and 'wildcard-search-value' must be used together");
    }

    if (workbasketKeyIn != null && domain == null) {
      throw new InvalidArgumentException(
          "'workbasket-key' can only be used together with 'domain'.");
    }

    if (workbasketKeyNotIn != null && domain == null) {
      throw new InvalidArgumentException(
          "'workbasket-key-not' can only be used together with 'domain'.");
    }

    if (workbasketKeyIn == null && workbasketKeyNotIn == null && domain != null) {
      throw new InvalidArgumentException(
          "'domain' can only be used together with 'workbasket-key' or 'workbasket-key-not'.");
    }

    if (plannedWithin != null && plannedWithin.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'planned' is not dividable by 2");
    }

    if (receivedWithin != null && receivedWithin.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'received' is not dividable by 2");
    }

    if (dueWithin != null && dueWithin.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'due' is not dividable by 2");
    }

    if (modifiedWithin != null && modifiedWithin.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'modified' is not dividable by 2");
    }

    if (createdWithin != null && createdWithin.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'created' is not dividable by 2");
    }

    if (completedWithin != null && completedWithin.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'completed' is not dividable by 2");
    }

    if (claimedWithin != null && claimedWithin.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'claimed' is not dividable by 2");
    }

    if (attachmentReceivedWithin != null && attachmentReceivedWithin.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'attachmentReceived' is not dividable by 2");
    }

    if (plannedNotWithin != null && plannedNotWithin.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'planned-not-in' is not dividable by 2");
    }

    if (receivedNotIn != null && receivedNotIn.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'received-not-in' is not dividable by 2");
    }

    if (dueNotWithin != null && dueNotWithin.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'due-not-in' is not dividable by 2");
    }

    if (modifiedNotWithin != null && modifiedNotWithin.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'modified-not-in' is not dividable by 2");
    }

    if (createdNotWithin != null && createdNotWithin.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'created-not-in' is not dividable by 2");
    }

    if (completedNotWithin != null && completedNotWithin.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'completed-not-in' is not dividable by 2");
    }

    if (claimedNotWithin != null && claimedNotWithin.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'claimed-not-in' is not dividable by 2");
    }

    if (attachmentReceivedNotWithin != null && attachmentReceivedNotWithin.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'attachment-not-received' is not dividable by 2");
    }
  }
}
