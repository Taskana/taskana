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
package org.camunda.bpm.dmn.xlsx.elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.camunda.bpm.dmn.xlsx.api.SpreadsheetCell;
import org.xlsx4j.sml.Cell;

/**
 * @author Thorben Lindhauer
 */
public class IndexedCell implements SpreadsheetCell {

  public static final Pattern CELL_REF_PATTERN = Pattern.compile("([A-Z]+)([0-9]+)");

  protected Cell cell;
  protected String column;
  protected int row;

  public IndexedCell(Cell cell) {
    this.cell = cell;

    String cellReference = cell.getR();
    Matcher matcher = CELL_REF_PATTERN.matcher(cellReference);

    boolean matches = matcher.matches();
    if (!matches) {
      throw new RuntimeException("Cannot parse cell reference " + cellReference);
    }

    column = matcher.group(1);
    row = Integer.parseInt(matcher.group(2));
  }

  public Cell getRaw() {
    return cell;
  }

  public String getColumn() {
    return column;
  }

  public int getRow() {
    return row;
  }
}
