package pro.taskana.monitor.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.beans.ConstructorProperties;
import lombok.RequiredArgsConstructor;

import pro.taskana.task.api.TaskState;

@RequiredArgsConstructor(
    onConstructor =
        @__({
          @ConstructorProperties({
            "in-working-days",
            "workbasket-id",
            "state",
            "classification-category",
            "domain",
            "classification-id",
            "excluded-classification-id",
            "custom-1",
            "custom-1-like",
            "custom-1-not-in",
            "custom-2",
            "custom-2-like",
            "custom-2-not-in",
            "custom-3",
            "custom-3-like",
            "custom-3-not-in",
            "custom-4",
            "custom-4-like",
            "custom-4-not-in",
            "custom-5",
            "custom-5-like",
            "custom-5-not-in",
            "custom-6",
            "custom-6-like",
            "custom-6-not-in",
            "custom-7",
            "custom-7-like",
            "custom-7-not-in",
            "custom-8",
            "custom-8-like",
            "custom-8-not-in",
            "custom-9",
            "custom-9-like",
            "custom-9-not-in",
            "custom-10",
            "custom-10-like",
            "custom-10-not-in",
            "custom-11",
            "custom-11-like",
            "custom-11-not-in",
            "custom-12",
            "custom-12-like",
            "custom-12-not-in",
            "custom-13",
            "custom-13-like",
            "custom-13-not-in",
            "custom-14",
            "custom-14-like",
            "custom-14-not-in",
            "custom-15",
            "custom-15-like",
            "custom-15-not-in",
            "custom-16",
            "custom-16-like",
            "custom-16-not-in"
          })
        }))
public class ReportFilterParameter {

  /** Determine weather the report should convert the age of the Tasks into working days. */
  @JsonProperty("in-working-days")
  protected final Boolean inWorkingDays;

  /** Filter by workbasket id of the Task. This is an exact match. */
  @JsonProperty("workbasket-id")
  protected final String[] workbasketId;

  /** Filter by the task state. This is an exact match. */
  @JsonProperty("state")
  protected final TaskState[] state;

  /** Filter by the classification category of the Task. This is an exact match. */
  @JsonProperty("classification-category")
  protected final String[] classificationCategory;

  /** Filter by domain of the Task. This is an exact match. */
  @JsonProperty("domain")
  protected final String[] domain;

  /** Filter by the classification id of the Task. This is an exact match. */
  @JsonProperty("classification-id")
  protected final String[] classificationId;

  /** Filter by the classification id of the Task. This is an exact match. */
  @JsonProperty("excluded-classification-id")
  protected final String[] excludedClassificationId;

  /** Filter by the value of the field custom1 of the Task. This is an exact match. */
  @JsonProperty("custom-1")
  protected final String[] custom1;

  /**
   * Filter by the custom1 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-1-like")
  protected final String[] custom1Like;

  /** Filter out by values of the field custom1 of the Task. This is an exact match. */
  @JsonProperty("custom-1-not-in")
  protected final String[] custom1NotIn;

  /** Filter by the value of the field custom2 of the Task. This is an exact match. */
  @JsonProperty("custom-2")
  protected final String[] custom2;

  /**
   * Filter by the custom2 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-2-like")
  protected final String[] custom2Like;

  /** Filter out by values of the field custom2 of the Task. This is an exact match. */
  @JsonProperty("custom-2-not-in")
  protected final String[] custom2NotIn;

  /** Filter by the value of the field custom3 of the Task. This is an exact match. */
  @JsonProperty("custom-3")
  protected final String[] custom3;

  /**
   * Filter by the custom3 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-3-like")
  protected final String[] custom3Like;

  /** Filter out by values of the field custom3 of the Task. This is an exact match. */
  @JsonProperty("custom-3-not-in")
  protected final String[] custom3NotIn;

  /** Filter by the value of the field custom4 of the Task. This is an exact match. */
  @JsonProperty("custom-4")
  protected final String[] custom4;

  /**
   * Filter by the custom4 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-4-like")
  protected final String[] custom4Like;

  /** Filter out by values of the field custom4 of the Task. This is an exact match. */
  @JsonProperty("custom-4-not-in")
  protected final String[] custom4NotIn;

  /** Filter by the value of the field custom5 of the Task. This is an exact match. */
  @JsonProperty("custom-5")
  protected final String[] custom5;

  /**
   * Filter by the custom5 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-5-like")
  protected final String[] custom5Like;

  /** Filter out by values of the field custom5 of the Task. This is an exact match. */
  @JsonProperty("custom-5-not-in")
  protected final String[] custom5NotIn;

  /** Filter by the value of the field custom6 of the Task. This is an exact match. */
  @JsonProperty("custom-6")
  protected final String[] custom6;

  /**
   * Filter by the custom6 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-6-like")
  protected final String[] custom6Like;

  /** Filter out by values of the field custom6 of the Task. This is an exact match. */
  @JsonProperty("custom-6-not-in")
  protected final String[] custom6NotIn;

  /** Filter by the value of the field custom7 of the Task. This is an exact match. */
  @JsonProperty("custom-7")
  protected final String[] custom7;

  /**
   * Filter by the custom7 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-7-like")
  protected final String[] custom7Like;

  /** Filter out by values of the field custom7 of the Task. This is an exact match. */
  @JsonProperty("custom-7-not-in")
  protected final String[] custom7NotIn;

  /** Filter by the value of the field custom8 of the Task. This is an exact match. */
  @JsonProperty("custom-8")
  protected final String[] custom8;

  /**
   * Filter by the custom8 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-8-like")
  protected final String[] custom8Like;

  /** Filter out by values of the field custom8 of the Task. This is an exact match. */
  @JsonProperty("custom-8-not-in")
  protected final String[] custom8NotIn;

  /** Filter by the value of the field custom9 of the Task. This is an exact match. */
  @JsonProperty("custom-9")
  protected final String[] custom9;

  /**
   * Filter by the custom9 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-9-like")
  protected final String[] custom9Like;

  /** Filter out by values of the field custom9 of the Task. This is an exact match. */
  @JsonProperty("custom-9-not-in")
  protected final String[] custom9NotIn;

  /** Filter by the value of the field custom10 of the Task. This is an exact match. */
  @JsonProperty("custom-10")
  protected final String[] custom10;

  /**
   * Filter by the custom10 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-10-like")
  protected final String[] custom10Like;

  /** Filter out by values of the field custom10 of the Task. This is an exact match. */
  @JsonProperty("custom-10-not-in")
  protected final String[] custom10NotIn;

  /** Filter by the value of the field custom11 of the Task. This is an exact match. */
  @JsonProperty("custom-11")
  protected final String[] custom11;

  /**
   * Filter by the custom11 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-11-like")
  protected final String[] custom11Like;

  /** Filter out by values of the field custom11 of the Task. This is an exact match. */
  @JsonProperty("custom-11-not-in")
  protected final String[] custom11NotIn;

  /** Filter by the value of the field custom12 of the Task. This is an exact match. */
  @JsonProperty("custom-12")
  protected final String[] custom12;

  /**
   * Filter by the custom12 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-12-like")
  protected final String[] custom12Like;

  /** Filter out by values of the field custom12 of the Task. This is an exact match. */
  @JsonProperty("custom-12-not-in")
  protected final String[] custom12NotIn;

  /** Filter by the value of the field custom13 of the Task. This is an exact match. */
  @JsonProperty("custom-13")
  protected final String[] custom13;

  /**
   * Filter by the custom13 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-13-like")
  protected final String[] custom13Like;

  /** Filter out by values of the field custom13 of the Task. This is an exact match. */
  @JsonProperty("custom-13-not-in")
  protected final String[] custom13NotIn;

  /** Filter by the value of the field custom14 of the Task. This is an exact match. */
  @JsonProperty("custom-14")
  protected final String[] custom14;

  /**
   * Filter by the custom14 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-14-like")
  protected final String[] custom14Like;

  /** Filter out by values of the field custom14 of the Task. This is an exact match. */
  @JsonProperty("custom-14-not-in")
  protected final String[] custom14NotIn;

  /** Filter by the value of the field custom15 of the Task. This is an exact match. */
  @JsonProperty("custom-15")
  protected final String[] custom15;

  /**
   * Filter by the custom15 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-15-like")
  protected final String[] custom15Like;

  /** Filter out by values of the field custom15 of the Task. This is an exact match. */
  @JsonProperty("custom-15-not-in")
  protected final String[] custom15NotIn;

  /** Filter by the value of the field custom16 of the Task. This is an exact match. */
  @JsonProperty("custom-16")
  protected final String[] custom16;

  /**
   * Filter by the custom16 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-16-like")
  protected final String[] custom16Like;

  /** Filter out by values of the field custom16 of the Task. This is an exact match. */
  @JsonProperty("custom-16-not-in")
  protected final String[] custom16NotIn;
}
