package io.kadai.common.internal.util;

import io.kadai.common.api.exceptions.SystemException;
import java.util.function.Function;

@FunctionalInterface
public interface CheckedFunction<T, R, E extends Throwable> {
  static <T, R> Function<T, R> wrap(CheckedFunction<T, R, Throwable> checkedFunction) {
    return t -> {
      try {
        return checkedFunction.apply(t);
      } catch (Throwable e) {
        throw new SystemException("Caught exception", e);
      }
    };
  }

  static <T, R> Function<T, R> wrapExceptFor(
      CheckedFunction<T, R, Throwable> checkedFunction, Class<? extends RuntimeException> ignore) {
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

  R apply(T t) throws E;
}
