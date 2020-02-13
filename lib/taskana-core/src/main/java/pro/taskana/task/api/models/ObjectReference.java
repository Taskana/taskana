package pro.taskana.task.api.models;

import java.util.Objects;

/** ObjectReference entity. */
public class ObjectReference {

  private String id;
  private String company;
  private String system;
  private String systemInstance;
  private String type;
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

  @Override
  public int hashCode() {
    return Objects.hash(id, company, system, systemInstance, type, value);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ObjectReference)) {
      return false;
    }
    ObjectReference other = (ObjectReference) obj;
    return Objects.equals(id, other.id)
        && Objects.equals(company, other.company)
        && Objects.equals(system, other.system)
        && Objects.equals(systemInstance, other.systemInstance)
        && Objects.equals(type, other.type)
        && Objects.equals(value, other.value);
  }

  @Override
  public String toString() {
    return "ObjectReference ["
        + "id="
        + this.id
        + ", company="
        + this.company
        + ", system="
        + this.system
        + ", systemInstance="
        + this.systemInstance
        + ", type="
        + this.type
        + ", value="
        + this.value
        + "]";
  }
}
