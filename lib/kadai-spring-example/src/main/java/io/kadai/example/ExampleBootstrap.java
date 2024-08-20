package io.kadai.example;

import io.kadai.classification.api.models.Classification;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.exceptions.KadaiException;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.models.Task;
import io.kadai.task.internal.models.ObjectReferenceImpl;
import io.kadai.workbasket.api.WorkbasketType;
import io.kadai.workbasket.api.models.Workbasket;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** TODO. */
@Component
@Transactional
public class ExampleBootstrap {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExampleBootstrap.class);

  private final TaskService taskService;

  private final KadaiEngine kadaiEngine;

  public ExampleBootstrap(@Autowired TaskService taskService, @Autowired KadaiEngine kadaiEngine) {
    this.taskService = taskService;
    this.kadaiEngine = kadaiEngine;
  }

  @PostConstruct
  public void postConstruct() throws Exception {
    try {
      LOGGER.info("---------------------------> Start App");

      Workbasket wb = kadaiEngine.getWorkbasketService().newWorkbasket("workbasket", "DOMAIN_A");
      wb.setName("workbasket");
      wb.setType(WorkbasketType.GROUP);
      kadaiEngine.getWorkbasketService().createWorkbasket(wb);
      Classification classification =
          kadaiEngine.getClassificationService().newClassification("TEST", "DOMAIN_A", "TASK");
      classification.setServiceLevel("P1D");
      kadaiEngine.getClassificationService().createClassification(classification);

      Task task = kadaiEngine.getTaskService().newTask(wb.getId());
      task.setName("Spring example task");
      task.setClassificationKey(classification.getKey());
      ObjectReferenceImpl objRef = new ObjectReferenceImpl();
      objRef.setCompany("aCompany");
      objRef.setSystem("aSystem");
      objRef.setSystemInstance("anInstance");
      objRef.setType("aType");
      objRef.setValue("aValue");
      task.setPrimaryObjRef(objRef);
      task = taskService.createTask(task);
      LOGGER.info("---------------------------> Task started: {}", task.getId());
      taskService.claim(task.getId());
      LOGGER.info(
          "---------------------------> Task claimed: {}",
          taskService.getTask(task.getId()).getOwner());
      taskService.forceCompleteTask(task.getId());
      LOGGER.info("---------------------------> Task completed");
    } catch (KadaiException e) {
      throw new ExampleStartupException(e);
    }
  }
}
