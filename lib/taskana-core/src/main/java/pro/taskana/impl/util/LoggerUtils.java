package pro.taskana.impl.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Utility methods for logging.
 *
 * @author bbr
 */
public final class LoggerUtils {

    private LoggerUtils() {
    }

    /**
     * Creating an appended log-string of a list with generic type T. The logging does append informations using
     * toString() on the entries.
     *
     * @param list
     *            the input list to be stringified
     * @param <T>
     *            the type of the objects in the input list
     * @return A String representation of the list.
     */
    public static <T> String listToString(List<T> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            for (T t : list) {
                builder.append(String.valueOf(t));
                builder.append(";\n");
            }
            builder.append("]");
            return builder.toString();
        }
    }

    /**
     * Creating an appended log-string of a map with generic types K/V. The logging does append informations using
     * toString() on the entries.
     *
     * @param map
     *            the map to be stringified
     * @param <K>
     *            the type of the keys in the map
     * @param <V>
     *            the type of the values in the map
     * @return A String representation of the map.
     */
    public static <K, V> String mapToString(Map<K, V> map) {
        if (map == null || map.isEmpty()) {
            return "[]";
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            Set<Entry<K, V>> entrySet = map.entrySet();
            for (Entry<K, V> entry : entrySet) {
                builder.append("(");
                builder.append(entry.getKey());
                builder.append(" , ");
                builder.append(entry.getValue());
                builder.append(")");
                builder.append(" , ");
            }
            builder.append("]");
            return builder.toString();
        }
    }

    public static <T> String setToString(Set<T> set) {
        if (set == null || set.isEmpty()) {
            return "[]";
        }

        StringBuilder result = new StringBuilder("[");
        set.forEach(e -> result.append("(").append(e).append(") ,"));
        result.append("]");
        return result.toString();
    }
}
