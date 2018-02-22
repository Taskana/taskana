package pro.taskana.rest.util;

import java.sql.Timestamp;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * This class is used to convert {@link Instant} to {@link Timestamp} and {@link Timestamp} to {@link Instant} for
 * incompatible REST-Models.
 */
public class TimeConverter {

    /**
     * Converting a {@link Timestamp} to {@link Instant} using UTC as ZoneOffset.
     *
     * @param timestamp
     *            which should be converted to Instant.
     * @return {@link Instant} representing the timestamp with UTC ZoneOffset or NULL if parameter is NULL, too.
     */
    public static Instant convertToInstantFromTimestamp(Timestamp timestamp) {
        Instant instant = null;
        if (timestamp != null) {
            instant = timestamp.toLocalDateTime().atOffset(ZoneOffset.UTC).toInstant();
        }
        return instant;
    }

    /**
     * Converting an {@link Instant}-Time into a UTC-{@link Timestamp} using UTC-ZoneOffset.
     *
     * @param instantTime
     *            which should be converted to {@link Timestamp}.
     * @return timestamp using UTC or NULL if the parameter was NULL, too.
     * @throws DateTimeException
     *             when the value does exceeds the supported range.
     */
    public static Timestamp convertToTimestampFromInstant(Instant instantTime)
        throws DateTimeException {
        Timestamp timestamp = null;
        if (instantTime != null) {
            LocalDateTime ldt = LocalDateTime.ofInstant(instantTime, ZoneOffset.UTC);
            timestamp = Timestamp.valueOf(ldt);
        }
        return timestamp;
    }
}
