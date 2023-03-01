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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import pro.taskana.monitor.api.reports.Report;
import pro.taskana.monitor.api.reports.item.QueryItem;

/**
 * The FoldableRow extends the {@linkplain SingleRow}. In contrast to the {@linkplain SingleRow} the
 * FoldableRow contains rows which can be collapsed or expanded. The FoldableRow itself displays the
 * sum of all foldable rows.
 *
 * @param <I> the {@linkplain QueryItem} on which the {@linkplain Report} is based on.
 */
public abstract class FoldableRow<I extends QueryItem> extends SingleRow<I> {

  private final Map<String, Row<I>> foldableRows = new LinkedHashMap<>();
  private final Function<? super I, String> calcFoldableRowKey;
  private final int columnSize;

  protected FoldableRow(
      String key, int columnSize, Function<? super I, String> calcFoldableRowKey) {
    super(key, columnSize);
    this.columnSize = columnSize;
    this.calcFoldableRowKey = calcFoldableRowKey;
  }

  public final int getFoldableRowCount() {
    return foldableRows.size();
  }

  public final Set<String> getFoldableRowKeySet() {
    return foldableRows.keySet();
  }

  @Override
  public void addItem(I item, int index) throws IndexOutOfBoundsException {
    super.addItem(item, index);
    foldableRows
        .computeIfAbsent(calcFoldableRowKey.apply(item), key -> buildRow(key, columnSize))
        .addItem(item, index);
  }

  @Override
  public void updateTotalValue(I item) {
    super.updateTotalValue(item);
    foldableRows
        .computeIfAbsent(calcFoldableRowKey.apply(item), key -> buildRow(key, columnSize))
        .updateTotalValue(item);
  }

  @Override
  public void setDisplayName(Map<String, String> displayMap) {
    super.setDisplayName(displayMap);
    foldableRows.values().forEach(row -> row.setDisplayName(displayMap));
  }

  public Row<I> getFoldableRow(String key) {
    return foldableRows.get(key);
  }

  public Map<String, Row<I>> getFoldableRows() {
    return foldableRows;
  }

  protected abstract Row<I> buildRow(String key, int columnSize);

  @Override
  public String toString() {
    return String.format(
        "FoldableRow [detailRows= %s, columnSize= %d]", this.foldableRows, columnSize);
  }
}
