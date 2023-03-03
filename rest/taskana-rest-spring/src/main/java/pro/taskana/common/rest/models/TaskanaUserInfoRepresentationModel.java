/*-
 * #%L
 * pro.taskana:taskana-rest-spring
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package pro.taskana.common.rest.models;

import java.util.ArrayList;
import java.util.List;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.lang.NonNull;

import pro.taskana.common.api.TaskanaRole;

/** EntityModel class for user information. */
public class TaskanaUserInfoRepresentationModel
    extends RepresentationModel<TaskanaUserInfoRepresentationModel> {

  /** The user Id of the current user. */
  private String userId;
  /** All groups the current user is a member of. */
  private List<String> groupIds = new ArrayList<>();
  /** All taskana roles the current user fulfills. */
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
  public @NonNull String toString() {
    return "TaskanaUserInfoRepresentationModel [userId="
        + userId
        + ", groupIds="
        + groupIds
        + ", roles="
        + roles
        + "]";
  }
}
