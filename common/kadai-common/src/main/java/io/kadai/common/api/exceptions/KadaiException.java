package io.kadai.common.api.exceptions;

import java.io.Serializable;

/** common base class for KADAI's checked exceptions. */
public class KadaiException extends Exception {

  private final ErrorCode errorCode;

  protected KadaiException(String message, ErrorCode errorCode) {
    this(message, errorCode, null);
  }

  protected KadaiException(String message, ErrorCode errorCode, Throwable cause) {
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
