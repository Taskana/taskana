/*-
 * #%L
 * pro.taskana:taskana-core
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package pro.taskana.monitor.api;

import java.util.Objects;

/**
 * An item that contains information of a selected item of a Report. It is used to get the task ids
 * of the selected item of the Report.
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
