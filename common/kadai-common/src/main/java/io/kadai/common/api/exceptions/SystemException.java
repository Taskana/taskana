package io.kadai.common.api.exceptions;

/** This exception is thrown when a generic KADAI problem is encountered. */
public class SystemException extends KadaiRuntimeException {

  public static final String ERROR_KEY = "CRITICAL_SYSTEM_ERROR";

  public SystemException(String msg) {
    this(msg, null);
  }

  public SystemException(String msg, Throwable cause) {
    super(msg, ErrorCode.of(ERROR_KEY), cause);
  }
}
