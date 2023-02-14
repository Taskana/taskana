package pro.taskana.workbasket.rest.models;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

import pro.taskana.workbasket.api.models.Workbasket;

/** EntityModel class for {@link Workbasket}. */
@Getter
@Setter
public class WorkbasketRepresentationModel extends WorkbasketSummaryRepresentationModel {

  /**
   * The creation timestamp of the workbasket in the system.
   *
   * <p>The format is ISO-8601.
   */
  private Instant created;
  /**
   * The timestamp of the last modification.
   *
   * <p>The format is ISO-8601.
   */
  private Instant modified;
}
