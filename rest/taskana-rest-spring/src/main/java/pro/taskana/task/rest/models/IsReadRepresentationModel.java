package pro.taskana.task.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.beans.ConstructorProperties;

public class IsReadRepresentationModel {

  /** The value to set the Task property isRead. */
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
