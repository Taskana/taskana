package pro.taskana.rest.resource;

import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import pro.taskana.history.api.events.TaskanaHistoryEvent;
import pro.taskana.simplehistory.impl.HistoryEventImpl;

/** Transforms any {@link HistoryEventImpl} into its {@link TaskHistoryEventResource}. */
public class TaskHistoryEventAssembler
    extends ResourceAssemblerSupport<TaskanaHistoryEvent, TaskHistoryEventResource> {

  public TaskHistoryEventAssembler() {
    super(HistoryEventImpl.class, TaskHistoryEventResource.class);
  }

  @Override
  public TaskHistoryEventResource toResource(TaskanaHistoryEvent historyEvent) {
    TaskHistoryEventResource resource = createResourceWithId(historyEvent.getId(), historyEvent);
    BeanUtils.copyProperties(historyEvent, resource);
    if (historyEvent.getCreated() != null) {
      resource.setCreated(historyEvent.getCreated().toString());
    }
    resource.setTaskHistoryId(String.valueOf(historyEvent.getId()));
    resource.removeLinks();
    return resource;
  }
}
