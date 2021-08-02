package pro.taskana.common.api.exceptions;

import java.util.Arrays;

import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.internal.util.MapCreator;

/**
 * This exception is thrown when the current user is not in a certain {@linkplain TaskanaRole role}
 * it is supposed to be.
 */
public class MismatchedRoleException extends NotAuthorizedException {

  public static final String ERROR_KEY = "ROLE_MISMATCHED";
  private final String currentUserId;
  private final TaskanaRole[] roles;

  public MismatchedRoleException(String currentUserId, TaskanaRole... roles) {
    super(
        String.format(
            "Not authorized. The current user '%s' is not member of role(s) '%s'.",
            currentUserId, Arrays.toString(roles)),
        ErrorCode.of(ERROR_KEY, MapCreator.of("roles", roles, "currentUserId", currentUserId)));

    this.currentUserId = currentUserId;
    this.roles = roles;
  }

  public TaskanaRole[] getRoles() {
    return roles;
  }

  public String getCurrentUserId() {
    return currentUserId;
  }

  @Override
  public String toString() {
    return "MismatchedRoleException [currentUserId="
        + currentUserId
        + ", roles="
        + Arrays.toString(roles)
        + "]";
  }
}
