package io.kadai.routing.dmn.spi.internal;

import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.routing.dmn.spi.api.DmnValidator;
import java.util.Objects;
import java.util.ServiceLoader;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Loads DmnValidator SPI implementation(s) and passes requests to validate DmnModelInstances. */
public class DmnValidatorManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(DmnValidatorManager.class);
  private static DmnValidatorManager singleton;
  private final ServiceLoader<DmnValidator> serviceLoader;
  private boolean enabled = false;

  private DmnValidatorManager(KadaiEngine kadaiEngine) {
    serviceLoader = ServiceLoader.load(DmnValidator.class);
    for (DmnValidator dmnValidator : serviceLoader) {
      dmnValidator.initialize(kadaiEngine);
      LOGGER.info("Registered DmnValidator: {}", dmnValidator.getClass().getName());
      enabled = true;
    }
    if (!enabled) {
      LOGGER.info("No DmnValidator found. Running without DmnValidator.");
    }
  }

  public static synchronized DmnValidatorManager getInstance(KadaiEngine kadaiEngine) {
    if (singleton == null) {
      singleton = new DmnValidatorManager(kadaiEngine);
    }
    return singleton;
  }

  public static boolean isDmnUploadProviderEnabled() {
    return Objects.nonNull(singleton) && singleton.enabled;
  }

  public void validate(DmnModelInstance dmnModelInstanceToValidate) {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Sending DmnModelInstance to DmnValidators: {}", dmnModelInstanceToValidate);
    }
    serviceLoader.forEach(
        dmnValidator -> {
          try {
            dmnValidator.validate(dmnModelInstanceToValidate);
          } catch (Exception e) {
            throw new SystemException("Caught exception while validating dmnModelInstance", e);
          }
        });
  }
}
