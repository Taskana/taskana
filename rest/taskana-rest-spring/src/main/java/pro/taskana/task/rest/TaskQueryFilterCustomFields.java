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
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;

import pro.taskana.common.internal.util.Pair;
import pro.taskana.common.rest.QueryParameter;
import pro.taskana.task.api.TaskQuery;

@RequiredArgsConstructor(
    onConstructor =
        @__({
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
        }))
public class TaskQueryFilterCustomFields implements QueryParameter<TaskQuery, Void> {
  /** Filter by the value of the field custom1 of the Task. This is an exact match. */
  @JsonProperty("custom-1")
  private final String[] custom1In;

  /** Exclude values of the field custom1 of the Task. */
  @JsonProperty("custom-1-not")
  private final String[] custom1NotIn;

  /**
   * Filter by the custom1 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-1-like")
  private final String[] custom1Like;

  /**
   * Filter by what the custom1 field of the Task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-1-not-like")
  private final String[] custom1NotLike;

  /** Filter by the value of the field custom2 of the Task. This is an exact match. */
  @JsonProperty("custom-2")
  private final String[] custom2In;

  /** Filter out by values of the field custom2 of the Task. This is an exact match. */
  @JsonProperty("custom-2-not")
  private final String[] custom2NotIn;

  /**
   * Filter by the custom2 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-2-like")
  private final String[] custom2Like;

  /**
   * Filter by what the custom2 field of the Task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-2-not-like")
  private final String[] custom2NotLike;

  /** Filter by the value of the field custom3 of the Task. This is an exact match. */
  @JsonProperty("custom-3")
  private final String[] custom3In;

  /** Filter out by values of the field custom3 of the Task. This is an exact match. */
  @JsonProperty("custom-3-not")
  private final String[] custom3NotIn;

  /**
   * Filter by the custom3 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-3-like")
  private final String[] custom3Like;

  /**
   * Filter by what the custom3 field of the Task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-3-not-like")
  private final String[] custom3NotLike;

  /** Filter by the value of the field custom4 of the Task. This is an exact match. */
  @JsonProperty("custom-4")
  private final String[] custom4In;

  /** Filter out by values of the field custom4 of the Task. This is an exact match. */
  @JsonProperty("custom-4-not")
  private final String[] custom4NotIn;

  /**
   * Filter by the custom4 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-4-like")
  private final String[] custom4Like;

  /**
   * Filter by what the custom4 field of the Task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-4-not-like")
  private final String[] custom4NotLike;

  /** Filter by the value of the field custom5 of the Task. This is an exact match. */
  @JsonProperty("custom-5")
  private final String[] custom5In;

  /** Filter out by values of the field custom5 of the Task. This is an exact match. */
  @JsonProperty("custom-5-not")
  private final String[] custom5NotIn;

  /**
   * Filter by the custom5 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-5-like")
  private final String[] custom5Like;

  /**
   * Filter by what the custom5 field of the Task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-5-not-like")
  private final String[] custom5NotLike;

  /** Filter by the value of the field custom6 of the Task. This is an exact match. */
  @JsonProperty("custom-6")
  private final String[] custom6In;

  /** Filter out by values of the field custom6 of the Task. This is an exact match. */
  @JsonProperty("custom-6-not")
  private final String[] custom6NotIn;

  /**
   * Filter by the custom6 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-6-like")
  private final String[] custom6Like;

  /**
   * Filter by what the custom6 field of the Task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-6-not-like")
  private final String[] custom6NotLike;

  /** Filter by the value of the field custom7 of the Task. This is an exact match. */
  @JsonProperty("custom-7")
  private final String[] custom7In;

  /** Filter out by values of the field custom7 of the Task. This is an exact match. */
  @JsonProperty("custom-7-not")
  private final String[] custom7NotIn;

  /**
   * Filter by the custom7 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-7-like")
  private final String[] custom7Like;

  /**
   * Filter by what the custom7 field of the Task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-7-not-like")
  private final String[] custom7NotLike;

  /** Filter by the value of the field custom8 of the Task. This is an exact match. */
  @JsonProperty("custom-8")
  private final String[] custom8In;

  /** Filter out by values of the field custom8 of the Task. This is an exact match. */
  @JsonProperty("custom-8-not")
  private final String[] custom8NotIn;

  /**
   * Filter by the custom8 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-8-like")
  private final String[] custom8Like;

  /**
   * Filter by what the custom8 field of the Task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-8-not-like")
  private final String[] custom8NotLike;

  /** Filter by the value of the field custom9 of the Task. This is an exact match. */
  @JsonProperty("custom-9")
  private final String[] custom9In;

  /** Filter out by values of the field custom9 of the Task. This is an exact match. */
  @JsonProperty("custom-9-not")
  private final String[] custom9NotIn;

  /**
   * Filter by the custom9 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-9-like")
  private final String[] custom9Like;

  /**
   * Filter by what the custom9 field of the Task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-9-not-like")
  private final String[] custom9NotLike;

  /** Filter by the value of the field custom10 of the Task. This is an exact match. */
  @JsonProperty("custom-10")
  private final String[] custom10In;

  /** Filter out by values of the field custom10 of the Task. This is an exact match. */
  @JsonProperty("custom-10-not")
  private final String[] custom10NotIn;

  /**
   * Filter by the custom10 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-10-like")
  private final String[] custom10Like;

  /**
   * Filter by what the custom10 field of the Task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-10-not-like")
  private final String[] custom10NotLike;

  /** Filter by the value of the field custom11 of the Task. This is an exact match. */
  @JsonProperty("custom-11")
  private final String[] custom11In;

  /** Filter out by values of the field custom11 of the Task. This is an exact match. */
  @JsonProperty("custom-11-not")
  private final String[] custom11NotIn;

  /**
   * Filter by the custom11 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-11-like")
  private final String[] custom11Like;

  /**
   * Filter by what the custom11 field of the Task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-11-not-like")
  private final String[] custom11NotLike;

  /** Filter by the value of the field custom12 of the Task. This is an exact match. */
  @JsonProperty("custom-12")
  private final String[] custom12In;

  /** Filter out by values of the field custom12 of the Task. This is an exact match. */
  @JsonProperty("custom-12-not")
  private final String[] custom12NotIn;

  /**
   * Filter by the custom12 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-12-like")
  private final String[] custom12Like;

  /**
   * Filter by what the custom12 field of the Task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-12-not-like")
  private final String[] custom12NotLike;

  /** Filter by the value of the field custom13 of the Task. This is an exact match. */
  @JsonProperty("custom-13")
  private final String[] custom13In;

  /** Filter out by values of the field custom13 of the Task. This is an exact match. */
  @JsonProperty("custom-13-not")
  private final String[] custom13NotIn;

  /**
   * Filter by the custom13 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-13-like")
  private final String[] custom13Like;

  /**
   * Filter by what the custom13 field of the Task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-13-not-like")
  private final String[] custom13NotLike;

  /** Filter by the value of the field custom14 of the Task. This is an exact match. */
  @JsonProperty("custom-14")
  private final String[] custom14In;

  /** Filter out by values of the field custom14 of the Task. This is an exact match. */
  @JsonProperty("custom-14-not")
  private final String[] custom14NotIn;

  /**
   * Filter by the custom14 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-14-like")
  private final String[] custom14Like;

  /**
   * Filter by what the custom14 field of the Task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-14-not-like")
  private final String[] custom14NotLike;

  /** Filter by the value of the field custom15 of the Task. This is an exact match. */
  @JsonProperty("custom-15")
  private final String[] custom15In;

  /** Filter out by values of the field custom15 of the Task. This is an exact match. */
  @JsonProperty("custom-15-not")
  private final String[] custom15NotIn;

  /**
   * Filter by the custom15 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-15-like")
  private final String[] custom15Like;

  /**
   * Filter by what the custom15 field of the Task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-15-not-like")
  private final String[] custom15NotLike;

  /** Filter by the value of the field custom16 of the Task. This is an exact match. */
  @JsonProperty("custom-16")
  private final String[] custom16In;

  /** Filter out by values of the field custom16 of the Task. This is an exact match. */
  @JsonProperty("custom-16-not")
  private final String[] custom16NotIn;
  /**
   * Filter by the custom16 field of the Task. This results in a substring search (% is appended to
   * the front and end of the requested value). Further SQL "LIKE" wildcard characters will be
   * resolved correctly.
   */
  @JsonProperty("custom-16-like")
  private final String[] custom16Like;

  /**
   * Filter by what the custom16 field of the Task shouldn't be. This results in a substring search
   * (% is appended to the front and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("custom-16-not-like")
  private final String[] custom16NotLike;

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
