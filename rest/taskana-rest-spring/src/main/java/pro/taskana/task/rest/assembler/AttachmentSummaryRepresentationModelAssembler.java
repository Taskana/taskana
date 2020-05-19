package pro.taskana.task.rest.assembler;

import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.rest.models.AttachmentSummaryRepresentationModel;

/**
 * EntityModel assembler for {@link AttachmentSummaryRepresentationModel}.
 */
@Component
public class AttachmentSummaryRepresentationModelAssembler implements
    RepresentationModelAssembler<AttachmentSummary, AttachmentSummaryRepresentationModel> {

  @NonNull
  @Override
  public AttachmentSummaryRepresentationModel toModel(
      @NonNull AttachmentSummary attachmentSummary) {
    return new AttachmentSummaryRepresentationModel(attachmentSummary);
  }
}
