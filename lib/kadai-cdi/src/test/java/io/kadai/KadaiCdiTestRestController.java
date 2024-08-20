package io.kadai;

import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.Classification;
import io.kadai.task.api.models.Task;
import io.kadai.task.internal.models.ObjectReferenceImpl;
import io.kadai.workbasket.api.WorkbasketType;
import io.kadai.workbasket.api.models.Workbasket;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/test")
public class KadaiCdiTestRestController {

  private static final Logger LOGGER = LoggerFactory.getLogger(KadaiCdiTestRestController.class);
  private static final String CDIDOMAIN = "CDIDOMAIN";
  private static final String CLASSIFICATION_TYPE = "T1";

  private final KadaiEjb kadaiEjb;

  private final ClassificationService classificationService;

  public KadaiCdiTestRestController() {
    this.kadaiEjb = null;
    this.classificationService = null;
  }

  @Inject
  public KadaiCdiTestRestController(
      KadaiEjb kadaiEjb, ClassificationService classificationService) {
    this.kadaiEjb = kadaiEjb;
    this.classificationService = classificationService;
  }

  @GET
  public Response startTask() throws Exception {
    Workbasket workbasket = kadaiEjb.getWorkbasketService().newWorkbasket("KEY", CDIDOMAIN);
    workbasket.setName("wb");
    workbasket.setType(WorkbasketType.PERSONAL);
    workbasket = kadaiEjb.getWorkbasketService().createWorkbasket(workbasket);
    Classification classification =
        classificationService.newClassification("TEST", CDIDOMAIN, CLASSIFICATION_TYPE);
    kadaiEjb.getClassificationService().createClassification(classification);

    Task task = kadaiEjb.getTaskService().newTask(workbasket.getId());
    task.setClassificationKey(classification.getKey());
    task.setName("startTask");
    ObjectReferenceImpl objRef = new ObjectReferenceImpl();
    objRef.setCompany("aCompany");
    objRef.setSystem("aSystem");
    objRef.setSystemInstance("anInstance");
    objRef.setType("aType");
    objRef.setValue("aValue");
    task.setPrimaryObjRef(objRef);

    Task result = kadaiEjb.getTaskService().createTask(task);

    LOGGER.info(result.getId() + ":" + result.getOwner());
    return Response.status(200).entity(result.getId()).build();
  }

  @POST
  public Response rollbackTask() throws Exception {
    Workbasket workbasket =
        kadaiEjb.getWorkbasketService().newWorkbasket("KEY_ROLLBACK", CDIDOMAIN);
    workbasket.setName("wb_rollback");
    workbasket.setType(WorkbasketType.PERSONAL);
    workbasket = kadaiEjb.getWorkbasketService().createWorkbasket(workbasket);
    Classification classification =
        classificationService.newClassification("TEST_ROLLBACK", CDIDOMAIN, CLASSIFICATION_TYPE);
    kadaiEjb.getClassificationService().createClassification(classification);

    try {
      kadaiEjb.triggerRollback(workbasket.getId(), classification.getKey());
    } catch (Exception e) {
      if (!"java.lang.RuntimeException: Expected Test Exception".equals(e.getMessage())) {
        throw e;
      }
    }
    return Response.status(204).build();
  }

  @DELETE
  @Path("{id}")
  public void completeTask(@PathParam("id") String id) throws Exception {
    LOGGER.info(id);
    kadaiEjb.getTaskService().forceCompleteTask(id);
  }
}
