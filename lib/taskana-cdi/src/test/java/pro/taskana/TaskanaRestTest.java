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

import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.exceptions.WorkbasketNotFoundException;

@Path("/test")
public class TaskanaRestTest {

    private static final Logger logger = LoggerFactory.getLogger(TaskanaRestTest.class);

    @EJB
    private TaskanaEjb taskanaEjb;

    @Inject
    private ClassificationService classificationService;

    @GET
    public Response startTask() throws NotAuthorizedException, WorkbasketNotFoundException,
        ClassificationNotFoundException, ClassificationAlreadyExistException, InvalidWorkbasketException,
        TaskAlreadyExistException, InvalidArgumentException, WorkbasketAlreadyExistException, DomainNotFoundException {
        Workbasket workbasket = taskanaEjb.getWorkbasketService().newWorkbasket("key", "cdiDomain");
        workbasket.setName("wb");
        workbasket.setType(WorkbasketType.PERSONAL);
        taskanaEjb.getWorkbasketService().createWorkbasket(workbasket);
        Classification classification = classificationService.newClassification("TEST", "cdiDomain", "t1");
        taskanaEjb.getClassificationService().createClassification(classification);

        Task task = taskanaEjb.getTaskService().newTask(workbasket.getKey());
        task.setClassificationKey(classification.getKey());
        ObjectReference objRef = new ObjectReference();
        objRef.setCompany("aCompany");
        objRef.setSystem("aSystem");
        objRef.setSystemInstance("anInstance");
        objRef.setType("aType");
        objRef.setValue("aValue");
        task.setPrimaryObjRef(objRef);

        Task result = taskanaEjb.getTaskService().createTask(task);

        logger.info(result.getId() + ":" + result.getOwner());
        return Response.status(200).entity(result.getId()).build();
    }

    @POST
    public Response rollbackTask()
        throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException,
        TaskAlreadyExistException, InvalidArgumentException {
        taskanaEjb.triggerRollback();
        return Response.status(204).build();
    }

    @DELETE
    @Path("{id}")
    public void completeTask(@PathParam("id") String id)
        throws TaskNotFoundException, InvalidOwnerException, InvalidStateException, NotAuthorizedException {
        logger.info(id);
        taskanaEjb.getTaskService().forceCompleteTask(id);
    }

}
