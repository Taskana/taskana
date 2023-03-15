package pro.taskana.workbasket.api.exceptions;

import java.util.Arrays;
import java.util.Map;
import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.models.Workbasket;

/**
 * This exception is thrown when the current user does not have a certain {@linkplain
 * WorkbasketPermission permission} on a {@linkplain Workbasket}.
 */
public class NotAuthorizedOnWorkbasketException extends TaskanaException {

  public static final String ERROR_KEY_KEY_DOMAIN =
      "NOT_AUTHORIZED_ON_WORKBASKET_WITH_KEY_AND_DOMAIN";
  public static final String ERROR_KEY_ID = "NOT_AUTHORIZED_ON_WORKBASKET_WITH_ID";
  private final String currentUserId;
  private final WorkbasketPermission[] requiredPermissions;
  private final String workbasketId;
  private final String workbasketKey;
  private final String domain;

  public NotAuthorizedOnWorkbasketException(
      String currentUserId, String workbasketId, WorkbasketPermission... requiredPermissions) {
    super(
        String.format(
            "Not authorized. The current user '%s' has no '%s' permission(s) for Workbasket '%s'.",
            currentUserId, Arrays.toString(requiredPermissions), workbasketId),
        ErrorCode.of(
            ERROR_KEY_ID,
            Map.ofEntries(
                Map.entry("currentUserId", ensureNullIsHandled(currentUserId)),
                Map.entry("workbasketId", ensureNullIsHandled(workbasketId)),
                Map.entry("requiredPermissions", ensureNullIsHandled(requiredPermissions)))));

    this.currentUserId = currentUserId;
    this.requiredPermissions = requiredPermissions;
    this.workbasketId = workbasketId;
    workbasketKey = null;
    domain = null;
  }

  public NotAuthorizedOnWorkbasketException(
      String currentUserId,
      String workbasketKey,
      String domain,
      WorkbasketPermission... requiredPermissions) {
    super(
        String.format(
            "Not authorized. The current user '%s' has no '%s' permission for "
                + "Workbasket with key '%s' in domain '%s'.",
            currentUserId, Arrays.toString(requiredPermissions), workbasketKey, domain),
        ErrorCode.of(
            ERROR_KEY_KEY_DOMAIN,
            Map.ofEntries(
                Map.entry("currentUserId", ensureNullIsHandled(currentUserId)),
                Map.entry("workbasketKey", ensureNullIsHandled(workbasketKey)),
                Map.entry("domain", ensureNullIsHandled(domain)),
                Map.entry("requiredPermissions", ensureNullIsHandled(requiredPermissions)))));

    this.currentUserId = currentUserId;
    this.requiredPermissions = requiredPermissions;
    this.workbasketKey = workbasketKey;
    this.domain = domain;
    workbasketId = null;
  }

  public String getWorkbasketKey() {
    return workbasketKey;
  }

  public String getDomain() {
    return domain;
  }

  public String getWorkbasketId() {
    return workbasketId;
  }

  public WorkbasketPermission[] getRequiredPermissions() {
    return requiredPermissions;
  }

  public String getCurrentUserId() {
    return currentUserId;
  }
}
