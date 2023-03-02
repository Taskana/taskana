package pro.taskana.workbasket.internal;

import static pro.taskana.common.api.SharedConstants.MASTER_DOMAIN;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;

import pro.taskana.TaskanaConfiguration;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.DomainNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.common.internal.util.IdGenerator;
import pro.taskana.common.internal.util.ObjectAttributeChangeDetector;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketAccessItemCreatedEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketAccessItemDeletedEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketAccessItemUpdatedEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketAccessItemsUpdatedEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketCreatedEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketDeletedEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketDistributionTargetAddedEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketDistributionTargetRemovedEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketDistributionTargetsUpdatedEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketMarkedForDeletionEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketUpdatedEvent;
import pro.taskana.spi.history.internal.HistoryEventManager;
import pro.taskana.task.api.TaskState;
import pro.taskana.workbasket.api.WorkbasketAccessItemQuery;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketQuery;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.MismatchedWorkbasketPermissionException;
import pro.taskana.workbasket.api.exceptions.WorkbasketAccessItemAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketInUseException;
import pro.taskana.workbasket.api.exceptions.WorkbasketMarkedForDeletionException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.internal.models.WorkbasketAccessItemImpl;
import pro.taskana.workbasket.internal.models.WorkbasketImpl;
import pro.taskana.workbasket.internal.models.WorkbasketSummaryImpl;

/** This is the implementation of WorkbasketService. */
@Slf4j
@AllArgsConstructor
public class WorkbasketServiceImpl implements WorkbasketService {

  private final InternalTaskanaEngine taskanaEngine;
  private final HistoryEventManager historyEventManager;
  private final WorkbasketMapper workbasketMapper;
  private final DistributionTargetMapper distributionTargetMapper;
  private final WorkbasketAccessMapper workbasketAccessMapper;

  @Override
  public Workbasket getWorkbasket(String workbasketId)
      throws WorkbasketNotFoundException, MismatchedWorkbasketPermissionException {
    Workbasket result;
    try {
      taskanaEngine.openConnection();
      result = workbasketMapper.findById(workbasketId);

      if (result == null) {
        throw new WorkbasketNotFoundException(workbasketId);
      }

      if (!taskanaEngine
          .getEngine()
          .isUserInRole(
              TaskanaRole.ADMIN,
              TaskanaRole.BUSINESS_ADMIN,
              TaskanaRole.TASK_ADMIN,
              TaskanaRole.TASK_ROUTER)) {
        this.checkAuthorization(workbasketId, WorkbasketPermission.READ);
      }
      return result;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public Workbasket getWorkbasket(String workbasketKey, String domain)
      throws WorkbasketNotFoundException, MismatchedWorkbasketPermissionException {
    if (!taskanaEngine
        .getEngine()
        .isUserInRole(
            TaskanaRole.ADMIN,
            TaskanaRole.BUSINESS_ADMIN,
            TaskanaRole.TASK_ADMIN,
            TaskanaRole.TASK_ROUTER)) {
      this.checkAuthorization(workbasketKey, domain, WorkbasketPermission.READ);
    }

    Workbasket workbasket =
        taskanaEngine.executeInDatabaseConnection(
            () -> workbasketMapper.findByKeyAndDomain(workbasketKey, domain));
    if (workbasket == null) {
      throw new WorkbasketNotFoundException(workbasketKey, domain);
    }

    return workbasket;
  }

  @Override
  public Workbasket createWorkbasket(Workbasket newWorkbasket)
      throws InvalidArgumentException, WorkbasketAlreadyExistException, DomainNotFoundException,
          MismatchedRoleException {
    taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);

    WorkbasketImpl workbasket = (WorkbasketImpl) newWorkbasket;
    try {
      taskanaEngine.openConnection();
      Instant now = Instant.now();
      workbasket.setCreated(now);
      workbasket.setModified(now);
      Workbasket existingWorkbasket =
          workbasketMapper.findByKeyAndDomain(newWorkbasket.getKey(), newWorkbasket.getDomain());
      if (existingWorkbasket != null) {
        throw new WorkbasketAlreadyExistException(
            existingWorkbasket.getKey(), existingWorkbasket.getDomain());
      }

      if (workbasket.getId() == null || workbasket.getId().isEmpty()) {
        workbasket.setId(IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_WORKBASKET));
      }
      validateWorkbasket(workbasket);

      workbasketMapper.insert(workbasket);

      if (historyEventManager.isEnabled()) {
        String details =
            ObjectAttributeChangeDetector.determineChangesInAttributes(
                newWorkbasket("", MASTER_DOMAIN), newWorkbasket);

        historyEventManager.createEvent(
            new WorkbasketCreatedEvent(
                IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_WORKBASKET_HISTORY_EVENT),
                newWorkbasket,
                taskanaEngine.getEngine().getCurrentUserContext().getUserid(),
                details));
      }
      if (log.isDebugEnabled()) {
        log.debug("Method createWorkbasket() created Workbasket '{}'", workbasket);
      }
      return workbasket;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public Workbasket updateWorkbasket(Workbasket workbasketToUpdate)
      throws InvalidArgumentException, WorkbasketNotFoundException, ConcurrencyException,
          MismatchedRoleException, MismatchedWorkbasketPermissionException {

    taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
    WorkbasketImpl workbasketImplToUpdate = (WorkbasketImpl) workbasketToUpdate;
    validateNameAndType(workbasketToUpdate);

    try {
      taskanaEngine.openConnection();

      Workbasket oldWorkbasket;

      if (workbasketImplToUpdate.getId() == null || workbasketImplToUpdate.getId().isEmpty()) {
        oldWorkbasket =
            getWorkbasket(workbasketImplToUpdate.getKey(), workbasketImplToUpdate.getDomain());
      } else {
        oldWorkbasket = getWorkbasket(workbasketImplToUpdate.getId());
        // changing key or domain is not allowed
        if (!oldWorkbasket.getKey().equals(workbasketToUpdate.getKey())
            || !oldWorkbasket.getDomain().equals(workbasketToUpdate.getDomain())) {
          throw new WorkbasketNotFoundException(
              workbasketToUpdate.getKey(), workbasketToUpdate.getDomain());
        }
      }

      checkModifiedHasNotChanged(oldWorkbasket, workbasketImplToUpdate);
      workbasketImplToUpdate.setModified(Instant.now());

      if (workbasketImplToUpdate.getId() == null || workbasketImplToUpdate.getId().isEmpty()) {
        workbasketMapper.updateByKeyAndDomain(workbasketImplToUpdate);
      } else {
        workbasketMapper.update(workbasketImplToUpdate);
      }

      if (historyEventManager.isEnabled()) {
        String details =
            ObjectAttributeChangeDetector.determineChangesInAttributes(
                oldWorkbasket, workbasketToUpdate);

        historyEventManager.createEvent(
            new WorkbasketUpdatedEvent(
                IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_WORKBASKET_HISTORY_EVENT),
                workbasketToUpdate,
                taskanaEngine.getEngine().getCurrentUserContext().getUserid(),
                details));
      }

      if (log.isDebugEnabled()) {
        log.debug(
            "Method updateWorkbasket() updated workbasket '{}'", workbasketImplToUpdate.getId());
      }

      return workbasketImplToUpdate;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public WorkbasketAccessItem newWorkbasketAccessItem(String workbasketId, String accessId) {
    WorkbasketAccessItemImpl accessItem = new WorkbasketAccessItemImpl();
    accessItem.setWorkbasketId(workbasketId);
    if (TaskanaConfiguration.shouldUseLowerCaseForAccessIds()) {
      accessItem.setAccessId(accessId != null ? accessId.toLowerCase() : null);
    } else {
      accessItem.setAccessId(accessId);
    }
    return accessItem;
  }

  @Override
  public WorkbasketAccessItem createWorkbasketAccessItem(WorkbasketAccessItem workbasketAccessItem)
      throws InvalidArgumentException, WorkbasketNotFoundException,
          WorkbasketAccessItemAlreadyExistException, MismatchedRoleException {

    taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
    WorkbasketAccessItemImpl accessItem = (WorkbasketAccessItemImpl) workbasketAccessItem;
    try {
      taskanaEngine.openConnection();
      accessItem.setId(
          IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_WORKBASKET_AUTHORIZATION));
      if (workbasketAccessItem.getId() == null
          || workbasketAccessItem.getAccessId() == null
          || workbasketAccessItem.getWorkbasketId() == null) {
        throw new InvalidArgumentException(
            String.format(
                "Checking the preconditions of the current "
                    + "WorkbasketAccessItem failed. WorkbasketAccessItem=%s",
                workbasketAccessItem));
      }
      WorkbasketImpl wb = workbasketMapper.findById(workbasketAccessItem.getWorkbasketId());
      if (wb == null) {
        throw new WorkbasketNotFoundException(workbasketAccessItem.getWorkbasketId());
      }
      accessItem.setWorkbasketKey(wb.getKey());
      try {
        workbasketAccessMapper.insert(accessItem);

        if (historyEventManager.isEnabled()) {

          String details =
              ObjectAttributeChangeDetector.determineChangesInAttributes(
                  newWorkbasketAccessItem("", ""), accessItem);

          historyEventManager.createEvent(
              new WorkbasketAccessItemCreatedEvent(
                  IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_WORKBASKET_HISTORY_EVENT),
                  wb,
                  taskanaEngine.getEngine().getCurrentUserContext().getUserid(),
                  details));
        }
        if (log.isDebugEnabled()) {
          log.debug(
              "Method createWorkbasketAccessItem() created workbaskteAccessItem {}", accessItem);
        }
      } catch (PersistenceException e) {
        if (log.isDebugEnabled()) {
          log.debug(
              "when trying to insert WorkbasketAccessItem {} caught exception", accessItem, e);
        }
        Stream<String> accessItemExistsIdentifier =
            Stream.of(
                "SQLCODE=-803", // DB2
                "uc_accessid_wbid", // POSTGRES
                "UC_ACCESSID_WBID_INDEX_E", // H2
                "ORA-00001" // ORACLE
                );
        if (accessItemExistsIdentifier.anyMatch(e.getMessage()::contains)) {
          throw new WorkbasketAccessItemAlreadyExistException(
              accessItem.getAccessId(), accessItem.getWorkbasketId());
        }
        throw e;
      }
      return accessItem;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public WorkbasketAccessItem updateWorkbasketAccessItem(WorkbasketAccessItem workbasketAccessItem)
      throws InvalidArgumentException, MismatchedRoleException {

    taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
    WorkbasketAccessItemImpl accessItem = (WorkbasketAccessItemImpl) workbasketAccessItem;
    try {
      taskanaEngine.openConnection();
      WorkbasketAccessItem originalItem = workbasketAccessMapper.findById(accessItem.getId());

      if ((originalItem.getAccessId() != null
              && !originalItem.getAccessId().equals(accessItem.getAccessId()))
          || (originalItem.getWorkbasketId() != null
              && !originalItem.getWorkbasketId().equals(accessItem.getWorkbasketId()))) {
        throw new InvalidArgumentException(
            "AccessId and WorkbasketId must not be changed in updateWorkbasketAccessItem calls");
      }

      workbasketAccessMapper.update(accessItem);

      if (historyEventManager.isEnabled()) {

        String details =
            ObjectAttributeChangeDetector.determineChangesInAttributes(originalItem, accessItem);

        Workbasket workbasket = workbasketMapper.findById(accessItem.getWorkbasketId());

        historyEventManager.createEvent(
            new WorkbasketAccessItemUpdatedEvent(
                IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_WORKBASKET_HISTORY_EVENT),
                workbasket,
                taskanaEngine.getEngine().getCurrentUserContext().getUserid(),
                details));
      }

      if (log.isDebugEnabled()) {
        log.debug(
            "Method updateWorkbasketAccessItem() updated workbasketAccessItem {}", accessItem);
      }
      return accessItem;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public void deleteWorkbasketAccessItem(String accessItemId) throws MismatchedRoleException {
    taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
    try {
      taskanaEngine.openConnection();

      WorkbasketAccessItem accessItem = null;

      if (historyEventManager.isEnabled()) {
        accessItem = workbasketAccessMapper.findById(accessItemId);
      }

      workbasketAccessMapper.delete(accessItemId);

      if (historyEventManager.isEnabled() && accessItem != null) {

        String details =
            ObjectAttributeChangeDetector.determineChangesInAttributes(
                accessItem, newWorkbasketAccessItem("", ""));
        Workbasket workbasket = workbasketMapper.findById(accessItem.getWorkbasketId());
        historyEventManager.createEvent(
            new WorkbasketAccessItemDeletedEvent(
                IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_WORKBASKET_HISTORY_EVENT),
                workbasket,
                taskanaEngine.getEngine().getCurrentUserContext().getUserid(),
                details));
      }

      if (log.isDebugEnabled()) {
        log.debug(
            "Method deleteWorkbasketAccessItem() deleted workbasketAccessItem wit Id {}",
            accessItemId);
      }
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public void checkAuthorization(String workbasketId, WorkbasketPermission... requestedPermissions)
      throws WorkbasketNotFoundException, MismatchedWorkbasketPermissionException {
    try {
      taskanaEngine.openConnection();

      if (workbasketMapper.findById(workbasketId) == null) {
        throw new WorkbasketNotFoundException(workbasketId);
      }

      if (skipAuthorizationCheck(requestedPermissions)) {
        return;
      }

      Optional<List<WorkbasketPermission>> grantedPermissions =
          Optional.ofNullable(
                  workbasketAccessMapper.findByWorkbasketAndAccessId(
                      workbasketId,
                      taskanaEngine.getEngine().getCurrentUserContext().getAccessIds()))
              .map(this::getPermissionsFromWorkbasketAccessItem);

      if (grantedPermissions.isEmpty()
          || !new HashSet<>(grantedPermissions.get())
              .containsAll(Arrays.asList(requestedPermissions))) {
        throw new MismatchedWorkbasketPermissionException(
            taskanaEngine.getEngine().getCurrentUserContext().getUserid(),
            workbasketId,
            requestedPermissions);
      }
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public void checkAuthorization(
      String workbasketKey, String domain, WorkbasketPermission... requestedPermissions)
      throws WorkbasketNotFoundException, MismatchedWorkbasketPermissionException {
    try {
      taskanaEngine.openConnection();

      if (workbasketMapper.findByKeyAndDomain(workbasketKey, domain) == null) {
        throw new WorkbasketNotFoundException(workbasketKey, domain);
      }
      if (skipAuthorizationCheck(requestedPermissions)) {
        return;
      }

      Optional<List<WorkbasketPermission>> grantedPermissions =
          Optional.ofNullable(
                  workbasketAccessMapper.findByWorkbasketKeyDomainAndAccessId(
                      workbasketKey,
                      domain,
                      taskanaEngine.getEngine().getCurrentUserContext().getAccessIds()))
              .map(this::getPermissionsFromWorkbasketAccessItem);

      if (grantedPermissions.isEmpty()
          || !new HashSet<>(grantedPermissions.get())
              .containsAll(Arrays.asList(requestedPermissions))) {
        throw new MismatchedWorkbasketPermissionException(
            taskanaEngine.getEngine().getCurrentUserContext().getUserid(),
            workbasketKey,
            domain,
            requestedPermissions);
      }
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public List<WorkbasketAccessItem> getWorkbasketAccessItems(String workbasketId)
      throws MismatchedRoleException {
    taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
    List<WorkbasketAccessItem> result = new ArrayList<>();
    try {
      taskanaEngine.openConnection();
      List<WorkbasketAccessItemImpl> queryResult =
          workbasketAccessMapper.findByWorkbasketId(workbasketId);
      result.addAll(queryResult);
      return result;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public void setWorkbasketAccessItems(
      String workbasketId, List<WorkbasketAccessItem> wbAccessItems)
      throws WorkbasketAccessItemAlreadyExistException, InvalidArgumentException,
          WorkbasketNotFoundException, MismatchedRoleException,
          MismatchedWorkbasketPermissionException {
    taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);

    Set<WorkbasketAccessItemImpl> accessItems =
        checkAccessItemsPreconditionsAndSetId(workbasketId, wbAccessItems);

    try {
      taskanaEngine.openConnection();
      // this is necessary to verify that the requested workbasket exists.
      getWorkbasket(workbasketId);

      List<WorkbasketAccessItemImpl> originalAccessItems = new ArrayList<>();

      if (historyEventManager.isEnabled()) {
        originalAccessItems = workbasketAccessMapper.findByWorkbasketId(workbasketId);
      }
      // delete all current ones
      workbasketAccessMapper.deleteAllAccessItemsForWorkbasketId(workbasketId);
      accessItems.forEach(workbasketAccessMapper::insert);

      if (historyEventManager.isEnabled()) {

        String details =
            ObjectAttributeChangeDetector.determineChangesInAttributes(
                originalAccessItems, new ArrayList<>(accessItems));

        Workbasket workbasket = workbasketMapper.findById(workbasketId);

        historyEventManager.createEvent(
            new WorkbasketAccessItemsUpdatedEvent(
                IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_WORKBASKET_HISTORY_EVENT),
                workbasket,
                taskanaEngine.getEngine().getCurrentUserContext().getUserid(),
                details));
      }
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public WorkbasketQuery createWorkbasketQuery() {
    return new WorkbasketQueryImpl(taskanaEngine);
  }

  @Override
  public WorkbasketAccessItemQuery createWorkbasketAccessItemQuery()
      throws MismatchedRoleException {
    taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.ADMIN, TaskanaRole.BUSINESS_ADMIN);
    return new WorkbasketAccessItemQueryImpl(this.taskanaEngine);
  }

  @Override
  public Workbasket newWorkbasket(String key, String domain) {
    WorkbasketImpl wb = new WorkbasketImpl();
    wb.setDomain(domain);
    wb.setKey(key);
    return wb;
  }

  @Override
  public List<WorkbasketPermission> getPermissionsForWorkbasket(String workbasketId) {
    WorkbasketAccessItem wbAcc =
        workbasketAccessMapper.findByWorkbasketAndAccessId(
            workbasketId, taskanaEngine.getEngine().getCurrentUserContext().getAccessIds());
    return this.getPermissionsFromWorkbasketAccessItem(wbAcc);
  }

  @Override
  public List<WorkbasketSummary> getDistributionTargets(String workbasketId)
      throws WorkbasketNotFoundException, MismatchedWorkbasketPermissionException {
    List<WorkbasketSummary> result = new ArrayList<>();
    try {
      taskanaEngine.openConnection();
      // check that source workbasket exists
      getWorkbasket(workbasketId);
      if (!taskanaEngine
          .getEngine()
          .isUserInRole(TaskanaRole.ADMIN, TaskanaRole.BUSINESS_ADMIN, TaskanaRole.TASK_ADMIN)) {
        checkAuthorization(workbasketId, WorkbasketPermission.READ);
      }
      List<WorkbasketSummaryImpl> distributionTargets =
          workbasketMapper.findDistributionTargets(workbasketId);
      result.addAll(distributionTargets);
      return result;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public List<WorkbasketSummary> getDistributionTargets(String workbasketKey, String domain)
      throws WorkbasketNotFoundException, MismatchedWorkbasketPermissionException {

    List<WorkbasketSummary> result = new ArrayList<>();
    try {
      taskanaEngine.openConnection();
      // check that source workbasket exists
      Workbasket workbasket = getWorkbasket(workbasketKey, domain);
      if (!taskanaEngine
          .getEngine()
          .isUserInRole(TaskanaRole.ADMIN, TaskanaRole.BUSINESS_ADMIN, TaskanaRole.TASK_ADMIN)) {
        checkAuthorization(workbasket.getId(), WorkbasketPermission.READ);
      }
      List<WorkbasketSummaryImpl> distributionTargets =
          workbasketMapper.findDistributionTargets(workbasket.getId());
      result.addAll(distributionTargets);
      return result;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public void setDistributionTargets(String sourceWorkbasketId, List<String> targetWorkbasketIds)
      throws WorkbasketNotFoundException, MismatchedRoleException,
          MismatchedWorkbasketPermissionException {

    taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
    try {
      taskanaEngine.openConnection();
      // check existence of source workbasket
      WorkbasketImpl sourceWorkbasket = (WorkbasketImpl) getWorkbasket(sourceWorkbasketId);

      List<String> originalTargetWorkbasketIds = new ArrayList<>();

      if (historyEventManager.isEnabled()) {
        originalTargetWorkbasketIds = distributionTargetMapper.findBySourceId(sourceWorkbasketId);
      }

      distributionTargetMapper.deleteAllDistributionTargetsBySourceId(sourceWorkbasketId);

      sourceWorkbasket.setModified(Instant.now());
      workbasketMapper.update(sourceWorkbasket);

      if (targetWorkbasketIds != null) {
        for (String targetId : targetWorkbasketIds) {
          // check for existence of target workbasket
          getWorkbasket(targetId);
          distributionTargetMapper.insert(sourceWorkbasketId, targetId);
          if (log.isDebugEnabled()) {
            log.debug(
                "Method setDistributionTargets() created distribution target "
                    + "for source '{}' and target {}",
                sourceWorkbasketId,
                targetId);
          }
        }

        if (historyEventManager.isEnabled() && !targetWorkbasketIds.isEmpty()) {

          String details =
              ObjectAttributeChangeDetector.determineChangesInAttributes(
                  originalTargetWorkbasketIds, targetWorkbasketIds);

          historyEventManager.createEvent(
              new WorkbasketDistributionTargetsUpdatedEvent(
                  IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_WORKBASKET_HISTORY_EVENT),
                  sourceWorkbasket,
                  taskanaEngine.getEngine().getCurrentUserContext().getUserid(),
                  details));
        }
      }

    } finally {
      taskanaEngine.returnConnection();
      if (log.isDebugEnabled()) {
        log.debug(
            "setDistributionTargets set {} distribution targets to source workbasket {} ",
            targetWorkbasketIds == null ? 0 : targetWorkbasketIds.size(),
            sourceWorkbasketId);
      }
    }
  }

  @Override
  public void addDistributionTarget(String sourceWorkbasketId, String targetWorkbasketId)
      throws WorkbasketNotFoundException, MismatchedRoleException,
          MismatchedWorkbasketPermissionException {

    taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
    try {
      taskanaEngine.openConnection();
      // check existence of source workbasket
      WorkbasketImpl sourceWorkbasket = (WorkbasketImpl) getWorkbasket(sourceWorkbasketId);
      // check existence of target workbasket
      getWorkbasket(targetWorkbasketId);
      // check whether the target is already set as target
      int numOfDistTargets =
          distributionTargetMapper.getNumberOfDistributionTargets(
              sourceWorkbasketId, targetWorkbasketId);
      if (numOfDistTargets > 0) {
        if (log.isDebugEnabled()) {
          log.debug(
              "addDistributionTarget detected that the specified "
                  + "distribution target exists already. Doing nothing.");
        }
      } else {
        distributionTargetMapper.insert(sourceWorkbasketId, targetWorkbasketId);

        if (historyEventManager.isEnabled()) {

          String details =
              "{\"changes\":{\"newValue\":\"" + targetWorkbasketId + "\",\"oldValue\":\"\"}}";

          historyEventManager.createEvent(
              new WorkbasketDistributionTargetAddedEvent(
                  IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_WORKBASKET_HISTORY_EVENT),
                  sourceWorkbasket,
                  taskanaEngine.getEngine().getCurrentUserContext().getUserid(),
                  details));
        }
        if (log.isDebugEnabled()) {
          log.debug(
              "addDistributionTarget inserted distribution target sourceId = {}, targetId = {}",
              sourceWorkbasketId,
              targetWorkbasketId);
        }
        sourceWorkbasket.setModified(Instant.now());
        workbasketMapper.update(sourceWorkbasket);
      }

    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public void removeDistributionTarget(String sourceWorkbasketId, String targetWorkbasketId)
      throws MismatchedRoleException, MismatchedWorkbasketPermissionException {

    taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
    try {
      taskanaEngine.openConnection();
      // don't check existence of source / target workbasket to enable cleanup even if the db is
      // corrupted
      // check whether the target is set as target
      int numberOfDistTargets =
          distributionTargetMapper.getNumberOfDistributionTargets(
              sourceWorkbasketId, targetWorkbasketId);
      if (numberOfDistTargets > 0) {
        distributionTargetMapper.delete(sourceWorkbasketId, targetWorkbasketId);

        if (historyEventManager.isEnabled()) {

          Workbasket workbasket = workbasketMapper.findById(sourceWorkbasketId);

          if (workbasket != null) {

            String details =
                "{\"changes\":{\"newValue\":\"\",\"oldValue\":\"" + targetWorkbasketId + "\"}}";

            historyEventManager.createEvent(
                new WorkbasketDistributionTargetRemovedEvent(
                    IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_WORKBASKET_HISTORY_EVENT),
                    workbasket,
                    taskanaEngine.getEngine().getCurrentUserContext().getUserid(),
                    details));
          }
        }
        if (log.isDebugEnabled()) {
          log.debug(
              "removeDistributionTarget deleted distribution target sourceId = {}, targetId = {}",
              sourceWorkbasketId,
              targetWorkbasketId);
        }

        try {
          WorkbasketImpl sourceWorkbasket = (WorkbasketImpl) getWorkbasket(sourceWorkbasketId);
          sourceWorkbasket.setModified(Instant.now());
          workbasketMapper.update(sourceWorkbasket);
        } catch (WorkbasketNotFoundException e) {
          if (log.isDebugEnabled()) {
            log.debug(
                "removeDistributionTarget found that the source workbasket {} "
                    + "doesn't exist. Ignoring the request... ",
                sourceWorkbasketId);
          }
        }

      } else {
        if (log.isDebugEnabled()) {
          log.debug(
              "removeDistributionTarget detected that the specified distribution "
                  + "target doesn't exist. Doing nothing...");
        }
      }
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public boolean deleteWorkbasket(String workbasketId)
      throws WorkbasketNotFoundException, WorkbasketInUseException, InvalidArgumentException,
          MismatchedRoleException, MismatchedWorkbasketPermissionException {
    taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);

    validateId(workbasketId);

    try {
      taskanaEngine.openConnection();

      Workbasket workbasketToDelete;
      try {
        workbasketToDelete = this.getWorkbasket(workbasketId);
      } catch (WorkbasketNotFoundException ex) {
        if (log.isDebugEnabled()) {
          log.debug("Workbasket with workbasketId = {} is already deleted?", workbasketId);
        }
        throw ex;
      }

      long countTasksNotCompletedInWorkbasket =
          taskanaEngine
              .getEngine()
              .runAsAdmin(() -> getCountTasksNotCompletedByWorkbasketId(workbasketId));

      if (countTasksNotCompletedInWorkbasket > 0) {
        throw new WorkbasketInUseException(workbasketId);
      }

      long countTasksInWorkbasket =
          taskanaEngine.getEngine().runAsAdmin(() -> getCountTasksByWorkbasketId(workbasketId));

      boolean canBeDeletedNow = countTasksInWorkbasket == 0;

      if (canBeDeletedNow) {
        workbasketMapper.delete(workbasketId);
        deleteReferencesToWorkbasket(workbasketId);

        if (historyEventManager.isEnabled()) {

          String details =
              ObjectAttributeChangeDetector.determineChangesInAttributes(
                  workbasketToDelete, newWorkbasket("", MASTER_DOMAIN));

          historyEventManager.createEvent(
              new WorkbasketDeletedEvent(
                  IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_WORKBASKET_HISTORY_EVENT),
                  workbasketToDelete,
                  taskanaEngine.getEngine().getCurrentUserContext().getUserid(),
                  details));
        }
      } else {
        markWorkbasketForDeletion(workbasketId);
      }

      return canBeDeletedNow;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  public BulkOperationResults<String, TaskanaException> deleteWorkbaskets(
      List<String> workbasketsIds) throws InvalidArgumentException, MismatchedRoleException {

    taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);

    try {
      taskanaEngine.openConnection();
      if (workbasketsIds == null || workbasketsIds.isEmpty()) {
        throw new InvalidArgumentException("List of WorkbasketIds must not be null.");
      }
      BulkOperationResults<String, TaskanaException> bulkLog = new BulkOperationResults<>();

      Iterator<String> iterator = workbasketsIds.iterator();
      String workbasketIdForDeleting = null;
      while (iterator.hasNext()) {
        try {
          workbasketIdForDeleting = iterator.next();
          if (!deleteWorkbasket(workbasketIdForDeleting)) {
            bulkLog.addError(
                workbasketIdForDeleting,
                new WorkbasketMarkedForDeletionException(workbasketIdForDeleting));
          }
        } catch (TaskanaException ex) {
          bulkLog.addError(workbasketIdForDeleting, ex);
        }
      }
      return bulkLog;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public List<WorkbasketSummary> getDistributionSources(String workbasketId)
      throws WorkbasketNotFoundException, MismatchedWorkbasketPermissionException {
    List<WorkbasketSummary> result = new ArrayList<>();
    try {
      taskanaEngine.openConnection();
      // check that source workbasket exists
      getWorkbasket(workbasketId);
      if (!taskanaEngine.getEngine().isUserInRole(TaskanaRole.ADMIN, TaskanaRole.BUSINESS_ADMIN)) {
        checkAuthorization(workbasketId, WorkbasketPermission.READ);
      }
      List<WorkbasketSummaryImpl> distributionSources =
          workbasketMapper.findDistributionSources(workbasketId);
      result.addAll(distributionSources);
      return result;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public List<WorkbasketSummary> getDistributionSources(String workbasketKey, String domain)
      throws WorkbasketNotFoundException, MismatchedWorkbasketPermissionException {

    List<WorkbasketSummary> result = new ArrayList<>();
    try {
      taskanaEngine.openConnection();
      // check that source workbasket exists
      Workbasket workbasket = getWorkbasket(workbasketKey, domain);
      if (!taskanaEngine.getEngine().isUserInRole(TaskanaRole.ADMIN, TaskanaRole.BUSINESS_ADMIN)) {
        checkAuthorization(workbasket.getId(), WorkbasketPermission.READ);
      }
      List<WorkbasketSummaryImpl> distributionSources =
          workbasketMapper.findDistributionSources(workbasket.getId());
      result.addAll(distributionSources);
      return result;
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  @Override
  public void deleteWorkbasketAccessItemsForAccessId(String accessId)
      throws MismatchedRoleException {
    taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
    try {
      taskanaEngine.openConnection();
      if (TaskanaConfiguration.shouldUseLowerCaseForAccessIds() && accessId != null) {
        accessId = accessId.toLowerCase();
      }

      List<WorkbasketAccessItemImpl> workbasketAccessItems = new ArrayList<>();
      if (historyEventManager.isEnabled()) {
        workbasketAccessItems = workbasketAccessMapper.findByAccessId(accessId);
      }
      workbasketAccessMapper.deleteAccessItemsForAccessId(accessId);

      if (historyEventManager.isEnabled()) {

        for (WorkbasketAccessItemImpl workbasketAccessItem : workbasketAccessItems) {

          String details =
              ObjectAttributeChangeDetector.determineChangesInAttributes(
                  workbasketAccessItem, new WorkbasketAccessItemImpl());

          Workbasket workbasket = workbasketMapper.findById(workbasketAccessItem.getWorkbasketId());

          historyEventManager.createEvent(
              new WorkbasketAccessItemDeletedEvent(
                  IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_WORKBASKET_HISTORY_EVENT),
                  workbasket,
                  taskanaEngine.getEngine().getCurrentUserContext().getUserid(),
                  details));
        }
      }
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  /**
   * Check if current workbasket is based on the newest (by modified).
   *
   * @param oldWorkbasket the old workbasket in the system
   * @param workbasketImplToUpdate the workbasket to update
   * @throws ConcurrencyException if the workbasket has been modified by some other process; that's
   *     the case if the given modified timestamp differs from the one in the database
   */
  public void checkModifiedHasNotChanged(
      Workbasket oldWorkbasket, WorkbasketImpl workbasketImplToUpdate) throws ConcurrencyException {

    if (!oldWorkbasket.getModified().equals(workbasketImplToUpdate.getModified())) {
      throw new ConcurrencyException(workbasketImplToUpdate.getId());
    }
  }

  private Set<WorkbasketAccessItemImpl> checkAccessItemsPreconditionsAndSetId(
      String workbasketId, List<WorkbasketAccessItem> wbAccessItems)
      throws InvalidArgumentException, WorkbasketAccessItemAlreadyExistException {

    Set<String> ids = new HashSet<>();
    Set<WorkbasketAccessItemImpl> accessItems = new HashSet<>();

    for (WorkbasketAccessItem workbasketAccessItem : wbAccessItems) {
      if (workbasketAccessItem != null) {
        WorkbasketAccessItemImpl wbAccessItemImpl = (WorkbasketAccessItemImpl) workbasketAccessItem;

        if (wbAccessItemImpl.getWorkbasketId() == null) {
          throw new InvalidArgumentException(
              String.format(
                  "Checking the preconditions of the current WorkbasketAccessItem failed "
                      + "- WBID is NULL. WorkbasketAccessItem=%s",
                  workbasketAccessItem));
        } else if (!wbAccessItemImpl.getWorkbasketId().equals(workbasketId)) {
          throw new InvalidArgumentException(
              String.format(
                  "Checking the preconditions of the current WorkbasketAccessItem failed "
                      + "- the WBID does not match. Target-WBID=''%s'' WorkbasketAccessItem=%s",
                  workbasketId, workbasketAccessItem));
        }
        if (wbAccessItemImpl.getId() == null || wbAccessItemImpl.getId().isEmpty()) {
          wbAccessItemImpl.setId(
              IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_WORKBASKET_AUTHORIZATION));
        }
        if (ids.contains(wbAccessItemImpl.getAccessId())) {
          throw new WorkbasketAccessItemAlreadyExistException(
              wbAccessItemImpl.getAccessId(), wbAccessItemImpl.getWorkbasketId());
        }
        ids.add(wbAccessItemImpl.getAccessId());
        accessItems.add(wbAccessItemImpl);
      }
    }
    return accessItems;
  }

  private long getCountTasksByWorkbasketId(String workbasketId) {
    return taskanaEngine
        .getEngine()
        .getTaskService()
        .createTaskQuery()
        .workbasketIdIn(workbasketId)
        .count();
  }

  private long getCountTasksNotCompletedByWorkbasketId(String workbasketId) {
    return taskanaEngine
        .getEngine()
        .getTaskService()
        .createTaskQuery()
        .workbasketIdIn(workbasketId)
        .stateNotIn(TaskState.COMPLETED, TaskState.TERMINATED, TaskState.CANCELLED)
        .count();
  }

  private boolean skipAuthorizationCheck(WorkbasketPermission... requestedPermissions) {

    // Skip permission check if security is not enabled
    if (!taskanaEngine.getEngine().getConfiguration().isSecurityEnabled()) {
      if (log.isDebugEnabled()) {
        log.debug("Skipping permissions check since security is disabled.");
      }
      return true;
    }

    if (Arrays.asList(requestedPermissions).contains(WorkbasketPermission.READ)) {

      if (taskanaEngine.getEngine().isUserInRole(TaskanaRole.ADMIN)) {
        if (log.isDebugEnabled()) {
          log.debug("Skipping read permissions check since user is in role ADMIN");
        }
        return true;
      }
    } else if (taskanaEngine.getEngine().isUserInRole(TaskanaRole.ADMIN, TaskanaRole.TASK_ADMIN)) {
      if (log.isDebugEnabled()) {
        log.debug("Skipping permissions check since user is in role ADMIN or TASK_ADMIN.");
      }
      return true;
    }

    return false;
  }

  private void validateWorkbasket(Workbasket workbasket)
      throws DomainNotFoundException, InvalidArgumentException {
    // check that required properties (database not null) are set
    validateNameAndType(workbasket);

    if (workbasket.getId() == null || workbasket.getId().length() == 0) {
      throw new InvalidArgumentException("Id must not be null for " + workbasket);
    }
    if (workbasket.getKey() == null || workbasket.getKey().length() == 0) {
      throw new InvalidArgumentException("Key must not be null for " + workbasket);
    }
    if (workbasket.getDomain() == null) {
      throw new InvalidArgumentException("Domain must not be null for " + workbasket);
    }
    if (!taskanaEngine.domainExists(workbasket.getDomain())) {
      throw new DomainNotFoundException(workbasket.getDomain());
    }
  }

  private void validateId(String workbasketId) throws InvalidArgumentException {
    if (workbasketId == null) {
      throw new InvalidArgumentException("The WorkbasketId can't be NULL");
    }
    if (workbasketId.isEmpty()) {
      throw new InvalidArgumentException("The WorkbasketId can't be EMPTY for deleteWorkbasket()");
    }
  }

  private void validateNameAndType(Workbasket workbasket) throws InvalidArgumentException {
    if (workbasket.getName() == null) {
      throw new InvalidArgumentException("Name must not be NULL for " + workbasket);
    }
    if (workbasket.getName().length() == 0) {
      throw new InvalidArgumentException("Name must not be EMPTY for " + workbasket);
    }
    if (workbasket.getType() == null) {
      throw new InvalidArgumentException("Type must not be NULL for " + workbasket);
    }
  }

  private List<WorkbasketPermission> getPermissionsFromWorkbasketAccessItem(
      WorkbasketAccessItem workbasketAccessItem) {
    List<WorkbasketPermission> permissions = new ArrayList<>();
    if (workbasketAccessItem == null) {
      return permissions;
    }
    for (WorkbasketPermission permission : WorkbasketPermission.values()) {
      if (workbasketAccessItem.getPermission(permission)) {
        permissions.add(permission);
      }
    }
    return permissions;
  }

  private void markWorkbasketForDeletion(String workbasketId)
      throws InvalidArgumentException, MismatchedRoleException {
    taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
    try {
      taskanaEngine.openConnection();
      validateId(workbasketId);
      WorkbasketImpl workbasket = workbasketMapper.findById(workbasketId);
      workbasket.setMarkedForDeletion(true);
      workbasketMapper.update(workbasket);
      if (historyEventManager.isEnabled()) {

        historyEventManager.createEvent(
            new WorkbasketMarkedForDeletionEvent(
                IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_WORKBASKET_HISTORY_EVENT),
                workbasket,
                taskanaEngine.getEngine().getCurrentUserContext().getUserid(),
                null));
      }
    } finally {
      taskanaEngine.returnConnection();
    }
  }

  private void deleteReferencesToWorkbasket(String workbasketId) {
    // deletes sub-tables workbasket references
    distributionTargetMapper.deleteAllDistributionTargetsBySourceId(workbasketId);
    distributionTargetMapper.deleteAllDistributionTargetsByTargetId(workbasketId);
    workbasketAccessMapper.deleteAllAccessItemsForWorkbasketId(workbasketId);
  }
}
