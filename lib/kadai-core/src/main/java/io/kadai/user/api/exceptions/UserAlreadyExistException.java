package io.kadai.user.api.exceptions;

import io.kadai.common.api.exceptions.ErrorCode;
import io.kadai.common.api.exceptions.KadaiException;
import io.kadai.user.api.models.User;
import java.util.Map;

/**
 * This exception is thrown when a {@linkplain User} was tried to be created with an {@linkplain
 * User#getId() id} already existing.
 */
public class UserAlreadyExistException extends KadaiException {
  public static final String ERROR_KEY = "USER_ALREADY_EXISTS";
  private final String userId;

  public UserAlreadyExistException(String userId, Exception cause) {
    super(
        String.format("User with id '%s' already exists.", userId),
        ErrorCode.of(ERROR_KEY, Map.of("userId", ensureNullIsHandled(userId))),
        cause);
    this.userId = userId;
  }

  public String getUserId() {
    return userId;
  }
}
