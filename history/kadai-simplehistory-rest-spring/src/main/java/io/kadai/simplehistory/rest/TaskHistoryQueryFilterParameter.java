package io.kadai.simplehistory.rest;

import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.rest.QueryParameter;
import io.kadai.simplehistory.impl.task.TaskHistoryQuery;
import io.kadai.spi.history.api.events.task.TaskHistoryCustomField;
import io.swagger.v3.oas.annotations.media.Schema;
import java.beans.ConstructorProperties;
import java.time.Instant;

public class TaskHistoryQueryFilterParameter implements QueryParameter<TaskHistoryQuery, Void> {
  @Schema(
      name = "event-type",
      description = "Filter by the event type of the Task History Event. This is an exact match.")
  @JsonProperty("event-type")
  private final String[] eventType;

  @Schema(
      name = "event-type-like",
      description =
          "Filter by the event type of the Task History Event. This results in a substring search.."
              + " (% is appended to the beginning and end of the requested value). Further SQL "
              + "\"LIKE\" wildcard characters will be resolved correctly.")
  @JsonProperty("event-type-like")
  private final String[] eventTypeLike;

  @Schema(
      name = "user-id",
      description = "Filter by the user id of the Task History Event. This is an exact match.")
  @JsonProperty("user-id")
  private final String[] userId;

  @Schema(
      name = "user-id-like",
      description =
          "Filter by the user id of the Task History Event. This results in a substring search.. "
              + "(% is appended to the beginning and end of the requested value). Further SQL "
              + "\"LIKE\" wildcard characters will be resolved correctly.")
  @JsonProperty("user-id-like")
  private final String[] userIdLike;

  @Schema(
      name = "created",
      description =
          "Filter by a created time interval. The length of the provided values has to be even. To "
              + "create an open interval you can either use 'null' or just leave it blank.<p>The "
              + "format is ISO-8601.")
  private final Instant[] created;

  @Schema(
      name = "domain",
      description = "Filter by the domain of the Task History Event. This is an exact match.")
  private final String[] domain;

  @Schema(
      name = "task-id",
      description = "Filter by the task id of the Task History Event. This is an exact match.")
  @JsonProperty("task-id")
  private final String[] taskId;

  @Schema(
      name = "task-id-like",
      description =
          "Filter by the task id of the Task History Event. This results in a substring search.. (%"
              + " is appended to the beginning and end of the requested value). Further SQL "
              + "\"LIKE\" wildcard characters will be resolved correctly.")
  @JsonProperty("task-id-like")
  private final String[] taskIdLike;

  @Schema(
      name = "business-process-id",
      description =
          "Filter by the business process id of the Task History Event. This is an exact match.")
  @JsonProperty("business-process-id")
  private final String[] businessProcessId;

  @Schema(
      name = "business-process-id-like",
      description =
          "Filter by the business process id of the Task History Event. This results into a "
              + "substring search. (% is appended to the beginning and end of the requested value)."
              + " Further SQL \"LIKE\" wildcard characters will be resolved correctly.")
  @JsonProperty("business-process-id-like")
  private final String[] businessProcessIdLike;

  @Schema(
      name = "parent-business-process-id",
      description =
          "Filter by the parent business process id of the Task History Event. This is an exact "
              + "match.")
  @JsonProperty("parent-business-process-id")
  private final String[] parentBusinessProcessId;

  @Schema(
      name = "parent-business-process-id-like",
      description =
          "Filter by the parent business process id of the Task History Event. This results into a "
              + "substring search. (% is appended to the beginning and end of the requested value)."
              + " Further SQL \"Like\" wildcard characters will be resolved correctly.")
  @JsonProperty("parent-business-process-id-like")
  private final String[] parentBusinessProcessIdLike;

  @Schema(
      name = "task-classification-key",
      description =
          "Filter by the task classification key of the Task History Event. This is an exact "
              + "match.")
  @JsonProperty("task-classification-key")
  private final String[] taskClassificationKey;

  @Schema(
      name = "task-classification-key-like",
      description =
          "Filter by the task classification key of the Task History Event. This results into a "
              + "substring search. (% is appended to the beginning and end of the requested value)."
              + " Further SQL \"LIKE\" wildcard characters will be resolved correctly.")
  @JsonProperty("task-classification-key-like")
  private final String[] taskClassificationKeyLike;

  @Schema(
      name = "task-classification-category",
      description =
          "Filter by the task classification category of the Task History Event. This is an exact "
              + "match.")
  @JsonProperty("task-classification-category")
  private final String[] taskClassificationCategory;

  @Schema(
      name = "task-classification-category-like",
      description =
          "Filter by the task classification category of the Task History Event. This results into "
              + "a substring search. (% is appended to the beginning and end of the requested "
              + "value). Further SQL \"Like\" wildcard characters will be resolved correctly.")
  @JsonProperty("task-classification-category-like")
  private final String[] taskClassificationCategoryLike;

  @Schema(
      name = "attachment-classification-key",
      description =
          "Filter by the attachment classification key of the Task History Event. This is an exact "
              + "match.")
  @JsonProperty("attachment-classification-key")
  private final String[] attachmentClassificationKey;

  @Schema(
      name = "attachment-classification-key-like",
      description =
          "Filter by the attachment classification key of the Task History Event. This results into"
              + " a substring search. (% is appended to the beginning and end of the requested "
              + "value). Further SQL \"Like\" wildcard characters will be resolved correctly.")
  @JsonProperty("attachment-classification-key-like")
  private final String[] attachmentClassificationKeyLike;

  @Schema(
      name = "workbasket-key",
      description =
          "Filter by the workbasket key of the Task History Event. This is an exact match.")
  @JsonProperty("workbasket-key")
  private final String[] workbasketKey;

  @Schema(
      name = "workbasket-key-like",
      description =
          "Filter by the workbasket key of the Task History Event. This results in a substring "
              + "search.. (% is appended to the beginning and end of the requested value). Further "
              + "SQL \"LIKE\" wildcard characters will be resolved correctly.")
  @JsonProperty("workbasket-key-like")
  private final String[] workbasketKeyLike;

  @Schema(
      name = "por-company",
      description =
          "* Filter by the company of the primary object reference of the Task History Event. This "
              + "is an exact match.")
  @JsonProperty("por-company")
  private final String[] porCompany;

  @Schema(
      name = "por-company-like",
      description =
          "Filter by the company of the primary object reference of the Task History Event. This "
              + "results into a substring search. (% is appended to the beginning and end of the "
              + "requested value). Further SQL \"LIKE\" wildcard characters will be resolved "
              + "correctly.")
  @JsonProperty("por-company-like")
  private final String[] porCompanyLike;

  @Schema(
      name = "por-system",
      description =
          "Filter by the system of the primary object reference of the Task History Event. This is "
              + "an exact match.")
  @JsonProperty("por-system")
  private final String[] porSystem;

  @Schema(
      name = "por-system-like",
      description =
          "Filter by the system of the primary object reference of the Task History Event. This "
              + "results into a substring search. (% is appended to the beginning and end of the "
              + "requested value). Further SQL \"LIKE\" wildcard characters will be resolved "
              + "correctly.")
  @JsonProperty("por-system-like")
  private final String[] porSystemLike;

  @Schema(
      name = "por-instance",
      description =
          "Filter by the system instance of the primary object reference of the Task History Event."
              + " This is an exact match.")
  @JsonProperty("por-instance")
  private final String[] porInstance;

  @Schema(
      name = "por-instance-like",
      description =
          "Filter by the system instance of the primary object reference of the Task History Event."
              + " This results into a substring search. (% is appended to the beginning and end of "
              + "the requested value). Further SQL \"LIKE\" wildcard characters will be resolved "
              + "correctly.")
  @JsonProperty("por-instance-like")
  private final String[] porInstanceLike;

  @Schema(
      name = "por-value",
      description =
          "Filter by the value of the primary object reference of the Task History Event. This is "
              + "an exact match.")
  @JsonProperty("por-value")
  private final String[] porValue;

  @Schema(
      name = "por-value-like",
      description =
          "Filter by the value of the primary object reference of the Task History Event. This "
              + "results into a substring search. (% is appended to the beginning and end of the "
              + "requested value). Further SQL \"LIKE\" wildcard characters will be resolved "
              + "correctly.")
  @JsonProperty("por-value-like")
  private final String[] porValueLike;

  @Schema(
      name = "custom-1",
      description = "Filter by the value of the field custom1. This is an exact match.")
  @JsonProperty("custom-1")
  private final String[] custom1;

  @Schema(
      name = "custom-1-like",
      description =
          "Filter by the value of the field custom1. This is an exact match. This results into a "
              + "substring search. (% is appended to the beginning and end of the requested value)."
              + " Further SQL \"LIKE\" wildcard characters will be resolved correctly.")
  @JsonProperty("custom-1-like")
  private final String[] custom1Like;

  @Schema(
      name = "custom-2",
      description = "Filter by the value of the field custom2. This is an exact match.")
  @JsonProperty("custom-2")
  private final String[] custom2;

  @Schema(
      name = "custom-2-like",
      description =
          "Filter by the value of the field custom2. This is an exact match. This results into a "
              + "substring search. (% is appended to the beginning and end of the requested value)."
              + " Further SQL \"LIKE\" wildcard characters will be resolved correctly.")
  @JsonProperty("custom-2-like")
  private final String[] custom2Like;

  @Schema(
      name = "custom-3",
      description = "Filter by the value of the field custom3. This is an exact match.")
  @JsonProperty("custom-3")
  private final String[] custom3;

  @Schema(
      name = "custom-3-like",
      description =
          "Filter by the value of the field custom3. This is an exact match. This results into a "
              + "substring search. (% is appended to the beginning and end of the requested value)."
              + " Further SQL \"LIKE\" wildcard characters will be resolved correctly.")
  @JsonProperty("custom-3-like")
  private final String[] custom3Like;

  @Schema(
      name = "custom-4",
      description = "Filter by the value of the field custom4. This is an exact match.")
  @JsonProperty("custom-4")
  private final String[] custom4;

  @Schema(
      name = "custom-4-like",
      description =
          "Filter by the value of the field custom4. This is an exact match. This results into a "
              + "substring search. (% is appended to the beginning and end of the requested value)."
              + " Further SQL \"LIKE\" wildcard characters will be resolved correctly.")
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

  public String[] getEventType() {
    return eventType;
  }

  public String[] getEventTypeLike() {
    return eventTypeLike;
  }

  public String[] getUserId() {
    return userId;
  }

  public String[] getUserIdLike() {
    return userIdLike;
  }

  public Instant[] getCreated() {
    return created;
  }

  public String[] getDomain() {
    return domain;
  }

  public String[] getTaskId() {
    return taskId;
  }

  public String[] getTaskIdLike() {
    return taskIdLike;
  }

  public String[] getBusinessProcessId() {
    return businessProcessId;
  }

  public String[] getBusinessProcessIdLike() {
    return businessProcessIdLike;
  }

  public String[] getParentBusinessProcessId() {
    return parentBusinessProcessId;
  }

  public String[] getParentBusinessProcessIdLike() {
    return parentBusinessProcessIdLike;
  }

  public String[] getTaskClassificationKey() {
    return taskClassificationKey;
  }

  public String[] getTaskClassificationKeyLike() {
    return taskClassificationKeyLike;
  }

  public String[] getTaskClassificationCategory() {
    return taskClassificationCategory;
  }

  public String[] getTaskClassificationCategoryLike() {
    return taskClassificationCategoryLike;
  }

  public String[] getAttachmentClassificationKey() {
    return attachmentClassificationKey;
  }

  public String[] getAttachmentClassificationKeyLike() {
    return attachmentClassificationKeyLike;
  }

  public String[] getWorkbasketKey() {
    return workbasketKey;
  }

  public String[] getWorkbasketKeyLike() {
    return workbasketKeyLike;
  }

  public String[] getPorCompany() {
    return porCompany;
  }

  public String[] getPorCompanyLike() {
    return porCompanyLike;
  }

  public String[] getPorSystem() {
    return porSystem;
  }

  public String[] getPorSystemLike() {
    return porSystemLike;
  }

  public String[] getPorInstance() {
    return porInstance;
  }

  public String[] getPorInstanceLike() {
    return porInstanceLike;
  }

  public String[] getPorValue() {
    return porValue;
  }

  public String[] getPorValueLike() {
    return porValueLike;
  }

  public String[] getCustom1() {
    return custom1;
  }

  public String[] getCustom1Like() {
    return custom1Like;
  }

  public String[] getCustom2() {
    return custom2;
  }

  public String[] getCustom2Like() {
    return custom2Like;
  }

  public String[] getCustom3() {
    return custom3;
  }

  public String[] getCustom3Like() {
    return custom3Like;
  }

  public String[] getCustom4() {
    return custom4;
  }

  public String[] getCustom4Like() {
    return custom4Like;
  }

  @Override
  public Void apply(TaskHistoryQuery query) {
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
