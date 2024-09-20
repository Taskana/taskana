package io.kadai.simplehistory.rest.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.rest.assembler.PagedRepresentationModelAssembler;
import io.kadai.common.rest.models.PageMetadata;
import io.kadai.simplehistory.rest.TaskHistoryEventController;
import io.kadai.simplehistory.rest.models.TaskHistoryEventPagedRepresentationModel;
import io.kadai.simplehistory.rest.models.TaskHistoryEventRepresentationModel;
import io.kadai.spi.history.api.events.task.TaskHistoryCustomField;
import io.kadai.spi.history.api.events.task.TaskHistoryEvent;
import java.util.Collection;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class TaskHistoryEventRepresentationModelAssembler
    implements PagedRepresentationModelAssembler<
        TaskHistoryEvent,
        TaskHistoryEventRepresentationModel,
        TaskHistoryEventPagedRepresentationModel> {

  @NonNull
  @Override
  public TaskHistoryEventRepresentationModel toModel(@NonNull TaskHistoryEvent historyEvent) {
    TaskHistoryEventRepresentationModel repModel = new TaskHistoryEventRepresentationModel();
    repModel.setTaskHistoryId(historyEvent.getId());
    repModel.setBusinessProcessId(historyEvent.getBusinessProcessId());
    repModel.setParentBusinessProcessId(historyEvent.getParentBusinessProcessId());
    repModel.setTaskId(historyEvent.getTaskId());
    repModel.setEventType(historyEvent.getEventType());
    repModel.setCreated(historyEvent.getCreated());
    repModel.setUserId(historyEvent.getUserId());
    repModel.setUserLongName(historyEvent.getUserLongName());
    repModel.setDomain(historyEvent.getDomain());
    repModel.setWorkbasketKey(historyEvent.getWorkbasketKey());
    repModel.setPorCompany(historyEvent.getPorCompany());
    repModel.setPorType(historyEvent.getPorType());
    repModel.setPorInstance(historyEvent.getPorInstance());
    repModel.setPorSystem(historyEvent.getPorSystem());
    repModel.setPorValue(historyEvent.getPorValue());
    repModel.setTaskOwnerLongName(historyEvent.getTaskOwnerLongName());
    repModel.setTaskClassificationKey(historyEvent.getTaskClassificationKey());
    repModel.setTaskClassificationCategory(historyEvent.getTaskClassificationCategory());
    repModel.setAttachmentClassificationKey(historyEvent.getAttachmentClassificationKey());
    repModel.setOldValue(historyEvent.getOldValue());
    repModel.setNewValue(historyEvent.getNewValue());
    repModel.setCustom1(historyEvent.getCustomAttribute(TaskHistoryCustomField.CUSTOM_1));
    repModel.setCustom2(historyEvent.getCustomAttribute(TaskHistoryCustomField.CUSTOM_2));
    repModel.setCustom3(historyEvent.getCustomAttribute(TaskHistoryCustomField.CUSTOM_3));
    repModel.setCustom4(historyEvent.getCustomAttribute(TaskHistoryCustomField.CUSTOM_4));
    repModel.setDetails(historyEvent.getDetails());
    try {
      repModel.add(
          linkTo(
                  methodOn(TaskHistoryEventController.class)
                      .getTaskHistoryEvent(historyEvent.getId()))
              .withSelfRel());
    } catch (Exception e) {
      throw new SystemException("caught unexpected Exception", e);
    }
    return repModel;
  }

  @Override
  public TaskHistoryEventPagedRepresentationModel buildPageableEntity(
      Collection<TaskHistoryEventRepresentationModel> content, PageMetadata pageMetadata) {
    return new TaskHistoryEventPagedRepresentationModel(content, pageMetadata);
  }
}
