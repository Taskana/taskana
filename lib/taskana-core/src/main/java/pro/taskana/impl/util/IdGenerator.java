package pro.taskana.impl.util;

import java.util.UUID;

/**
 * This class contains util emthods for generating ids.
 */
public final class IdGenerator {

    private static final String SEPERATOR = ":";

    /**
     * This method create an id with an specific prefix.
     *
     * @param prefix
     *            only 3 characters!
     * @return a String with a length of 40 characters
     */
    public static String generateWithPrefix(String prefix) {
        return new StringBuilder().append(prefix)
            .append(SEPERATOR)
            .append(UUID.randomUUID().toString())
            .toString();
    }

    private IdGenerator() {
    }
}
