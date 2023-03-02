package pro.taskana.spi.user.internal;

import static pro.taskana.common.internal.util.CheckedConsumer.wrap;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

import pro.taskana.common.internal.util.SpiLoader;
import pro.taskana.spi.user.api.RefreshUserPostprocessor;
import pro.taskana.user.api.models.User;

@Slf4j
public class RefreshUserPostprocessorManager {

  private final List<RefreshUserPostprocessor> refreshUserPostprocessors;

  public RefreshUserPostprocessorManager() {
    refreshUserPostprocessors = SpiLoader.load(RefreshUserPostprocessor.class);
    for (RefreshUserPostprocessor postprocessor : refreshUserPostprocessors) {
      log.info(
          "Registered RefreshUserPostprocessor provider: {}", postprocessor.getClass().getName());
    }
    if (refreshUserPostprocessors.isEmpty()) {
      log.info("No RefreshUserPostprocessor found. Running without RefreshUserPostprocessor.");
    }
  }

  public User processUserAfterRefresh(User userToProcess) {
    log.debug("Sending user to RefreshUserPostprocessor providers: {}", userToProcess);

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
