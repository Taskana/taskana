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

import java.util.List;
import org.camunda.bpm.dmn.xlsx.CellContentHandler;
import org.camunda.bpm.dmn.xlsx.DmnConversionContext;
import org.camunda.bpm.dmn.xlsx.DmnValueNumberConverter;
import org.camunda.bpm.dmn.xlsx.DmnValueRangeConverter;
import org.camunda.bpm.dmn.xlsx.DmnValueStringConverter;
import org.camunda.bpm.dmn.xlsx.FeelSimpleUnaryTestConverter;
import org.camunda.bpm.dmn.xlsx.InputOutputColumns;
import org.camunda.bpm.dmn.xlsx.XlsxWorksheetContext;
import org.camunda.bpm.dmn.xlsx.api.SpreadsheetAdapter;
import org.camunda.bpm.dmn.xlsx.api.SpreadsheetCell;
import org.camunda.bpm.dmn.xlsx.api.SpreadsheetRow;
import org.camunda.bpm.dmn.xlsx.elements.HeaderValuesContainer;
import org.camunda.bpm.dmn.xlsx.elements.IndexedDmnColumns;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.HitPolicy;
import org.camunda.bpm.model.dmn.impl.DmnModelConstants;
import org.camunda.bpm.model.dmn.instance.Decision;
import org.camunda.bpm.model.dmn.instance.DecisionTable;
import org.camunda.bpm.model.dmn.instance.Definitions;
import org.camunda.bpm.model.dmn.instance.Description;
import org.camunda.bpm.model.dmn.instance.DmnElement;
import org.camunda.bpm.model.dmn.instance.Input;
import org.camunda.bpm.model.dmn.instance.InputEntry;
import org.camunda.bpm.model.dmn.instance.InputExpression;
import org.camunda.bpm.model.dmn.instance.NamedElement;
import org.camunda.bpm.model.dmn.instance.Output;
import org.camunda.bpm.model.dmn.instance.OutputEntry;
import org.camunda.bpm.model.dmn.instance.Rule;
import org.camunda.bpm.model.dmn.instance.Text;

/**
 * Converter for XLSX worksheets to DMN 1.1 decision tables.
 *
 * @author Thorben Lindhauer
 * @author Holger Hagen
 */
public class XlsxWorksheetConverter {

  static {
    CellContentHandler.DEFAULT_HANDLERS.add(new DmnValueRangeConverter());
    CellContentHandler.DEFAULT_HANDLERS.add(new FeelSimpleUnaryTestConverter());
    CellContentHandler.DEFAULT_HANDLERS.add(new DmnValueStringConverter());
    CellContentHandler.DEFAULT_HANDLERS.add(new DmnValueNumberConverter());
  }

  protected XlsxWorksheetContext worksheetContext;
  protected DmnConversionContext dmnConversionContext;
  protected SpreadsheetAdapter spreadsheetAdapter;

  public XlsxWorksheetConverter(
      XlsxWorksheetContext worksheetContext, SpreadsheetAdapter spreadsheetAdapter) {
    this.worksheetContext = worksheetContext;
    this.dmnConversionContext =
        new DmnConversionContext(
            worksheetContext, spreadsheetAdapter.getCellContentHandlers(worksheetContext));
    this.spreadsheetAdapter = spreadsheetAdapter;
  }

  public DmnModelInstance convert(StringBuilder serializedRules) {

    DmnModelInstance dmnModel = initializeEmptyDmnModel();

    Decision decision = generateElement(dmnModel, Decision.class, worksheetContext.getName());
    decision.setName(spreadsheetAdapter.determineDecisionName(worksheetContext));
    dmnModel.getDefinitions().addChildElement(decision);

    DecisionTable decisionTable = generateElement(dmnModel, DecisionTable.class, "decisionTable");
    decision.addChildElement(decisionTable);

    setHitPolicy(decisionTable);
    convertInputsOutputs(dmnModel, decisionTable);
    convertRules(
        dmnModel,
        decisionTable,
        spreadsheetAdapter.determineRuleRows(worksheetContext),
        serializedRules);

    return dmnModel;
  }

  public <E extends NamedElement> E generateNamedElement(
      DmnModelInstance modelInstance, Class<E> elementClass, String name) {
    E element = generateElement(modelInstance, elementClass, name);
    element.setName(name);
    return element;
  }

  public <E extends DmnElement> E generateElement(
      DmnModelInstance modelInstance, Class<E> elementClass, String id) {
    E element = modelInstance.newInstance(elementClass);
    element.setId(id);
    return element;
  }

  public <E extends DmnElement> E generateElement(
      DmnModelInstance modelInstance, Class<E> elementClass) {
    // TODO: use a proper generator for random IDs
    String generatedId = elementClass.getSimpleName() + (int) (Integer.MAX_VALUE * Math.random());
    return generateElement(modelInstance, elementClass, generatedId);
  }

  protected void setHitPolicy(DecisionTable decisionTable) {
    HitPolicy hitPolicy = spreadsheetAdapter.determineHitPolicy(worksheetContext);
    if (hitPolicy != null) {
      decisionTable.setHitPolicy(hitPolicy);
    }
  }

  protected void convertInputsOutputs(DmnModelInstance dmnModel, DecisionTable decisionTable) {

    InputOutputColumns inputOutputColumns =
        spreadsheetAdapter.determineInputOutputs(worksheetContext);

    // inputs
    for (HeaderValuesContainer hvc : inputOutputColumns.getInputHeaders()) {
      Input input = generateElement(dmnModel, Input.class, hvc.getId());
      decisionTable.addChildElement(input);

      // mandatory
      InputExpression inputExpression = generateElement(dmnModel, InputExpression.class);
      Text text = generateText(dmnModel, hvc.getText());
      inputExpression.setText(text);
      input.setInputExpression(inputExpression);

      // optionals
      if (hvc.getLabel() != null) {
        input.setLabel(hvc.getLabel());
      }
      if (hvc.getTypeRef() != null) {
        inputExpression.setTypeRef(hvc.getTypeRef());
      }
      if (hvc.getExpressionLanguage() != null) {
        inputExpression.setExpressionLanguage(hvc.getExpressionLanguage());
      }

      dmnConversionContext.getIndexedDmnColumns().addInput(hvc.getColumn(), input);
    }

    // outputs
    for (HeaderValuesContainer hvc : inputOutputColumns.getOutputHeaders()) {
      Output output = generateElement(dmnModel, Output.class, hvc.getId());
      decisionTable.addChildElement(output);

      // mandatory
      output.setName(hvc.getText());

      // optionals
      if (hvc.getLabel() != null) {
        output.setLabel(hvc.getLabel());
      }
      if (hvc.getTypeRef() != null) {
        output.setTypeRef(hvc.getTypeRef());
      }

      dmnConversionContext.getIndexedDmnColumns().addOutput(hvc.getColumn(), output);
    }
  }

  protected void convertRules(
      DmnModelInstance dmnModel,
      DecisionTable decisionTable,
      List<SpreadsheetRow> rulesRows,
      StringBuilder serializedRules) {
    for (SpreadsheetRow rule : rulesRows) {
      convertRule(dmnModel, decisionTable, rule, serializedRules);
    }
  }

  protected void convertRule(
      DmnModelInstance dmnModel,
      DecisionTable decisionTable,
      SpreadsheetRow ruleRow,
      StringBuilder serializedRules) {
    Rule rule = generateElement(dmnModel, Rule.class, "excelRow" + ruleRow.getRaw().getR());
    // decisionTable.addChildElement(rule);

    IndexedDmnColumns dmnColumns = dmnConversionContext.getIndexedDmnColumns();

    for (Input input : dmnColumns.getOrderedInputs()) {
      String xlsxColumn = dmnColumns.getSpreadsheetColumn(input);
      SpreadsheetCell cell = ruleRow.getCell(xlsxColumn);
      String coordinate = xlsxColumn + ruleRow.getRaw().getR();

      InputEntry inputEntry = generateElement(dmnModel, InputEntry.class, coordinate);
      String textValue =
          cell != null ? dmnConversionContext.resolveCellValue(cell) : getDefaultCellContent();
      Text text = generateText(dmnModel, textValue);
      inputEntry.setText(text);
      rule.addChildElement(inputEntry);
    }

    for (Output output : dmnColumns.getOrderedOutputs()) {
      String xlsxColumn = dmnColumns.getSpreadsheetColumn(output);
      SpreadsheetCell cell = ruleRow.getCell(xlsxColumn);
      String coordinate = xlsxColumn + ruleRow.getRaw().getR();

      OutputEntry outputEntry = generateElement(dmnModel, OutputEntry.class, coordinate);
      String textValue =
          cell != null ? dmnConversionContext.resolveCellValue(cell) : getDefaultCellContent();
      Text text = generateText(dmnModel, textValue);
      outputEntry.setText(text);
      rule.addChildElement(outputEntry);
    }

    SpreadsheetCell annotationCell = ruleRow.getCells().get(ruleRow.getCells().size() - 1);
    Description description =
        generateDescription(dmnModel, worksheetContext.resolveCellContent(annotationCell));
    rule.setDescription(description);

    serializeRuleToStringBuilder(rule, ruleRow, description, serializedRules);
  }

  protected void serializeRuleToStringBuilder(
      Rule rule, SpreadsheetRow ruleRow, Description description, StringBuilder serializedRules) {
    serializedRules
        .append("<rule id=\"excelRow")
        .append(ruleRow.getRaw().getR())
        .append("\">\n<description>")
        .append(description.getTextContent())
        .append("</description>\n");
    for (InputEntry inputEntry : rule.getInputEntries()) {
      serializedRules
          .append("<inputEntry id=\"")
          .append(inputEntry.getId())
          .append("\">\n<text>")
          .append(inputEntry.getText().getTextContent())
          .append("</text>\n</inputEntry>\n");
    }
    for (OutputEntry outputEntry : rule.getOutputEntries()) {
      serializedRules
          .append("<outputEntry id=\"")
          .append(outputEntry.getId())
          .append("\">\n<text>")
          .append(outputEntry.getText().getTextContent())
          .append("</text>\n</outputEntry>\n");
    }
    serializedRules.append("</rule>\n");
  }

  protected String getDefaultCellContent() {
    return "-";
  }

  protected DmnModelInstance initializeEmptyDmnModel() {
    DmnModelInstance dmnModel = Dmn.createEmptyModel();
    Definitions definitions = generateNamedElement(dmnModel, Definitions.class, "definitions");
    definitions.setNamespace(DmnModelConstants.CAMUNDA_NS);
    dmnModel.setDefinitions(definitions);

    return dmnModel;
  }

  protected Text generateText(DmnModelInstance dmnModel, String content) {
    Text text = dmnModel.newInstance(Text.class);
    text.setTextContent(content);
    return text;
  }

  protected Description generateDescription(DmnModelInstance dmnModel, String content) {
    Description description = dmnModel.newInstance(Description.class);
    description.setTextContent(content);
    return description;
  }
}
