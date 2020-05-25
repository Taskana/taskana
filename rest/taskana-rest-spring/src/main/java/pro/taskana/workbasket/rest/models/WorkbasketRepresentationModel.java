package pro.taskana.workbasket.rest.models;

import java.time.Instant;

import pro.taskana.workbasket.api.models.Workbasket;

/**
 * EntityModel class for {@link Workbasket}.
 */
public class WorkbasketRepresentationModel
    extends WorkbasketSummaryRepresentationModel {


  private Instant created; // ISO-8601
  private Instant modified; // ISO-8601

  public WorkbasketRepresentationModel() {
  }

  public WorkbasketRepresentationModel(Workbasket workbasket) {
    super(workbasket);
    this.created = workbasket.getCreated();
    this.modified = workbasket.getModified();
  }

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
