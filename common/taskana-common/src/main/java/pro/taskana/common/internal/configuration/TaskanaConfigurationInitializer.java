package pro.taskana.common.internal.configuration;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
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

public class TaskanaConfigurationInitializer {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(TaskanaConfigurationInitializer.class);
  private static final String TASKANA_CUSTOM_HOLIDAY_DAY_MONTH_SEPARATOR = ".";
  private static final String TASKANA_CLASSIFICATION_CATEGORIES_PROPERTY =
      "taskana.classification.categories";

  private TaskanaConfigurationInitializer() {
    throw new IllegalStateException("utility class");
  }

  public static <T> Optional<T> parseProperty(
      Properties props, String key, CheckedFunction<String, T, Exception> function) {
    String property = props.getProperty(key, "");
    if (!property.isEmpty()) {
      try {
        return Optional.ofNullable(function.apply(property));
      } catch (Throwable t) {
        LOGGER.warn(
            "Could not parse property {} ({}). Using default. Exception: {}",
            key,
            property,
            t.getMessage());
      }
    }
    return Optional.empty();
  }

  public static Map<String, List<String>> configureClassificationCategoriesForType(
      Properties props, List<String> classificationTypes) {
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
      String separator, Properties props, boolean shouldUseLowerCaseForAccessIds) {
    Function<TaskanaRole, Set<String>> getAccessIdsForRole =
        role ->
            new HashSet<>(
                splitStringAndTrimElements(
                    props.getProperty(role.getPropertyName().toLowerCase(), ""),
                    separator,
                    shouldUseLowerCaseForAccessIds
                        ? String::toLowerCase
                        : UnaryOperator.identity()));

    return Arrays.stream(TaskanaRole.values())
        .map(role -> Pair.of(role, getAccessIdsForRole.apply(role)))
        .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
  }

  public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
    fields.addAll(Arrays.asList(type.getDeclaredFields()));
    if (type.getSuperclass() != null) {
      getAllFields(fields, type.getSuperclass());
    }
    return fields;
  }

  public static void configureAnnotatedFields(Object instance, String separator, Properties props) {

    final List<Field> fields = getAllFields(new ArrayList<>(), instance.getClass());
    for (Field field : fields) {
      Optional.ofNullable(field.getAnnotation(TaskanaProperty.class))
          .ifPresent(
              taskanaProperty -> {
                final String fieldPropertyName = taskanaProperty.value();
                final Class<?> type = field.getType();
                String name = type.getSimpleName();
                switch (name) {
                  case "int":
                    parseProperty(props, fieldPropertyName, Integer::parseInt)
                        .ifPresent(value -> setFieldValue(instance, field, value));
                    break;
                  case "boolean":
                    parseProperty(props, fieldPropertyName, Boolean::parseBoolean)
                        .ifPresent(value -> setFieldValue(instance, field, value));
                    break;
                  case "String":
                    parseProperty(props, fieldPropertyName, String::new)
                        .ifPresent(value -> setFieldValue(instance, field, value));
                    break;
                  case "Duration":
                    parseProperty(props, fieldPropertyName, Duration::parse)
                        .ifPresent(value -> setFieldValue(instance, field, value));
                    break;
                  case "Instant":
                    parseProperty(props, fieldPropertyName, Instant::parse)
                        .ifPresent(value -> setFieldValue(instance, field, value));
                    break;
                  case "List":
                    final String typeName =
                        ((ParameterizedType) field.getGenericType())
                            .getActualTypeArguments()[0].getTypeName();

                    if (typeName.equals("pro.taskana.common.api.CustomHoliday")) {
                      CheckedFunction<String, List<CustomHoliday>, Exception> parseFunction2 =
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
                      parseProperty(props, fieldPropertyName, parseFunction2)
                          .ifPresent(value -> setFieldValue(instance, field, value));

                    } else {
                      CheckedFunction<String, List<String>, Exception> parseListFunction =
                          p -> splitStringAndTrimElements(p, ",", String::toUpperCase);
                      parseProperty(props, fieldPropertyName, parseListFunction)
                          .ifPresent(value -> setFieldValue(instance, field, value));
                    }
                    break;
                  case "Map":
                    // TODO
                    break;
                  default:
                    throw new SystemException("Unknown configuration type " + name);
                }
              });
    }
  }

  static List<String> splitStringAndTrimElements(String str, String separator) {
    return splitStringAndTrimElements(str, separator, UnaryOperator.identity());
  }

  static List<String> splitStringAndTrimElements(
      String str, String separator, UnaryOperator<String> modifier) {
    return Arrays.stream(str.split(Pattern.quote(separator)))
        .filter(s -> !s.isEmpty())
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
    final Optional<Method> hasSetterMethod =
        Arrays.stream(instance.getClass().getMethods())
            .filter(m -> m.getParameterCount() == 1)
            .filter(m -> m.getName().startsWith("set"))
            .filter(m -> m.getName().toLowerCase().contains(field.getName().toLowerCase()))
            .findFirst();

    if (!hasSetterMethod.isPresent()) {
      throw new SystemException("No setter method for " + field.getName());
    }

    try {
      final Method method = hasSetterMethod.get();
      method.invoke(instance, value);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new SystemException(
          "Property value " + value + " is invalid for field " + field.getName(), e);
    }
  }
}
