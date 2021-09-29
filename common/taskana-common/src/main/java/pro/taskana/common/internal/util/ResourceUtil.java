package pro.taskana.common.internal.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class ResourceUtil {

  private ResourceUtil() {
    throw new IllegalStateException("utility class");
  }

  public static String readResourceAsString(Class<?> clazz, String resource) throws IOException {
    try (InputStream fileStream = clazz.getResourceAsStream(resource)) {
      if (fileStream == null) {
        return null;
      }
      try (Reader inputStreamReader = new InputStreamReader(fileStream, StandardCharsets.UTF_8);
          BufferedReader reader = new BufferedReader(inputStreamReader)) {
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
      }
    }
  }
}
