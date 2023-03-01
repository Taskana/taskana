package pro.taskana.common.internal.configuration;

import static java.util.function.Predicate.not;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.CustomHoliday;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.api.exceptions.WrongCustomHolidayFormatException;
import pro.taskana.common.internal.util.CheckedFunction;
import pro.taskana.common.internal.util.Pair;
import pro.taskana.common.internal.util.ReflectionUtil;

public class TaskanaConfigurationInitializer {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(TaskanaConfigurationInitializer.class);
  private static final String TASKANA_CUSTOM_HOLIDAY_DAY_MONTH_SEPARATOR = ".";
  private static final String TASKANA_CLASSIFICATION_CATEGORIES_PROPERTY =
      "taskana.classification.categories";
  private static final Map<Class<?>, PropertyParser<?>> PROPERTY_INITIALIZER_BY_CLASS =
      new HashMap<>();

  static {
    PROPERTY_INITIALIZER_BY_CLASS.put(Integer.class, new IntegerPropertyParser());
    PROPERTY_INITIALIZER_BY_CLASS.put(Boolean.class, new BooleanPropertyParser());
    PROPERTY_INITIALIZER_BY_CLASS.put(String.class, new StringPropertyParser());
    PROPERTY_INITIALIZER_BY_CLASS.put(Duration.class, new DurationPropertyParser());
    PROPERTY_INITIALIZER_BY_CLASS.put(Instant.class, new InstantPropertyParser());
    PROPERTY_INITIALIZER_BY_CLASS.put(List.class, new ListPropertyParser());
  }

  private TaskanaConfigurationInitializer() {
    throw new IllegalStateException("utility class");
  }

  public static void configureAnnotatedFields(
      Object instance, String separator, Map<String, String> props) {
    final List<Field> fields = ReflectionUtil.retrieveAllFields(instance.getClass());
    for (Field field : fields) {
      Optional.ofNullable(field.getAnnotation(TaskanaProperty.class))
          .ifPresent(
              taskanaProperty -> {
                Class<?> type = ReflectionUtil.wrap(field.getType());
                PropertyParser<?> propertyParser =
                    Optional.ofNullable(PROPERTY_INITIALIZER_BY_CLASS.get(type))
                        .orElseThrow(
                            () ->
                                new SystemException(
                                    String.format("Unknown configuration type '%s'", type)));
                propertyParser
                    .initialize(props, separator, field, taskanaProperty)
                    .ifPresent(value -> setFieldValue(instance, field, value));
              });
    }
  }

  public static Map<String, List<String>> configureClassificationCategoriesForType(
      Map<String, String> props, List<String> classificationTypes) {
    Function<String, List<String>> getClassificationCategoriesForType =
        type ->
            parseProperty(
                    props,
                    TASKANA_CLASSIFICATION_CATEGORIES_PROPERTY + "." + type.toLowerCase(),
                    p -> splitStringAndTrimElements(p, ",", String::toUpperCase))
                .orElseGet(ArrayList::new);
    return classificationTypes.stream()
        .map(type -> Pair.of(type, getClassificationCategoriesForType.apply(type)))
        .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
  }

  public static Map<TaskanaRole, Set<String>> configureRoles(
      String separator, Map<String, String> props, boolean shouldUseLowerCaseForAccessIds) {
    Function<TaskanaRole, Set<String>> getAccessIdsForRole =
        role ->
            new HashSet<>(
                splitStringAndTrimElements(
                    props.getOrDefault(role.getPropertyName().toLowerCase(), ""),
                    separator,
                    shouldUseLowerCaseForAccessIds
                        ? String::toLowerCase
                        : UnaryOperator.identity()));

    return Arrays.stream(TaskanaRole.values())
        .map(role -> Pair.of(role, getAccessIdsForRole.apply(role)))
        .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
  }

  static List<String> splitStringAndTrimElements(String str, String separator) {
    return splitStringAndTrimElements(str, separator, UnaryOperator.identity());
  }

  static List<String> splitStringAndTrimElements(
      String str, String separator, UnaryOperator<String> modifier) {
    return Arrays.stream(str.split(Pattern.quote(separator)))
        .filter(not(String::isEmpty))
        .map(String::trim)
        .map(modifier)
        .collect(Collectors.toList());
  }

  static CustomHoliday createCustomHolidayFromPropsEntry(String customHolidayEntry)
      throws WrongCustomHolidayFormatException {
    List<String> parts =
        splitStringAndTrimElements(customHolidayEntry, TASKANA_CUSTOM_HOLIDAY_DAY_MONTH_SEPARATOR);
    if (parts.size() == 2) {
      return CustomHoliday.of(Integer.valueOf(parts.get(0)), Integer.valueOf(parts.get(1)));
    }
    throw new WrongCustomHolidayFormatException(customHolidayEntry);
  }

  private static void setFieldValue(Object instance, Field field, Object value) {
    try {
      field.setAccessible(true);
      field.set(instance, value);
    } catch (IllegalAccessException | IllegalArgumentException e) {
      throw new SystemException(
          "Property value " + value + " is invalid for field " + field.getName(), e);
    }
  }

  private static <T> Optional<T> parseProperty(
      Map<String, String> props, String key, CheckedFunction<String, T, Exception> function) {
    String property = props.getOrDefault(key, "");
    if (property.isEmpty()) {
      return Optional.empty();
    }

    try {
      return Optional.ofNullable(function.apply(property));
    } catch (Exception t) {
      throw new SystemException(
          String.format("Could not parse property '%s' ('%s').", key, property), t);
    }
  }

  interface PropertyParser<T> {
    Optional<T> initialize(
        Map<String, String> properties,
        String separator,
        Field field,
        TaskanaProperty taskanaProperty);
  }

  static class ListPropertyParser implements PropertyParser<List<?>> {
    @Override
    public Optional<List<?>> initialize(
        Map<String, String> properties,
        String separator,
        Field field,
        TaskanaProperty taskanaProperty) {
      if (!List.class.isAssignableFrom(field.getType())) {
        throw new SystemException(
            String.format(
                "Cannot initialize field '%s' because field type '%s' is not a List",
                field, field.getType()));
      }
      Class<?> genericClass =
          (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

      if (genericClass.isEnum()) {
        Map<String, ?> enumConstants =
            Arrays.stream(genericClass.getEnumConstants())
                .collect(Collectors.toMap(e -> e.toString().toLowerCase(), Function.identity()));
        CheckedFunction<String, List<?>, Exception> parseFunction =
            s ->
                splitStringAndTrimElements(s, separator).stream()
                    .map(String::toLowerCase)
                    .map(enumConstants::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        return parseProperty(properties, taskanaProperty.value(), parseFunction);
      } else if (CustomHoliday.class.isAssignableFrom(genericClass)) {
        CheckedFunction<String, List<?>, Exception> parseFunction =
            s ->
                splitStringAndTrimElements(s, separator).stream()
                    .map(
                        str -> {
                          try {
                            return createCustomHolidayFromPropsEntry(str);
                          } catch (WrongCustomHolidayFormatException e) {
                            LOGGER.warn(e.getMessage());
                            return null;
                          }
                        })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        return parseProperty(properties, taskanaProperty.value(), parseFunction);
      } else if (String.class.isAssignableFrom(genericClass)) {
        CheckedFunction<String, List<?>, Exception> parseListFunction =
            p -> splitStringAndTrimElements(p, ",", String::toUpperCase);
        return parseProperty(properties, taskanaProperty.value(), parseListFunction);
      } else {
        throw new SystemException(
            String.format(
                "Cannot initialize field '%s' because field type '%s' is unknown",
                field, genericClass));
      }
    }
  }

  static class InstantPropertyParser implements PropertyParser<Instant> {
    @Override
    public Optional<Instant> initialize(
        Map<String, String> properties,
        String separator,
        Field field,
        TaskanaProperty taskanaProperty) {
      return parseProperty(properties, taskanaProperty.value(), Instant::parse);
    }
  }

  static class DurationPropertyParser implements PropertyParser<Duration> {
    @Override
    public Optional<Duration> initialize(
        Map<String, String> properties,
        String separator,
        Field field,
        TaskanaProperty taskanaProperty) {
      return parseProperty(properties, taskanaProperty.value(), Duration::parse);
    }
  }

  static class StringPropertyParser implements PropertyParser<String> {
    @Override
    public Optional<String> initialize(
        Map<String, String> properties,
        String separator,
        Field field,
        TaskanaProperty taskanaProperty) {
      return parseProperty(properties, taskanaProperty.value(), String::new);
    }
  }

  static class IntegerPropertyParser implements PropertyParser<Integer> {
    @Override
    public Optional<Integer> initialize(
        Map<String, String> properties,
        String separator,
        Field field,
        TaskanaProperty taskanaProperty) {
      return parseProperty(properties, taskanaProperty.value(), Integer::parseInt);
    }
  }

  static class BooleanPropertyParser implements PropertyParser<Boolean> {
    @Override
    public Optional<Boolean> initialize(
        Map<String, String> properties,
        String separator,
        Field field,
        TaskanaProperty taskanaProperty) {
      return parseProperty(properties, taskanaProperty.value(), Boolean::parseBoolean);
    }
  }
}
