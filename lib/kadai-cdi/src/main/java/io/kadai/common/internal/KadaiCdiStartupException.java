package io.kadai.common.internal;

public class KadaiCdiStartupException extends RuntimeException {

  public KadaiCdiStartupException(Throwable cause) {
    super("Can't init KadaiProducers", cause);
  }
}
