package pro.taskana.task.rest;

import static pro.taskana.common.internal.util.CheckedConsumer.wrap;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;
import pro.taskana.common.api.IntInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.internal.util.Quadruple;
import pro.taskana.common.internal.util.Triplet;
import pro.taskana.common.rest.QueryParameter;
import pro.taskana.task.api.TaskCustomIntField;
import pro.taskana.task.api.TaskQuery;

public class TaskQueryFilterCustomIntFields implements QueryParameter<TaskQuery, Void> {

  /** Filter by the value of the field customInt1 of the Task. This is an exact match. */
  @JsonProperty("custom-int-1")
  private final Integer[] customInt1In;

  /** Exclude values of the field customInt1 of the Task. */
  @JsonProperty("custom-int-1-not")
  private final Integer[] customInt1NotIn;

  /** Filter by the range of value of the field customInt1 of the Task. */
  @JsonProperty("custom-int-1-within")
  private final Integer[] customInt1Within;

  /** Exclude range of values of the field customInt1 of the Task. */
  @JsonProperty("custom-int-1-not-within")
  private final Integer[] customInt1NotWithin;

  /**
   * Filter by lower bound of customInt1.
   *
   * <p>This parameter can't be used together with 'custom-int-1-within'.
   */
  @JsonProperty("custom-int-1-from")
  private final Integer customInt1From;

  /** Exclude values from a lower bound of the field customInt1 of the Task. */
  @JsonProperty("custom-int-1-from-not")
  private final Integer customInt1FromNot;

  /**
   * Filter by upper bound of customInt1.
   *
   * <p>This parameter can't be used together with 'custom-int-1-within'.
   */
  @JsonProperty("custom-int-1-to")
  private final Integer customInt1To;

  /** Exclude values to an upper bound of the field customInt1 of the Task. */
  @JsonProperty("custom-int-1-to-not")
  private final Integer customInt1ToNot;

  /** Filter by the value of the field customInt2 of the Task. This is an exact match. */
  @JsonProperty("custom-int-2")
  private final Integer[] customInt2In;

  /** Exclude values of the field customInt2 of the Task. */
  @JsonProperty("custom-int-2-not")
  private final Integer[] customInt2NotIn;

  /** Filter by the range of value of the field customInt2 of the Task. */
  @JsonProperty("custom-int-2-within")
  private final Integer[] customInt2Within;

  /** Exclude range of values of the field customInt2 of the Task. */
  @JsonProperty("custom-int-2-not-within")
  private final Integer[] customInt2NotWithin;

  /**
   * Filter by lower bound of customInt2.
   *
   * <p>This parameter can't be used together with 'custom-int-2-within'.
   */
  @JsonProperty("custom-int-2-from")
  private final Integer customInt2From;

  /** Exclude values from a lower bound of the field customInt2 of the Task. */
  @JsonProperty("custom-int-2-from-not")
  private final Integer customInt2FromNot;

  /**
   * Filter by upper bound of customInt2.
   *
   * <p>This parameter can't be used together with 'custom-int-2-within'.
   */
  @JsonProperty("custom-int-2-to")
  private final Integer customInt2To;

  /** Exclude values to an upper bound of the field customInt2 of the Task. */
  @JsonProperty("custom-int-2-to-not")
  private final Integer customInt2ToNot;

  /** Filter by the value of the field customInt3 of the Task. This is an exact match. */
  @JsonProperty("custom-int-3")
  private final Integer[] customInt3In;

  /** Exclude values of the field customInt3 of the Task. */
  @JsonProperty("custom-int-3-not")
  private final Integer[] customInt3NotIn;

  /** Filter by the range of value of the field customInt3 of the Task. */
  @JsonProperty("custom-int-3-within")
  private final Integer[] customInt3Within;

  /** Exclude range of values of the field customInt3 of the Task. */
  @JsonProperty("custom-int-3-not-within")
  private final Integer[] customInt3NotWithin;

  /**
   * Filter by lower bound of customInt3.
   *
   * <p>This parameter can't be used together with 'custom-int-3-within'.
   */
  @JsonProperty("custom-int-3-from")
  private final Integer customInt3From;

  /** Exclude values from a lower bound of the field customInt3 of the Task. */
  @JsonProperty("custom-int-3-from-not")
  private final Integer customInt3FromNot;

  /**
   * Filter by upper bound of customInt3.
   *
   * <p>This parameter can't be used together with 'custom-int-3-within'.
   */
  @JsonProperty("custom-int-3-to")
  private final Integer customInt3To;

  /** Exclude values to an upper bound of the field customInt3 of the Task. */
  @JsonProperty("custom-int-3-to-not")
  private final Integer customInt3ToNot;

  /** Filter by the value of the field customInt4 of the Task. This is an exact match. */
  @JsonProperty("custom-int-4")
  private final Integer[] customInt4In;

  /** Exclude values of the field customInt4 of the Task. */
  @JsonProperty("custom-int-4-not")
  private final Integer[] customInt4NotIn;

  /** Filter by the range of value of the field customInt4 of the Task. */
  @JsonProperty("custom-int-4-within")
  private final Integer[] customInt4Within;

  /** Exclude range of values of the field customInt4 of the Task. */
  @JsonProperty("custom-int-4-not-within")
  private final Integer[] customInt4NotWithin;

  /**
   * Filter by lower bound of customInt4.
   *
   * <p>This parameter can't be used together with 'custom-int-4-within'.
   */
  @JsonProperty("custom-int-4-from")
  private final Integer customInt4From;

  /** Exclude values from a lower bound of the field customInt4 of the Task. */
  @JsonProperty("custom-int-4-from-not")
  private final Integer customInt4FromNot;

  /**
   * Filter by upper bound of customInt4.
   *
   * <p>This parameter can't be used together with 'custom-int-4-within'.
   */
  @JsonProperty("custom-int-4-to")
  private final Integer customInt4To;

  /** Exclude values to an upper bound of the field customInt4 of the Task. */
  @JsonProperty("custom-int-4-to-not")
  private final Integer customInt4ToNot;

  /** Filter by the value of the field customInt5 of the Task. This is an exact match. */
  @JsonProperty("custom-int-5")
  private final Integer[] customInt5In;

  /** Exclude values of the field customInt5 of the Task. */
  @JsonProperty("custom-int-5-not")
  private final Integer[] customInt5NotIn;

  /** Filter by the range of value of the field customInt5 of the Task. */
  @JsonProperty("custom-int-5-within")
  private final Integer[] customInt5Within;

  /** Exclude range of values of the field customInt5 of the Task. */
  @JsonProperty("custom-int-5-not-within")
  private final Integer[] customInt5NotWithin;

  /**
   * Filter by lower bound of customInt5.
   *
   * <p>This parameter can't be used together with 'custom-int-5-within'.
   */
  @JsonProperty("custom-int-5-from")
  private final Integer customInt5From;

  /** Exclude values from a lower bound of the field customInt5 of the Task. */
  @JsonProperty("custom-int-5-from-not")
  private final Integer customInt5FromNot;

  /**
   * Filter by upper bound of customInt5.
   *
   * <p>This parameter can't be used together with 'custom-int-5-within'.
   */
  @JsonProperty("custom-int-5-to")
  private final Integer customInt5To;

  /** Exclude values to an upper bound of the field customInt5 of the Task. */
  @JsonProperty("custom-int-5-to-not")
  private final Integer customInt5ToNot;

  /** Filter by the value of the field customInt6 of the Task. This is an exact match. */
  @JsonProperty("custom-int-6")
  private final Integer[] customInt6In;

  /** Exclude values of the field customInt6 of the Task. */
  @JsonProperty("custom-int-6-not")
  private final Integer[] customInt6NotIn;

  /** Filter by the range of value of the field customInt6 of the Task. */
  @JsonProperty("custom-int-6-within")
  private final Integer[] customInt6Within;

  /** Exclude range of values of the field customInt6 of the Task. */
  @JsonProperty("custom-int-6-not-within")
  private final Integer[] customInt6NotWithin;

  /**
   * Filter by lower bound of customInt6.
   *
   * <p>This parameter can't be used together with 'custom-int-6-within'.
   */
  @JsonProperty("custom-int-6-from")
  private final Integer customInt6From;

  /** Exclude values from a lower bound of the field customInt6 of the Task. */
  @JsonProperty("custom-int-6-from-not")
  private final Integer customInt6FromNot;

  /**
   * Filter by upper bound of customInt6.
   *
   * <p>This parameter can't be used together with 'custom-int-6-within'.
   */
  @JsonProperty("custom-int-6-to")
  private final Integer customInt6To;

  /** Exclude values to an upper bound of the field customInt6 of the Task. */
  @JsonProperty("custom-int-6-to-not")
  private final Integer customInt6ToNot;

  /** Filter by the value of the field customInt7 of the Task. This is an exact match. */
  @JsonProperty("custom-int-7")
  private final Integer[] customInt7In;

  /** Exclude values of the field customInt7 of the Task. */
  @JsonProperty("custom-int-7-not")
  private final Integer[] customInt7NotIn;

  /** Filter by the range of value of the field customInt7 of the Task. */
  @JsonProperty("custom-int-7-within")
  private final Integer[] customInt7Within;

  /** Exclude range of values of the field customInt7 of the Task. */
  @JsonProperty("custom-int-7-not-within")
  private final Integer[] customInt7NotWithin;

  /**
   * Filter by lower bound of customInt7.
   *
   * <p>This parameter can't be used together with 'custom-int-7-within'.
   */
  @JsonProperty("custom-int-7-from")
  private final Integer customInt7From;

  /** Exclude values from a lower bound of the field customInt7 of the Task. */
  @JsonProperty("custom-int-7-from-not")
  private final Integer customInt7FromNot;

  /**
   * Filter by upper bound of customInt7.
   *
   * <p>This parameter can't be used together with 'custom-int-7-within'.
   */
  @JsonProperty("custom-int-7-to")
  private final Integer customInt7To;

  /** Exclude values to an upper bound of the field customInt7 of the Task. */
  @JsonProperty("custom-int-7-to-not")
  private final Integer customInt7ToNot;

  /** Filter by the value of the field customInt8 of the Task. This is an exact match. */
  @JsonProperty("custom-int-8")
  private final Integer[] customInt8In;

  /** Exclude values of the field customInt8 of the Task. */
  @JsonProperty("custom-int-8-not")
  private final Integer[] customInt8NotIn;

  /** Filter by the range of value of the field customInt8 of the Task. */
  @JsonProperty("custom-int-8-within")
  private final Integer[] customInt8Within;

  /** Exclude range of values of the field customInt8 of the Task. */
  @JsonProperty("custom-int-8-not-within")
  private final Integer[] customInt8NotWithin;

  /**
   * Filter by lower bound of customInt8.
   *
   * <p>This parameter can't be used together with 'custom-int-8-within'.
   */
  @JsonProperty("custom-int-8-from")
  private final Integer customInt8From;

  /** Exclude values from a lower bound of the field customInt8 of the Task. */
  @JsonProperty("custom-int-8-from-not")
  private final Integer customInt8FromNot;

  /**
   * Filter by upper bound of customInt8.
   *
   * <p>This parameter can't be used together with 'custom-int-8-within'.
   */
  @JsonProperty("custom-int-8-to")
  private final Integer customInt8To;

  /** Exclude values to an upper bound of the field customInt8 of the Task. */
  @JsonProperty("custom-int-8-to-not")
  private final Integer customInt8ToNot;

  @ConstructorProperties({
    "custom-int-1",
    "custom-int-1-not",
    "custom-int-1-within",
    "custom-int-1-not-within",
    "custom-int-1-from",
    "custom-int-1-from-not",
    "custom-int-1-to",
    "custom-int-1-to-not",
    "custom-int-2",
    "custom-int-2-not",
    "custom-int-2-within",
    "custom-int-2-not-within",
    "custom-int-2-from",
    "custom-int-2-from-not",
    "custom-int-2-to",
    "custom-int-2-to-not",
    "custom-int-3",
    "custom-int-3-not",
    "custom-int-3-within",
    "custom-int-3-not-within",
    "custom-int-3-from",
    "custom-int-3-from-not",
    "custom-int-3-to",
    "custom-int-3-to-not",
    "custom-int-4",
    "custom-int-4-not",
    "custom-int-4-within",
    "custom-int-4-not-within",
    "custom-int-4-from",
    "custom-int-4-from-not",
    "custom-int-4-to",
    "custom-int-4-to-not",
    "custom-int-5",
    "custom-int-5-not",
    "custom-int-5-within",
    "custom-int-5-not-within",
    "custom-int-5-from",
    "custom-int-5-from-not",
    "custom-int-5-to",
    "custom-int-5-to-not",
    "custom-int-6",
    "custom-int-6-not",
    "custom-int-6-within",
    "custom-int-6-not-within",
    "custom-int-6-from",
    "custom-int-6-from-not",
    "custom-int-6-to",
    "custom-int-6-to-not",
    "custom-int-7",
    "custom-int-7-not",
    "custom-int-7-within",
    "custom-int-7-not-within",
    "custom-int-7-from",
    "custom-int-7-from-not",
    "custom-int-7-to",
    "custom-int-7-to-not",
    "custom-int-8",
    "custom-int-8-not",
    "custom-int-8-within",
    "custom-int-8-not-within",
    "custom-int-8-from",
    "custom-int-8-from-not",
    "custom-int-8-to",
    "custom-int-8-to-not"
  })
  public TaskQueryFilterCustomIntFields(
      Integer[] customInt1In,
      Integer[] customInt1NotIn,
      Integer[] customInt1Within,
      Integer[] customInt1NotWithin,
      Integer customInt1From,
      Integer customInt1FromNot,
      Integer customInt1To,
      Integer customInt1ToNot,
      Integer[] customInt2In,
      Integer[] customInt2NotIn,
      Integer[] customInt2Within,
      Integer[] customInt2NotWithin,
      Integer customInt2From,
      Integer customInt2FromNot,
      Integer customInt2To,
      Integer customInt2ToNot,
      Integer[] customInt3In,
      Integer[] customInt3NotIn,
      Integer[] customInt3Within,
      Integer[] customInt3NotWithin,
      Integer customInt3From,
      Integer customInt3FromNot,
      Integer customInt3To,
      Integer customInt3ToNot,
      Integer[] customInt4In,
      Integer[] customInt4NotIn,
      Integer[] customInt4Within,
      Integer[] customInt4NotWithin,
      Integer customInt4From,
      Integer customInt4FromNot,
      Integer customInt4To,
      Integer customInt4ToNot,
      Integer[] customInt5In,
      Integer[] customInt5NotIn,
      Integer[] customInt5Within,
      Integer[] customInt5NotWithin,
      Integer customInt5From,
      Integer customInt5FromNot,
      Integer customInt5To,
      Integer customInt5ToNot,
      Integer[] customInt6In,
      Integer[] customInt6NotIn,
      Integer[] customInt6Within,
      Integer[] customInt6NotWithin,
      Integer customInt6From,
      Integer customInt6FromNot,
      Integer customInt6To,
      Integer customInt6ToNot,
      Integer[] customInt7In,
      Integer[] customInt7NotIn,
      Integer[] customInt7Within,
      Integer[] customInt7NotWithin,
      Integer customInt7From,
      Integer customInt7FromNot,
      Integer customInt7To,
      Integer customInt7ToNot,
      Integer[] customInt8In,
      Integer[] customInt8NotIn,
      Integer[] customInt8Within,
      Integer[] customInt8NotWithin,
      Integer customInt8From,
      Integer customInt8FromNot,
      Integer customInt8To,
      Integer customInt8ToNot)
      throws InvalidArgumentException {
    this.customInt1In = customInt1In;
    this.customInt1NotIn = customInt1NotIn;
    this.customInt1Within = customInt1Within;
    this.customInt1NotWithin = customInt1NotWithin;
    this.customInt1From = customInt1From;
    this.customInt1FromNot = customInt1FromNot;
    this.customInt1To = customInt1To;
    this.customInt1ToNot = customInt1ToNot;
    this.customInt2In = customInt2In;
    this.customInt2NotIn = customInt2NotIn;
    this.customInt2Within = customInt2Within;
    this.customInt2NotWithin = customInt2NotWithin;
    this.customInt2From = customInt2From;
    this.customInt2FromNot = customInt2FromNot;
    this.customInt2To = customInt2To;
    this.customInt2ToNot = customInt2ToNot;
    this.customInt3In = customInt3In;
    this.customInt3NotIn = customInt3NotIn;
    this.customInt3Within = customInt3Within;
    this.customInt3NotWithin = customInt3NotWithin;
    this.customInt3From = customInt3From;
    this.customInt3FromNot = customInt3FromNot;
    this.customInt3To = customInt3To;
    this.customInt3ToNot = customInt3ToNot;
    this.customInt4In = customInt4In;
    this.customInt4NotIn = customInt4NotIn;
    this.customInt4Within = customInt4Within;
    this.customInt4NotWithin = customInt4NotWithin;
    this.customInt4From = customInt4From;
    this.customInt4FromNot = customInt4FromNot;
    this.customInt4To = customInt4To;
    this.customInt4ToNot = customInt4ToNot;
    this.customInt5In = customInt5In;
    this.customInt5NotIn = customInt5NotIn;
    this.customInt5Within = customInt5Within;
    this.customInt5NotWithin = customInt5NotWithin;
    this.customInt5From = customInt5From;
    this.customInt5FromNot = customInt5FromNot;
    this.customInt5To = customInt5To;
    this.customInt5ToNot = customInt5ToNot;
    this.customInt6In = customInt6In;
    this.customInt6NotIn = customInt6NotIn;
    this.customInt6Within = customInt6Within;
    this.customInt6NotWithin = customInt6NotWithin;
    this.customInt6From = customInt6From;
    this.customInt6FromNot = customInt6FromNot;
    this.customInt6To = customInt6To;
    this.customInt6ToNot = customInt6ToNot;
    this.customInt7In = customInt7In;
    this.customInt7NotIn = customInt7NotIn;
    this.customInt7Within = customInt7Within;
    this.customInt7NotWithin = customInt7NotWithin;
    this.customInt7From = customInt7From;
    this.customInt7FromNot = customInt7FromNot;
    this.customInt7To = customInt7To;
    this.customInt7ToNot = customInt7ToNot;
    this.customInt8In = customInt8In;
    this.customInt8NotIn = customInt8NotIn;
    this.customInt8Within = customInt8Within;
    this.customInt8NotWithin = customInt8NotWithin;
    this.customInt8From = customInt8From;
    this.customInt8FromNot = customInt8FromNot;
    this.customInt8To = customInt8To;
    this.customInt8ToNot = customInt8ToNot;

    validateFilterParameters();
  }

  @Override
  public Void apply(TaskQuery query) {
    Stream.of(
            Triplet.of(
                TaskCustomIntField.CUSTOM_INT_1,
                Quadruple.of(customInt1In, customInt1NotIn, customInt1Within, customInt1NotWithin),
                Quadruple.of(customInt1From, customInt1FromNot, customInt1To, customInt1ToNot)),
            Triplet.of(
                TaskCustomIntField.CUSTOM_INT_2,
                Quadruple.of(customInt2In, customInt2NotIn, customInt2Within, customInt2NotWithin),
                Quadruple.of(customInt2From, customInt2FromNot, customInt2To, customInt2ToNot)),
            Triplet.of(
                TaskCustomIntField.CUSTOM_INT_3,
                Quadruple.of(customInt3In, customInt3NotIn, customInt3Within, customInt3NotWithin),
                Quadruple.of(customInt3From, customInt3FromNot, customInt3To, customInt3ToNot)),
            Triplet.of(
                TaskCustomIntField.CUSTOM_INT_4,
                Quadruple.of(customInt4In, customInt4NotIn, customInt4Within, customInt4NotWithin),
                Quadruple.of(customInt4From, customInt4FromNot, customInt4To, customInt4ToNot)),
            Triplet.of(
                TaskCustomIntField.CUSTOM_INT_5,
                Quadruple.of(customInt5In, customInt5NotIn, customInt5Within, customInt5NotWithin),
                Quadruple.of(customInt5From, customInt5FromNot, customInt5To, customInt5ToNot)),
            Triplet.of(
                TaskCustomIntField.CUSTOM_INT_6,
                Quadruple.of(customInt6In, customInt6NotIn, customInt6Within, customInt6NotWithin),
                Quadruple.of(customInt6From, customInt6FromNot, customInt6To, customInt6ToNot)),
            Triplet.of(
                TaskCustomIntField.CUSTOM_INT_7,
                Quadruple.of(customInt7In, customInt7NotIn, customInt7Within, customInt7NotWithin),
                Quadruple.of(customInt7From, customInt7FromNot, customInt7To, customInt7ToNot)),
            Triplet.of(
                TaskCustomIntField.CUSTOM_INT_8,
                Quadruple.of(customInt8In, customInt8NotIn, customInt8Within, customInt8NotWithin),
                Quadruple.of(customInt8From, customInt8FromNot, customInt8To, customInt8ToNot)))
        .forEach(
            triplet -> {
              TaskCustomIntField customField = triplet.getLeft();
              Optional.ofNullable(triplet.getMiddle().getFirst())
                  .ifPresent(wrap(l -> query.customIntAttributeIn(customField, l)));
              Optional.ofNullable(triplet.getMiddle().getSecond())
                  .ifPresent(wrap(l -> query.customIntAttributeNotIn(customField, l)));
              Optional.ofNullable(triplet.getMiddle().getThird())
                  .map(this::extractIntIntervals)
                  .ifPresent(wrap(l -> query.customIntAttributeWithin(customField, l)));
              Optional.ofNullable(triplet.getMiddle().getFourth())
                  .map(this::extractIntIntervals)
                  .ifPresent(wrap(l -> query.customIntAttributeNotWithin(customField, l)));
              Integer from = triplet.getRight().getFirst();
              Integer to = triplet.getRight().getThird();
              if (from != null || to != null) {
                query.customIntAttributeWithin(customField, new IntInterval(from, to));
              }
              Integer fromNot = triplet.getRight().getSecond();
              Integer toNot = triplet.getRight().getFourth();
              if (fromNot != null || toNot != null) {
                query.customIntAttributeWithin(customField, new IntInterval(fromNot, toNot));
              }
            });
    return null;
  }

  private void validateFilterParameters() throws InvalidArgumentException {
    if (customInt1Within != null && customInt1Within.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'custom-int-1-within' is not dividable by 2");
    }
    if (customInt1Within != null
        && (Collections.indexOfSubList(
                    Arrays.asList(customInt1Within), Collections.nCopies(2, (Integer) null))
                % 2
            == 0)) {
      throw new InvalidArgumentException(
          "Each interval in 'custom-int-1-within' shouldn't consist of two 'null' values");
    }

    if (customInt2Within != null && customInt2Within.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'custom-int-2-within' is not dividable by 2");
    }

    if (customInt2Within != null
        && (Collections.indexOfSubList(
                    Arrays.asList(customInt2Within), Collections.nCopies(2, (Integer) null))
                % 2
            == 0)) {
      throw new InvalidArgumentException(
          "Each interval in 'custom-int-2-within' shouldn't consist of two 'null' values");
    }

    if (customInt3Within != null && customInt3Within.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'custom-int-3-within' is not dividable by 2");
    }

    if (customInt3Within != null
        && (Collections.indexOfSubList(
                    Arrays.asList(customInt3Within), Collections.nCopies(2, (Integer) null))
                % 2
            == 0)) {
      throw new InvalidArgumentException(
          "Each interval in 'custom-int-3-within' shouldn't consist of two 'null' values");
    }

    if (customInt4Within != null && customInt4Within.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'custom-int-4-within' is not dividable by 2");
    }
    if (customInt4Within != null
        && (Collections.indexOfSubList(
                    Arrays.asList(customInt4Within), Collections.nCopies(2, (Integer) null))
                % 2
            == 0)) {
      throw new InvalidArgumentException(
          "Each interval in 'custom-int-4-within' shouldn't consist of two 'null' values");
    }
    if (customInt5Within != null && customInt5Within.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'custom-int-5-within' is not dividable by 2");
    }
    if (customInt5Within != null
        && (Collections.indexOfSubList(
                    Arrays.asList(customInt5Within), Collections.nCopies(2, (Integer) null))
                % 2
            == 0)) {
      throw new InvalidArgumentException(
          "Each interval in 'custom-int-5-within' shouldn't consist of two 'null' values");
    }
    if (customInt6Within != null && customInt6Within.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'custom-int-6-within' is not dividable by 2");
    }
    if (customInt6Within != null
        && (Collections.indexOfSubList(
                    Arrays.asList(customInt6Within), Collections.nCopies(2, (Integer) null))
                % 2
            == 0)) {
      throw new InvalidArgumentException(
          "Each interval in 'custom-int-6-within' shouldn't consist of two 'null' values");
    }
    if (customInt7Within != null && customInt7Within.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'custom-int-7-within' is not dividable by 2");
    }
    if (customInt7Within != null
        && (Collections.indexOfSubList(
                    Arrays.asList(customInt7Within), Collections.nCopies(2, (Integer) null))
                % 2
            == 0)) {
      throw new InvalidArgumentException(
          "Each interval in 'custom-int-7-within' shouldn't consist of two 'null' values");
    }
    if (customInt8Within != null && customInt8Within.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'custom-int-8-within' is not dividable by 2");
    }
    if (customInt8Within != null
        && (Collections.indexOfSubList(
                    Arrays.asList(customInt8Within), Collections.nCopies(2, (Integer) null))
                % 2
            == 0)) {
      throw new InvalidArgumentException(
          "Each interval in 'custom-int-8-within' shouldn't consist of two 'null' values");
    }
    if (customInt1NotWithin != null && customInt1NotWithin.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'custom-int-1-not-within' is not dividable by 2");
    }
    if (customInt1NotWithin != null
        && (Collections.indexOfSubList(
                    Arrays.asList(customInt1NotWithin), Collections.nCopies(2, (Integer) null))
                % 2
            == 0)) {
      throw new InvalidArgumentException(
          "Each interval in 'custom-int-1-not-within' shouldn't consist of two 'null' values");
    }
    if (customInt2NotWithin != null && customInt2NotWithin.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'custom-int-2-not-within' is not dividable by 2");
    }
    if (customInt2NotWithin != null
        && (Collections.indexOfSubList(
                    Arrays.asList(customInt2NotWithin), Collections.nCopies(2, (Integer) null))
                % 2
            == 0)) {
      throw new InvalidArgumentException(
          "Each interval in 'custom-int-2-not-within' shouldn't consist of two 'null' values");
    }
    if (customInt3NotWithin != null && customInt3NotWithin.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'custom-int-3-not-within' is not dividable by 2");
    }
    if (customInt3NotWithin != null
        && (Collections.indexOfSubList(
                    Arrays.asList(customInt3NotWithin), Collections.nCopies(2, (Integer) null))
                % 2
            == 0)) {
      throw new InvalidArgumentException(
          "Each interval in 'custom-int-3-not-within' shouldn't consist of two 'null' values");
    }
    if (customInt4NotWithin != null && customInt4NotWithin.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'custom-int-4-not-within' is not dividable by 2");
    }
    if (customInt4NotWithin != null
        && (Collections.indexOfSubList(
                    Arrays.asList(customInt4NotWithin), Collections.nCopies(2, (Integer) null))
                % 2
            == 0)) {
      throw new InvalidArgumentException(
          "Each interval in 'custom-int-4-not-within' shouldn't consist of two 'null' values");
    }
    if (customInt5NotWithin != null && customInt5NotWithin.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'custom-int-5-not-within' is not dividable by 2");
    }
    if (customInt5NotWithin != null
        && (Collections.indexOfSubList(
                    Arrays.asList(customInt5NotWithin), Collections.nCopies(2, (Integer) null))
                % 2
            == 0)) {
      throw new InvalidArgumentException(
          "Each interval in 'custom-int-5-not-within' shouldn't consist of two 'null' values");
    }
    if (customInt6NotWithin != null && customInt6NotWithin.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'custom-int-6-not-within' is not dividable by 2");
    }
    if (customInt6NotWithin != null
        && (Collections.indexOfSubList(
                    Arrays.asList(customInt6NotWithin), Collections.nCopies(2, (Integer) null))
                % 2
            == 0)) {
      throw new InvalidArgumentException(
          "Each interval in 'custom-int-6-not-within' shouldn't consist of two 'null' values");
    }
    if (customInt7NotWithin != null && customInt7NotWithin.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'custom-int-7-not-within' is not dividable by 2");
    }
    if (customInt7NotWithin != null
        && (Collections.indexOfSubList(
                    Arrays.asList(customInt7NotWithin), Collections.nCopies(2, (Integer) null))
                % 2
            == 0)) {
      throw new InvalidArgumentException(
          "Each interval in 'custom-int-7-not-within' shouldn't consist of two 'null' values");
    }
    if (customInt8NotWithin != null && customInt8NotWithin.length % 2 != 0) {
      throw new InvalidArgumentException(
          "provided length of the property 'custom-int-8-not-within' is not dividable by 2");
    }
    if (customInt8NotWithin != null
        && (Collections.indexOfSubList(
                    Arrays.asList(customInt8NotWithin), Collections.nCopies(2, (Integer) null))
                % 2
            == 0)) {
      throw new InvalidArgumentException(
          "Each interval in 'custom-int-8-not-within' shouldn't consist of two 'null' values");
    }

    if (customInt1Within != null && (customInt1From != null || customInt1To != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'custom-int-1-within' in combination "
              + "with the params 'custom-int-1-from'  and / or 'custom-int-1-to'");
    }
    if (customInt1NotWithin != null && (customInt1FromNot != null || customInt1ToNot != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'custom-int-1-not-within' in combination "
              + "with the params 'custom-int-1-from-not'  and / or 'custom-int-1-to-not'");
    }

    if (customInt2Within != null && (customInt2From != null || customInt2To != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'custom-int-2-within' in combination "
              + "with the params 'custom-int-2-from'  and / or 'custom-int-2-to'");
    }
    if (customInt2NotWithin != null && (customInt2FromNot != null || customInt2ToNot != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'custom-int-2-not-within' in combination "
              + "with the params 'custom-int-2-from-not'  and / or 'custom-int-2-to-not'");
    }

    if (customInt3Within != null && (customInt3From != null || customInt3To != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'custom-int-3-within' in combination "
              + "with the params 'custom-int-3-from'  and / or 'custom-int-3-to'");
    }
    if (customInt3NotWithin != null && (customInt3FromNot != null || customInt3ToNot != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'custom-int-3-not-within' in combination "
              + "with the params 'custom-int-3-from-not'  and / or 'custom-int-3-to-not'");
    }

    if (customInt4Within != null && (customInt4From != null || customInt4To != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'custom-int-4-within' in combination "
              + "with the params 'custom-int-4-from'  and / or 'custom-int-4-to'");
    }
    if (customInt4NotWithin != null && (customInt4FromNot != null || customInt4ToNot != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'custom-int-4-not-within' in combination "
              + "with the params 'custom-int-4-from-not'  and / or 'custom-int-4-to-not'");
    }

    if (customInt5Within != null && (customInt5From != null || customInt5To != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'custom-int-5-within' in combination "
              + "with the params 'custom-int-5-from'  and / or 'custom-int-5-to'");
    }
    if (customInt5NotWithin != null && (customInt5FromNot != null || customInt5ToNot != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'custom-int-5-not-within' in combination "
              + "with the params 'custom-int-5-from-not'  and / or 'custom-int-5-to-not'");
    }

    if (customInt6Within != null && (customInt6From != null || customInt6To != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'custom-int-6-within' in combination "
              + "with the params 'custom-int-6-from'  and / or 'custom-int-6-to'");
    }
    if (customInt6NotWithin != null && (customInt6FromNot != null || customInt6ToNot != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'custom-int-6-not-within' in combination "
              + "with the params 'custom-int-6-from-not'  and / or 'custom-int-6-to-not'");
    }

    if (customInt7Within != null && (customInt7From != null || customInt7To != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'custom-int-7-within' in combination "
              + "with the params 'custom-int-7-from'  and / or 'custom-int-7-to'");
    }
    if (customInt7NotWithin != null && (customInt7FromNot != null || customInt7ToNot != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'custom-int-7-not-within' in combination "
              + "with the params 'custom-int-7-from-not'  and / or 'custom-int-7-to-not'");
    }
    if (customInt8Within != null && (customInt8From != null || customInt8To != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'custom-int-8-within' in combination "
              + "with the params 'custom-int-8-from'  and / or 'custom-int-8-to'");
    }
    if (customInt8NotWithin != null && (customInt8FromNot != null || customInt8ToNot != null)) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param 'custom-int-8-not-within' in combination "
              + "with the params 'custom-int-8-from-not'  and / or 'custom-int-8-to-not'");
    }
  }
}
