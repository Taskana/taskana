package io.kadai.task.api;

import io.kadai.common.api.QueryColumnName;

public enum TaskCommentQueryColumnName implements QueryColumnName {
  ID("tc.id"),
  TASK_ID("tc.task_id"),
  TEXT_FIELD("tc.text_field"),
  CREATOR("tc.creator"),
  CREATOR_FULL_NAME("u.full_name"),
  CREATED("tc.created"),
  MODIFIED("tc.modified");

  private final String name;

  TaskCommentQueryColumnName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
