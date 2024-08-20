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
 *
 * Copied from the project https://github.com/camunda-community-hub/camunda-dmn-xlsx
 */

package io.kadai.routing.dmn.service;

import java.io.InputStream;
import org.camunda.bpm.dmn.xlsx.SimpleInputOutputDetectionStrategy;
import org.camunda.bpm.dmn.xlsx.XlsxWorksheetContext;
import org.camunda.bpm.dmn.xlsx.api.SpreadsheetAdapter;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.DocPropsExtendedPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.SharedStrings;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;

/**
 * Converter for XLSX files to DMN 1.1 decision tables.
 *
 * @author Thorben Lindhauer
 * @author Holger Hagen
 */
public class XlsxConverter {
  protected SpreadsheetAdapter ioDetectionStrategy = new SimpleInputOutputDetectionStrategy();

  public XlsxConverter() {}

  public DmnModelInstance convert(InputStream inputStream, StringBuilder serializedRules) {
    SpreadsheetMLPackage spreadSheetPackage;

    try {
      spreadSheetPackage = SpreadsheetMLPackage.load(inputStream);
    } catch (Docx4JException e) {
      throw new RuntimeException("cannot load document", e);
    }

    WorkbookPart workbookPart = spreadSheetPackage.getWorkbookPart();
    XlsxWorksheetContext worksheetContext;

    try {
      DocPropsExtendedPart docPropsExtendedPart = spreadSheetPackage.getDocPropsExtendedPart();
      String worksheetName;
      if (docPropsExtendedPart != null
          && (docPropsExtendedPart.getContents()).getTitlesOfParts() != null) {
        worksheetName =
            (String)
                ((docPropsExtendedPart.getContents())
                        .getTitlesOfParts()
                        .getVector()
                        .getVariantOrI1OrI2()
                        .get(0))
                    .getValue();
      } else {
        worksheetName = "default";
      }

      WorksheetPart worksheetPart = workbookPart.getWorksheet(0);
      SharedStrings sharedStrings = workbookPart.getSharedStrings();
      worksheetContext =
          new XlsxWorksheetContext(
              sharedStrings.getContents(), worksheetPart.getContents(), worksheetName);
    } catch (Exception e) {
      throw new RuntimeException("Could not determine worksheet", e);
    }

    XlsxWorksheetConverter converter =
        new XlsxWorksheetConverter(worksheetContext, this.ioDetectionStrategy);
    return converter.convert(serializedRules);
  }

  public void setIoDetectionStrategy(SpreadsheetAdapter ioDetectionStrategy) {
    this.ioDetectionStrategy = ioDetectionStrategy;
  }
}
