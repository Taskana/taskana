package io.kadai.workbasket.api.exceptions;

import io.kadai.common.api.exceptions.ErrorCode;
import io.kadai.common.api.exceptions.KadaiRuntimeException;
import io.kadai.workbasket.api.models.Workbasket;

/** This exception is thrown when a user is not authorized to query a {@linkplain Workbasket}. */
public class NotAuthorizedToQueryWorkbasketException extends KadaiRuntimeException {

  public NotAuthorizedToQueryWorkbasketException(
      String message, ErrorCode errorCode, Throwable cause) {
    super(message, errorCode, cause);
  }
}
