package pro.taskana.common.internal.util;

import java.util.function.Function;

import pro.taskana.common.api.exceptions.SystemException;

@FunctionalInterface
public interface CheckedFunction<T, E> {
  static <T, E> Function<T, E> wrap(CheckedFunction<T, E> checkedFunction) {
    return t -> {
      try {
        return checkedFunction.apply(t);
      } catch (Throwable e) {
        throw new SystemException("Caught exception", e);
      }
    };
  }

  static <T, E> Function<T, E> wrapExceptFor(
      CheckedFunction<T, E> checkedFunction, Class<? extends RuntimeException> ignore) {
    return t -> {
      try {
        return checkedFunction.apply(t);
      } catch (Throwable e) {
        if (e.getClass().equals(ignore)) {
          throw (RuntimeException) e;
        } else {
          throw new SystemException("Caught exception", e);
        }
      }
    };
  }

  E apply(T t) throws Throwable;
}
