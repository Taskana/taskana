package io.kadai.user.rest;

import io.kadai.spi.user.api.RefreshUserPostprocessor;
import io.kadai.user.api.models.User;

public class SecondRefreshUserPostprocessor implements RefreshUserPostprocessor {
  @Override
  public User processUserAfterRefresh(User userToProcess) {
    if (userToProcess.getId().equals("user-2-2")) {
      userToProcess.setOrgLevel1(userToProcess.getOrgLevel1() + "Second");
    }
    return userToProcess;
  }
}
