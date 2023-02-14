package pro.taskana.monitor.api;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * An item that contains information of a selected item of a Report. It is used to get the task ids
 * of the selected item of the Report.
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class SelectedItem {

  private final String key;
  private final String subKey;
  private final int lowerAgeLimit;
  private final int upperAgeLimit;
}
