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
