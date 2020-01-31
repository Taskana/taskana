package pro.taskana.common.internal.transaction;

/**
 * This class provides support for transactions.
 *
 * @param <T> the type of the returned objects.
 */
@FunctionalInterface
public interface TaskanaTransactionProvider<T> {

  T executeInTransaction(TaskanaCallable<T> action);
}
