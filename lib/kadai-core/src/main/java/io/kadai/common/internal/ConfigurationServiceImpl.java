package io.kadai.common.internal;

import io.kadai.common.api.ConfigurationService;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.internal.util.CheckedRunnable;
import io.kadai.common.internal.util.ResourceUtil;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationServiceImpl implements ConfigurationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationServiceImpl.class);

  private final InternalKadaiEngine internalKadaiEngine;
  private final ConfigurationMapper mapper;

  public ConfigurationServiceImpl(
      InternalKadaiEngine internalKadaiEngine, ConfigurationMapper mapper) {
    this.internalKadaiEngine = internalKadaiEngine;
    this.mapper = mapper;
  }

  public void checkSecureAccess(boolean securityEnabled) {
    Boolean isSecurityEnabled =
        internalKadaiEngine.executeInDatabaseConnection(() -> mapper.isSecurityEnabled(false));

    if (isSecurityEnabled == null) {
      initializeSecurityEnabled(securityEnabled);
    } else if (isSecurityEnabled && !securityEnabled) {
      LOGGER.error("Tried to start KADAI in unsecured mode while secured mode is enforced!");
      throw new SystemException("Secured KADAI mode is enforced, can't start in unsecured mode");
    }
  }

  public void setupDefaultCustomAttributes() {
    internalKadaiEngine.executeInDatabaseConnection(
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
    return internalKadaiEngine.executeInDatabaseConnection(
        () -> mapper.getAllCustomAttributes(false));
  }

  @Override
  public void setAllCustomAttributes(Map<String, ?> customAttributes) {
    internalKadaiEngine.executeInDatabaseConnection(
        () -> mapper.setAllCustomAttributes(customAttributes));
  }

  @Override
  public Optional<Object> getValue(String attribute) {
    return Optional.ofNullable(getAllCustomAttributes().get(attribute));
  }

  private void initializeSecurityEnabled(boolean securityEnabled) {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Security-mode is not yet set. Setting security flag to {}", securityEnabled);
    }
    Boolean isStillSecurityEnabled = mapper.isSecurityEnabled(true);
    if (isStillSecurityEnabled == null) {
      mapper.setSecurityEnabled(securityEnabled);
      isStillSecurityEnabled = Boolean.valueOf(securityEnabled);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Successfully set security mode to {}", securityEnabled);
      }
    }
    if (isStillSecurityEnabled && !securityEnabled) {
      LOGGER.error("Tried to start KADAI in unsecured mode while secured mode is enforced!");
      throw new SystemException("Secured KADAI mode is enforced, can't start in unsecured mode");
    }
  }

  private Map<String, Object> generateDefaultCustomAttributes() throws IOException {
    JSONObject jsonObject =
        new JSONObject(
            ResourceUtil.readResourceAsString(getClass(), "defaultCustomAttributes.json"));
    return jsonObject.toMap();
  }
}
