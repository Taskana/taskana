/*-
 * #%L
 * pro.taskana:taskana-core
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
package pro.taskana.workbasket.api.exceptions;

import java.util.Arrays;

import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.util.MapCreator;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.models.Workbasket;

/**
 * This exception is thrown when the current user does not have a certain {@linkplain
 * WorkbasketPermission permission} on a {@linkplain Workbasket}.
 */
public class MismatchedWorkbasketPermissionException extends TaskanaException {

  public static final String ERROR_KEY_KEY_DOMAIN = "WORKBASKET_WITH_KEY_MISMATCHED_PERMISSION";
  public static final String ERROR_KEY_ID = "WORKBASKET_WITH_ID_MISMATCHED_PERMISSION";
  private final String currentUserId;
  private final WorkbasketPermission[] requiredPermissions;
  private final String workbasketId;
  private final String workbasketKey;
  private final String domain;

  public MismatchedWorkbasketPermissionException(
      String currentUserId, String workbasketId, WorkbasketPermission... requiredPermissions) {
    super(
        String.format(
            "Not authorized. The current user '%s' has no '%s' permission(s) for Workbasket '%s'.",
            currentUserId, Arrays.toString(requiredPermissions), workbasketId),
        ErrorCode.of(
            ERROR_KEY_ID,
            MapCreator.of(
                "currentUserId",
                currentUserId,
                "workbasketId",
                workbasketId,
                "requiredPermissions",
                requiredPermissions)));

    this.currentUserId = currentUserId;
    this.requiredPermissions = requiredPermissions;
    this.workbasketId = workbasketId;
    workbasketKey = null;
    domain = null;
  }

  public MismatchedWorkbasketPermissionException(
      String currentUserId,
      String workbasketKey,
      String domain,
      WorkbasketPermission... requiredPermissions) {
    super(
        String.format(
            "Not authorized. The current user '%s' has no '%s' permission for "
                + "Workbasket with key '%s' in domain '%s'.",
            currentUserId, Arrays.toString(requiredPermissions), workbasketKey, domain),
        ErrorCode.of(
            ERROR_KEY_KEY_DOMAIN,
            MapCreator.of(
                "currentUserId",
                currentUserId,
                "workbasketKey",
                workbasketKey,
                "domain",
                domain,
                "requiredPermissions",
                requiredPermissions)));

    this.currentUserId = currentUserId;
    this.requiredPermissions = requiredPermissions;
    this.workbasketKey = workbasketKey;
    this.domain = domain;
    workbasketId = null;
  }

  public String getWorkbasketKey() {
    return workbasketKey;
  }

  public String getDomain() {
    return domain;
  }

  public String getWorkbasketId() {
    return workbasketId;
  }

  public WorkbasketPermission[] getRequiredPermissions() {
    return requiredPermissions;
  }

  public String getCurrentUserId() {
    return currentUserId;
  }
}
