package io.kadai.common.internal.transaction;

import java.util.function.Supplier;

/** This functional interface provides support for transactions. */
@FunctionalInterface
public interface KadaiTransactionProvider {

  static <T> T executeInTransactionIfPossible(
      KadaiTransactionProvider transactionProvider, Supplier<T> supplier) {
    return transactionProvider != null
        ? transactionProvider.executeInTransaction(supplier)
        : supplier.get();
  }

  static void executeInTransactionIfPossible(
      KadaiTransactionProvider transactionProvider, Runnable runnable) {
    executeInTransactionIfPossible(
        transactionProvider,
        () -> {
          runnable.run();
          return null;
        });
  }

  <T> T executeInTransaction(Supplier<T> supplier);
}
