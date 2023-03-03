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
package pro.taskana.monitor.api.reports.item;

/**
 * The MonitorQueryItem entity contains the number of tasks for a key (e.g. workbasketKey) and age
 * in days.
 */
public class MonitorQueryItem implements AgeQueryItem {

  private String key;
  private int ageInDays;
  private int numberOfTasks;

  @Override
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  @Override
  public int getValue() {
    return numberOfTasks;
  }

  @Override
  public int getAgeInDays() {
    return ageInDays;
  }

  @Override
  public void setAgeInDays(int ageInDays) {
    this.ageInDays = ageInDays;
  }

  public void setNumberOfTasks(int numberOfTasks) {
    this.numberOfTasks = numberOfTasks;
  }

  @Override
  public String toString() {
    return String.format(
        "MonitorQueryItem [key= %s, ageInDays= %d, numberOfTasks= %d]",
        key, ageInDays, numberOfTasks);
  }
}
