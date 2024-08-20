package io.kadai.testapi.builder;

import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.exceptions.WorkbasketAccessItemAlreadyExistException;
import io.kadai.workbasket.api.exceptions.WorkbasketNotFoundException;
import io.kadai.workbasket.api.models.WorkbasketAccessItem;
import io.kadai.workbasket.internal.models.WorkbasketAccessItemImpl;

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
      throws InvalidArgumentException,
          WorkbasketAccessItemAlreadyExistException,
          WorkbasketNotFoundException,
          NotAuthorizedException {
    return workbasketService.createWorkbasketAccessItem(testWorkbasketAccessItem);
  }
}
