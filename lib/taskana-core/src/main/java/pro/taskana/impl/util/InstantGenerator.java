package pro.taskana.impl.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * This is an util class for instant generator.
 *
 * @author mmr
 */
public final class InstantGenerator {

    private InstantGenerator() {
    }

    public static Instant getInstant(String dateTime) {
        return LocalDateTime.parse(dateTime).atZone(ZoneId.systemDefault()).toInstant();
    }
}
