package io.kadai.task.api;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public enum WildcardSearchField {
  NAME("NAME"),
  DESCRIPTION("DESCRIPTION"),
  CUSTOM_1("CUSTOM_1"),
  CUSTOM_2("CUSTOM_2"),
  CUSTOM_3("CUSTOM_3"),
  CUSTOM_4("CUSTOM_4"),
  CUSTOM_5("CUSTOM_5"),
  CUSTOM_6("CUSTOM_6"),
  CUSTOM_7("CUSTOM_7"),
  CUSTOM_8("CUSTOM_8"),
  CUSTOM_9("CUSTOM_9"),
  CUSTOM_10("CUSTOM_10"),
  CUSTOM_11("CUSTOM_11"),
  CUSTOM_12("CUSTOM_12"),
  CUSTOM_13("CUSTOM_13"),
  CUSTOM_14("CUSTOM_14"),
  CUSTOM_15("CUSTOM_15"),
  CUSTOM_16("CUSTOM_16");

  private static final Map<String, WildcardSearchField> STRING_TO_ENUM =
      Arrays.stream(values())
          .collect(
              Collectors.toMap(
                  WildcardSearchField::toString,
                  e -> e,
                  (first, second) -> first,
                  () -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER)));
  private String name;

  WildcardSearchField(String name) {
    this.name = name;
  }

  public static WildcardSearchField fromString(String name) {

    return STRING_TO_ENUM.get(name);
  }

  @Override
  public String toString() {
    return name;
  }
}
