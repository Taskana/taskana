package io.kadai.example;

import io.kadai.classification.api.models.Classification;
import io.kadai.common.api.exceptions.KadaiException;
import io.kadai.task.api.models.Task;
import io.kadai.task.internal.models.ObjectReferenceImpl;
import io.kadai.workbasket.api.WorkbasketType;
import io.kadai.workbasket.api.models.Workbasket;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Startup;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Example Bootstrap Application. */
@ApplicationScoped
public class ExampleBootstrap {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExampleBootstrap.class);

  private static final String CDIDOMAIN = "CDIDOMAIN";
  private static final String CLASSIFICATION_TYPE = "T1";

  private final KadaiEjb kadaiEjb;

  public ExampleBootstrap() {
    this.kadaiEjb = null;
  }

  @Inject
  public ExampleBootstrap(KadaiEjb kadaiEjb) {
    this.kadaiEjb = kadaiEjb;
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
      Workbasket workbasket = kadaiEjb.getWorkbasketService().newWorkbasket("KEY", CDIDOMAIN);
      workbasket.setName("wb");
      workbasket.setType(WorkbasketType.PERSONAL);
      workbasket = kadaiEjb.getWorkbasketService().createWorkbasket(workbasket);
      Classification classification =
          kadaiEjb
              .getClassificationService()
              .newClassification("TEST", CDIDOMAIN, CLASSIFICATION_TYPE);
      kadaiEjb.getClassificationService().createClassification(classification);
      ObjectReferenceImpl objRef = new ObjectReferenceImpl();
      objRef.setCompany("aCompany");
      objRef.setSystem("aSystem");
      objRef.setSystemInstance("anInstance");
      objRef.setType("aType");
      objRef.setValue("aValue");
      Task task = kadaiEjb.getTaskService().newTask(workbasket.getId());
      task.setClassificationKey(classification.getKey());
      task.setName("BootstrapTask");
      task.setPrimaryObjRef(objRef);
      task = kadaiEjb.getTaskService().createTask(task);
      LOGGER.info("---------------------------> Task started: {}", task.getId());
      kadaiEjb.getTaskService().claim(task.getId());
      LOGGER.info(
          "---------------------------> Task claimed: {}",
          kadaiEjb.getTaskService().getTask(task.getId()).getOwner());
      kadaiEjb.getTaskService().completeTask(task.getId());
      LOGGER.info("---------------------------> Task completed");
    } catch (KadaiException e) {
      throw new ExampleStartupException(e);
    }
  }
}
