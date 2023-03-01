/*-
 * #%L
 * pro.taskana:taskana-test-api
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
package pro.taskana.testapi.builder;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.WorkbasketAccessItemAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;
import pro.taskana.workbasket.internal.models.WorkbasketAccessItemImpl;

public class WorkbasketAccessItemBuilder
    implements EntityBuilder<WorkbasketAccessItem, WorkbasketService> {

  WorkbasketAccessItemImpl testWorkbasketAccessItem = new WorkbasketAccessItemImpl();

  private WorkbasketAccessItemBuilder() {}

  public static WorkbasketAccessItemBuilder newWorkbasketAccessItem() {
    return new WorkbasketAccessItemBuilder();
  }

  public WorkbasketAccessItemBuilder workbasketId(String workbasketId) {
    testWorkbasketAccessItem.setWorkbasketId(workbasketId);
    return this;
  }

  public WorkbasketAccessItemBuilder accessId(String accessId) {
    testWorkbasketAccessItem.setAccessId(accessId);
    return this;
  }

  public WorkbasketAccessItemBuilder accessName(String accessName) {
    testWorkbasketAccessItem.setAccessName(accessName);
    return this;
  }

  public WorkbasketAccessItemBuilder permission(WorkbasketPermission permission) {
    return permission(permission, true);
  }

  public WorkbasketAccessItemBuilder permission(WorkbasketPermission permission, boolean value) {
    testWorkbasketAccessItem.setPermission(permission, value);
    return this;
  }

  @Override
  public WorkbasketAccessItem buildAndStore(WorkbasketService workbasketService)
      throws InvalidArgumentException, WorkbasketAccessItemAlreadyExistException,
          WorkbasketNotFoundException, MismatchedRoleException {
    return workbasketService.createWorkbasketAccessItem(testWorkbasketAccessItem);
  }
}
