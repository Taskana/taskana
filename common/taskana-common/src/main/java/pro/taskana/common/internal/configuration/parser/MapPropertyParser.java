package pro.taskana.common.internal.configuration.parser;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.configuration.TaskanaProperty;
import pro.taskana.common.internal.util.Pair;
import pro.taskana.common.internal.util.ReflectionUtil;

public class MapPropertyParser implements PropertyParser<Map<?, ?>> {

  @Override
  public Class<?> getTargetClass() {
    return Map.class;
  }

  @Override
  public Optional<Map<?, ?>> parse(
      Map<String, String> properties,
      String separator,
      Field field,
      TaskanaProperty taskanaProperty) {
    if (!Map.class.isAssignableFrom(field.getType())) {
      throw new SystemException(
          String.format(
              "Cannot initialize field '%s' because field type '%s' is not a Map",
              field, field.getType()));
    }

    ParameterizedType genericType = (ParameterizedType) field.getGenericType();
    Type[] actualTypeArguments = genericType.getActualTypeArguments();
    Class<?> keyClass = ReflectionUtil.getRawClass(actualTypeArguments[0]);
    Class<?> valueClass = ReflectionUtil.getRawClass(actualTypeArguments[1]);

    // Parses property files into a Map using the following layout: <Property>.<Key> = <value>
    String propertyKey = taskanaProperty.value();
    Map<?, ?> mapFromProperties =
        properties.keySet().stream()
            .filter(it -> it.startsWith(propertyKey))
            .map(
                it -> {
                  // Keys of the map entry is everything after the propertyKey + "."
                  String rawKey = it.substring(propertyKey.length() + 1);
                  // key is always present. filter guarantees that.
                  @SuppressWarnings("OptionalGetWithoutIsPresent")
                  Object key =
                      PropertyParser.getPropertyParser(keyClass)
                          .parse(rawKey, separator, actualTypeArguments[0])
                          .get();

                  // Value of the map entry is the value from the property
                  String rawValue = properties.get(it);
                  Optional<?> value =
                      PropertyParser.getPropertyParser(valueClass)
                          .parse(rawValue, separator, actualTypeArguments[1]);

                  return value.map(o -> Pair.of(key, o)).orElse(null);
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

    if (mapFromProperties.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(mapFromProperties);
    }
  }

  @Override
  public Optional<Map<?, ?>> parse(String value, String separator, Field field) {
    throw new SystemException("Map type can't be nested!");
  }

  @Override
  public Optional<Map<?, ?>> parse(String value, String separator, Type type) {
    throw new SystemException("Map type can't be nested!");
  }
}
