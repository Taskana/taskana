package io.kadai.task.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.beans.ConstructorProperties;

public class IsReadRepresentationModel {

  @Schema(name = "is-read", description = "The value to set the Task property isRead.")
  @JsonProperty("is-read")
  private final boolean isRead;

  @ConstructorProperties({"is-read"})
  public IsReadRepresentationModel(boolean isRead) {
    this.isRead = isRead;
  }

  public boolean getIsRead() {
    return isRead;
  }
}
