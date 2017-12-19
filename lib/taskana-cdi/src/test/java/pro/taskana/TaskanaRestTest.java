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
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
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
        ClassificationNotFoundException, ClassificationAlreadyExistException, TaskAlreadyExistException {
        Workbasket workbasket = taskanaEjb.getWorkbasketService().newWorkbasket();
        ;
        workbasket.setName("wb");
        taskanaEjb.getWorkbasketService().createWorkbasket(workbasket);
        Classification classification = classificationService.newClassification();
        classification.setKey("TEST");
        taskanaEjb.getClassificationService().createClassification(classification);

        Task task = taskanaEjb.getTaskService().newTask();
        task.setClassification(classification);
        task.setWorkbasketId(workbasket.getId());

        Task resultTask = taskanaEjb.getTaskService().createTask(task);

        logger.info(resultTask.getId() + ":" + resultTask.getOwner());
        return Response.status(200).entity(resultTask.getId()).build();
    }

    @POST
    public Response rollbackTask() throws NotAuthorizedException, WorkbasketNotFoundException,
        ClassificationNotFoundException, TaskAlreadyExistException {
        taskanaEjb.triggerRollback();
        return Response.status(204).build();
    }

    @DELETE
    @Path("{id}")
    public void completeTask(@PathParam("id") String id)
        throws TaskNotFoundException, InvalidOwnerException, InvalidStateException {
        logger.info(id);
        taskanaEjb.getTaskService().completeTask(id, true);
    }

}
