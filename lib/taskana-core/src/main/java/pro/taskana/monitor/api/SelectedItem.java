package pro.taskana.monitor.api;

import java.util.Objects;

/**
 * An Item that contains information of a selected Item of a {@linkplain
 * pro.taskana.monitor.api.reports.Report Report}.
 *
 * <p>It is used to get the taskIds of the selected Item of the {@linkplain
 * pro.taskana.monitor.api.reports.Report Report}.
 */
public class SelectedItem {

  private final String key;
  private final String subKey;
  private final int lowerAgeLimit;
  private final int upperAgeLimit;

  public SelectedItem(String key, String subKey, int lowerAgeLimit, int upperAgeLimit) {
    this.key = key;
    this.subKey = subKey;
    this.lowerAgeLimit = lowerAgeLimit;
    this.upperAgeLimit = upperAgeLimit;
  }

  public String getKey() {
    return key;
  }

  public String getSubKey() {
    return subKey;
  }

  public int getUpperAgeLimit() {
    return upperAgeLimit;
  }

  public int getLowerAgeLimit() {
    return lowerAgeLimit;
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, subKey, upperAgeLimit, lowerAgeLimit);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof SelectedItem)) {
      return false;
    }
    SelectedItem other = (SelectedItem) obj;
    return upperAgeLimit == other.upperAgeLimit
        && lowerAgeLimit == other.lowerAgeLimit
        && Objects.equals(key, other.key)
        && Objects.equals(subKey, other.subKey);
  }

  @Override
  public String toString() {
    return "Key: "
        + this.key
        + ", SubKey: "
        + this.subKey
        + ", Limits: ("
        + this.lowerAgeLimit
        + ","
        + this.getUpperAgeLimit()
        + ")";
  }
}
