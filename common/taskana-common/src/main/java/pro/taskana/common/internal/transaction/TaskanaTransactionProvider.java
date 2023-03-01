/*-
 * #%L
 * pro.taskana:taskana-common
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
package pro.taskana.common.internal.transaction;

import java.util.function.Supplier;

/** This functional interface provides support for transactions. */
@FunctionalInterface
public interface TaskanaTransactionProvider {

  <T> T executeInTransaction(Supplier<T> supplier);

  static <T> T executeInTransactionIfPossible(
      TaskanaTransactionProvider transactionProvider, Supplier<T> supplier) {
    return transactionProvider != null
        ? transactionProvider.executeInTransaction(supplier)
        : supplier.get();
  }

  static void executeInTransactionIfPossible(
      TaskanaTransactionProvider transactionProvider, Runnable runnable) {
    executeInTransactionIfPossible(
        transactionProvider,
        () -> {
          runnable.run();
          return null;
        });
  }
}
