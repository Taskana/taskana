package org.taskana;

import javax.ejb.EJB;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.taskana.exceptions.NotAuthorizedException;
import org.taskana.exceptions.TaskNotFoundException;
import org.taskana.model.Task;

@Path("/test")
public class TaskanaRestTest {

	private static final Logger logger = LoggerFactory.getLogger(TaskanaRestTest.class);

	@EJB
	private TaskanaEjb taskanaEjb;

	@GET
	public Response startTask() throws NotAuthorizedException {
		Task result = taskanaEjb.getTaskService().create(new Task());
		logger.info(result.getId() + ":" + result.getOwner());
		return Response.status(200).entity(result.getId()).build();
	}
	
	@POST
	public Response rollbackTask() throws NotAuthorizedException {
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
