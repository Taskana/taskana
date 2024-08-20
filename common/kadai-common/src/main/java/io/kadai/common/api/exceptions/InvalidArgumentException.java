package io.kadai.common.api.exceptions;

/** This exception is thrown when a method is called with an invalid argument. */
public class InvalidArgumentException extends KadaiRuntimeException {

  public static final String ERROR_KEY = "INVALID_ARGUMENT";

  public InvalidArgumentException(String msg) {
    this(msg, null);
  }

  public InvalidArgumentException(String msg, Throwable cause) {
    super(msg, ErrorCode.of(ERROR_KEY), cause);
  }
}
