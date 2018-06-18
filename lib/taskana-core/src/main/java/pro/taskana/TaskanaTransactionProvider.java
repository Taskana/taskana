package pro.taskana;

/**
 * This class provides support for transactions.
 *
 * @author bbr
 * @param <T>
 *            the type of the returned objects.
 */
public interface TaskanaTransactionProvider<T> {

    T executeInTransaction(TaskanaCallable<T> action);
}
