package pro.taskana.common.internal.util;

import pro.taskana.common.api.exceptions.SystemException;

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
