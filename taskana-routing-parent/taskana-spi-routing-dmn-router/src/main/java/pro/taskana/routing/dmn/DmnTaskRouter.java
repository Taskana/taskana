package pro.taskana.routing.dmn;

import java.io.FileInputStream;
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

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.util.FileLoaderUtil;
import pro.taskana.common.internal.util.Pair;
import pro.taskana.spi.routing.api.TaskRoutingProvider;
import pro.taskana.task.api.models.Task;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

public class DmnTaskRouter implements TaskRoutingProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(DmnTaskRouter.class);
  private static final String DECISION_ID = "workbasketRouting";
  private static final String DECISION_VARIABLE_MAP_NAME = "task";
  private static final String OUTPUT_WORKBASKET_KEY = "workbasketKey";
  private static final String OUTPUT_DOMAIN = "domain";
  private static final String DMN_TABLE_PROPERTY = "taskana.routing.dmn";
  private TaskanaEngine taskanaEngine;
  private DmnEngine dmnEngine;
  private DmnDecision decision;

  @Override
  public void initialize(TaskanaEngine taskanaEngine) {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entering initialize()");
    }

    this.taskanaEngine = taskanaEngine;
    dmnEngine = DmnEngineConfiguration.createDefaultDmnEngineConfiguration().buildEngine();

    DmnModelInstance dmnModel = readModelFromDmnTable();

    decision = dmnEngine.parseDecision(DECISION_ID, dmnModel);

    validateOutputs(dmnModel);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exiting initialize()");
    }
  }

  @Override
  public String determineWorkbasketId(Task task) {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entering determineWorkbasketId(task = {})", task);
    }

    VariableMap variables = Variables.putValue(DECISION_VARIABLE_MAP_NAME, task);

    DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(decision, variables);

    if (result.getSingleResult() == null) {
      return null;
    }

    String workbasketKey = result.getSingleResult().getEntry(OUTPUT_WORKBASKET_KEY);
    String domain = result.getSingleResult().getEntry(OUTPUT_DOMAIN);

    try {

      String determinedWorkbasketId =
          taskanaEngine.getWorkbasketService().getWorkbasket(workbasketKey, domain).getId();
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            String.format("Exiting determineWorkbasketId, returning %s", determinedWorkbasketId));
      }
      return determinedWorkbasketId;
    } catch (WorkbasketNotFoundException e) {
      throw new SystemException(
          String.format(
              "Unknown workbasket defined in DMN Table. key: '%s', domain: '%s'",
              workbasketKey, domain));
    } catch (NotAuthorizedException e) {
      throw new SystemException(
          String.format(
              "The current user is not authorized to create a task in the routed workbasket. "
                  + "key: '%s', domain: '%s'",
              workbasketKey, domain));
    }
  }

  protected Set<Pair<String, String>> getAllWorkbasketAndDomainOutputs(DmnModelInstance dmnModel) {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entering getAllWorkbasketAndDomainOutputs()");
    }

    Set<Pair<String, String>> allWorkbasketAndDomainOutputs = new HashSet<>();

    for (Rule rule : dmnModel.getModelElementsByType(Rule.class)) {

      List<OutputEntry> outputEntries = new ArrayList<>(rule.getOutputEntries());
      String workbasketKey = outputEntries.get(0).getTextContent();
      String domain = outputEntries.get(1).getTextContent();

      allWorkbasketAndDomainOutputs.add(Pair.of(workbasketKey, domain));
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exiting getAllWorkbasketAndDomainOutputs()");
    }

    return allWorkbasketAndDomainOutputs;
  }

  protected DmnModelInstance readModelFromDmnTable() {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entering readModelFromDmnTable()");
    }

    String pathToDmn =
        taskanaEngine.getConfiguration().readPropertiesFromFile().getProperty(DMN_TABLE_PROPERTY);

    if (FileLoaderUtil.loadFromClasspath(pathToDmn)) {
      try (InputStream inputStream = DmnTaskRouter.class.getResourceAsStream(pathToDmn)) {
        if (inputStream == null) {
          LOGGER.error("dmn file {} was not found on classpath.", pathToDmn);
        } else {
          return Dmn.readModelFromStream(inputStream);
        }
      } catch (IOException e) {
        LOGGER.error("caught IOException when processing dmn file");
        throw new SystemException("Internal System error when processing dmn file", e.getCause());
      }
    }

    try (FileInputStream inputStream = new FileInputStream(pathToDmn)) {
      return Dmn.readModelFromStream(inputStream);
    } catch (IOException e) {
      throw new SystemException(
          String.format("Could not find a dmn file with provided path %s", pathToDmn));
    }
  }

  private void validateOutputs(DmnModelInstance dmnModel) {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entering validateOutputs()");
    }

    Set<Pair<String, String>> allWorkbasketAndDomainOutputs =
        getAllWorkbasketAndDomainOutputs(dmnModel);

    validate(allWorkbasketAndDomainOutputs);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exiting validateOutputs()");
    }
  }

  private void validate(Set<Pair<String, String>> allWorkbasketAndDomainOutputs) {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Entering validate(allWorkbasketAndDomainOutputs = {}", allWorkbasketAndDomainOutputs);
    }

    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    for (Pair<String, String> pair : allWorkbasketAndDomainOutputs) {
      String workbasketKey = pair.getLeft().replace("\"", "");
      String domain = pair.getRight().replace("\"", "");
      // This can be replaced with a workbasketQuery call.
      // Unfortunately the WorkbasketQuery does not support a keyDomainIn operation.
      // Therefore we fetch every workbasket separately

      taskanaEngine.runAsAdmin(
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

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exiting validate()");
    }
  }
}
