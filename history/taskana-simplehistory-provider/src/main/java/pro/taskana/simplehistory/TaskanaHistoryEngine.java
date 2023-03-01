/*-
 * #%L
 * pro.taskana.history:taskana-simplehistory-provider
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
package pro.taskana.simplehistory;

import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.spi.history.api.TaskanaHistory;

/** The TaskanaHistoryEngine represents an overall set of all needed services. */
public interface TaskanaHistoryEngine {
  /**
   * The TaskanaHistory can be used for operations on all history events.
   *
   * @return the HistoryService
   */
  TaskanaHistory getTaskanaHistoryService();

  /**
   * check whether the current user is member of one of the roles specified.
   *
   * @param roles The roles that are checked for membership of the current user
   * @return true if the current user is a member of at least one of the specified groups
   */
  boolean isUserInRole(TaskanaRole... roles);

  /**
   * Checks whether current user is member of any of the specified roles.
   *
   * @param roles The roles that are checked for membership of the current user
   * @throws MismatchedRoleException If the current user is not member of any specified role
   */
  void checkRoleMembership(TaskanaRole... roles) throws MismatchedRoleException;
}
