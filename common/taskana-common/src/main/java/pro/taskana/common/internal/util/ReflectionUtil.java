package pro.taskana.common.internal.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReflectionUtil {

  private static final Map<Class<?>, Class<?>> PRIMITIVES_TO_WRAPPERS = new HashMap<>();

  static {
    PRIMITIVES_TO_WRAPPERS.put(boolean.class, Boolean.class);
    PRIMITIVES_TO_WRAPPERS.put(byte.class, Byte.class);
    PRIMITIVES_TO_WRAPPERS.put(char.class, Character.class);
    PRIMITIVES_TO_WRAPPERS.put(double.class, Double.class);
    PRIMITIVES_TO_WRAPPERS.put(float.class, Float.class);
    PRIMITIVES_TO_WRAPPERS.put(int.class, Integer.class);
    PRIMITIVES_TO_WRAPPERS.put(long.class, Long.class);
    PRIMITIVES_TO_WRAPPERS.put(short.class, Short.class);
    PRIMITIVES_TO_WRAPPERS.put(void.class, Void.class);
  }

  private ReflectionUtil() {
    throw new IllegalStateException("utility class");
  }

  public static List<Field> retrieveAllFields(Class<?> currentClass) {
    List<Field> fields = new ArrayList<>();
    while (currentClass.getSuperclass() != null) {
      fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
      currentClass = currentClass.getSuperclass();
    }
    return fields.stream().filter(f -> !f.isSynthetic()).collect(Collectors.toList());
  }

  // safe because both Long.class and long.class are of type Class<Long>
  @SuppressWarnings("unchecked")
  public static <T> Class<T> wrap(Class<T> c) {
    return c.isPrimitive() ? (Class<T>) PRIMITIVES_TO_WRAPPERS.get(c) : c;
  }
}
