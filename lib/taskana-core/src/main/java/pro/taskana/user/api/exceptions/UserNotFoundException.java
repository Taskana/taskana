package pro.taskana.user.api.exceptions;

import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.util.MapCreator;
import pro.taskana.user.api.models.User;

/**
 * This exception is thrown when a specific {@linkplain User} referenced by its {@linkplain
 * User#getId() id} is not in the database.
 */
public class UserNotFoundException extends TaskanaException {
  public static final String ERROR_KEY = "USER_NOT_FOUND";
  private final String userId;

  public UserNotFoundException(String userId) {
    super(
        String.format("User with id '%s' was not found.", userId),
        ErrorCode.of(ERROR_KEY, MapCreator.of("userId", userId)));
    this.userId = userId;
  }

  public String getUserId() {
    return userId;
  }
}
