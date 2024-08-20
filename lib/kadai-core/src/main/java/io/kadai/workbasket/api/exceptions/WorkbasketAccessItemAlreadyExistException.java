package io.kadai.workbasket.api.exceptions;

import io.kadai.common.api.exceptions.ErrorCode;
import io.kadai.common.api.exceptions.KadaiException;
import io.kadai.workbasket.api.models.WorkbasketAccessItem;
import java.util.Map;

/**
 * This exception is thrown when an already existing {@linkplain WorkbasketAccessItem} was tried to
 * be created.
 */
public class WorkbasketAccessItemAlreadyExistException extends KadaiException {

  public static final String ERROR_KEY = "WORKBASKET_ACCESS_ITEM_ALREADY_EXISTS";
  private final String accessId;
  private final String workbasketId;

  public WorkbasketAccessItemAlreadyExistException(String accessId, String workbasketId) {
    super(
        String.format(
            "WorkbasketAccessItem with access id '%s' and workbasket id '%s' already exists.",
            accessId, workbasketId),
        ErrorCode.of(
            ERROR_KEY,
            Map.ofEntries(
                Map.entry("accessId", ensureNullIsHandled(accessId)),
                Map.entry("workbasketId", ensureNullIsHandled(workbasketId)))));
    this.accessId = accessId;
    this.workbasketId = workbasketId;
  }

  public String getAccessId() {
    return accessId;
  }

  public String getWorkbasketId() {
    return workbasketId;
  }
}
