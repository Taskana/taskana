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
package pro.taskana.monitor.api.reports.row;

import java.util.Map;

import pro.taskana.monitor.api.reports.Report;
import pro.taskana.monitor.api.reports.header.ColumnHeader;
import pro.taskana.monitor.api.reports.item.QueryItem;

/**
 * Representation of a row in a {@linkplain Report}. It contains an array of cells whose index
 * corresponds to the {@linkplain ColumnHeader} index in the {@linkplain Report}.
 *
 * @param <I> {@linkplain QueryItem} on which the {@linkplain Report} is based on.
 */
public interface Row<I extends QueryItem> {

  /**
   * Appends a specific item value at a specific index.
   *
   * @param item the item which will be appended
   * @param index the index at which the item will be appended at.
   * @throws IndexOutOfBoundsException if the given index is invalid.
   */
  void addItem(I item, int index) throws IndexOutOfBoundsException;

  /**
   * updates the total value of the row without changing any cell value.
   *
   * @param item the item whose value will be added to the total value of this row.
   */
  void updateTotalValue(I item);

  String getKey();

  String getDisplayName();

  void setDisplayName(Map<String, String> displayMap);

  int getTotalValue();

  int[] getCells();
}
