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
        return "TimeInterval [" +
            "begin=" + this.begin +
            ", end=" + this.end +
            "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((begin == null) ? 0 : begin.hashCode());
        result = prime * result + ((end == null) ? 0 : end.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TimeInterval other = (TimeInterval) obj;
        if (begin == null) {
            if (other.begin != null) {
                return false;
            }
        } else if (!begin.equals(other.begin)) {
            return false;
        }
        if (end == null) {
            if (other.end != null) {
                return false;
            }
        } else if (!end.equals(other.end)) {
            return false;
        }
        return true;
    }

}
