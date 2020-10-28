package pro.taskana.common.internal.transaction;

/**
 * represents a callable Object.
 *
 * @param <T> the type of the returned objects.
 */
@FunctionalInterface
public interface TaskanaCallable<T> {

  T call();
}
