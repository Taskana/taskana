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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.util.Iterator;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.HitPolicy;
import org.camunda.bpm.model.dmn.instance.DecisionTable;
import org.camunda.bpm.model.dmn.instance.Input;
import org.camunda.bpm.model.dmn.instance.InputEntry;
import org.camunda.bpm.model.dmn.instance.Rule;
import org.junit.jupiter.api.Test;

/**
 * @author Thorben Lindhauer
 */
public class XslxToDmnConversionTest {

  public static final String DMN_11_NAMESPACE = "https://www.omg.org/spec/DMN/20191111/MODEL/";

  private static final String JAVASCRIPT_SNIPPET =
      "if (exp1 % 2 == 0)\n" + "    {erg = 2;}\n" + "else\n" + "    {erg = 1;}\n" + "erg;";

  // TODO: assert input entry text content

  @Test
  public void testSimpleConversion() {
    XlsxConverter converter = new XlsxConverter();
    InputStream inputStream = TestHelper.getClassPathResource("test1.xlsx");
    DmnModelInstance dmnModelInstance = converter.convert(inputStream);
    assertThat(dmnModelInstance).isNotNull();

    DecisionTable table = TestHelper.assertAndGetSingleDecisionTable(dmnModelInstance);
    assertThat(table).isNotNull();
    assertThat(table.getInputs()).hasSize(2);
    assertThat(table.getOutputs()).hasSize(1);
    assertThat(table.getRules()).hasSize(4);
  }

  @Test
  public void testConversionOfMixedNumberAndStringColumns() {
    XlsxConverter converter = new XlsxConverter();
    InputStream inputStream = TestHelper.getClassPathResource("test2.xlsx");
    DmnModelInstance dmnModelInstance = converter.convert(inputStream);
    assertThat(dmnModelInstance).isNotNull();

    DecisionTable table = TestHelper.assertAndGetSingleDecisionTable(dmnModelInstance);
    assertThat(table).isNotNull();
    assertThat(table.getInputs()).hasSize(3);
    assertThat(table.getOutputs()).hasSize(1);
    assertThat(table.getRules()).hasSize(4);
  }

  @Test
  public void testConversionOfEmptyCells() {
    XlsxConverter converter = new XlsxConverter();
    InputStream inputStream = TestHelper.getClassPathResource("test3.xlsx");
    DmnModelInstance dmnModelInstance = converter.convert(inputStream);
    assertThat(dmnModelInstance).isNotNull();

    DecisionTable table = TestHelper.assertAndGetSingleDecisionTable(dmnModelInstance);
    assertThat(table).isNotNull();
    assertThat(table.getInputs()).hasSize(3);
    assertThat(table.getOutputs()).hasSize(1);
    assertThat(table.getRules()).hasSize(4);
  }

  @Test
  public void testDmnNamespace() {
    XlsxConverter converter = new XlsxConverter();
    InputStream inputStream = TestHelper.getClassPathResource("test1.xlsx");
    DmnModelInstance dmnModelInstance = converter.convert(inputStream);

    assertThat(dmnModelInstance.getDefinitions().getDomElement().getNamespaceURI())
        .isEqualTo(DMN_11_NAMESPACE);
  }

  @Test
  public void testConversionOfNullTitleOfParts() {
    XlsxConverter converter = new XlsxConverter();
    InputStream inputStream = TestHelper.getClassPathResource("test4.xlsx");
    DmnModelInstance dmnModelInstance = converter.convert(inputStream);
    assertThat(dmnModelInstance).isNotNull();

    DecisionTable table = TestHelper.assertAndGetSingleDecisionTable(dmnModelInstance);

    assertThat(table).isNotNull();
    assertThat(table.getInputs()).hasSize(2);
    assertThat(table.getOutputs()).hasSize(1);
    assertThat(table.getRules()).hasSize(1);
  }

  @Test
  public void testConversionWithRanges() {
    XlsxConverter converter = new XlsxConverter();
    InputStream inputStream = TestHelper.getClassPathResource("test5.xlsx");
    DmnModelInstance dmnModelInstance = converter.convert(inputStream);
    assertThat(dmnModelInstance).isNotNull();

    DecisionTable table = TestHelper.assertAndGetSingleDecisionTable(dmnModelInstance);
    assertThat(table).isNotNull();
    assertThat(table.getInputs()).hasSize(1);
    assertThat(table.getOutputs()).hasSize(1);
    assertThat(table.getRules()).hasSize(4);

    Rule firstRule = table.getRules().iterator().next();

    InputEntry inputEntry = firstRule.getInputEntries().iterator().next();
    String firstInput = inputEntry.getTextContent();
    assertThat(firstInput).isEqualTo("[1..2]");
  }

  @Test
  public void testConversionWithComplexHeaders() {
    XlsxConverter converter = new XlsxConverter();
    converter.setIoDetectionStrategy(new AdvancedSpreadsheetAdapter());
    InputStream inputStream = TestHelper.getClassPathResource("test6.xlsx");
    DmnModelInstance dmnModelInstance = converter.convert(inputStream);
    assertThat(dmnModelInstance).isNotNull();

    DecisionTable table = TestHelper.assertAndGetSingleDecisionTable(dmnModelInstance);
    assertThat(table).isNotNull();
    assertThat(table.getInputs()).hasSize(2);
    assertThat(table.getOutputs()).hasSize(2);
    assertThat(table.getRules()).hasSize(2);
    assertThat(table.getHitPolicy()).isEqualTo(HitPolicy.FIRST);

    Iterator<Input> inputIterator = table.getInputs().iterator();
    Input input = inputIterator.next();

    assertThat(input.getId()).isEqualTo("input1");
    assertThat(input.getLabel()).isEqualTo("InputLabel1");
    assertThat(input.getInputExpression().getTypeRef()).isEqualTo("string");
    assertThat(input.getTextContent()).isEqualTo("Exp1");

    input = inputIterator.next();
    assertThat(input.getId()).isEqualTo("input2");
    assertThat(input.getLabel()).isEqualTo("InputLabel2");
    assertThat(input.getInputExpression().getTypeRef()).isEqualTo("integer");
    assertThat(input.getInputExpression().getExpressionLanguage()).isEqualTo("javascript");
    assertThat(input.getInputExpression().getTextContent()).isEqualTo(JAVASCRIPT_SNIPPET);

    Iterator<Rule> ruleIterator = table.getRules().iterator();
    Rule rule = ruleIterator.next();
    assertThat(rule.getDescription().getTextContent()).isEqualTo("Comment1");

    InputEntry inputEntry = rule.getInputEntries().iterator().next();
    assertThat(inputEntry.getTextContent()).isEqualTo("\"Foo\"");

    rule = ruleIterator.next();
    assertThat(rule.getDescription().getTextContent()).isEqualTo("Another Comment");
  }
}
