package pro.taskana.testapi.builder;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
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
      throws InvalidArgumentException,
          WorkbasketAccessItemAlreadyExistException,
          WorkbasketNotFoundException,
          NotAuthorizedException {
    return workbasketService.createWorkbasketAccessItem(testWorkbasketAccessItem);
  }
}
