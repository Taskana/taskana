package pro.taskana.workbasket.rest.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** EntityModel class for {@link WorkbasketSummary}. */
@Getter
@Setter
public class WorkbasketSummaryRepresentationModel
    extends RepresentationModel<WorkbasketSummaryRepresentationModel> {

  /** Unique Id. */
  protected String workbasketId;
  /** the professional key for the workbasket. */
  protected String key;
  /** The name of the workbasket. */
  protected String name;
  /** The domain the workbasket belongs to. */
  protected String domain;
  /** The type of the workbasket. */
  protected WorkbasketType type;
  /** the description of the workbasket. */
  protected String description;
  /**
   * The owner of the workbasket. The owner is responsible for the on-time completion of all tasks
   * in the workbasket.
   */
  protected String owner;
  /** A custom property with name "1". */
  protected String custom1;
  /** A custom property with name "2". */
  protected String custom2;
  /** A custom property with name "3". */
  protected String custom3;
  /** A custom property with name "4". */
  protected String custom4;
  /**
   * The first Org Level (the top one).
   *
   * <p>The Org Level is an association with an org hierarchy level in the organization. The values
   * are used for monitoring and statistical purposes and should reflect who is responsible of the
   * tasks in the workbasket.
   */
  protected String orgLevel1;
  /** The second Org Level. */
  protected String orgLevel2;
  /** The third Org Level. */
  protected String orgLevel3;
  /** The fourth Org Level (the lowest one). */
  protected String orgLevel4;
  /** Identifier to tell if this workbasket can be deleted. */
  private boolean markedForDeletion;
}
