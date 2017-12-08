package pro.taskana.impl.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
     * @param list TODO
     * @param <T> TODO
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

    /**
     * make a String for logging from a map.
     *
     * @param map   the map to be stringified
     * @param <K> TODO
     * @param <V> TODO
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
}
