package pro.taskana.common.internal.util;

public class LogSanitizer {

  private LogSanitizer() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Removes characters which break the log file pattern. Protects against injection attacks.
   *
   * @param loggingMessage String which should be sanitized
   * @return sanitized logging message
   */
  public static String stripLineBreakingChars(Object loggingMessage) {
    return loggingMessage.toString().replaceAll("[\n\r\t]", "_");
  }
}
