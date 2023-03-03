/*-
 * #%L
 * pro.taskana:taskana-common
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
package pro.taskana.common.api.exceptions;

import java.util.Arrays;

import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.internal.util.MapCreator;

/**
 * This exception is thrown when the current user is not in a certain {@linkplain TaskanaRole role}
 * it is supposed to be.
 */
public class MismatchedRoleException extends TaskanaException {

  public static final String ERROR_KEY = "ROLE_MISMATCHED";
  private final String currentUserId;
  private final TaskanaRole[] roles;

  public MismatchedRoleException(String currentUserId, TaskanaRole... roles) {
    super(
        String.format(
            "Not authorized. The current user '%s' is not member of role(s) '%s'.",
            currentUserId, Arrays.toString(roles)),
        ErrorCode.of(ERROR_KEY, MapCreator.of("roles", roles, "currentUserId", currentUserId)));

    this.currentUserId = currentUserId;
    this.roles = roles;
  }

  public TaskanaRole[] getRoles() {
    return roles;
  }

  public String getCurrentUserId() {
    return currentUserId;
  }
}
