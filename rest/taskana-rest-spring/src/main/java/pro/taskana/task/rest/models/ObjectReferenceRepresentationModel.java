package pro.taskana.task.rest.models;

import org.springframework.hateoas.RepresentationModel;

public class ObjectReferenceRepresentationModel
    extends RepresentationModel<ObjectReferenceRepresentationModel> {

  /** Unique ID. */
  private String id;
  /** The company referenced primary object belongs to. */
  private String company;
  /** The (kind of) system, the object resides in (e.g. SAP, MySystem A, ...). */
  private String system;
  /** The instance of the system, the object resides in. */
  private String systemInstance;
  /** The type of the reference (contract, claim, policy, customer, ...). */
  private String type;
  /** The value of the primary object reference. */
  private String value;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getSystem() {
    return system;
  }

  public void setSystem(String system) {
    this.system = system;
  }

  public String getSystemInstance() {
    return systemInstance;
  }

  public void setSystemInstance(String systemInstance) {
    this.systemInstance = systemInstance;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
