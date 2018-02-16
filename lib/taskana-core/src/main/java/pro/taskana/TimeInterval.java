package pro.taskana;

import java.time.Instant;

/**
 * Capture a time interval. A fixed interval has defined begin and end Instant. An open ended interval has either begin
 * == null or end ==null.
 *
 * @author bbr
 */
public class TimeInterval {

    private Instant begin;
    private Instant end;

    public TimeInterval(Instant begin, Instant end) {
        this.begin = begin;
        this.end = end;
    }

    public boolean contains(Instant i) {
        if (i == null) {
            return false;
        }
        boolean isAfterBegin = begin == null ? true : !i.isBefore(begin);
        boolean isBeforeEnd = end == null ? true : !i.isAfter(end);
        return (isAfterBegin && isBeforeEnd);
    }

    public boolean isValid() {
        boolean isValid = begin != null || end != null;
        if (begin != null && end != null && begin.isAfter(end)) {
            isValid = false;
        }
        return isValid;
    }

    public Instant getBegin() {
        return begin;
    }

    public void setBegin(Instant begin) {
        this.begin = begin;
    }

    public Instant getEnd() {
        return end;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TimeInterval [begin=");
        builder.append(begin);
        builder.append(", end=");
        builder.append(end);
        builder.append("]");
        return builder.toString();
    }

}
