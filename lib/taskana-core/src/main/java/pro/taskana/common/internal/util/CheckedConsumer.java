package pro.taskana.common.internal.util;

import java.util.function.Consumer;

import pro.taskana.common.api.exceptions.SystemException;

@FunctionalInterface
public interface CheckedConsumer<T, E extends Throwable> {
  static <T, E extends Throwable> Consumer<T> wrap(CheckedConsumer<T, E> checkedConsumer) {
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
