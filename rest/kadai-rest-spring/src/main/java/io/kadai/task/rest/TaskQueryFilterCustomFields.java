package io.kadai.task.rest;

import static io.kadai.common.internal.util.CheckedConsumer.wrap;
import static io.kadai.common.internal.util.Quadruple.of;
import static io.kadai.task.api.TaskCustomField.CUSTOM_1;
import static io.kadai.task.api.TaskCustomField.CUSTOM_10;
import static io.kadai.task.api.TaskCustomField.CUSTOM_11;
import static io.kadai.task.api.TaskCustomField.CUSTOM_12;
import static io.kadai.task.api.TaskCustomField.CUSTOM_13;
import static io.kadai.task.api.TaskCustomField.CUSTOM_14;
import static io.kadai.task.api.TaskCustomField.CUSTOM_15;
import static io.kadai.task.api.TaskCustomField.CUSTOM_16;
import static io.kadai.task.api.TaskCustomField.CUSTOM_2;
import static io.kadai.task.api.TaskCustomField.CUSTOM_3;
import static io.kadai.task.api.TaskCustomField.CUSTOM_4;
import static io.kadai.task.api.TaskCustomField.CUSTOM_5;
import static io.kadai.task.api.TaskCustomField.CUSTOM_6;
import static io.kadai.task.api.TaskCustomField.CUSTOM_7;
import static io.kadai.task.api.TaskCustomField.CUSTOM_8;
import static io.kadai.task.api.TaskCustomField.CUSTOM_9;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.kadai.common.internal.util.Pair;
import io.kadai.common.rest.QueryParameter;
import io.kadai.task.api.TaskQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.beans.ConstructorProperties;
import java.util.Optional;
import java.util.stream.Stream;

public class TaskQueryFilterCustomFields implements QueryParameter<TaskQuery, Void> {
  @Schema(
      name = "custom-1",
      description = "Filter by the value of the field custom1 of the Task. This is an exact match.")
  @JsonProperty("custom-1")
  private final String[] custom1In;

  @Schema(name = "custom-1-not", description = "Exclude values of the field custom1 of the Task.")
  @JsonProperty("custom-1-not")
  private final String[] custom1NotIn;

  @Schema(
      name = "custom-1-like",
      description =
          "Filter by the custom1 field of the Task. This results in a substring search (% is "
              + "appended to the front and end of the requested value). Further SQL \"LIKE\" "
              + "wildcard characters will be resolved correctly.")
  @JsonProperty("custom-1-like")
  private final String[] custom1Like;

  @Schema(
      name = "custom-1-not-like",
      description =
          "Filter by what the custom1 field of the Task shouldn't be. This results in a substring"
              + " search (% is appended to the front and end of the requested value). Further SQL "
              + "\"LIKE\" wildcard characters will be resolved correctly.")
  @JsonProperty("custom-1-not-like")
  private final String[] custom1NotLike;

  @Schema(
      name = "custom-2",
      description = "Filter by the value of the field custom2 of the Task. This is an exact match.")
  @JsonProperty("custom-2")
  private final String[] custom2In;

  @Schema(
      name = "custom-2-not",
      description =
          "Filter out by values of the field custom2 of the Task. This is an exact match.")
  @JsonProperty("custom-2-not")
  private final String[] custom2NotIn;

  @Schema(
      name = "custom-2-like",
      description =
          "Filter by the custom2 field of the Task. This results in a substring search (% is "
              + "appended to the front and end of the requested value). Further SQL \"LIKE\" "
              + "wildcard characters will be resolved correctly.")
  @JsonProperty("custom-2-like")
  private final String[] custom2Like;

  @Schema(
      name = "custom-2-not-like",
      description =
          "Filter by what the custom2 field of the Task shouldn't be. This results in a substring"
              + " search (% is appended to the front and end of the requested value). Further SQL "
              + "\"LIKE\" wildcard characters will be resolved correctly.")
  @JsonProperty("custom-2-not-like")
  private final String[] custom2NotLike;

  @Schema(
      name = "custom-3",
      description = "Filter by the value of the field custom3 of the Task. This is an exact match.")
  @JsonProperty("custom-3")
  private final String[] custom3In;

  @Schema(
      name = "custom-3-not",
      description =
          "Filter out by values of the field custom3 of the Task. This is an exact match.")
  @JsonProperty("custom-3-not")
  private final String[] custom3NotIn;

  @Schema(
      name = "custom-3-like",
      description =
          "Filter by the custom3 field of the Task. This results in a substring search (% is "
              + "appended to the front and end of the requested value). Further SQL \"LIKE\" "
              + "wildcard characters will be resolved correctly.")
  @JsonProperty("custom-3-like")
  private final String[] custom3Like;

  @Schema(
      name = "custom-3-not-like",
      description =
          "Filter by what the custom3 field of the Task shouldn't be. This results in a substring"
              + " search (% is appended to the front and end of the requested value). Further SQL "
              + "\"LIKE\" wildcard characters will be resolved correctly.")
  @JsonProperty("custom-3-not-like")
  private final String[] custom3NotLike;

  @Schema(
      name = "custom-4",
      description = "Filter by the value of the field custom4 of the Task. This is an exact match.")
  @JsonProperty("custom-4")
  private final String[] custom4In;

  @Schema(
      name = "custom-4-not",
      description =
          "Filter out by values of the field custom4 of the Task. This is an exact match.")
  @JsonProperty("custom-4-not")
  private final String[] custom4NotIn;

  @Schema(
      name = "custom-4-like",
      description =
          "Filter by the custom4 field of the Task. This results in a substring search (% is "
              + "appended to the front and end of the requested value). Further SQL \"LIKE\" "
              + "wildcard characters will be resolved correctly.")
  @JsonProperty("custom-4-like")
  private final String[] custom4Like;

  @Schema(
      name = "custom-4-not-like",
      description =
          "Filter by what the custom4 field of the Task shouldn't be. This results in a substring"
              + " search (% is appended to the front and end of the requested value). Further SQL "
              + "\"LIKE\" wildcard characters will be resolved correctly.")
  @JsonProperty("custom-4-not-like")
  private final String[] custom4NotLike;

  @Schema(
      name = "custom-5",
      description = "Filter by the value of the field custom5 of the Task. This is an exact match.")
  @JsonProperty("custom-5")
  private final String[] custom5In;

  @Schema(
      name = "custom-5-not",
      description =
          "Filter out by values of the field custom5 of the Task. This is an exact match.")
  @JsonProperty("custom-5-not")
  private final String[] custom5NotIn;

  @Schema(
      name = "custom-5-like",
      description =
          "Filter by the custom5 field of the Task. This results in a substring search (% is "
              + "appended to the front and end of the requested value). Further SQL \"LIKE\" "
              + "wildcard characters will be resolved correctly.")
  @JsonProperty("custom-5-like")
  private final String[] custom5Like;

  @Schema(
      name = "custom-5-not-like",
      description =
          "Filter by what the custom5 field of the Task shouldn't be. This results in a substring"
              + " search (% is appended to the front and end of the requested value). Further SQL "
              + "\"LIKE\" wildcard characters will be resolved correctly.")
  @JsonProperty("custom-5-not-like")
  private final String[] custom5NotLike;

  @Schema(
      name = "custom-6",
      description = "Filter by the value of the field custom6 of the Task. This is an exact match.")
  @JsonProperty("custom-6")
  private final String[] custom6In;

  @Schema(
      name = "custom-6-not",
      description =
          "Filter out by values of the field custom6 of the Task. This is an exact match.")
  @JsonProperty("custom-6-not")
  private final String[] custom6NotIn;

  @Schema(
      name = "custom-6-like",
      description =
          "Filter by the custom6 field of the Task. This results in a substring search (% is "
              + "appended to the front and end of the requested value). Further SQL \"LIKE\" "
              + "wildcard characters will be resolved correctly.")
  @JsonProperty("custom-6-like")
  private final String[] custom6Like;

  @Schema(
      name = "custom-6-not-like",
      description =
          "Filter by what the custom6 field of the Task shouldn't be. This results in a substring"
              + " search (% is appended to the front and end of the requested value). Further SQL "
              + "\"LIKE\" wildcard characters will be resolved correctly.")
  @JsonProperty("custom-6-not-like")
  private final String[] custom6NotLike;

  @Schema(
      name = "custom-7",
      description = "Filter by the value of the field custom7 of the Task. This is an exact match.")
  @JsonProperty("custom-7")
  private final String[] custom7In;

  @Schema(
      name = "custom-7-not",
      description =
          "Filter out by values of the field custom7 of the Task. This is an exact match.")
  @JsonProperty("custom-7-not")
  private final String[] custom7NotIn;

  @Schema(
      name = "custom-7-like",
      description =
          "Filter by the custom7 field of the Task. This results in a substring search (% is "
              + "appended to the front and end of the requested value). Further SQL \"LIKE\" "
              + "wildcard characters will be resolved correctly.")
  @JsonProperty("custom-7-like")
  private final String[] custom7Like;

  @Schema(
      name = "custom-7-not-like",
      description =
          "Filter by what the custom7 field of the Task shouldn't be. This results in a substring"
              + " search (% is appended to the front and end of the requested value). Further SQL "
              + "\"LIKE\" wildcard characters will be resolved correctly.")
  @JsonProperty("custom-7-not-like")
  private final String[] custom7NotLike;

  @Schema(
      name = "custom-8",
      description = "Filter by the value of the field custom8 of the Task. This is an exact match.")
  @JsonProperty("custom-8")
  private final String[] custom8In;

  @Schema(
      name = "custom-8-not",
      description =
          "Filter out by values of the field custom8 of the Task. This is an exact match.")
  @JsonProperty("custom-8-not")
  private final String[] custom8NotIn;

  @Schema(
      name = "custom-8-like",
      description =
          "Filter by the custom8 field of the Task. This results in a substring search (% is "
              + "appended to the front and end of the requested value). Further SQL \"LIKE\" "
              + "wildcard characters will be resolved correctly.")
  @JsonProperty("custom-8-like")
  private final String[] custom8Like;

  @Schema(
      name = "custom-8-not-like",
      description =
          "Filter by what the custom8 field of the Task shouldn't be. This results in a substring"
              + " search (% is appended to the front and end of the requested value). Further SQL "
              + "\"LIKE\" wildcard characters will be resolved correctly.")
  @JsonProperty("custom-8-not-like")
  private final String[] custom8NotLike;

  @Schema(
      name = "custom-9",
      description = "Filter by the value of the field custom9 of the Task. This is an exact match.")
  @JsonProperty("custom-9")
  private final String[] custom9In;

  @Schema(
      name = "custom-9-not",
      description =
          "Filter out by values of the field custom9 of the Task. This is an exact match.")
  @JsonProperty("custom-9-not")
  private final String[] custom9NotIn;

  @Schema(
      name = "custom-9-like",
      description =
          "Filter by the custom9 field of the Task. This results in a substring search (% is "
              + "appended to the front and end of the requested value). Further SQL \"LIKE\" "
              + "wildcard characters will be resolved correctly.")
  @JsonProperty("custom-9-like")
  private final String[] custom9Like;

  @Schema(
      name = "custom-9-not-like",
      description =
          "Filter by what the custom9 field of the Task shouldn't be. This results in a substring"
              + " search (% is appended to the front and end of the requested value). Further SQL "
              + "\"LIKE\" wildcard characters will be resolved correctly.")
  @JsonProperty("custom-9-not-like")
  private final String[] custom9NotLike;

  @Schema(
      name = "custom-10",
      description =
          "Filter by the value of the field custom10 of the Task. This is an exact match.")
  @JsonProperty("custom-10")
  private final String[] custom10In;

  @Schema(
      name = "custom-10-not",
      description =
          "Filter out by values of the field custom10 of the Task. This is an exact match.")
  @JsonProperty("custom-10-not")
  private final String[] custom10NotIn;

  @Schema(
      name = "custom-10-like",
      description =
          "Filter by the custom10 field of the Task. This results in a substring search (% is "
              + "appended to the front and end of the requested value). Further SQL \"LIKE\" "
              + "wildcard characters will be resolved correctly.")
  @JsonProperty("custom-10-like")
  private final String[] custom10Like;

  @Schema(
      name = "custom-10-not-like",
      description =
          "Filter by what the custom10 field of the Task shouldn't be. This results in a substring"
              + " search (% is appended to the front and end of the requested value). Further SQL "
              + "\"LIKE\" wildcard characters will be resolved correctly.")
  @JsonProperty("custom-10-not-like")
  private final String[] custom10NotLike;

  @Schema(
      name = "custom-11",
      description =
          "Filter by the value of the field custom11 of the Task. This is an exact match.")
  @JsonProperty("custom-11")
  private final String[] custom11In;

  @Schema(
      name = "custom-11-not",
      description =
          "Filter out by values of the field custom11 of the Task. This is an exact match.")
  @JsonProperty("custom-11-not")
  private final String[] custom11NotIn;

  @Schema(
      name = "custom-11-like",
      description =
          "Filter by the custom11 field of the Task. This results in a substring search (% is "
              + "appended to the front and end of the requested value). Further SQL \"LIKE\" "
              + "wildcard characters will be resolved correctly.")
  @JsonProperty("custom-11-like")
  private final String[] custom11Like;

  @Schema(
      name = "custom-11-not-like",
      description =
          "Filter by what the custom11 field of the Task shouldn't be. This results in a substring"
              + " search (% is appended to the front and end of the requested value). Further SQL "
              + "\"LIKE\" wildcard characters will be resolved correctly.")
  @JsonProperty("custom-11-not-like")
  private final String[] custom11NotLike;

  @Schema(
      name = "custom-12",
      description =
          "Filter by the value of the field custom12 of the Task. This is an exact match.")
  @JsonProperty("custom-12")
  private final String[] custom12In;

  @Schema(
      name = "custom-12-not",
      description =
          "Filter out by values of the field custom12 of the Task. This is an exact match.")
  @JsonProperty("custom-12-not")
  private final String[] custom12NotIn;

  @Schema(
      name = "custom-12-like",
      description =
          "Filter by the custom12 field of the Task. This results in a substring search (% is "
              + "appended to the front and end of the requested value). Further SQL \"LIKE\" "
              + "wildcard characters will be resolved correctly.")
  @JsonProperty("custom-12-like")
  private final String[] custom12Like;

  @Schema(
      name = "custom-12-not-like",
      description =
          "Filter by what the custom12 field of the Task shouldn't be. This results in a substring"
              + " search (% is appended to the front and end of the requested value). Further SQL "
              + "\"LIKE\" wildcard characters will be resolved correctly.")
  @JsonProperty("custom-12-not-like")
  private final String[] custom12NotLike;

  @Schema(
      name = "custom-13",
      description =
          "Filter by the value of the field custom13 of the Task. This is an exact match.")
  @JsonProperty("custom-13")
  private final String[] custom13In;

  @Schema(
      name = "custom-13-not",
      description =
          "Filter out by values of the field custom13 of the Task. This is an exact match.")
  @JsonProperty("custom-13-not")
  private final String[] custom13NotIn;

  @Schema(
      name = "custom-13-like",
      description =
          "Filter by the custom13 field of the Task. This results in a substring search (% is "
              + "appended to the front and end of the requested value). Further SQL \"LIKE\" "
              + "wildcard characters will be resolved correctly.")
  @JsonProperty("custom-13-like")
  private final String[] custom13Like;

  @Schema(
      name = "custom-13-not-like",
      description =
          "Filter by what the custom13 field of the Task shouldn't be. This results in a substring"
              + " search (% is appended to the front and end of the requested value). Further SQL "
              + "\"LIKE\" wildcard characters will be resolved correctly.")
  @JsonProperty("custom-13-not-like")
  private final String[] custom13NotLike;

  @Schema(
      name = "custom-14",
      description =
          "Filter by the value of the field custom14 of the Task. This is an exact match.")
  @JsonProperty("custom-14")
  private final String[] custom14In;

  @Schema(
      name = "custom-14-not",
      description =
          "Filter out by values of the field custom14 of the Task. This is an exact match.")
  @JsonProperty("custom-14-not")
  private final String[] custom14NotIn;

  @Schema(
      name = "custom-14-like",
      description =
          "Filter by the custom14 field of the Task. This results in a substring search (% is "
              + "appended to the front and end of the requested value). Further SQL \"LIKE\" "
              + "wildcard characters will be resolved correctly.")
  @JsonProperty("custom-14-like")
  private final String[] custom14Like;

  @Schema(
      name = "custom-14-not-like",
      description =
          "Filter by what the custom14 field of the Task shouldn't be. This results in a substring"
              + " search (% is appended to the front and end of the requested value). Further SQL "
              + "\"LIKE\" wildcard characters will be resolved correctly.")
  @JsonProperty("custom-14-not-like")
  private final String[] custom14NotLike;

  @Schema(
      name = "custom-15",
      description =
          "Filter by the value of the field custom15 of the Task. This is an exact match.")
  @JsonProperty("custom-15")
  private final String[] custom15In;

  @Schema(
      name = "custom-15-not",
      description =
          "Filter out by values of the field custom15 of the Task. This is an exact match.")
  @JsonProperty("custom-15-not")
  private final String[] custom15NotIn;

  @Schema(
      name = "custom-15-like",
      description =
          "Filter by the custom15 field of the Task. This results in a substring search (% is "
              + "appended to the front and end of the requested value). Further SQL \"LIKE\" "
              + "wildcard characters will be resolved correctly.")
  @JsonProperty("custom-15-like")
  private final String[] custom15Like;

  @Schema(
      name = "custom-15-not-like",
      description =
          "Filter by what the custom15 field of the Task shouldn't be. This results in a substring"
              + " search (% is appended to the front and end of the requested value). Further SQL "
              + "\"LIKE\" wildcard characters will be resolved correctly.")
  @JsonProperty("custom-15-not-like")
  private final String[] custom15NotLike;

  @Schema(
      name = "custom-16",
      description =
          "Filter by the value of the field custom16 of the Task. This is an exact match.")
  @JsonProperty("custom-16")
  private final String[] custom16In;

  @Schema(
      name = "custom-16-not",
      description =
          "Filter out by values of the field custom16 of the Task. This is an exact match.")
  @JsonProperty("custom-16-not")
  private final String[] custom16NotIn;

  @Schema(
      name = "custom-16-like",
      description =
          "Filter by the custom16 field of the Task. This results in a substring search (% is "
              + "appended to the front and end of the requested value). Further SQL \"LIKE\" "
              + "wildcard characters will be resolved correctly.")
  @JsonProperty("custom-16-like")
  private final String[] custom16Like;

  @Schema(
      name = "custom-16-not-like",
      description =
          "Filter by what the custom16 field of the Task shouldn't be. This results in a substring"
              + " search (% is appended to the front and end of the requested value). Further SQL "
              + "\"LIKE\" wildcard characters will be resolved correctly.")
  @JsonProperty("custom-16-not-like")
  private final String[] custom16NotLike;

  @ConstructorProperties({
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
    "custom-16-not-like"
  })
  public TaskQueryFilterCustomFields(
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
      String[] custom16NotLike) {
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
  }

  public String[] getCustom1In() {
    return custom1In;
  }

  public String[] getCustom1NotIn() {
    return custom1NotIn;
  }

  public String[] getCustom1Like() {
    return custom1Like;
  }

  public String[] getCustom1NotLike() {
    return custom1NotLike;
  }

  public String[] getCustom2In() {
    return custom2In;
  }

  public String[] getCustom2NotIn() {
    return custom2NotIn;
  }

  public String[] getCustom2Like() {
    return custom2Like;
  }

  public String[] getCustom2NotLike() {
    return custom2NotLike;
  }

  public String[] getCustom3In() {
    return custom3In;
  }

  public String[] getCustom3NotIn() {
    return custom3NotIn;
  }

  public String[] getCustom3Like() {
    return custom3Like;
  }

  public String[] getCustom3NotLike() {
    return custom3NotLike;
  }

  public String[] getCustom4In() {
    return custom4In;
  }

  public String[] getCustom4NotIn() {
    return custom4NotIn;
  }

  public String[] getCustom4Like() {
    return custom4Like;
  }

  public String[] getCustom4NotLike() {
    return custom4NotLike;
  }

  public String[] getCustom5In() {
    return custom5In;
  }

  public String[] getCustom5NotIn() {
    return custom5NotIn;
  }

  public String[] getCustom5Like() {
    return custom5Like;
  }

  public String[] getCustom5NotLike() {
    return custom5NotLike;
  }

  public String[] getCustom6In() {
    return custom6In;
  }

  public String[] getCustom6NotIn() {
    return custom6NotIn;
  }

  public String[] getCustom6Like() {
    return custom6Like;
  }

  public String[] getCustom6NotLike() {
    return custom6NotLike;
  }

  public String[] getCustom7In() {
    return custom7In;
  }

  public String[] getCustom7NotIn() {
    return custom7NotIn;
  }

  public String[] getCustom7Like() {
    return custom7Like;
  }

  public String[] getCustom7NotLike() {
    return custom7NotLike;
  }

  public String[] getCustom8In() {
    return custom8In;
  }

  public String[] getCustom8NotIn() {
    return custom8NotIn;
  }

  public String[] getCustom8Like() {
    return custom8Like;
  }

  public String[] getCustom8NotLike() {
    return custom8NotLike;
  }

  public String[] getCustom9In() {
    return custom9In;
  }

  public String[] getCustom9NotIn() {
    return custom9NotIn;
  }

  public String[] getCustom9Like() {
    return custom9Like;
  }

  public String[] getCustom9NotLike() {
    return custom9NotLike;
  }

  public String[] getCustom10In() {
    return custom10In;
  }

  public String[] getCustom10NotIn() {
    return custom10NotIn;
  }

  public String[] getCustom10Like() {
    return custom10Like;
  }

  public String[] getCustom10NotLike() {
    return custom10NotLike;
  }

  public String[] getCustom11In() {
    return custom11In;
  }

  public String[] getCustom11NotIn() {
    return custom11NotIn;
  }

  public String[] getCustom11Like() {
    return custom11Like;
  }

  public String[] getCustom11NotLike() {
    return custom11NotLike;
  }

  public String[] getCustom12In() {
    return custom12In;
  }

  public String[] getCustom12NotIn() {
    return custom12NotIn;
  }

  public String[] getCustom12Like() {
    return custom12Like;
  }

  public String[] getCustom12NotLike() {
    return custom12NotLike;
  }

  public String[] getCustom13In() {
    return custom13In;
  }

  public String[] getCustom13NotIn() {
    return custom13NotIn;
  }

  public String[] getCustom13Like() {
    return custom13Like;
  }

  public String[] getCustom13NotLike() {
    return custom13NotLike;
  }

  public String[] getCustom14In() {
    return custom14In;
  }

  public String[] getCustom14NotIn() {
    return custom14NotIn;
  }

  public String[] getCustom14Like() {
    return custom14Like;
  }

  public String[] getCustom14NotLike() {
    return custom14NotLike;
  }

  public String[] getCustom15In() {
    return custom15In;
  }

  public String[] getCustom15NotIn() {
    return custom15NotIn;
  }

  public String[] getCustom15Like() {
    return custom15Like;
  }

  public String[] getCustom15NotLike() {
    return custom15NotLike;
  }

  public String[] getCustom16In() {
    return custom16In;
  }

  public String[] getCustom16NotIn() {
    return custom16NotIn;
  }

  public String[] getCustom16Like() {
    return custom16Like;
  }

  public String[] getCustom16NotLike() {
    return custom16NotLike;
  }

  @Override
  public Void apply(TaskQuery query) {
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
    return null;
  }
}
