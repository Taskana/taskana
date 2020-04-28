package pro.taskana.simplehistory.rest.resource;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.simplehistory.impl.HistoryEventImpl;
import pro.taskana.simplehistory.rest.TaskHistoryEventController;
import pro.taskana.spi.history.api.events.TaskanaHistoryEvent;
import pro.taskana.spi.history.api.exceptions.TaskanaHistoryEventNotFoundException;

/** Transforms any {@link HistoryEventImpl} into its {@link TaskHistoryEventResource}. */
public class TaskHistoryEventResourceAssembler
    extends RepresentationModelAssemblerSupport<TaskanaHistoryEvent, TaskHistoryEventResource> {

  public TaskHistoryEventResourceAssembler() {
    super(HistoryEventImpl.class, TaskHistoryEventResource.class);
  }

  @NonNull
  @Override
  public TaskHistoryEventResource toModel(@NonNull TaskanaHistoryEvent historyEvent) {
    TaskHistoryEventResource resource = createModelWithId(historyEvent.getId(), historyEvent);
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
