package io.kadai.routing.dmn;

import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.internal.util.FileLoaderUtil;
import io.kadai.common.internal.util.Pair;
import io.kadai.spi.routing.api.TaskRoutingProvider;
import io.kadai.task.api.models.Task;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
import io.kadai.workbasket.api.exceptions.WorkbasketNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.instance.OutputEntry;
import org.camunda.bpm.model.dmn.instance.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DmnTaskRouter implements TaskRoutingProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(DmnTaskRouter.class);
  private static final String DMN_TABLE_PROPERTY = "kadai.routing.dmn";

  private static final String DECISION_ID = "workbasketRouting";
  private static final String DECISION_VARIABLE_MAP_NAME = "task";
  private static final String OUTPUT_WORKBASKET_KEY = "workbasketKey";
  private static final String OUTPUT_DOMAIN = "domain";
  private KadaiEngine kadaiEngine;
  private DmnEngine dmnEngine;
  private DmnDecision decision;

  @Override
  public void initialize(KadaiEngine kadaiEngine) {
    this.kadaiEngine = kadaiEngine;
    dmnEngine = DmnEngineConfiguration.createDefaultDmnEngineConfiguration().buildEngine();

    DmnModelInstance dmnModel = readModelFromDmnTable();

    decision = dmnEngine.parseDecision(DECISION_ID, dmnModel);

    validateOutputs(dmnModel);
  }

  @Override
  public String determineWorkbasketId(Task task) {
    VariableMap variables = Variables.putValue(DECISION_VARIABLE_MAP_NAME, task);

    DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(decision, variables);

    if (result.getSingleResult() == null) {
      return null;
    }

    String workbasketKey = result.getSingleResult().getEntry(OUTPUT_WORKBASKET_KEY);
    String domain = result.getSingleResult().getEntry(OUTPUT_DOMAIN);

    try {

      return kadaiEngine.getWorkbasketService().getWorkbasket(workbasketKey, domain).getId();
    } catch (WorkbasketNotFoundException e) {
      throw new SystemException(
          String.format(
              "Unknown workbasket defined in DMN Table. key: '%s', domain: '%s'",
              workbasketKey, domain));
    } catch (NotAuthorizedOnWorkbasketException e) {
      throw new SystemException(
          String.format(
              "The current user is not authorized to create a task in the routed workbasket. "
                  + "key: '%s', domain: '%s'",
              workbasketKey, domain));
    }
  }

  protected Set<Pair<String, String>> getAllWorkbasketAndDomainOutputs(DmnModelInstance dmnModel) {
    Set<Pair<String, String>> allWorkbasketAndDomainOutputs = new HashSet<>();

    for (Rule rule : dmnModel.getModelElementsByType(Rule.class)) {

      List<OutputEntry> outputEntries = new ArrayList<>(rule.getOutputEntries());
      String workbasketKey = outputEntries.get(0).getTextContent();
      String domain = outputEntries.get(1).getTextContent();

      allWorkbasketAndDomainOutputs.add(Pair.of(workbasketKey, domain));
    }
    return allWorkbasketAndDomainOutputs;
  }

  protected DmnModelInstance readModelFromDmnTable() {
    String pathToDmn = kadaiEngine.getConfiguration().getProperties().get(DMN_TABLE_PROPERTY);
    try (InputStream stream = FileLoaderUtil.openFileFromClasspathOrSystem(pathToDmn, getClass())) {
      return Dmn.readModelFromStream(stream);
    } catch (IOException e) {
      LOGGER.error("caught IOException when processing dmn file {}.", pathToDmn);
      throw new SystemException(
          "internal System error when processing dmn file " + pathToDmn, e.getCause());
    }
  }

  private void validateOutputs(DmnModelInstance dmnModel) {
    Set<Pair<String, String>> allWorkbasketAndDomainOutputs =
        getAllWorkbasketAndDomainOutputs(dmnModel);

    validate(allWorkbasketAndDomainOutputs);
  }

  private void validate(Set<Pair<String, String>> allWorkbasketAndDomainOutputs) {
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();

    for (Pair<String, String> pair : allWorkbasketAndDomainOutputs) {
      String workbasketKey = pair.getLeft().replace("\"", "");
      String domain = pair.getRight().replace("\"", "");
      // This can be replaced with a workbasketQuery call.
      // Unfortunately the WorkbasketQuery does not support a keyDomainIn operation.
      // Therefore we fetch every workbasket separately

      kadaiEngine.runAsAdmin(
          () -> {
            try {
              return workbasketService.getWorkbasket(workbasketKey, domain);
            } catch (Exception e) {
              throw new SystemException(
                  String.format(
                      "Unknown workbasket defined in DMN Table. key: '%s', domain: '%s'",
                      workbasketKey, domain),
                  e);
            }
          });
    }
  }
}
