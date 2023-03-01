/*-
 * #%L
 * pro.taskana:taskana-routing-rest
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
package pro.taskana.routing.dmn.service.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.instance.InputEntry;
import org.camunda.bpm.model.dmn.instance.Rule;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

/** Test InputEntriesSanitizer. */
class InputEntriesSanitizerAccTest {

  private static final String TEST_DMN = "testDmnRouting.dmn";

  @Test
  void should_SanitizeInputEntries_When_ContainingRegex() throws Exception {

    File testDmnModel = new ClassPathResource(TEST_DMN).getFile();
    DmnModelInstance dmnModelInstance = Dmn.readModelFromFile(testDmnModel);

    List<Rule> allRules =
        dmnModelInstance.getModelElementsByType(Rule.class).stream().collect(Collectors.toList());

    List<InputEntry> inputEntriesOfFirstRuleToSanitize =
        new ArrayList(allRules.get(1).getInputEntries());
    List<InputEntry> inputEntriesOfSecondRuleToSanitize =
        new ArrayList(allRules.get(2).getInputEntries());

    String inputEntryContainingMatchesFunction =
        inputEntriesOfFirstRuleToSanitize.get(1).getTextContent();

    assertThat(inputEntryContainingMatchesFunction)
        .isEqualTo("\"matches(cellInput,\"12924|12925|\")\"");

    String inputEntryContainingContainsFunction =
        inputEntriesOfSecondRuleToSanitize.get(1).getTextContent();

    assertThat(inputEntryContainingContainsFunction).isEqualTo("\"contains(\"someString\")\"");

    InputEntriesSanitizer.sanitizeRegexInsideInputEntries(dmnModelInstance);

    List<Rule> allRulesAfterSanitzing =
        dmnModelInstance.getModelElementsByType(Rule.class).stream().collect(Collectors.toList());

    List<InputEntry> inputEntriesOfFirstRuleAfterSanitizing =
        new ArrayList(allRulesAfterSanitzing.get(1).getInputEntries());

    inputEntryContainingMatchesFunction =
        inputEntriesOfFirstRuleAfterSanitizing.get(1).getTextContent();

    assertThat(inputEntryContainingMatchesFunction)
        .isEqualTo("matches(cellInput,\"12924|12925|\")");

    List<InputEntry> inputEntriesOfSecondRuleAfterSanitizing =
        new ArrayList(allRulesAfterSanitzing.get(2).getInputEntries());

    inputEntryContainingContainsFunction =
        inputEntriesOfSecondRuleAfterSanitizing.get(1).getTextContent();
    assertThat(inputEntryContainingContainsFunction).isEqualTo("contains(\"someString\")");
  }
}
