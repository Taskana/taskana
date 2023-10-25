package pro.taskana.example;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Startup;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.ObjectReferenceImpl;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.models.Workbasket;

/** Example Bootstrap Application. */
@ApplicationScoped
public class ExampleBootstrap {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExampleBootstrap.class);

  private static final String CDIDOMAIN = "CDIDOMAIN";
  private static final String CLASSIFICATION_TYPE = "T1";

  private final TaskanaEjb taskanaEjb;

  public ExampleBootstrap() {
    this.taskanaEjb = null;
  }

  @Inject
  public ExampleBootstrap(TaskanaEjb taskanaEjb) {
    this.taskanaEjb = taskanaEjb;
  }

  /**
   * The parameter `@Observes Startup` makes sure that the dependency injection framework calls this
   * method on system startup. And to do that, it needs to call `@PostConstruct start()` first.
   *
   * @param startup just the startup event
   * @throws ExampleStartupException in case of task creation fails
   */
  public void init(@Observes Startup startup) throws ExampleStartupException {
    try {
      LOGGER.info("---------------------------> Start App -- {}", startup);
      Workbasket workbasket = taskanaEjb.getWorkbasketService().newWorkbasket("KEY", CDIDOMAIN);
      workbasket.setName("wb");
      workbasket.setType(WorkbasketType.PERSONAL);
      workbasket = taskanaEjb.getWorkbasketService().createWorkbasket(workbasket);
      Classification classification =
          taskanaEjb
              .getClassificationService()
              .newClassification("TEST", CDIDOMAIN, CLASSIFICATION_TYPE);
      taskanaEjb.getClassificationService().createClassification(classification);
      ObjectReferenceImpl objRef = new ObjectReferenceImpl();
      objRef.setCompany("aCompany");
      objRef.setSystem("aSystem");
      objRef.setSystemInstance("anInstance");
      objRef.setType("aType");
      objRef.setValue("aValue");
      Task task = taskanaEjb.getTaskService().newTask(workbasket.getId());
      task.setClassificationKey(classification.getKey());
      task.setName("BootstrapTask");
      task.setPrimaryObjRef(objRef);
      task = taskanaEjb.getTaskService().createTask(task);
      LOGGER.info("---------------------------> Task started: {}", task.getId());
      taskanaEjb.getTaskService().claim(task.getId());
      LOGGER.info(
          "---------------------------> Task claimed: {}",
          taskanaEjb.getTaskService().getTask(task.getId()).getOwner());
      taskanaEjb.getTaskService().completeTask(task.getId());
      LOGGER.info("---------------------------> Task completed");
    } catch (TaskanaException e) {
      throw new ExampleStartupException(e);
    }
  }
}
