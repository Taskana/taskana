package io.kadai.common.internal.util;

import io.kadai.common.api.exceptions.SystemException;
import java.util.function.Supplier;

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
