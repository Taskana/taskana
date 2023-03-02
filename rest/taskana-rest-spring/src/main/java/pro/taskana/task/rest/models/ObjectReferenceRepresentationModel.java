package pro.taskana.task.rest.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
public class ObjectReferenceRepresentationModel
    extends RepresentationModel<ObjectReferenceRepresentationModel> {

  /** Unique ID. */
  private String id;

  /** Task Id. */
  private String taskId;
  /** The company the referenced primary object belongs to. */
  private String company;
  /** The (kind of) system, the referenced primary object resides in (e.g. SAP, MySystem A, ...). */
  private String system;
  /** The instance of the system where the referenced primary object is located. */
  private String systemInstance;
  /** The type of the referenced primary object (contract, claim, policy, customer, ...). */
  private String type;
  /** The value of the primary object reference. */
  private String value;
}
