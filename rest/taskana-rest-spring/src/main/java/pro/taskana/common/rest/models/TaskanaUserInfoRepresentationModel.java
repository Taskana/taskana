package pro.taskana.common.rest.models;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

import pro.taskana.common.api.TaskanaRole;

/** EntityModel class for user information. */
@Getter
@Setter
@ToString
public class TaskanaUserInfoRepresentationModel
    extends RepresentationModel<TaskanaUserInfoRepresentationModel> {

  /** The user Id of the current user. */
  private String userId;
  /** All groups the current user is a member of. */
  private List<String> groupIds = new ArrayList<>();
  /** All taskana roles the current user fulfills. */
  private List<TaskanaRole> roles = new ArrayList<>();
}
