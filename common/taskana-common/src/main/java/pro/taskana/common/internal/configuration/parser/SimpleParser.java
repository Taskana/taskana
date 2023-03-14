package pro.taskana.common.internal.configuration.parser;

import static java.util.function.Predicate.not;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.util.ReflectionUtil;

public class SimpleParser<T> implements PropertyParser<T> {

  private final Class<?> targetClass;
  private final Function<String, T> parseFunction;

  public SimpleParser(Class<?> targetClass, Function<String, T> parseFunction) {
    this.targetClass = targetClass;
    this.parseFunction = parseFunction;
  }

  @Override
  public Optional<T> parse(String value, String separator, Type type) {
    Class<?> rawClass = ReflectionUtil.getRawClass(type);
    if (!getTargetClass().isAssignableFrom(ReflectionUtil.wrap(rawClass))) {
      throw new SystemException(
          String.format(
              "Cannot initialize '%s' because target type '%s' is not a '%s'",
              value, rawClass, getTargetClass()));
    }
    return Optional.ofNullable(value).map(it -> parseFunction.apply(value));
  }

  @Override
  public Class<?> getTargetClass() {
    return targetClass;
  }

  protected static List<String> splitStringAndTrimElements(String str, String separator) {
    return Arrays.stream(str.split(Pattern.quote(separator)))
        .filter(not(String::isEmpty))
        .map(String::trim)
        .collect(Collectors.toList());
  }
}
