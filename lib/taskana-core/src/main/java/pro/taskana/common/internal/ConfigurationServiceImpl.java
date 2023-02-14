package pro.taskana.common.internal;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import pro.taskana.common.api.ConfigurationService;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.util.CheckedRunnable;
import pro.taskana.common.internal.util.ResourceUtil;

@Slf4j
public class ConfigurationServiceImpl implements ConfigurationService {

  private final InternalTaskanaEngine internalTaskanaEngine;
  private final ConfigurationMapper mapper;

  public ConfigurationServiceImpl(
      InternalTaskanaEngine internalTaskanaEngine, ConfigurationMapper mapper) {
    this.internalTaskanaEngine = internalTaskanaEngine;
    this.mapper = mapper;
  }

  public void checkSecureAccess(boolean securityEnabled) {
    Boolean isSecurityEnabled =
        internalTaskanaEngine.executeInDatabaseConnection(() -> mapper.isSecurityEnabled(false));

    if (isSecurityEnabled == null) {
      initializeSecurityEnabled(securityEnabled);
    } else if (isSecurityEnabled && !securityEnabled) {
      log.error("Tried to start TASKANA in unsecured mode while secured mode is enforced!");
      throw new SystemException("Secured TASKANA mode is enforced, can't start in unsecured mode");
    }
  }

  public void setupDefaultCustomAttributes() {
    internalTaskanaEngine.executeInDatabaseConnection(
        CheckedRunnable.wrap(
            () -> {
              if (mapper.getAllCustomAttributes(true) == null) {
                log.debug("custom attributes are not set. Setting default value");
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

  private void initializeSecurityEnabled(boolean securityEnabled) {

    log.debug("Security-mode is not yet set. Setting security flag to {}", securityEnabled);
    Boolean isStillSecurityEnabled = mapper.isSecurityEnabled(true);
    if (isStillSecurityEnabled == null) {
      mapper.setSecurityEnabled(securityEnabled);
      isStillSecurityEnabled = Boolean.valueOf(securityEnabled);
      log.debug("Successfully set security mode to {}", securityEnabled);
    }
    if (isStillSecurityEnabled && !securityEnabled) {
      log.error("Tried to start TASKANA in unsecured mode while secured mode is enforced!");
      throw new SystemException("Secured TASKANA mode is enforced, can't start in unsecured mode");
    }
  }

  private Map<String, Object> generateDefaultCustomAttributes() throws IOException {
    JSONObject jsonObject =
        new JSONObject(
            ResourceUtil.readResourceAsString(getClass(), "defaultCustomAttributes.json"));
    return jsonObject.toMap();
  }
}
