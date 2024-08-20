package io.kadai.routing.dmn.service.util;

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
