package pro.taskana.common.internal.util;

import java.util.function.Supplier;
import pro.taskana.common.api.exceptions.SystemException;

@FunctionalInterface
public interface CheckedSupplier<T, E extends Throwable> {

  static <T> Supplier<T> wrap(CheckedSupplier<T, Throwable> checkedSupplier) {
    return () -> {
      try {
        return checkedSupplier.get();
      } catch (Throwable e) {
        throw new SystemException("Caught exception", e);
      }
    };
  }

  T get() throws E;
}
