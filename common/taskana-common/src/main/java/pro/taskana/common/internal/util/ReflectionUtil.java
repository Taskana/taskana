package pro.taskana.common.internal.util;

import static java.util.function.Predicate.not;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    return fields.stream().filter(not(Field::isSynthetic)).toList();
  }

  public static Class<?> getRawClass(Type type) {
    if (type instanceof ParameterizedType parameterizedType) {
      return getRawClass(parameterizedType.getRawType());
    }
    return (Class<?>) type;
  }

  // safe because both Long.class and long.class are of type Class<Long>
  @SuppressWarnings("unchecked")
  public static <T> Class<T> wrap(Class<T> c) {
    return c.isPrimitive() ? (Class<T>) PRIMITIVES_TO_WRAPPERS.get(c) : c;
  }

  public static Object getEnclosingInstance(Object instance) {
    return Arrays.stream(instance.getClass().getDeclaredFields())
        .filter(Field::isSynthetic)
        .filter(f -> f.getName().startsWith("this"))
        .findFirst()
        .map(
            CheckedFunction.wrap(
                field -> {
                  field.setAccessible(true);
                  return field.get(instance);
                }))
        .orElse(null);
  }
}
