package pro.taskana.common.api;

import java.util.Map;
import java.util.Optional;

public interface ConfigurationService {

  Map<String, Object> getAllCustomAttributes();

  void setAllCustomAttributes(Map<String, ?> customAttributes);

  Optional<Object> getValue(String attribute);
}
