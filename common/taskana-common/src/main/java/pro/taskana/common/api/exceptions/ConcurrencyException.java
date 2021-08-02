package pro.taskana.common.api.exceptions;

import pro.taskana.common.internal.util.MapCreator;

/**
 * This exception is thrown when an attempt is made to update an object that has already been
 * updated by another user.
 */
public class ConcurrencyException extends TaskanaException {

  public static final String ERROR_KEY = "ENTITY_NOT_UP_TO_DATE";
  private final String entityId;

  public ConcurrencyException(String entityId) {
    super(
        String.format(
            "The entity with id '%s' cannot be updated since it has been modified while editing.",
            entityId),
        ErrorCode.of(ERROR_KEY, MapCreator.of("entityId", entityId)));
    this.entityId = entityId;
  }

  public String getEntityId() {
    return entityId;
  }

  @Override
  public String toString() {
    return "ConcurrencyException{}";
  }
}
