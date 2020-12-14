package pro.taskana.simplehistory.rest;

import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.beans.ConstructorProperties;
import java.time.Instant;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.rest.QueryParameter;
import pro.taskana.simplehistory.impl.task.TaskHistoryQuery;
import pro.taskana.spi.history.api.events.task.TaskHistoryCustomField;

public class TaskHistoryQueryFilterParameter implements QueryParameter<TaskHistoryQuery, Void> {

  /** Filter by the event type of the Task History Event. This is an exact match. */
  @JsonProperty("event-type")
  private final String[] eventType;

  /**
   * Filter by the event type of the Task History Event. This results in a substring search.. (% is
   * appended to the beginning and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("event-type-like")
  private final String[] eventTypeLike;

  /** Filter by the user id of the Task History Event. This is an exact match. */
  @JsonProperty("user-id")
  private final String[] userId;

  /**
   * Filter by the user id of the Task History Event. This results in a substring search.. (% is
   * appended to the beginning and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("user-id-like")
  private final String[] userIdLike;

  /**
   * Filter by a created time interval. The length of the provided values has to be even. To create
   * an open interval you can either use 'null' or just leave it blank.
   *
   * <p>The format is ISO-8601.
   */
  private final Instant[] created;

  /** Filter by the domain of the Task History Event. This is an exact match. */
  private final String[] domain;

  /** Filter by the task id of the Task History Event. This is an exact match. */
  @JsonProperty("task-id")
  private final String[] taskId;

  /**
   * Filter by the task id of the Task History Event. This results in a substring search.. (% is
   * appended to the beginning and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("task-id-like")
  private final String[] taskIdLike;

  /** Filter by the business process id of the Task History Event. This is an exact match. */
  @JsonProperty("business-process-id")
  private final String[] businessProcessId;

  /**
   * Filter by the business process id of the Task History Event. This results into a substring
   * search. (% is appended to the beginning and end of the requested value). Further SQL "LIKE"
   * wildcard characters will be resolved correctly.
   */
  @JsonProperty("business-process-id-like")
  private final String[] businessProcessIdLike;

  /** Filter by the parent business process id of the Task History Event. This is an exact match. */
  @JsonProperty("parent-business-process-id")
  private final String[] parentBusinessProcessId;

  /**
   * Filter by the parent business process id of the Task History Event. This results into a
   * substring search. (% is appended to the beginning and end of the requested value). Further SQL
   * "Like" wildcard characters will be resolved correctly.
   */
  @JsonProperty("parent-business-process-id-like")
  private final String[] parentBusinessProcessIdLike;

  /** Filter by the task classification key of the Task History Event. This is an exact match. */
  @JsonProperty("task-classification-key")
  private final String[] taskClassificationKey;

  /**
   * Filter by the task classification key of the Task History Event. This results into a substring
   * search. (% is appended to the beginning and end of the requested value). Further SQL "LIKE"
   * wildcard characters will be resolved correctly.
   */
  @JsonProperty("task-classification-key-like")
  private final String[] taskClassificationKeyLike;

  /**
   * Filter by the task classification category of the Task History Event. This is an exact match.
   */
  @JsonProperty("task-classification-category")
  private final String[] taskClassificationCategory;

  /**
   * Filter by the task classification category of the Task History Event. This results into a
   * substring search. (% is appended to the beginning and end of the requested value). Further SQL
   * "Like" wildcard characters will be resolved correctly.
   */
  @JsonProperty("task-classification-category-like")
  private final String[] taskClassificationCategoryLike;

  /**
   * Filter by the attachment classification key of the Task History Event. This is an exact match.
   */
  @JsonProperty("attachment-classification-key")
  private final String[] attachmentClassificationKey;

  /**
   * Filter by the attachment classification key of the Task History Event. This results into a
   * substring search. (% is appended to the beginning and end of the requested value). Further SQL
   * "Like" wildcard characters will be resolved correctly.
   */
  @JsonProperty("attachment-classification-key-like")
  private final String[] attachmentClassificationKeyLike;

  /** Filter by the workbasket key of the Task History Event. This is an exact match. */
  @JsonProperty("workbasket-key")
  private final String[] workbasketKey;

  /**
   * Filter by the workbasket key of the Task History Event. This results in a substring search.. (%
   * is appended to the beginning and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("workbasket-key-like")
  private final String[] workbasketKeyLike;

  /**
   * Filter by the company of the primary object reference of the Task History Event. This is an
   * exact match.
   */
  @JsonProperty("por-company")
  private final String[] porCompany;

  /**
   * Filter by the company of the primary object reference of the Task History Event. This results
   * into a substring search. (% is appended to the beginning and end of the requested value).
   * Further SQL "LIKE" wildcard characters will be resolved correctly.
   */
  @JsonProperty("por-company-like")
  private final String[] porCompanyLike;

  /**
   * Filter by the system of the primary object reference of the Task History Event. This is an
   * exact match.
   */
  @JsonProperty("por-system")
  private final String[] porSystem;

  /**
   * Filter by the system of the primary object reference of the Task History Event. This results
   * into a substring search. (% is appended to the beginning and end of the requested value).
   * Further SQL "LIKE" wildcard characters will be resolved correctly.
   */
  @JsonProperty("por-system-like")
  private final String[] porSystemLike;

  /**
   * Filter by the system instance of the primary object reference of the Task History Event. This
   * is an exact match.
   */
  @JsonProperty("por-instance")
  private final String[] porInstance;

  /**
   * Filter by the system instance of the primary object reference of the Task History Event. This
   * results into a substring search. (% is appended to the beginning and end of the requested
   * value). Further SQL "LIKE" wildcard characters will be resolved correctly.
   */
  @JsonProperty("por-instance-like")
  private final String[] porInstanceLike;

  /**
   * Filter by the value of the primary object reference of the Task History Event. This is an exact
   * match.
   */
  @JsonProperty("por-value")
  private final String[] porValue;

  /**
   * Filter by the value of the primary object reference of the Task History Event. This results
   * into a substring search. (% is appended to the beginning and end of the requested value).
   * Further SQL "LIKE" wildcard characters will be resolved correctly.
   */
  @JsonProperty("por-value-like")
  private final String[] porValueLike;

  /** Filter by the value of the field custom1. This is an exact match. */
  @JsonProperty("custom-1")
  private final String[] custom1;

  /**
   * Filter by the value of the field custom1. This is an exact match. This results into a substring
   * search. (% is appended to the beginning and end of the requested value). Further SQL "LIKE"
   * wildcard characters will be resolved correctly.
   */
  @JsonProperty("custom-1-like")
  private final String[] custom1Like;

  /** Filter by the value of the field custom2. This is an exact match. */
  @JsonProperty("custom-2")
  private final String[] custom2;

  /**
   * Filter by the value of the field custom2. This is an exact match. This results into a substring
   * search. (% is appended to the beginning and end of the requested value). Further SQL "LIKE"
   * wildcard characters will be resolved correctly.
   */
  @JsonProperty("custom-2-like")
  private final String[] custom2Like;

  /** Filter by the value of the field custom3. This is an exact match. */
  @JsonProperty("custom-3")
  private final String[] custom3;

  /**
   * Filter by the value of the field custom3. This is an exact match. This results into a substring
   * search. (% is appended to the beginning and end of the requested value). Further SQL "LIKE"
   * wildcard characters will be resolved correctly.
   */
  @JsonProperty("custom-3-like")
  private final String[] custom3Like;

  /** Filter by the value of the field custom4. This is an exact match. */
  @JsonProperty("custom-4")
  private final String[] custom4;

  /**
   * Filter by the value of the field custom4. This is an exact match. This results into a substring
   * search. (% is appended to the beginning and end of the requested value). Further SQL "LIKE"
   * wildcard characters will be resolved correctly.
   */
  @JsonProperty("custom-4-like")
  private final String[] custom4Like;

  @SuppressWarnings("indentation")
  @ConstructorProperties({
    "event-type",
    "event-type-like",
    "user-id",
    "user-id-like",
    "created",
    "domain",
    "task-id",
    "task-id-like",
    "business-process-id",
    "business-process-id-like",
    "parent-business-process-id",
    "parent-business-process-id-like",
    "task-classification-key",
    "task-classification-key-like",
    "task-classification-category",
    "task-classification-category-like",
    "attachment-classification-key",
    "attachment-classification-key-like",
    "workbasket-key",
    "workbasket-key-like",
    "por-company",
    "por-company-like",
    "por-system",
    "por-system-like",
    "por-instance",
    "por-instance-like",
    "por-value",
    "por-value-like",
    "custom-1",
    "custom-1-like",
    "custom-2",
    "custom-2-like",
    "custom-3",
    "custom-3-like",
    "custom-4",
    "custom-4-like",
  })
  public TaskHistoryQueryFilterParameter(
      String[] eventType,
      String[] eventTypeLike,
      String[] userId,
      String[] userIdLike,
      Instant[] created,
      String[] domain,
      String[] taskId,
      String[] taskIdLike,
      String[] businessProcessId,
      String[] businessProcessIdLike,
      String[] parentBusinessProcessId,
      String[] parentBusinessProcessIdLike,
      String[] taskClassificationKey,
      String[] taskClassificationKeyLike,
      String[] taskClassificationCategory,
      String[] taskClassificationCategoryLike,
      String[] attachmentClassificationKey,
      String[] attachmentClassificationKeyLike,
      String[] workbasketKey,
      String[] workbasketKeyLike,
      String[] porCompany,
      String[] porCompanyLike,
      String[] porSystem,
      String[] porSystemLike,
      String[] porInstance,
      String[] porInstanceLike,
      String[] porValue,
      String[] porValueLike,
      String[] custom1,
      String[] custom1Like,
      String[] custom2,
      String[] custom2Like,
      String[] custom3,
      String[] custom3Like,
      String[] custom4,
      String[] custom4Like)
      throws InvalidArgumentException {
    this.eventType = eventType;
    this.eventTypeLike = eventTypeLike;
    this.userId = userId;
    this.userIdLike = userIdLike;
    this.created = created;
    this.domain = domain;
    this.taskId = taskId;
    this.taskIdLike = taskIdLike;
    this.businessProcessId = businessProcessId;
    this.businessProcessIdLike = businessProcessIdLike;
    this.parentBusinessProcessId = parentBusinessProcessId;
    this.parentBusinessProcessIdLike = parentBusinessProcessIdLike;
    this.taskClassificationKey = taskClassificationKey;
    this.taskClassificationKeyLike = taskClassificationKeyLike;
    this.taskClassificationCategory = taskClassificationCategory;
    this.taskClassificationCategoryLike = taskClassificationCategoryLike;
    this.attachmentClassificationKey = attachmentClassificationKey;
    this.attachmentClassificationKeyLike = attachmentClassificationKeyLike;
    this.workbasketKey = workbasketKey;
    this.workbasketKeyLike = workbasketKeyLike;
    this.porCompany = porCompany;
    this.porCompanyLike = porCompanyLike;
    this.porSystem = porSystem;
    this.porSystemLike = porSystemLike;
    this.porInstance = porInstance;
    this.porInstanceLike = porInstanceLike;
    this.porValue = porValue;
    this.porValueLike = porValueLike;
    this.custom1 = custom1;
    this.custom1Like = custom1Like;
    this.custom2 = custom2;
    this.custom2Like = custom2Like;
    this.custom3 = custom3;
    this.custom3Like = custom3Like;
    this.custom4 = custom4;
    this.custom4Like = custom4Like;

    validateFilterParameters();
  }

  @Override
  public Void applyToQuery(TaskHistoryQuery query) {
    ofNullable(eventType).ifPresent(query::eventTypeIn);
    ofNullable(eventTypeLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::eventTypeLike);
    ofNullable(userId).ifPresent(query::userIdIn);
    ofNullable(userIdLike).map(this::wrapElementsInLikeStatement).ifPresent(query::userIdLike);
    ofNullable(created).map(this::extractTimeIntervals).ifPresent(query::createdWithin);
    ofNullable(domain).ifPresent(query::domainIn);
    ofNullable(taskId).ifPresent(query::taskIdIn);
    ofNullable(taskIdLike).map(this::wrapElementsInLikeStatement).ifPresent(query::taskIdLike);
    ofNullable(businessProcessId).ifPresent(query::businessProcessIdIn);
    ofNullable(businessProcessIdLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::businessProcessIdLike);
    ofNullable(parentBusinessProcessId).ifPresent(query::parentBusinessProcessIdIn);
    ofNullable(parentBusinessProcessIdLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::parentBusinessProcessIdLike);
    ofNullable(taskClassificationKey).ifPresent(query::taskClassificationKeyIn);
    ofNullable(taskClassificationKeyLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::taskClassificationKeyLike);
    ofNullable(taskClassificationCategory).ifPresent(query::taskClassificationCategoryIn);
    ofNullable(taskClassificationCategoryLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::taskClassificationCategoryLike);
    ofNullable(attachmentClassificationKey).ifPresent(query::attachmentClassificationKeyIn);
    ofNullable(attachmentClassificationKeyLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::attachmentClassificationKeyLike);
    ofNullable(workbasketKey).ifPresent(query::workbasketKeyIn);
    ofNullable(workbasketKeyLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::workbasketKeyLike);
    ofNullable(porCompany).ifPresent(query::porCompanyIn);
    ofNullable(porCompanyLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::porCompanyLike);
    ofNullable(porSystem).ifPresent(query::porSystemIn);
    ofNullable(porSystemLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::porSystemLike);
    ofNullable(porInstance).ifPresent(query::porInstanceIn);
    ofNullable(porInstanceLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::porInstanceLike);
    ofNullable(porValue).ifPresent(query::porValueIn);
    ofNullable(porValueLike).map(this::wrapElementsInLikeStatement).ifPresent(query::porValueLike);
    ofNullable(custom1)
        .ifPresent(arr -> query.customAttributeIn(TaskHistoryCustomField.CUSTOM_1, arr));
    ofNullable(custom1Like)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(arr -> query.customAttributeLike(TaskHistoryCustomField.CUSTOM_1, arr));
    ofNullable(custom2)
        .ifPresent(arr -> query.customAttributeIn(TaskHistoryCustomField.CUSTOM_2, arr));
    ofNullable(custom2Like)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(arr -> query.customAttributeLike(TaskHistoryCustomField.CUSTOM_2, arr));
    ofNullable(custom3)
        .ifPresent(arr -> query.customAttributeIn(TaskHistoryCustomField.CUSTOM_3, arr));
    ofNullable(custom3Like)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(arr -> query.customAttributeLike(TaskHistoryCustomField.CUSTOM_3, arr));
    ofNullable(custom4)
        .ifPresent(arr -> query.customAttributeIn(TaskHistoryCustomField.CUSTOM_4, arr));
    ofNullable(custom4Like)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(arr -> query.customAttributeLike(TaskHistoryCustomField.CUSTOM_4, arr));
    return null;
  }

  private void validateFilterParameters() throws InvalidArgumentException {
    if (created != null && created.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'created' is not dividable by 2");
    }
  }
}
