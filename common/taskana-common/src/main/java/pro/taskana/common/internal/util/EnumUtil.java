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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.EnumSet;

public class EnumUtil {

  private EnumUtil() {
    throw new IllegalStateException("Utility class");
  }

  @SafeVarargs
  public static <E extends Enum<E>> E[] allValuesExceptFor(E... values) {
    if (values == null || values.length == 0) {
      throw new IllegalArgumentException("values must be present");
    }
    @SuppressWarnings("unchecked")
    E[] array = (E[]) Array.newInstance(values[0].getClass(), 0);
    return EnumSet.complementOf(EnumSet.copyOf(Arrays.asList(values))).toArray(array);
  }
}
