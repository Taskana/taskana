package pro.taskana.task.api.models;

/** ObjectReference-Interface to specify ObjectReference Attributes. */
public interface ObjectReference {

  /**
   * Gets the id of the ObjectReference.
   *
   * @return the id of the ObjectReference.
   */
  String getId();

  /**
   * Gets the id of the associated task.
   *
   * @return the taskId
   */
  String getTaskId();

  /**
   * Gets the company of the ObjectReference.
   *
   * @return the company
   */
  String getCompany();

  /**
   * Sets the company of the ObjectReference.
   *
   * @param company the company of the object reference
   */
  void setCompany(String company);

  /**
   * Gets the system of the ObjectReference.
   *
   * @return the system
   */
  String getSystem();

  /**
   * Sets the system of the ObjectReference.
   *
   * @param system the system of the ObjectReference
   */
  void setSystem(String system);

  /**
   * Gets the systemInstance of the ObjectReference.
   *
   * @return the systemInstance
   */
  String getSystemInstance();

  /**
   * Sets the system instance of the ObjectReference.
   *
   * @param systemInstance the system instance of the ObjectReference
   */
  void setSystemInstance(String systemInstance);

  /**
   * Gets the type of the ObjectReference.
   *
   * @return the type
   */
  String getType();

  /**
   * Sets the type of the ObjectReference.
   *
   * @param type the type of the ObjectReference
   */
  void setType(String type);

  /**
   * Gets the value of the ObjectReference.
   *
   * @return the value
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
