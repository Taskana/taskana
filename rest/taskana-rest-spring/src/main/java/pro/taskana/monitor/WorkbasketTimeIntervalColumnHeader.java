package pro.taskana.monitor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import pro.taskana.impl.report.TimeIntervalColumnHeader;

/**
 * Class for Workbasket Time Interval Column Header, overrides displayName.
 *
 * @author mmr
 */
public class WorkbasketTimeIntervalColumnHeader extends TimeIntervalColumnHeader {

    public WorkbasketTimeIntervalColumnHeader(int ageInDays) {
        super(ageInDays);
    }

    @Override
    public String getDisplayName() {
        LocalDateTime ldt = LocalDateTime.now().plusDays(getLowerAgeLimit());
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
        return dateFormat.format(ldt);
    }
}
