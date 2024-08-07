package pro.taskana.task.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.beans.ConstructorProperties;
import java.time.Instant;
import java.util.Optional;
import pro.taskana.common.rest.QueryParameter;
import pro.taskana.task.api.TaskCommentQuery;

public class TaskCommentQueryFilterParameter implements QueryParameter<TaskCommentQuery, Void> {

  @Schema(name = "id", description = "Filter by the id of the TaskComment. This is an exact match.")
  @JsonProperty("id")
  private final String[] idIn;

  @Schema(
      name = "id-not",
      description =
          "Filter by what the id of the TaskComment shouldn't be. This is an exact match.")
  @JsonProperty("id-not")
  private final String[] idNotIn;

  @Schema(
      name = "id-like",
      description =
          "Filter by the id of the TaskComment. This results in a substring search (% is appended "
              + "to the front and end of the requested value). Further SQL 'LIKE' wildcard "
              + "characters will be resolved correctly.")
  @JsonProperty("id-like")
  private final String[] idLike;

  @Schema(
      name = "id-not-like",
      description =
          "Filter by what the id of the TaskComment shouldn't be. This results in a substring "
              + "search (% is appended to the front and end of the requested value). Further SQL "
              + "'LIKE' wildcard characters will be resolved correctly.")
  @JsonProperty("id-not-like")
  private final String[] idNotLike;

  @Schema(
      name = "task-id",
      description = "Filter by the task id of the TaskComment. This is an exact match.")
  @JsonProperty("task-id")
  private final String[] taskIdIn;

  @Schema(
      name = "creator",
      description = "Filter by the creator of the TaskComment. This is an exact match.")
  @JsonProperty("creator")
  private final String[] creatorIn;

  @Schema(
      name = "creator-not",
      description =
          "Filter by what the creator of the TaskComment shouldn't be. This is an exact match.")
  @JsonProperty("creator-not")
  private final String[] creatorNotIn;

  @Schema(
      name = "creator-like",
      description =
          "Filter by the creator of the TaskComment. This results in a substring search (% is "
              + "appended to the front and end of the requested value). Further SQL 'LIKE' wildcard"
              + " characters will be resolved correctly.")
  @JsonProperty("creator-like")
  private final String[] creatorLike;

  @Schema(
      name = "creator-not-like",
      description =
          "Filter by what the creator of the TaskComment shouldn't be. This results in a substring "
              + "search (% is appended to the front and end of the requested value). Further SQL "
              + "'LIKE' wildcard characters will be resolved correctly.")
  @JsonProperty("creator-not-like")
  private final String[] creatorNotLike;

  @Schema(
      name = "textfield-like",
      description =
          "Filter by the textfield of the TaskComment. This results in a substring search (% is "
              + "appended to the front and end of the requested value). Further SQL 'LIKE' wildcard"
              + " characters will be resolved correctly.")
  @JsonProperty("textfield-like")
  private final String[] textfieldLike;

  @Schema(
      name = "textfield-not-like",
      description =
          "Filter by what the textfield of the TaskComment shouldn't be. This results in a "
              + "substring search (% is appended to the front and end of the requested value). "
              + "Further SQL 'LIKE' wildcard characters will be resolved correctly.")
  @JsonProperty("textfield-not-like")
  private final String[] textfieldNotLike;

  @Schema(
      name = "modified",
      description =
          "Filter by a time interval within which the TaskComment was modified. To create an open "
              + "interval you can just leave it blank. The format is ISO-8601.")
  @JsonProperty("modified")
  private final Instant[] modifiedWithin;

  @Schema(
      name = "modified-not",
      description =
          "Filter by a time interval within which the TaskComment wasn't modified. To create an "
              + "open interval you can just leave it blank. The format is ISO-8601.")
  @JsonProperty("modified-not")
  private final Instant[] modifiedNotWithin;

  @Schema(
      name = "created",
      description =
          "Filter by a time interval within which the TaskComment was created. To create an open "
              + "interval you can just leave it blank. The format is ISO-8601.")
  @JsonProperty("created")
  private final Instant[] createdWithin;

  @Schema(
      name = "created-not",
      description =
          "Filter by a time interval within which the TaskComment wasn't created. To create an "
              + "open interval you can just leave it blank. The format is ISO-8601.")
  @JsonProperty("created-not")
  private final Instant[] createdNotWithin;

  @SuppressWarnings("indentation")
  @ConstructorProperties({
    "id",
    "id-not",
    "id-like",
    "id-not-like",
    "task-id",
    "creator",
    "creator-not",
    "creator-like",
    "creator-not-like",
    "textfield-like",
    "textfield-not-like",
    "modified",
    "modified-not",
    "created",
    "created-not"
  })
  public TaskCommentQueryFilterParameter(
      String[] idIn,
      String[] idNotIn,
      String[] idLike,
      String[] idNotLike,
      String[] taskIdIn,
      String[] creatorIn,
      String[] creatorNotIn,
      String[] creatorLike,
      String[] creatorNotLike,
      String[] textfieldLike,
      String[] textfieldNotLike,
      Instant[] modifiedWithin,
      Instant[] modifiedNotWithin,
      Instant[] createdWithin,
      Instant[] createdNotWithin) {
    this.idIn = idIn;
    this.idNotIn = idNotIn;
    this.idLike = idLike;
    this.idNotLike = idNotLike;
    this.taskIdIn = taskIdIn;
    this.creatorIn = creatorIn;
    this.creatorNotIn = creatorNotIn;
    this.creatorLike = creatorLike;
    this.creatorNotLike = creatorNotLike;
    this.textfieldLike = textfieldLike;
    this.textfieldNotLike = textfieldNotLike;
    this.modifiedWithin = modifiedWithin;
    this.modifiedNotWithin = modifiedNotWithin;
    this.createdWithin = createdWithin;
    this.createdNotWithin = createdNotWithin;
  }

  @Override
  public Void apply(TaskCommentQuery query) {
    Optional.ofNullable(idIn).ifPresent(query::idIn);
    Optional.ofNullable(idNotIn).ifPresent(query::idNotIn);
    Optional.ofNullable(idLike).map(this::wrapElementsInLikeStatement).ifPresent(query::idLike);
    Optional.ofNullable(idNotLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::idNotLike);
    Optional.ofNullable(taskIdIn).ifPresent(query::taskIdIn);
    Optional.ofNullable(creatorIn).ifPresent(query::creatorIn);
    Optional.ofNullable(creatorNotIn).ifPresent(query::creatorNotIn);
    Optional.ofNullable(creatorLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::creatorLike);
    Optional.ofNullable(creatorNotLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::creatorNotLike);
    Optional.ofNullable(textfieldLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::textFieldLike);
    Optional.ofNullable(textfieldNotLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::textFieldNotLike);
    Optional.ofNullable(modifiedWithin)
        .map(this::extractTimeIntervals)
        .ifPresent(query::modifiedWithin);
    Optional.ofNullable(modifiedNotWithin)
        .map(this::extractTimeIntervals)
        .ifPresent(query::modifiedNotWithin);
    Optional.ofNullable(createdWithin)
        .map(this::extractTimeIntervals)
        .ifPresent(query::createdWithin);
    Optional.ofNullable(createdNotWithin)
        .map(this::extractTimeIntervals)
        .ifPresent(query::createdNotWithin);
    return null;
  }
}
