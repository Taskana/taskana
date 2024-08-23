package io.kadai.common.api.exceptions;

import java.io.Serializable;

/** The common base class for KADAI's runtime exceptions. */
public class KadaiRuntimeException extends RuntimeException {

  private final ErrorCode errorCode;

  protected KadaiRuntimeException(String message, ErrorCode errorCode) {
    this(message, errorCode, null);
  }

  protected KadaiRuntimeException(String message, ErrorCode errorCode, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  protected static Serializable ensureNullIsHandled(Serializable o) {
    return o == null ? "null" : o;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName()
        + " [errorCode="
        + errorCode
        + ", message="
        + getMessage()
        + "]";
  }
}
