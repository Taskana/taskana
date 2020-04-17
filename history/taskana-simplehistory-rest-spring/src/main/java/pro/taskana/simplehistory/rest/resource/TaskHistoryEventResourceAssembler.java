package pro.taskana.simplehistory.rest.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.simplehistory.impl.HistoryEventImpl;
import pro.taskana.simplehistory.rest.TaskHistoryEventController;
import pro.taskana.spi.history.api.events.TaskanaHistoryEvent;
import pro.taskana.spi.history.api.exceptions.TaskanaHistoryEventNotFoundException;

/** Transforms any {@link HistoryEventImpl} into its {@link TaskHistoryEventResource}. */
public class TaskHistoryEventResourceAssembler
    extends ResourceAssemblerSupport<TaskanaHistoryEvent, TaskHistoryEventResource> {

  public TaskHistoryEventResourceAssembler() {
    super(HistoryEventImpl.class, TaskHistoryEventResource.class);
  }

  @Override
  public TaskHistoryEventResource toResource(TaskanaHistoryEvent historyEvent) {
    TaskHistoryEventResource resource = createResourceWithId(historyEvent.getId(), historyEvent);
    try {
      resource.removeLinks();
      resource.add(
          linkTo(
                  methodOn(TaskHistoryEventController.class)
                      .getTaskHistoryEvent(String.valueOf(historyEvent.getId())))
              .withSelfRel());
    } catch (TaskanaHistoryEventNotFoundException e) {
      throw new SystemException("caught unexpected Exception.", e.getCause());
    }
    BeanUtils.copyProperties(historyEvent, resource);
    if (historyEvent.getCreated() != null) {
      resource.setCreated(historyEvent.getCreated().toString());
    }
    resource.setTaskHistoryId(String.valueOf(historyEvent.getId()));

    return resource;
  }
}
