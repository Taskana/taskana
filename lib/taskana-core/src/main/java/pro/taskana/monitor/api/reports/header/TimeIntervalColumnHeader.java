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
package pro.taskana.monitor.api.reports.header;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import pro.taskana.monitor.api.reports.item.AgeQueryItem;

/**
 * A TimeIntervalColumnHeader has a lower and an upper age limit which subdivide the count of tasks
 * into different sections. Days in past are represented as negative values and days in the future
 * are represented as positive values. To avoid tasks are counted multiple times or not be listed in
 * the report, these TimeIntervalColumnHeaders should not overlap and should not have gaps. If the
 * TimeIntervalColumnHeader should represent a single day, lowerAgeLimit and upperAgeLimit have to
 * be equal. The outer cluster of a report should have open ends. These open ends are represented
 * with Integer.MIN_VALUE and Integer.MAX_VALUE.
 */
public class TimeIntervalColumnHeader implements ColumnHeader<AgeQueryItem> {

  private final int lowerAgeLimit;
  private final int upperAgeLimit;

  public TimeIntervalColumnHeader(int ageInDays) {
    this(ageInDays, ageInDays);
  }

  public TimeIntervalColumnHeader(int lowerAgeLimit, int upperAgeLimit) {
    this.lowerAgeLimit = lowerAgeLimit;
    this.upperAgeLimit = upperAgeLimit;
  }

  public static int getSmallestUpperLimit(List<? extends TimeIntervalColumnHeader> columnHeaders) {
    return columnHeaders.stream()
        .mapToInt(TimeIntervalColumnHeader::getUpperAgeLimit)
        .min()
        .orElse(0);
  }

  public static int getLargestLowerLimit(List<? extends TimeIntervalColumnHeader> columnHeaders) {
    return columnHeaders.stream()
        .mapToInt(TimeIntervalColumnHeader::getLowerAgeLimit)
        .max()
        .orElse(0);
  }

  public int getLowerAgeLimit() {
    return lowerAgeLimit;
  }

  public int getUpperAgeLimit() {
    return upperAgeLimit;
  }

  @Override
  public String getDisplayName() {
    return "(" + this.lowerAgeLimit + "," + this.upperAgeLimit + ")";
  }

  @Override
  public boolean fits(AgeQueryItem item) {
    return lowerAgeLimit <= item.getAgeInDays() && upperAgeLimit >= item.getAgeInDays();
  }

  @Override
  public String toString() {
    return getDisplayName();
  }

  /** for Date representation. */
  public static class Date extends TimeIntervalColumnHeader {

    public Date(int ageInDays) {
      super(ageInDays);
    }

    @Override
    public String getDisplayName() {
      LocalDateTime ldt = LocalDateTime.now().plusDays(getLowerAgeLimit());
      DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
      return dateFormat.format(ldt);
    }
  }

  /** For representation of Range. */
  public static class Range extends TimeIntervalColumnHeader {

    public Range(int ageInDays) {
      super(ageInDays);
    }

    public Range(int lowerAgeLimit, int upperAgeLimit) {
      super(lowerAgeLimit, upperAgeLimit);
    }

    @Override
    public String getDisplayName() {
      if (this.getLowerAgeLimit() == Integer.MIN_VALUE) {
        return "<" + this.getUpperAgeLimit();
      } else if (this.getUpperAgeLimit() == Integer.MAX_VALUE) {
        return ">" + this.getLowerAgeLimit();
      } else if (this.getLowerAgeLimit() == -1) {
        return "-1 day";
      } else if (this.getLowerAgeLimit() == 1) {
        return "+1 day";
      } else if (this.getLowerAgeLimit() == 0) {
        return "today";
      } else if (this.getLowerAgeLimit() == this.getUpperAgeLimit()) {
        return this.getUpperAgeLimit() + "";
      } else if (this.getLowerAgeLimit() != this.getUpperAgeLimit()) {
        return "[" + this.getLowerAgeLimit() + " ... " + this.getUpperAgeLimit() + "]";
      }
      return super.getDisplayName();
    }
  }
}
