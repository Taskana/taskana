package pro.taskana.workbasket.rest.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import pro.taskana.workbasket.api.models.WorkbasketAccessItem;

/** EntityModel class for {@link WorkbasketAccessItem}. */
@Getter
@Setter
public class WorkbasketAccessItemRepresentationModel
    extends RepresentationModel<WorkbasketAccessItemRepresentationModel> {

  /** Unique Id. */
  private String accessItemId;
  /** The workbasket Id. */
  private String workbasketId;
  /** The Access Id. This could be either a user Id or a full qualified group Id. */
  private String accessId;
  /** The workbasket key. */
  private String workbasketKey;
  /** The name. */
  private String accessName;
  /** The permission to read the information about the workbasket. */
  private boolean permRead;
  /** The permission to view the content (the tasks) of a workbasket. */
  private boolean permOpen;
  /**
   * The permission to add tasks to the workbasket. Required for creation and transferring of tasks.
   */
  private boolean permAppend;
  /** The permission to transfer tasks (out of the current workbasket). */
  private boolean permTransfer;
  /** The permission to distribute tasks from the workbasket. */
  private boolean permDistribute;
  /** The custom permission with the name "1". */
  private boolean permCustom1;
  /** The custom permission with the name "2". */
  private boolean permCustom2;
  /** The custom permission with the name "3". */
  private boolean permCustom3;
  /** The custom permission with the name "4". */
  private boolean permCustom4;
  /** The custom permission with the name "5". */
  private boolean permCustom5;
  /** The custom permission with the name "6". */
  private boolean permCustom6;
  /** The custom permission with the name "7". */
  private boolean permCustom7;
  /** The custom permission with the name "8". */
  private boolean permCustom8;
  /** The custom permission with the name "9". */
  private boolean permCustom9;
  /** The custom permission with the name "10". */
  private boolean permCustom10;
  /** The custom permission with the name "11". */
  private boolean permCustom11;
  /** The custom permission with the name "12". */
  private boolean permCustom12;
}
