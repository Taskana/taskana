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

import java.io.InputStream;
import org.camunda.bpm.dmn.xlsx.api.SpreadsheetAdapter;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.DocPropsExtendedPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.SharedStrings;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;

/**
 * @author Thorben Lindhauer
 */
public class XlsxConverter {

  public static final String DEFAULT_HISTORY_TIME_TO_LIVE = "P180D";

  protected String historyTimeToLive = DEFAULT_HISTORY_TIME_TO_LIVE;

  protected SpreadsheetAdapter ioDetectionStrategy = new SimpleInputOutputDetectionStrategy();

  public DmnModelInstance convert(InputStream inputStream) {
    SpreadsheetMLPackage spreadSheetPackage = null;
    try {
      spreadSheetPackage = SpreadsheetMLPackage.load(inputStream);
    } catch (Docx4JException e) {
      // TODO: checked exception
      throw new RuntimeException("cannot load document", e);
    }

    WorkbookPart workbookPart = spreadSheetPackage.getWorkbookPart();
    // TODO: exception when no worksheet present
    // TODO: make worksheet number configurable/import all worksheets?
    org.camunda.bpm.dmn.xlsx.XlsxWorksheetContext worksheetContext = null;

    WorksheetPart worksheetPart;
    try {
      String worksheetName;
      DocPropsExtendedPart docPropsExtendedPart = spreadSheetPackage.getDocPropsExtendedPart();
      if (docPropsExtendedPart != null
          && docPropsExtendedPart.getContents().getTitlesOfParts() != null) {
        worksheetName =
            (String)
                docPropsExtendedPart
                    .getContents()
                    .getTitlesOfParts()
                    .getVector()
                    .getVariantOrI1OrI2()
                    .get(0)
                    .getValue();
      } else {
        worksheetName = "default";
      }
      worksheetPart = workbookPart.getWorksheet(0);
      SharedStrings sharedStrings = workbookPart.getSharedStrings();
      worksheetContext =
          new XlsxWorksheetContext(
              sharedStrings.getContents(), worksheetPart.getContents(), worksheetName);
    } catch (Exception e) {
      throw new RuntimeException("Could not determine worksheet", e);
    }

    return new XlsxWorksheetConverter(worksheetContext, ioDetectionStrategy, historyTimeToLive)
        .convert();
  }

  public SpreadsheetAdapter getIoDetectionStrategy() {
    return ioDetectionStrategy;
  }

  public void setIoDetectionStrategy(SpreadsheetAdapter ioDetectionStrategy) {
    this.ioDetectionStrategy = ioDetectionStrategy;
  }

  public String getHistoryTimeToLive() {
    return historyTimeToLive;
  }

  public void setHistoryTimeToLive(String historyTimeToLive) {
    this.historyTimeToLive = historyTimeToLive;
  }
}
