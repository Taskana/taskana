package pro.taskana.common.internal.transaction;

/**
 * represents a callable Object.
 *
 * @param <T> the type of the returned objects.
 * @author bbr
 */
@FunctionalInterface
public interface TaskanaCallable<T> {

  T call();
}
