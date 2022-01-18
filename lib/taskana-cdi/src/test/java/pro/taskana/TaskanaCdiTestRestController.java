package pro.taskana;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.ObjectReferenceImpl;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.models.Workbasket;

@Path("/test")
public class TaskanaCdiTestRestController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskanaCdiTestRestController.class);

  @EJB private TaskanaEjb taskanaEjb;

  @Inject private ClassificationService classificationService;

  @GET
  public Response startTask() throws Exception {
    Workbasket workbasket = taskanaEjb.getWorkbasketService().newWorkbasket("key", "cdiDomain");
    workbasket.setName("wb");
    workbasket.setType(WorkbasketType.PERSONAL);
    taskanaEjb.getWorkbasketService().createWorkbasket(workbasket);
    Classification classification =
        classificationService.newClassification("TEST", "cdiDomain", "t1");
    taskanaEjb.getClassificationService().createClassification(classification);

    Task task = taskanaEjb.getTaskService().newTask(workbasket.getKey());
    task.setClassificationKey(classification.getKey());
    ObjectReferenceImpl objRef = new ObjectReferenceImpl();
    objRef.setCompany("aCompany");
    objRef.setSystem("aSystem");
    objRef.setSystemInstance("anInstance");
    objRef.setType("aType");
    objRef.setValue("aValue");
    task.setPrimaryObjRef(objRef);

    Task result = taskanaEjb.getTaskService().createTask(task);

    LOGGER.info(result.getId() + ":" + result.getOwner());
    return Response.status(200).entity(result.getId()).build();
  }

  @POST
  public Response rollbackTask() throws Exception {
    taskanaEjb.triggerRollback();
    return Response.status(204).build();
  }

  @DELETE
  @Path("{id}")
  public void completeTask(@PathParam("id") String id) throws Exception {
    LOGGER.info(id);
    taskanaEjb.getTaskService().forceCompleteTask(id);
  }
}
