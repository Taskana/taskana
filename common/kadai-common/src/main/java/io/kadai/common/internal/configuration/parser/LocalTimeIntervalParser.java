package io.kadai.common.internal.configuration.parser;

import io.kadai.common.api.LocalTimeInterval;
import io.kadai.common.api.exceptions.SystemException;
import java.time.LocalTime;
import java.util.List;

public class LocalTimeIntervalParser extends SimpleParser<LocalTimeInterval> {
  public LocalTimeIntervalParser() {
    super(LocalTimeInterval.class, LocalTimeIntervalParser::parse);
  }

  private static LocalTimeInterval parse(String value) {
    List<String> startAndEnd = splitStringAndTrimElements(value, "-");
    if (startAndEnd.size() != 2) {
      throw new SystemException(
          String.format("Cannot convert '%s' to '%s'", value, LocalTimeInterval.class));
    }
    LocalTime start = LocalTime.parse(startAndEnd.get(0));
    LocalTime end = LocalTime.parse(startAndEnd.get(1));
    if (end.equals(LocalTime.MIN)) {
      end = LocalTime.MAX;
    }
    return new LocalTimeInterval(start, end);
  }
}
