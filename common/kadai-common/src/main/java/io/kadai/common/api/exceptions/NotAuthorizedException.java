package io.kadai.common.api.exceptions;

import io.kadai.common.api.KadaiRole;
import java.util.Arrays;
import java.util.Map;

/**
 * This exception is thrown when the current user is not in a certain {@linkplain KadaiRole role}
 * it is supposed to be.
 */
public class NotAuthorizedException extends KadaiException {

  public static final String ERROR_KEY = "NOT_AUTHORIZED";
  private final String currentUserId;
  private final KadaiRole[] roles;

  public NotAuthorizedException(String currentUserId, KadaiRole... roles) {
    super(
        String.format(
            "Not authorized. The current user '%s' is not member of role(s) '%s'.",
            currentUserId, Arrays.toString(roles)),
        ErrorCode.of(
            ERROR_KEY,
            Map.ofEntries(
                Map.entry("roles", ensureNullIsHandled(roles)),
                Map.entry("currentUserId", ensureNullIsHandled(currentUserId)))));

    this.currentUserId = currentUserId;
    this.roles = roles;
  }

  public KadaiRole[] getRoles() {
    return roles;
  }

  public String getCurrentUserId() {
    return currentUserId;
  }
}
