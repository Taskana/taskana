package pro.taskana.monitor.rest;

import static pro.taskana.common.internal.util.CheckedConsumer.wrap;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import pro.taskana.common.internal.util.Pair;
import pro.taskana.common.rest.QueryParameter;
import pro.taskana.monitor.api.reports.TimeIntervalReportBuilder;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskState;

public class TimeIntervalReportFilterParameter
    implements QueryParameter<TimeIntervalReportBuilder<?, ?, TimeIntervalColumnHeader>, Void> {

  /** Determine the ColumnHeaders for the given report. */
  @JsonProperty("column-header")
  private final TimeIntervalColumnHeader[] columnHeader;

  /** Determine weather the report should convert the age of the tasks into working days. */
  @JsonProperty("in-working-days")
  private final boolean inWorkingDays;

  /** Filter by workbasket id of the task. This is an exact match. */
  @JsonProperty("workbasket-id")
  private final String[] workbasketId;

  /** Filter by the task state. This is an exact match. */
  private final TaskState[] state;

  /** Filter by the classification category of the task. This is an exact match. */
  @JsonProperty("classification-category")
  private final String[] classificationCategory;

  /** Filter by domain of the task. This is an exact match. */
  private final String[] domain;

  /** Filter by the classification id of the task. This is an exact match. */
  @JsonProperty("classification-id")
  private final String[] classificationId;

  /** Filter by the classification id of the task. This is an exact match. */
  @JsonProperty("excluded-classification-id")
  private final String[] excludedClassificationId;

  /** Filter by the value of the field custom1 of the task. This is an exact match. */
  @JsonProperty("custom-1")
  private final String[] custom1;

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

  /**
   * Filter by the custom16 field of the task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-16-like")
  private final String[] custom16Like;

  @ConstructorProperties({
    "column-header",
    "in-working-days",
    "workbasket-id",
    "states",
    "classification-category",
    "domains",
    "classification-id",
    "excluded-classification-id",
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
  public TimeIntervalReportFilterParameter(
      TimeIntervalColumnHeader[] columnHeader,
      boolean inWorkingDays,
      String[] workbasketId,
      TaskState[] state,
      String[] classificationCategory,
      String[] domain,
      String[] classificationId,
      String[] excludedClassificationId,
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
      String[] custom16Like) {
    this.columnHeader = columnHeader;
    this.inWorkingDays = inWorkingDays;
    this.workbasketId = workbasketId;
    this.state = state;
    this.classificationCategory = classificationCategory;
    this.domain = domain;
    this.classificationId = classificationId;
    this.excludedClassificationId = excludedClassificationId;
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
  }

  @Override
  public Void apply(TimeIntervalReportBuilder<?, ?, TimeIntervalColumnHeader> builder) {
    Optional.ofNullable(columnHeader).map(Arrays::asList).ifPresent(builder::withColumnHeaders);
    if (inWorkingDays) {
      builder.inWorkingDays();
    }
    Optional.ofNullable(workbasketId).map(Arrays::asList).ifPresent(builder::workbasketIdIn);
    Optional.ofNullable(state).map(Arrays::asList).ifPresent(builder::stateIn);
    Optional.ofNullable(classificationCategory)
        .map(Arrays::asList)
        .ifPresent(builder::classificationCategoryIn);
    Optional.ofNullable(domain).map(Arrays::asList).ifPresent(builder::domainIn);
    Optional.ofNullable(classificationId)
        .map(Arrays::asList)
        .ifPresent(builder::classificationIdIn);
    Optional.ofNullable(excludedClassificationId)
        .map(Arrays::asList)
        .ifPresent(builder::excludedClassificationIdIn);

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
                    .ifPresent(wrap(l -> builder.customAttributeIn(pair.getLeft(), l))));
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
                    .ifPresent(wrap(l -> builder.customAttributeLike(pair.getLeft(), l))));
    return null;
  }
}
