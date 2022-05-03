package pro.taskana.testapi.builder;

import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.security.auth.Subject;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.security.UserPrincipal;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.WorkbasketAccessItemAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;
import pro.taskana.workbasket.internal.models.WorkbasketAccessItemImpl;

public class WorkbasketAccessItemBuilder {

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

  public WorkbasketAccessItem buildAndStore(WorkbasketService workbasketService)
      throws InvalidArgumentException, WorkbasketAccessItemAlreadyExistException,
          WorkbasketNotFoundException, NotAuthorizedException {
    return workbasketService.createWorkbasketAccessItem(testWorkbasketAccessItem);
  }

  public WorkbasketAccessItem buildAndStore(WorkbasketService workbasketService, String userId)
      throws PrivilegedActionException {
    Subject subject = new Subject();
    subject.getPrincipals().add(new UserPrincipal(userId));
    PrivilegedExceptionAction<WorkbasketAccessItem> performBuildAndStore =
        () -> buildAndStore(workbasketService);

    return Subject.doAs(subject, performBuildAndStore);
  }
}
