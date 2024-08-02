package pro.taskana.workbasket.rest.models;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "EntityModel class for Workbasket")
public class WorkbasketRepresentationModel extends WorkbasketSummaryRepresentationModel {

  @Schema(
      name = "created",
      description =
          "The creation timestamp of the workbasket in the system. The format is ISO-8601.")
  private Instant created;

  @Schema(
      name = "modified",
      description =
          "The timestamp of the last modification. The format is ISO-8601.")
  private Instant modified;

  public Instant getCreated() {
    return created;
  }

  public void setCreated(Instant created) {
    this.created = created;
  }

  public Instant getModified() {
    return modified;
  }

  public void setModified(Instant modified) {
    this.modified = modified;
  }
}
