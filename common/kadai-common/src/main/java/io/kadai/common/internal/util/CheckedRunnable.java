package io.kadai.common.internal.util;

import io.kadai.common.api.exceptions.SystemException;

@FunctionalInterface
public interface CheckedRunnable {

  static Runnable wrap(CheckedRunnable checkedRunnable) {
    return () -> {
      try {
        checkedRunnable.run();
      } catch (Exception e) {
        throw new SystemException("Caught exception", e);
      }
    };
  }

  void run() throws Exception;
}
