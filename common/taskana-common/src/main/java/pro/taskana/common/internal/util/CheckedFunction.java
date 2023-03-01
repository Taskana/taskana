/*-
 * #%L
 * pro.taskana:taskana-common
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package pro.taskana.common.internal.util;

import java.util.function.Function;

import pro.taskana.common.api.exceptions.SystemException;

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
