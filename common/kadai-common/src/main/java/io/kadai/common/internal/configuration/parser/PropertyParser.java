package io.kadai.common.internal.configuration.parser;

import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.internal.configuration.KadaiProperty;
import io.kadai.common.internal.util.ReflectionUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface PropertyParser<T> {

  Map<Class<?>, PropertyParser<?>> PROPERTY_INITIALIZER_BY_CLASS =
      Stream.of(
              new CollectionPropertyParser<>(List.class, ArrayList::new),
              new CollectionPropertyParser<>(Set.class, HashSet::new),
              new CustomHolidayParser(),
              new LocalTimeIntervalParser(),
              new MapPropertyParser(),
              new EnumPropertyParser(),
              new SimpleParser<>(Boolean.class, Boolean::parseBoolean),
              new SimpleParser<>(Duration.class, Duration::parse),
              new SimpleParser<>(Instant.class, Instant::parse),
              new SimpleParser<>(Integer.class, Integer::parseInt),
              new SimpleParser<>(Long.class, Long::parseLong),
              new SimpleParser<>(String.class, Function.identity()),
              new SimpleParser<>(ZoneId.class, ZoneId::of))
          .collect(Collectors.toUnmodifiableMap(PropertyParser::getTargetClass, t -> t));

  static PropertyParser<?> getPropertyParser(Class<?> forClass) {
    forClass = ReflectionUtil.wrap(forClass);
    PropertyParser<?> propertyParser;
    if (forClass.isEnum()) {
      propertyParser = PROPERTY_INITIALIZER_BY_CLASS.get(Enum.class);
    } else {
      propertyParser = PROPERTY_INITIALIZER_BY_CLASS.get(forClass);
    }
    if (propertyParser == null) {
      throw new SystemException(
          String.format("No PropertyParser configured for class '%s'", forClass));
    }
    return propertyParser;
  }

  Class<?> getTargetClass();

  Optional<T> parse(String value, String separator, Type type);

  default Optional<T> parse(String value, String separator, Field field) {
    if (!validateTargetField(field)) {
      throw new SystemException(
          String.format(
              "Cannot initialize field '%s' because field type '%s' is not compatible to a '%s'",
              field, field.getType(), getTargetClass()));
    }
    return parse(value, separator, field.getGenericType());
  }

  default Optional<T> parse(
      Map<String, String> properties, String separator, Field field, KadaiProperty kadaiProperty) {
    return parse(properties.get(kadaiProperty.value()), separator, field);
  }

  default boolean validateTargetField(Field field) {
    return getTargetClass().isAssignableFrom(ReflectionUtil.wrap(field.getType()));
  }
}
