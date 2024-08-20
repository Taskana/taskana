package io.kadai.testapi.builder;

import io.kadai.task.api.models.ObjectReference;
import io.kadai.task.internal.models.ObjectReferenceImpl;

public class ObjectReferenceBuilder {

  private final ObjectReferenceImpl objectReference = new ObjectReferenceImpl();

  private ObjectReferenceBuilder() {}

  public static ObjectReferenceBuilder newObjectReference() {
    return new ObjectReferenceBuilder();
  }

  public ObjectReferenceBuilder company(String company) {
    objectReference.setCompany(company);
    return this;
  }

  public ObjectReferenceBuilder system(String system) {
    objectReference.setSystem(system);
    return this;
  }

  public ObjectReferenceBuilder systemInstance(String systemInstance) {
    objectReference.setSystemInstance(systemInstance);
    return this;
  }

  public ObjectReferenceBuilder type(String type) {
    objectReference.setType(type);
    return this;
  }

  public ObjectReferenceBuilder value(String value) {
    objectReference.setValue(value);
    return this;
  }

  public ObjectReference build() {
    return objectReference.copy();
  }
}
