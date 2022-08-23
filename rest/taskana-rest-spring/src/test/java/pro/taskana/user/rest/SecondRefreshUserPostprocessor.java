package pro.taskana.user.rest;

import pro.taskana.spi.user.api.RefreshUserPostprocessor;
import pro.taskana.user.api.models.User;

public class SecondRefreshUserPostprocessor implements RefreshUserPostprocessor {
  @Override
  public User processUserAfterRefresh(User userToProcess) {
    if (userToProcess.getId().equals("user-2-2")) {
      userToProcess.setOrgLevel1(userToProcess.getOrgLevel1() + "Second");
    }
    return userToProcess;
  }
}
