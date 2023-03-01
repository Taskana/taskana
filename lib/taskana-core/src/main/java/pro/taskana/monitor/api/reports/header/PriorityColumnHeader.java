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

  public int getLowerBoundInc() {
    return lowerBoundInc;
  }

  public int getUpperBoundInc() {
    return upperBoundInc;
  }
}
