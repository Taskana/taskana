package pro.taskana.common.internal.configuration.parser;

import java.time.LocalTime;
import java.util.List;
import pro.taskana.common.api.LocalTimeInterval;
import pro.taskana.common.api.exceptions.SystemException;

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
