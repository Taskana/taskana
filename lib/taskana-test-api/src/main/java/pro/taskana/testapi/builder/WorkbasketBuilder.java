package pro.taskana.testapi.builder;

import java.time.Instant;

import pro.taskana.common.api.exceptions.DomainNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.testapi.builder.EntityBuilder.SummaryEntityBuilder;
import pro.taskana.workbasket.api.WorkbasketCustomField;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.exceptions.MismatchedWorkbasketPermissionException;
import pro.taskana.workbasket.api.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

public class WorkbasketBuilder
    implements SummaryEntityBuilder<WorkbasketSummary, Workbasket, WorkbasketService> {

  private final WorkbasketTestImpl testWorkbasket = new WorkbasketTestImpl();

  private WorkbasketBuilder() {}

  public static WorkbasketBuilder newWorkbasket() {
    return new WorkbasketBuilder();
  }

  public WorkbasketBuilder key(String key) {
    testWorkbasket.setKey(key);
    return this;
  }

  public WorkbasketBuilder name(String name) {
    testWorkbasket.setName(name);
    return this;
  }

  public WorkbasketBuilder description(String description) {
    testWorkbasket.setDescription(description);
    return this;
  }

  public WorkbasketBuilder owner(String owner) {
    testWorkbasket.setOwner(owner);
    return this;
  }

  public WorkbasketBuilder domain(String domain) {
    testWorkbasket.setDomain(domain);
    return this;
  }

  public WorkbasketBuilder type(WorkbasketType type) {
    testWorkbasket.setType(type);
    return this;
  }

  public WorkbasketBuilder customAttribute(WorkbasketCustomField customField, String value) {
    testWorkbasket.setCustomField(customField, value);
    return this;
  }

  public WorkbasketBuilder orgLevel1(String orgLevel1) {
    testWorkbasket.setOrgLevel1(orgLevel1);
    return this;
  }

  public WorkbasketBuilder orgLevel2(String orgLevel2) {
    testWorkbasket.setOrgLevel2(orgLevel2);
    return this;
  }

  public WorkbasketBuilder orgLevel3(String orgLevel3) {
    testWorkbasket.setOrgLevel3(orgLevel3);
    return this;
  }

  public WorkbasketBuilder orgLevel4(String orgLevel4) {
    testWorkbasket.setOrgLevel4(orgLevel4);

    return this;
  }

  public WorkbasketBuilder markedForDeletion(boolean markedForDeletion) {
    testWorkbasket.setMarkedForDeletion(markedForDeletion);
    return this;
  }

  public WorkbasketBuilder created(Instant created) {
    testWorkbasket.setCreatedIgnoringFreeze(created);
    if (created != null) {
      testWorkbasket.freezeCreated();
    } else {
      testWorkbasket.unfreezeCreated();
    }
    return this;
  }

  public WorkbasketBuilder modified(Instant modified) {
    testWorkbasket.setModifiedIgnoringFreeze(modified);
    if (modified != null) {
      testWorkbasket.freezeModified();
    } else {
      testWorkbasket.unfreezeModified();
    }
    return this;
  }

  @Override
  public WorkbasketSummary entityToSummary(Workbasket workbasket) {
    return workbasket.asSummary();
  }

  @Override
  public Workbasket buildAndStore(WorkbasketService workbasketService)
      throws InvalidArgumentException, WorkbasketAlreadyExistException, DomainNotFoundException,
          WorkbasketNotFoundException, MismatchedRoleException,
          MismatchedWorkbasketPermissionException {
    try {
      Workbasket w = workbasketService.createWorkbasket(testWorkbasket);
      return workbasketService.getWorkbasket(w.getId());
    } finally {
      testWorkbasket.setId(null);
    }
  }
}
