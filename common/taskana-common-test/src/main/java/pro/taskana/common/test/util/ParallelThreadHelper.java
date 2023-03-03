/*-
 * #%L
 * pro.taskana:taskana-common-test
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package pro.taskana.common.test.util;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

import pro.taskana.common.api.exceptions.SystemException;

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
