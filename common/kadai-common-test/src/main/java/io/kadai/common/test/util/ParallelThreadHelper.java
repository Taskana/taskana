package io.kadai.common.test.util;

import io.kadai.common.api.exceptions.SystemException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ParallelThreadHelper {

  private ParallelThreadHelper() {
    throw new IllegalStateException("Utility class");
  }

  public static void runInThread(Runnable runnable, int threadCount) throws Exception {
    Thread[] threads = new Thread[threadCount];

    Collection<Throwable> errors = new ConcurrentLinkedQueue<>();
    UncaughtExceptionHandler uncaughtExceptionHandler = (t, e) -> errors.add(e);

    for (int i = 0; i < threads.length; i++) {
      threads[i] = new Thread(runnable);
      threads[i].setUncaughtExceptionHandler(uncaughtExceptionHandler);
      threads[i].start();
    }
    for (Thread thread : threads) {
      thread.join();
    }
    if (!errors.isEmpty()) {
      errors.forEach(Throwable::printStackTrace);
      throw new SystemException("at least 1 thread caught an exception.");
    }
  }
}
