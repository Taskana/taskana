package io.kadai.user.api.exceptions;

import io.kadai.common.api.exceptions.ErrorCode;
import io.kadai.common.api.exceptions.KadaiException;
import io.kadai.user.api.models.User;
import java.util.Map;

/**
 * This exception is thrown when a specific {@linkplain User} referenced by its {@linkplain
 * User#getId() id} is not in the database.
 */
public class UserNotFoundException extends KadaiException {
  public static final String ERROR_KEY = "USER_NOT_FOUND";
  private final String userId;

  public UserNotFoundException(String userId) {
    super(
        String.format("User with id '%s' was not found.", userId),
        ErrorCode.of(ERROR_KEY, Map.of("userId", ensureNullIsHandled(userId))));
    this.userId = userId;
  }

  public String getUserId() {
    return userId;
  }
}
