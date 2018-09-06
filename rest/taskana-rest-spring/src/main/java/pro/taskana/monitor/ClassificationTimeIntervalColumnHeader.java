package pro.taskana.monitor;

import pro.taskana.impl.report.TimeIntervalColumnHeader;

/**
 * Class for Classification time Interval Column Header, overrides displayName.
 *
 * @author mmr
 */
public class ClassificationTimeIntervalColumnHeader extends TimeIntervalColumnHeader {

    public ClassificationTimeIntervalColumnHeader(int ageInDays) {
        super(ageInDays);
    }

    public ClassificationTimeIntervalColumnHeader(int lowerAgeLimit, int upperAgeLimit) {
        super(lowerAgeLimit, upperAgeLimit);
    }

    @Override
    public String getDisplayName() {
        if (this.getLowerAgeLimit() == Integer.MIN_VALUE) {
            return "<" + this.getUpperAgeLimit();
        } else if (this.getUpperAgeLimit() == Integer.MAX_VALUE) {
            return ">" + this.getLowerAgeLimit();
        } else if (this.getLowerAgeLimit() == this.getUpperAgeLimit()) {
            return this.getUpperAgeLimit() + "";
        } else if (this.getLowerAgeLimit() != this.getUpperAgeLimit()) {
            return "[" + this.getLowerAgeLimit() + "  ... " + this.getUpperAgeLimit() + "]";
        }
        return super.getDisplayName();
    }
}
