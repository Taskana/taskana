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
import org.camunda.bpm.dmn.xlsx.BaseAdapter;
import org.camunda.bpm.dmn.xlsx.InputOutputColumns;
import org.camunda.bpm.dmn.xlsx.api.Spreadsheet;
import org.camunda.bpm.dmn.xlsx.api.SpreadsheetCell;
import org.camunda.bpm.dmn.xlsx.api.SpreadsheetRow;
import org.camunda.bpm.dmn.xlsx.elements.HeaderValuesContainer;
import org.camunda.bpm.model.dmn.HitPolicy;

/**
 * @author Thorben Lindhauer
 */
public class SimpleInputOutputDetectionStrategy extends BaseAdapter {

  public org.camunda.bpm.dmn.xlsx.InputOutputColumns determineInputOutputs(Spreadsheet context) {

    SpreadsheetRow headerRow = context.getRows().get(0);

    if (!headerRow.hasCells()) {
      throw new RuntimeException(
          "A dmn table requires at least one output; the header row contains no entries");
    }

    org.camunda.bpm.dmn.xlsx.InputOutputColumns ioColumns = new InputOutputColumns();

    List<SpreadsheetCell> cells = headerRow.getCells();
    HeaderValuesContainer hvc = new HeaderValuesContainer();
    SpreadsheetCell outputCell = cells.get(cells.size() - 1);
    fillHvc(outputCell, context, hvc);
    hvc.setId("Output" + outputCell.getColumn());

    ioColumns.addOutputHeader(hvc);

    for (SpreadsheetCell inputCell : cells.subList(0, cells.size() - 1)) {
      hvc = new HeaderValuesContainer();
      fillHvc(inputCell, context, hvc);
      hvc.setId("Input" + inputCell.getColumn());
      ioColumns.addInputHeader(hvc);
    }

    return ioColumns;
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
