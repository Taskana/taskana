package pro.taskana.spi.user.internal;

import static pro.taskana.common.internal.util.CheckedConsumer.wrap;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.internal.util.SpiLoader;
import pro.taskana.spi.user.api.RefreshUserPostprocessor;
import pro.taskana.user.api.models.User;

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
