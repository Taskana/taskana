package io.kadai.task.api.models;

/** ObjectReference-Interface to specify ObjectReference Attributes. */
public interface ObjectReference {

  /**
   * Returns the id of the ObjectReference.
   *
   * @return the id of the ObjectReference.
   */
  String getId();

  /**
   * Returns the id of the associated {@linkplain Task}.
   *
   * @return taskId
   */
  String getTaskId();

  /**
   * Returns the company of the ObjectReference.
   *
   * @return company
   */
  String getCompany();

  /**
   * Sets the company of the ObjectReference.
   *
   * @param company the company of the ObjectReference
   */
  void setCompany(String company);

  /**
   * Returns the system of the ObjectReference.
   *
   * @return system
   */
  String getSystem();

  /**
   * Sets the system of the ObjectReference.
   *
   * @param system the system of the ObjectReference
   */
  void setSystem(String system);

  /**
   * Returns the systemInstance of the ObjectReference.
   *
   * @return systemInstance
   */
  String getSystemInstance();

  /**
   * Sets the system instance of the ObjectReference.
   *
   * @param systemInstance the systemInstance of the ObjectReference
   */
  void setSystemInstance(String systemInstance);

  /**
   * Returns the type of the ObjectReference.
   *
   * @return type
   */
  String getType();

  /**
   * Sets the type of the ObjectReference.
   *
   * @param type the type of the ObjectReference
   */
  void setType(String type);

  /**
   * Returns the value of the ObjectReference.
   *
   * @return value
   */
  String getValue();

  /**
   * Sets the value of the ObjectReference.
   *
   * @param value the value of the ObjectReference
   */
  void setValue(String value);

  /**
   * Duplicates this ObjectReference without the id and taskId.
   *
   * @return a copy of this ObjectReference
   */
  ObjectReference copy();
}
