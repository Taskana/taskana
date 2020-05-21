package pro.taskana.common.rest.models;

import java.util.ArrayList;
import java.util.List;
import org.springframework.hateoas.RepresentationModel;

import pro.taskana.common.api.LoggerUtils;
import pro.taskana.common.api.TaskanaRole;

/** EntityModel class for user information. */
public class TaskanaUserInfoRepresentationModel
    extends RepresentationModel<TaskanaUserInfoRepresentationModel> {

  private String userId;
  private List<String> groupIds = new ArrayList<>();
  private List<TaskanaRole> roles = new ArrayList<>();

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public List<String> getGroupIds() {
    return groupIds;
  }

  public void setGroupIds(List<String> groupIds) {
    this.groupIds = groupIds;
  }

  public List<TaskanaRole> getRoles() {
    return roles;
  }

  public void setRoles(List<TaskanaRole> roles) {
    this.roles = roles;
  }

  @Override
  public String toString() {
    return "TaskanaUserInfoRepresentationModel ["
        + "userId= "
        + this.userId
        + "groupIds= "
        + LoggerUtils.listToString(this.groupIds)
        + "roles= "
        + LoggerUtils.listToString(this.roles)
        + "]";
  }
}
