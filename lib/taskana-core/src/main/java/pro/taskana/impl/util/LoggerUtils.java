package pro.taskana.impl.util;

import java.util.List;

/**
 * Util methods for logging.
 * @author bbr
 *
 */
public final class LoggerUtils {

    private LoggerUtils() {
    }

    /**
     * make a String for logging from a list of objects.
     * @param list
     * @return A String representation of the list.
     */
    public static <T> String listToString(List<T> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            for (T t : list) {
                builder.append(t.toString());
                builder.append(";");
            }
            builder.append("]");
            return builder.toString();
        }
    }
}
