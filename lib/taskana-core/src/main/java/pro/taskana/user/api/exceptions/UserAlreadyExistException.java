package pro.taskana.user.api.exceptions;

import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.util.MapCreator;
import pro.taskana.user.api.models.User;

/**
 * The UserAlreadyExistException is thrown when a {@linkplain User} was tried to be created with an
 * {@linkplain User#getId() id} already existing.
 */
public class UserAlreadyExistException extends TaskanaException {
  public static final String ERROR_KEY = "USER_ALREADY_EXISTS";
  private final String userId;

  public UserAlreadyExistException(String userId) {
    super(
        String.format("User with id '%s' already exists.", userId),
        ErrorCode.of(ERROR_KEY, MapCreator.of("userId", userId)));
    this.userId = userId;
  }

  public String getUserId() {
    return userId;
  }
}
