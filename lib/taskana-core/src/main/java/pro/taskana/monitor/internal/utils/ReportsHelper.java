package pro.taskana.monitor.internal.utils;

public class ReportsHelper {
    public static String[] toLowerCopy(String... source) {
        if (source == null || source.length == 0) {
            return null;
        }
        String[] target = new String[source.length];
        for (int i = 0; i < source.length; i++) {
            target[i] = source[i].toLowerCase();
        }
        return target;
    }
}
