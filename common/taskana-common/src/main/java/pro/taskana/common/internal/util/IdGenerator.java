package pro.taskana.common.internal.util;

import java.util.UUID;

/** This class contains util methods for generating ids. */
public final class IdGenerator {

  private static final String SEPARATOR = ":";

  // disable initialization
  private IdGenerator() {}

  /**
   * This method create an id with an specific prefix.
   *
   * @param prefix only 3 characters!
   * @return a String with a length of 40 characters
   */
  public static String generateWithPrefix(String prefix) {
    return prefix + SEPARATOR + UUID.randomUUID();
  }
}
