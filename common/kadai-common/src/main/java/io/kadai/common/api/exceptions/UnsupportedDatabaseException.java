package io.kadai.common.api.exceptions;

import java.util.Map;

/**
 * This exception is thrown when the database name doesn't match to one of the desired databases.
 */
public class UnsupportedDatabaseException extends KadaiRuntimeException {

  public static final String ERROR_KEY = "DATABASE_UNSUPPORTED";
  private final String databaseProductName;

  public UnsupportedDatabaseException(String databaseProductName) {
    super(
        String.format("Database '%s' is not supported", databaseProductName),
        ErrorCode.of(
            ERROR_KEY, Map.of("databaseProductName", ensureNullIsHandled(databaseProductName))));
    this.databaseProductName = databaseProductName;
  }

  public String getDatabaseProductName() {
    return databaseProductName;
  }
}
