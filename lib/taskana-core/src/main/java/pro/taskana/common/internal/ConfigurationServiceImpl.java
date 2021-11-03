package pro.taskana.common.internal;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.ConfigurationService;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.util.CheckedRunnable;
import pro.taskana.common.internal.util.ResourceUtil;

public class ConfigurationServiceImpl implements ConfigurationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationServiceImpl.class);

  private final InternalTaskanaEngine internalTaskanaEngine;
  private final ConfigurationMapper mapper;

  public ConfigurationServiceImpl(
      InternalTaskanaEngine internalTaskanaEngine, ConfigurationMapper mapper) {
    this.internalTaskanaEngine = internalTaskanaEngine;
    this.mapper = mapper;
  }

  public void checkSecureAccess(boolean securityEnabled) {
    Boolean isSecurityEnabled =
        internalTaskanaEngine.executeInDatabaseConnection(mapper::isSecurityEnabled);

    if (isSecurityEnabled == null) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Security-mode is not yet set. Setting security flag to {}", securityEnabled);
      }
      mapper.setSecurityEnabled(securityEnabled);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Successfully set security mode to {}", securityEnabled);
      }
    } else if (isSecurityEnabled && !securityEnabled) {
      LOGGER.error("Tried to start TASKANA in unsecured mode while secured mode is enforced!");
      throw new SystemException("Secured TASKANA mode is enforced, can't start in unsecured mode");
    }
  }

  public void setupDefaultCustomAttributes() {
    internalTaskanaEngine.executeInDatabaseConnection(
        CheckedRunnable.wrap(
            () -> {
              if (mapper.getAllCustomAttributes(true) == null) {
                if (LOGGER.isDebugEnabled()) {
                  LOGGER.debug("custom attributes are not set. Setting default value");
                }
                setAllCustomAttributes(generateDefaultCustomAttributes());
              }
            }));
  }

  @Override
  public Map<String, Object> getAllCustomAttributes() {
    return internalTaskanaEngine.executeInDatabaseConnection(
        () -> mapper.getAllCustomAttributes(false));
  }

  @Override
  public void setAllCustomAttributes(Map<String, ?> customAttributes) {
    internalTaskanaEngine.executeInDatabaseConnection(
        () -> mapper.setAllCustomAttributes(customAttributes));
  }

  @Override
  public Optional<Object> getValue(String attribute) {
    return Optional.ofNullable(getAllCustomAttributes().get(attribute));
  }

  private Map<String, Object> generateDefaultCustomAttributes() throws IOException {
    JSONObject jsonObject =
        new JSONObject(
            ResourceUtil.readResourceAsString(getClass(), "defaultCustomAttributes.json"));
    return jsonObject.toMap();
  }
}
