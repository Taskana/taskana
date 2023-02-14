package pro.taskana.task.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.beans.ConstructorProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(onConstructor = @__({@ConstructorProperties({"is-read"})}))
public class IsReadRepresentationModel {

  /** The value to set the Task property isRead. */
  @JsonProperty("is-read")
  private final boolean isRead;
}
