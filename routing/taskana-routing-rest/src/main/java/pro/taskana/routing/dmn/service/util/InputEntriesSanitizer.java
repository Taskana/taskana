package pro.taskana.routing.dmn.service.util;

import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.instance.Rule;
import org.camunda.bpm.model.dmn.instance.Text;

/** Utility class to sanitize known regex calls inside a generated DmnModelInstance. */
public class InputEntriesSanitizer {

  private InputEntriesSanitizer() {
    throw new IllegalStateException("Utility class");
  }

  public static void sanitizeRegexInsideInputEntries(DmnModelInstance dmnModelInstance) {
    dmnModelInstance
        .getModelElementsByType(Rule.class)
        .forEach(
            rule ->
                rule.getInputEntries()
                    .forEach(
                        inputEntry -> {
                          Text input = inputEntry.getText();
                          String inputTextContent = input.getTextContent();
                          if (inputTextContent.contains("matches")
                              || inputTextContent.contains("contains")) {
                            input.setTextContent(inputTextContent.replaceAll("(^\")|(\"$)", ""));
                          }
                          inputEntry.setText(input);
                        }));
  }
}
