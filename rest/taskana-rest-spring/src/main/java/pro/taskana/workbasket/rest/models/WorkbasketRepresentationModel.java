package pro.taskana.workbasket.rest.models;

import pro.taskana.workbasket.api.models.Workbasket;

/**
 * EntityModel class for {@link Workbasket}.
 */
public class WorkbasketRepresentationModel
    extends WorkbasketSummaryRepresentationModel {


  private String created; // ISO-8601
  private String modified; // ISO-8601

  public WorkbasketRepresentationModel() {
  }

  public WorkbasketRepresentationModel(Workbasket workbasket) {
    super(workbasket);
    this.created = workbasket.getCreated() != null ? workbasket.getCreated().toString() : null;
    this.modified = workbasket.getModified() != null ? workbasket.getModified().toString() : null;
  }

  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }

  public String getModified() {
    return modified;
  }

  public void setModified(String modified) {
    this.modified = modified;
  }

  @Override
  public String toString() {
    return "WorkbasketResource ["
               + "workbasketId= "
               + this.workbasketId
               + "key= "
               + this.key
               + "name= "
               + this.name
               + "domain= "
               + this.domain
               + "type= "
               + this.type
               + "owner= "
               + this.owner
               + "]";
  }
}
