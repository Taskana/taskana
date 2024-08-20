package io.kadai.common.internal.util;

import io.kadai.common.api.exceptions.SystemException;
import java.util.function.Consumer;

@FunctionalInterface
public interface CheckedConsumer<T, E extends Throwable> {
  static <T> Consumer<T> wrap(CheckedConsumer<T, Throwable> checkedConsumer) {
    return t -> {
      try {
        checkedConsumer.accept(t);
      } catch (Throwable e) {
        throw new SystemException("Caught exception", e);
      }
    };
  }

  void accept(T t) throws E;
}
