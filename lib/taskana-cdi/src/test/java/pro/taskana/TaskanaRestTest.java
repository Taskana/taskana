package pro.taskana;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.model.Classification;
import pro.taskana.model.Task;
import pro.taskana.model.Workbasket;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/test")
public class TaskanaRestTest {

	private static final Logger logger = LoggerFactory.getLogger(TaskanaRestTest.class);

	@EJB
	private TaskanaEjb taskanaEjb;

	@GET
	public Response startTask() throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException {
		Workbasket workbasket = new Workbasket();
		workbasket.setName("wb");
		taskanaEjb.getWorkbasketService().createWorkbasket(workbasket);
		Classification classification = new Classification();
		taskanaEjb.getClassificationService().addClassification(classification);

		Task task = new Task();
		task.setClassification(classification);
		task.setWorkbasketId(workbasket.getId());

		Task result = taskanaEjb.getTaskService().createTask(task);

		logger.info(result.getId() + ":" + result.getOwner());
		return Response.status(200).entity(result.getId()).build();
	}
	
	@POST
	public Response rollbackTask() throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException {
		taskanaEjb.triggerRollback();
		return Response.status(204).build();
	}

	@DELETE
	@Path("{id}")
	public void completeTask(@PathParam("id") String id) throws TaskNotFoundException {
		logger.info(id);
		taskanaEjb.getTaskService().complete(id);
	}

}
