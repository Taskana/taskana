package pro.taskana.example;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.ObjectReferenceImpl;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.models.Workbasket;

/** TODO. */
@Component
@Transactional
public class ExampleBootstrap {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExampleBootstrap.class);

  private final TaskService taskService;

  private final TaskanaEngine taskanaEngine;

  public ExampleBootstrap(
      @Autowired TaskService taskService, @Autowired TaskanaEngine taskanaEngine) {
    this.taskService = taskService;
    this.taskanaEngine = taskanaEngine;
  }

  @PostConstruct
  public void postConstruct() throws Exception {
    try {
      LOGGER.info("---------------------------> Start App");

      Workbasket wb = taskanaEngine.getWorkbasketService().newWorkbasket("workbasket", "DOMAIN_A");
      wb.setName("workbasket");
      wb.setType(WorkbasketType.GROUP);
      taskanaEngine.getWorkbasketService().createWorkbasket(wb);
      Classification classification =
          taskanaEngine.getClassificationService().newClassification("TEST", "DOMAIN_A", "TASK");
      classification.setServiceLevel("P1D");
      taskanaEngine.getClassificationService().createClassification(classification);

      Task task = taskanaEngine.getTaskService().newTask(wb.getId());
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
    } catch (TaskanaException e) {
      throw new ExampleStartupException(e);
    }
  }
}
