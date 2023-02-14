package pro.taskana.monitor.api.reports.header;

import lombok.AllArgsConstructor;
import lombok.Getter;

import pro.taskana.monitor.api.reports.item.PriorityQueryItem;

@Getter
@AllArgsConstructor
public class PriorityColumnHeader implements ColumnHeader<PriorityQueryItem> {

  private final int lowerBoundInc;
  private final int upperBoundInc;

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
