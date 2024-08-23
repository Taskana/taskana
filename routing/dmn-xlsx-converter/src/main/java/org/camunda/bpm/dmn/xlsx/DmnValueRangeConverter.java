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

import java.util.regex.Pattern;
import org.camunda.bpm.dmn.xlsx.api.Spreadsheet;
import org.camunda.bpm.dmn.xlsx.api.SpreadsheetCell;
import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.STCellType;

/**
 * @author Thorben Lindhauer
 */
public class DmnValueRangeConverter implements CellContentHandler {

  public static final Pattern RANGE_REGEX =
      Pattern.compile(
          "[\\[\\]](?:[0-9.]+|(?:date and time\\(.+\\)))\\.\\.(?:[0-9.]+|(?:date and time\\(.+\\)))[\\[\\]]");

  @Override
  public boolean canConvert(SpreadsheetCell cell, Spreadsheet context) {
    Cell rawCell = cell.getRaw();

    if (STCellType.S.equals(rawCell.getT())) {
      String content = context.resolveCellContent(cell);
      return RANGE_REGEX.matcher(content).matches();
    } else {
      return false;
    }
  }

  @Override
  public String convert(SpreadsheetCell cell, Spreadsheet context) {
    return context.resolveCellContent(cell);
  }
}
