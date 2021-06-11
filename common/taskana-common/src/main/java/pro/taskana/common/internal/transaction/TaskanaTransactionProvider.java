package pro.taskana.common.internal.transaction;

import java.util.function.Supplier;

/** This functional interace provides support for transactions. */
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
