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
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.model.Task;
import pro.taskana.model.WorkbasketImpl;

@Path("/test")
public class TaskanaRestTest {

	private static final Logger logger = LoggerFactory.getLogger(TaskanaRestTest.class);

	@EJB
	private TaskanaEjb taskanaEjb;
	
	@Inject
	private ClassificationService classificationService;

	@GET
	public Response startTask() throws NotAuthorizedException, WorkbasketNotFoundException, ClassificationNotFoundException, ClassificationAlreadyExistException {
		WorkbasketImpl workbasket = new WorkbasketImpl();
		workbasket.setName("wb");
		taskanaEjb.getWorkbasketService().createWorkbasket(workbasket);
		Classification classification = classificationService.newClassification();
		taskanaEjb.getClassificationService().createClassification(classification);

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
