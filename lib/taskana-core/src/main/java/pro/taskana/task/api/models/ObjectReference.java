package pro.taskana.task.api.models;

import java.util.Objects;

import pro.taskana.common.api.exceptions.InvalidArgumentException;

/** ObjectReference entity. */
public class ObjectReference {
  private String id;
  private String company;
  private String system;
  private String systemInstance;
  private String type;
  private String value;

  public ObjectReference() {}

  private ObjectReference(ObjectReference copyFrom) {
    company = copyFrom.company;
    system = copyFrom.system;
    systemInstance = copyFrom.systemInstance;
    type = copyFrom.type;
    value = copyFrom.value;
  }

  public static void validate(ObjectReference objectReference, String objRefType, String objName)
      throws InvalidArgumentException {
    // check that all values in the ObjectReference are set correctly
    if (objectReference == null) {
      throw new InvalidArgumentException(
          String.format("%s of %s must not be null.", objRefType, objName));
    } else if (objectReference.getCompany() == null || objectReference.getCompany().isEmpty()) {
      throw new InvalidArgumentException(
          String.format("Company of %s of %s must not be empty", objRefType, objName));
    } else if (objectReference.getType() == null || objectReference.getType().length() == 0) {
      throw new InvalidArgumentException(
          String.format("Type of %s of %s must not be empty", objRefType, objName));
    } else if (objectReference.getValue() == null || objectReference.getValue().length() == 0) {
      throw new InvalidArgumentException(
          String.format("Value of %s of %s must not be empty", objRefType, objName));
    }
  }

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

  public ObjectReference copy() {
    return new ObjectReference(this);
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
