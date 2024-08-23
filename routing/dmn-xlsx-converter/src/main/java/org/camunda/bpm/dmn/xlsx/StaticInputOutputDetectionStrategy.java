/* Licensed under the Apache License, Version 2.0 (the "License");
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
 */
package org.camunda.bpm.dmn.xlsx;

import java.util.List;
import java.util.Set;
import org.camunda.bpm.dmn.xlsx.api.Spreadsheet;
import org.camunda.bpm.dmn.xlsx.api.SpreadsheetCell;
import org.camunda.bpm.dmn.xlsx.api.SpreadsheetRow;
import org.camunda.bpm.dmn.xlsx.elements.HeaderValuesContainer;
import org.camunda.bpm.model.dmn.HitPolicy;

/**
 * @author Thorben Lindhauer
 */
public class StaticInputOutputDetectionStrategy extends BaseAdapter {

  protected Set<String> inputColumns;
  protected Set<String> outputColumns;

  public StaticInputOutputDetectionStrategy(Set<String> inputColumns, Set<String> outputColumns) {
    this.inputColumns = inputColumns;
    this.outputColumns = outputColumns;
  }

  public InputOutputColumns determineInputOutputs(Spreadsheet context) {

    SpreadsheetRow headerRow = context.getRows().get(0);

    InputOutputColumns columns = new InputOutputColumns();

    HeaderValuesContainer hvc;
    for (SpreadsheetCell cell : headerRow.getCells()) {
      if (inputColumns.contains(cell.getColumn())) {
        hvc = new HeaderValuesContainer();
        fillHvc(cell, context, hvc);
        hvc.setId("Input" + cell.getColumn());
        columns.addInputHeader(hvc);
      } else if (outputColumns.contains(cell.getColumn())) {
        hvc = new HeaderValuesContainer();
        fillHvc(cell, context, hvc);
        hvc.setId("Output" + cell.getColumn());
        columns.addOutputHeader(hvc);
      }
    }

    return columns;
  }

  @Override
  public HitPolicy determineHitPolicy(Spreadsheet context) {
    return null;
  }

  @Override
  public List<SpreadsheetRow> determineRuleRows(Spreadsheet context) {
    List<SpreadsheetRow> rows = context.getRows();
    return rows.subList(1, rows.size());
  }

  private void fillHvc(SpreadsheetCell cell, Spreadsheet context, HeaderValuesContainer hvc) {
    hvc.setText(context.resolveCellContent(cell));
    hvc.setColumn(cell.getColumn());
  }
}
