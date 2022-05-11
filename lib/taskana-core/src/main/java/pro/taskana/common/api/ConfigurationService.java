package pro.taskana.common.api;

import java.util.Map;
import java.util.Optional;

/** The Configuration Service manages all custom configuration options. */
public interface ConfigurationService {

  /**
   * Retrieve a specific value from all custom attributes.
   *
   * @param attribute the attribute key
   * @return the attribute value or nothing if the attribute does not exist
   */
  Optional<Object> getValue(String attribute);

  /**
   * Retrieve all custom attributes from the database.
   *
   * @return the custom attributes from the database
   */
  Map<String, Object> getAllCustomAttributes();

  /**
   * Override all custom attributes with the provided one.
   *
   * @param customAttributes the new custom attributes which should be persisted
   */
  void setAllCustomAttributes(Map<String, ?> customAttributes);
}
