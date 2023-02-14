package pro.taskana.task.internal.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.task.api.models.ObjectReference;

/** ObjectReference entity. */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ObjectReferenceImpl implements ObjectReference {
  private String id;
  private String taskId;
  private String company;
  private String system;
  private String systemInstance;
  private String type;
  private String value;

  public ObjectReferenceImpl(
      String company, String system, String systemInstance, String type, String value) {
    this.company = company;
    this.system = system;
    this.systemInstance = systemInstance;
    this.type = type;
    this.value = value;
  }

  private ObjectReferenceImpl(ObjectReferenceImpl copyFrom) {
    company = copyFrom.company;
    system = copyFrom.system;
    systemInstance = copyFrom.systemInstance;
    type = copyFrom.type;
    value = copyFrom.value;
  }

  @Override
  public ObjectReferenceImpl copy() {
    return new ObjectReferenceImpl(this);
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
}
