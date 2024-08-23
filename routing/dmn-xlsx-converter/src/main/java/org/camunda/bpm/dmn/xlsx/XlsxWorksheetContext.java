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

import java.util.ArrayList;
import java.util.List;
import org.camunda.bpm.dmn.xlsx.api.Spreadsheet;
import org.camunda.bpm.dmn.xlsx.api.SpreadsheetCell;
import org.camunda.bpm.dmn.xlsx.api.SpreadsheetRow;
import org.camunda.bpm.dmn.xlsx.elements.IndexedRow;
import org.xlsx4j.sml.CTRst;
import org.xlsx4j.sml.CTSst;
import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.STCellType;
import org.xlsx4j.sml.Worksheet;

/**
 * @author Thorben Lindhauer
 */
public class XlsxWorksheetContext implements Spreadsheet {

  protected List<CellContentHandler> cellContentHandlers;
  protected CTSst sharedStrings;
  protected Worksheet worksheet;
  protected String worksheetName;

  // cached state
  protected List<SpreadsheetRow> indexedRows;

  public XlsxWorksheetContext(CTSst sharedStrings, Worksheet worksheet, String worksheetName) {
    this.sharedStrings = sharedStrings;
    this.worksheet = worksheet;
    this.cellContentHandlers = new ArrayList<>();
    this.worksheetName = worksheetName;
  }

  public List<SpreadsheetRow> getRows() {
    if (indexedRows == null) {
      indexedRows = new ArrayList<>();
      for (Row row : worksheet.getSheetData().getRow()) {
        indexedRows.add(new IndexedRow(row));
      }
    }
    return indexedRows;
  }

  public String resolveSharedString(int index) {
    List<CTRst> siElements = sharedStrings.getSi();
    return siElements.get(index).getT().getValue();
  }

  @Override
  public String resolveCellContent(SpreadsheetCell cell) {
    Cell rawCell = cell.getRaw();

    STCellType cellType = rawCell.getT();
    if (STCellType.S.equals(cellType)) {
      int sharedStringIndex = Integer.parseInt(rawCell.getV());
      return resolveSharedString(sharedStringIndex);
    } else {
      return rawCell.getV();
    }
  }

  @Override
  public String getName() {
    return worksheetName;
  }
}
