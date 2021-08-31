package pro.taskana.monitor.api.reports.header;

import pro.taskana.monitor.api.reports.item.PriorityQueryItem;

public class PriorityColumnHeader implements ColumnHeader<PriorityQueryItem> {

  private final int lowerBoundInc;
  private final int upperBoundInc;

  public PriorityColumnHeader(int lowerBoundInc, int upperBoundInc) {
    this.lowerBoundInc = lowerBoundInc;
    this.upperBoundInc = upperBoundInc;
  }

  @Override
  public String getDisplayName() {
    if (lowerBoundInc == Integer.MIN_VALUE) {
      return "<" + upperBoundInc;
    } else if (upperBoundInc == Integer.MAX_VALUE) {
      return ">" + lowerBoundInc;
    } else {
      return lowerBoundInc + " - " + upperBoundInc;
    }
  }

  @Override
  public boolean fits(PriorityQueryItem item) {
    return lowerBoundInc <= item.getPriority() && upperBoundInc >= item.getPriority();
  }
}
