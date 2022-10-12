package pro.taskana.spi.user.api;

import pro.taskana.user.api.models.User;

/**
 * The RefreshUserPostprocessor allows to implement custom behaviour after a {@linkplain User} has
 * been updated.
 */
public interface RefreshUserPostprocessor {

  /**
   * Processes a {@linkplain User} after its refresh.
   *
   * @param userToProcess {@linkplain User} the User to postprocess
   * @return the {@linkplain User} after it has been processed
   */
  User processUserAfterRefresh(User userToProcess);
}
