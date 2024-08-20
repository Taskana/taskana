package io.kadai.spi.user.internal;

import static io.kadai.common.internal.util.CheckedConsumer.wrap;

import io.kadai.common.internal.util.SpiLoader;
import io.kadai.spi.user.api.RefreshUserPostprocessor;
import io.kadai.user.api.models.User;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RefreshUserPostprocessorManager {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(RefreshUserPostprocessorManager.class);
  private final List<RefreshUserPostprocessor> refreshUserPostprocessors;

  public RefreshUserPostprocessorManager() {
    refreshUserPostprocessors = SpiLoader.load(RefreshUserPostprocessor.class);
    for (RefreshUserPostprocessor postprocessor : refreshUserPostprocessors) {
      LOGGER.info(
          "Registered RefreshUserPostprocessor provider: {}", postprocessor.getClass().getName());
    }
    if (refreshUserPostprocessors.isEmpty()) {
      LOGGER.info("No RefreshUserPostprocessor found. Running without RefreshUserPostprocessor.");
    }
  }

  public User processUserAfterRefresh(User userToProcess) {
    LOGGER.debug("Sending user to RefreshUserPostprocessor providers: {}", userToProcess);

    refreshUserPostprocessors.forEach(
        wrap(
            refreshUserPostprocessor ->
                refreshUserPostprocessor.processUserAfterRefresh(userToProcess)));
    return userToProcess;
  }

  public boolean isEnabled() {
    return !refreshUserPostprocessors.isEmpty();
  }
}
