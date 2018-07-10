package pro.taskana.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaRole;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketAccessItemQuery;
import pro.taskana.WorkbasketPermission;
import pro.taskana.WorkbasketQuery;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.exceptions.WorkbasketInUseException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.mappings.DistributionTargetMapper;
import pro.taskana.mappings.TaskMapper;
import pro.taskana.mappings.WorkbasketAccessMapper;
import pro.taskana.mappings.WorkbasketMapper;
import pro.taskana.security.CurrentUserContext;

/**
 * This is the implementation of WorkbasketService.
 */
public class WorkbasketServiceImpl implements WorkbasketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkbasketServiceImpl.class);
    private static final String ID_PREFIX_WORKBASKET = "WBI";
    private static final String ID_PREFIX_WORKBASKET_AUTHORIZATION = "WAI";
    private TaskanaEngineImpl taskanaEngine;
    private WorkbasketMapper workbasketMapper;
    private DistributionTargetMapper distributionTargetMapper;
    private WorkbasketAccessMapper workbasketAccessMapper;

    WorkbasketServiceImpl(TaskanaEngine taskanaEngine, WorkbasketMapper workbasketMapper,
        DistributionTargetMapper distributionTargetMapper, WorkbasketAccessMapper workbasketAccessMapper) {
        this.taskanaEngine = (TaskanaEngineImpl) taskanaEngine;
        this.workbasketMapper = workbasketMapper;
        this.distributionTargetMapper = distributionTargetMapper;
        this.workbasketAccessMapper = workbasketAccessMapper;
    }

    @Override
    public Workbasket getWorkbasket(String workbasketId)
        throws WorkbasketNotFoundException, NotAuthorizedException {
        LOGGER.debug("entry to getWorkbasket(workbasketId = {})", workbasketId);
        Workbasket result = null;
        try {
            taskanaEngine.openConnection();
            result = workbasketMapper.findById(workbasketId);
            if (result == null) {
                throw new WorkbasketNotFoundException(workbasketId,
                    "Workbasket with id " + workbasketId + " was not found.");
            }
            if (!taskanaEngine.isUserInRole(TaskanaRole.ADMIN, TaskanaRole.BUSINESS_ADMIN)) {
                this.checkAuthorization(workbasketId, WorkbasketPermission.READ);
            }
            return result;
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from getWorkbasket(workbasketId). Returning result {} ", result);
        }
    }

    @Override
    public Workbasket getWorkbasket(String workbasketKey, String domain)
        throws WorkbasketNotFoundException, NotAuthorizedException {
        LOGGER.debug("entry to getWorkbasketByKey(workbasketKey = {})", workbasketKey);
        Workbasket result = null;
        try {
            taskanaEngine.openConnection();
            result = workbasketMapper.findByKeyAndDomain(workbasketKey, domain);
            if (result == null) {
                throw new WorkbasketNotFoundException(workbasketKey, domain,
                    "Workbasket with key " + workbasketKey + " and domain " + domain + " was not found.");
            }
            if (!taskanaEngine.isUserInRole(TaskanaRole.ADMIN, TaskanaRole.BUSINESS_ADMIN)) {
                this.checkAuthorization(workbasketKey, domain, WorkbasketPermission.READ);
            }
            return result;
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from getWorkbasket(workbasketId). Returning result {} ", result);
        }
    }

    @Override
    public Workbasket createWorkbasket(Workbasket newWorkbasket)
        throws InvalidWorkbasketException, NotAuthorizedException, WorkbasketAlreadyExistException,
        DomainNotFoundException {
        LOGGER.debug("entry to createtWorkbasket(workbasket)", newWorkbasket);
        taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);

        WorkbasketImpl workbasket = (WorkbasketImpl) newWorkbasket;
        try {
            taskanaEngine.openConnection();
            Instant now = Instant.now();
            workbasket.setCreated(now);
            workbasket.setModified(now);
            Workbasket existingWorkbasket = workbasketMapper.findByKeyAndDomain(newWorkbasket.getKey(),
                newWorkbasket.getDomain());
            if (existingWorkbasket != null) {
                throw new WorkbasketAlreadyExistException(existingWorkbasket);
            }

            if (workbasket.getId() == null || workbasket.getId().isEmpty()) {
                workbasket.setId(IdGenerator.generateWithPrefix(ID_PREFIX_WORKBASKET));
            }
            validateWorkbasket(workbasket);

            workbasketMapper.insert(workbasket);
            LOGGER.debug("Method createWorkbasket() created Workbasket '{}'", workbasket);
            return workbasket;
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from createWorkbasket(workbasket). Returning result {} ", workbasket);
        }
    }

    @Override
    public Workbasket updateWorkbasket(Workbasket workbasketToUpdate)
        throws NotAuthorizedException {
        LOGGER.debug("entry to updateWorkbasket(workbasket)", workbasketToUpdate);
        taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);

        WorkbasketImpl workbasket = (WorkbasketImpl) workbasketToUpdate;
        try {
            taskanaEngine.openConnection();
            workbasket.setModified(Instant.now());
            workbasketMapper.update(workbasket);
            LOGGER.debug("Method updateWorkbasket() updated workbasket '{}'", workbasket.getId());
            return workbasket;
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from updateWorkbasket(). Returning result {} ", workbasket);
        }
    }

    @Override
    public WorkbasketAccessItem newWorkbasketAccessItem(String workbasketId, String accessId) {
        WorkbasketAccessItemImpl accessItem = new WorkbasketAccessItemImpl();
        accessItem.setWorkbasketId(workbasketId);
        accessItem.setAccessId(accessId);
        return accessItem;
    }

    @Override
    public WorkbasketAccessItem createWorkbasketAccessItem(WorkbasketAccessItem workbasketAccessItem)
        throws InvalidArgumentException, NotAuthorizedException, WorkbasketNotFoundException {
        LOGGER.debug("entry to createWorkbasketAccessItemn(workbasketAccessItem = {})", workbasketAccessItem);
        taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
        WorkbasketAccessItemImpl accessItem = (WorkbasketAccessItemImpl) workbasketAccessItem;
        try {
            taskanaEngine.openConnection();
            accessItem.setId(IdGenerator.generateWithPrefix(ID_PREFIX_WORKBASKET_AUTHORIZATION));
            if (workbasketAccessItem.getId() == null || workbasketAccessItem.getAccessId() == null
                || workbasketAccessItem.getWorkbasketId() == null) {
                throw new InvalidArgumentException(
                    "Checking the preconditions of the current WorkbasketAccessItem failed. WorkbasketAccessItem="
                        + workbasketAccessItem.toString());
            }
            WorkbasketImpl wb = workbasketMapper.findById(workbasketAccessItem.getWorkbasketId());
            if (wb == null) {
                throw new WorkbasketNotFoundException(workbasketAccessItem.getWorkbasketId(),
                    "WorkbasketAccessItem " + workbasketAccessItem + " refers to a not existing workbasket");
            }
            workbasketAccessMapper.insert(accessItem);
            LOGGER.debug("Method createWorkbasketAccessItem() created workbaskteAccessItem {}",
                accessItem);
            return accessItem;
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from createWorkbasketAccessItem(workbasketAccessItem). Returning result {}",
                accessItem);
        }
    }

    @Override
    public void setWorkbasketAccessItems(String workbasketId, List<WorkbasketAccessItem> wbAccessItems)
        throws InvalidArgumentException, NotAuthorizedException {
        LOGGER.debug("entry to setWorkbasketAccessItems(workbasketAccessItems = {})", wbAccessItems.toString());
        taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
        List<WorkbasketAccessItemImpl> newItems = new ArrayList<>();
        try {
            taskanaEngine.openConnection();
            // Check pre-conditions and set ID
            if (!wbAccessItems.isEmpty()) {
                for (WorkbasketAccessItem workbasketAccessItem : wbAccessItems) {
                    WorkbasketAccessItemImpl wbAccessItemImpl = (WorkbasketAccessItemImpl) workbasketAccessItem;
                    if (wbAccessItemImpl.getWorkbasketId() == null) {
                        throw new InvalidArgumentException(
                            "Checking the preconditions of the current WorkbasketAccessItem failed - WBID is NULL. WorkbasketAccessItem="
                                + workbasketAccessItem.toString());
                    } else if (!wbAccessItemImpl.getWorkbasketId().equals(workbasketId)) {
                        throw new InvalidArgumentException(
                            "Checking the preconditions of the current WorkbasketAccessItem failed - the WBID does not match. Target-WBID='"
                                + workbasketId + "' WorkbasketAccessItem="
                                + workbasketAccessItem.toString());
                    }
                    if (wbAccessItemImpl.getId() == null || wbAccessItemImpl.getId().isEmpty()) {
                        wbAccessItemImpl.setId(IdGenerator.generateWithPrefix(ID_PREFIX_WORKBASKET_AUTHORIZATION));
                    }
                    newItems.add(wbAccessItemImpl);
                }
            }

            // delete all current ones
            workbasketAccessMapper.deleteAllAccessItemsForWorkbasketId(workbasketId);

            // add all
            if (!newItems.isEmpty()) {
                newItems.stream().forEach(item -> workbasketAccessMapper.insert(item));
            }
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from setWorkbasketAccessItems(workbasketAccessItems = {})", wbAccessItems.toString());
        }
    }

    @Override
    public void deleteWorkbasketAccessItem(String accessItemId) throws NotAuthorizedException {
        LOGGER.debug("entry to deleteWorkbasketAccessItem(id = {})", accessItemId);
        taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
        try {
            taskanaEngine.openConnection();
            workbasketAccessMapper.delete(accessItemId);
            LOGGER.debug("Method deleteWorkbasketAccessItem() deleted workbasketAccessItem wit Id {}", accessItemId);
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from deleteWorkbasketAccessItem(id).");
        }
    }

    @Override
    public void deleteWorkbasketAccessItemsForAccessId(String accessId) throws NotAuthorizedException {
        LOGGER.debug("entry to deleteWorkbasketAccessItemsForAccessId(accessId = {})", accessId);
        taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
        try {
            taskanaEngine.openConnection();
            workbasketAccessMapper.deleteAccessItemsForAccessId(accessId);
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from deleteWorkbasketAccessItemsForAccessId(accessId={}).", accessId);
        }
    }

    @Override
    public void checkAuthorization(String workbasketId,
        WorkbasketPermission... requestedPermissions) throws NotAuthorizedException, WorkbasketNotFoundException {
        boolean isAuthorized = true;
        try {
            taskanaEngine.openConnection();

            if (workbasketMapper.findById(workbasketId) == null) {
                throw new WorkbasketNotFoundException(workbasketId,
                    "Workbasket with id " + workbasketId + " was not found.");
            }

            if (skipAuthorizationCheck()) {
                return;
            }

            List<String> accessIds = CurrentUserContext.getAccessIds();
            WorkbasketAccessItem wbAcc = workbasketAccessMapper.findByWorkbasketAndAccessId(workbasketId,
                accessIds);
            if (wbAcc == null) {
                throw new NotAuthorizedException(
                    "Not authorized. Permission '" + Arrays.toString(requestedPermissions) + "' on workbasket '"
                        + workbasketId
                        + "' is needed.");
            }

            List<WorkbasketPermission> grantedPermissions = this.getPermissionsFromWorkbasketAccessItem(wbAcc);

            for (WorkbasketPermission perm : requestedPermissions) {
                if (!grantedPermissions.contains(perm)) {
                    isAuthorized = false;
                    throw new NotAuthorizedException(
                        "Not authorized. Permission '" + perm.name() + "' on workbasket '" + workbasketId
                            + "' is needed.");
                }
            }
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from checkAuthorization(). User is authorized = {}.", isAuthorized);
        }
    }

    @Override
    public void checkAuthorization(String workbasketKey, String domain,
        WorkbasketPermission... requestedPermissions)
        throws NotAuthorizedException, WorkbasketNotFoundException {
        boolean isAuthorized = true;
        try {
            taskanaEngine.openConnection();

            if (workbasketMapper.findByKeyAndDomain(workbasketKey, domain) == null) {
                throw new WorkbasketNotFoundException(workbasketKey, domain,
                    "Workbasket with key " + workbasketKey + " and domain " + domain + " was not found");
            }
            if (skipAuthorizationCheck()) {
                return;
            }
            List<String> accessIds = CurrentUserContext.getAccessIds();
            WorkbasketAccessItem wbAcc = workbasketAccessMapper.findByWorkbasketKeyDomainAndAccessId(
                workbasketKey, domain, accessIds);
            if (wbAcc == null) {
                throw new NotAuthorizedException(
                    "Not authorized. Permission '" + Arrays.toString(requestedPermissions)
                        + "' on workbasket with key '"
                        + workbasketKey
                        + "' and domain '" + domain + "' is needed.");
            }
            List<WorkbasketPermission> grantedPermissions = this.getPermissionsFromWorkbasketAccessItem(wbAcc);

            for (WorkbasketPermission perm : requestedPermissions) {
                if (!grantedPermissions.contains(perm)) {
                    isAuthorized = false;
                    throw new NotAuthorizedException(
                        "Not authorized. Permission '" + perm.name() + "' on workbasket with key '" + workbasketKey
                            + "' and domain '" + domain + "' is needed.");
                }
            }
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from checkAuthorization(). User is authorized = {}.", isAuthorized);
        }
    }

    private boolean skipAuthorizationCheck() {

        // Skip permission check is security is not enabled
        if (!taskanaEngine.getConfiguration().isSecurityEnabled()) {
            LOGGER.debug("Skipping permissions check since security is disabled.");
            return true;
        }

        if (taskanaEngine.isUserInRole(TaskanaRole.ADMIN)) {
            LOGGER.debug("Skipping permissions check since user is in role ADMIN.");
            return true;
        }

        return false;
    }

    @Override
    public WorkbasketAccessItem updateWorkbasketAccessItem(WorkbasketAccessItem workbasketAccessItem)
        throws InvalidArgumentException, NotAuthorizedException {
        LOGGER.debug("entry to updateWorkbasketAccessItem(workbasketAccessItem = {}", workbasketAccessItem);
        taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
        WorkbasketAccessItemImpl accessItem = (WorkbasketAccessItemImpl) workbasketAccessItem;
        try {
            taskanaEngine.openConnection();
            WorkbasketAccessItem originalItem = workbasketAccessMapper.findById(accessItem.getId());

            if ((originalItem.getAccessId() != null && !originalItem.getAccessId().equals(accessItem.getAccessId()))
                || (originalItem.getWorkbasketId() != null
                && !originalItem.getWorkbasketId().equals(accessItem.getWorkbasketId()))) {
                throw new InvalidArgumentException(
                    "AccessId and WorkbasketId must not be changed in updateWorkbasketAccessItem calls");
            }

            workbasketAccessMapper.update(accessItem);
            LOGGER.debug("Method updateWorkbasketAccessItem() updated workbasketAccessItem {}",
                accessItem);
            return accessItem;
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from updateWorkbasketAccessItem(workbasketAccessItem). Returning {}",
                accessItem);
        }
    }

    @Override
    public List<WorkbasketAccessItem> getWorkbasketAccessItems(String workbasketId) throws NotAuthorizedException {
        LOGGER.debug("entry to getWorkbasketAccessItems(workbasketId = {})", workbasketId);
        taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
        List<WorkbasketAccessItem> result = new ArrayList<>();
        try {
            taskanaEngine.openConnection();
            List<WorkbasketAccessItemImpl> queryResult = workbasketAccessMapper.findByWorkbasketId(workbasketId);
            result.addAll(queryResult);
            return result;
        } finally {
            taskanaEngine.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = result == null ? 0 : result.size();
                LOGGER.debug("exit from getWorkbasketAccessItems(workbasketId). Returning {} resulting Objects: {} ",
                    numberOfResultObjects, LoggerUtils.listToString(result));
            }
        }
    }

    @Override
    public List<WorkbasketPermission> getPermissionsForWorkbasket(String workbasketId) {
        WorkbasketAccessItem wbAcc = workbasketAccessMapper.findByWorkbasketAndAccessId(workbasketId,
            CurrentUserContext.getAccessIds());
        return this.getPermissionsFromWorkbasketAccessItem(wbAcc);
    }

    @Override
    public WorkbasketQuery createWorkbasketQuery() {
        return new WorkbasketQueryImpl(taskanaEngine);
    }

    private void validateWorkbasket(Workbasket workbasket) throws InvalidWorkbasketException, DomainNotFoundException {
        // check that required properties (database not null) are set
        if (workbasket.getId() == null || workbasket.getId().length() == 0) {
            throw new InvalidWorkbasketException("Id must not be null for " + workbasket);
        } else if (workbasket.getKey() == null || workbasket.getKey().length() == 0) {
            throw new InvalidWorkbasketException("Key must not be null for " + workbasket);
        }
        if (workbasket.getName() == null || workbasket.getName().length() == 0) {
            throw new InvalidWorkbasketException("Name must not be null for " + workbasket);
        }
        if (workbasket.getDomain() == null) {
            throw new InvalidWorkbasketException("Domain must not be null for " + workbasket);
        }
        if (workbasket.getType() == null) {
            throw new InvalidWorkbasketException("Type must not be null for " + workbasket);
        }
        if (!taskanaEngine.domainExists(workbasket.getDomain())) {
            throw new DomainNotFoundException(workbasket.getDomain(),
                "Domain " + workbasket.getDomain() + " does not exist in the configuration.");
        }
    }

    private List<WorkbasketPermission> getPermissionsFromWorkbasketAccessItem(
        WorkbasketAccessItem workbasketAccessItem) {
        List<WorkbasketPermission> permissions = new ArrayList<WorkbasketPermission>();
        if (workbasketAccessItem == null) {
            return permissions;
        }
        if (workbasketAccessItem.isPermOpen()) {
            permissions.add(WorkbasketPermission.OPEN);
        }
        if (workbasketAccessItem.isPermRead()) {
            permissions.add(WorkbasketPermission.READ);
        }
        if (workbasketAccessItem.isPermAppend()) {
            permissions.add(WorkbasketPermission.APPEND);
        }
        if (workbasketAccessItem.isPermTransfer()) {
            permissions.add(WorkbasketPermission.TRANSFER);
        }
        if (workbasketAccessItem.isPermDistribute()) {
            permissions.add(WorkbasketPermission.DISTRIBUTE);
        }
        if (workbasketAccessItem.isPermCustom1()) {
            permissions.add(WorkbasketPermission.CUSTOM_1);
        }
        if (workbasketAccessItem.isPermCustom2()) {
            permissions.add(WorkbasketPermission.CUSTOM_2);
        }
        if (workbasketAccessItem.isPermCustom3()) {
            permissions.add(WorkbasketPermission.CUSTOM_3);
        }
        if (workbasketAccessItem.isPermCustom4()) {
            permissions.add(WorkbasketPermission.CUSTOM_4);
        }
        if (workbasketAccessItem.isPermCustom5()) {
            permissions.add(WorkbasketPermission.CUSTOM_5);
        }
        if (workbasketAccessItem.isPermCustom6()) {
            permissions.add(WorkbasketPermission.CUSTOM_6);
        }
        if (workbasketAccessItem.isPermCustom7()) {
            permissions.add(WorkbasketPermission.CUSTOM_7);
        }
        if (workbasketAccessItem.isPermCustom8()) {
            permissions.add(WorkbasketPermission.CUSTOM_8);
        }
        if (workbasketAccessItem.isPermCustom9()) {
            permissions.add(WorkbasketPermission.CUSTOM_9);
        }
        if (workbasketAccessItem.isPermCustom10()) {
            permissions.add(WorkbasketPermission.CUSTOM_10);
        }
        if (workbasketAccessItem.isPermCustom11()) {
            permissions.add(WorkbasketPermission.CUSTOM_11);
        }
        if (workbasketAccessItem.isPermCustom12()) {
            permissions.add(WorkbasketPermission.CUSTOM_12);
        }
        return permissions;
    }

    @Override
    public Workbasket newWorkbasket(String key, String domain) {
        WorkbasketImpl wb = new WorkbasketImpl();
        wb.setDomain(domain);
        wb.setKey(key);
        return wb;
    }

    @Override
    public List<WorkbasketSummary> getDistributionTargets(String workbasketId)
        throws NotAuthorizedException, WorkbasketNotFoundException {
        LOGGER.debug("entry to getDistributionTargets(workbasketId = {})", workbasketId);
        List<WorkbasketSummary> result = new ArrayList<>();
        try {
            taskanaEngine.openConnection();
            // check that source workbasket exists
            getWorkbasket(workbasketId);
            if (!taskanaEngine.isUserInRole(TaskanaRole.ADMIN, TaskanaRole.BUSINESS_ADMIN)) {
                checkAuthorization(workbasketId, WorkbasketPermission.READ);
            }
            List<WorkbasketSummaryImpl> distributionTargets = workbasketMapper
                .findDistributionTargets(workbasketId);
            result.addAll(distributionTargets);
            return result;
        } finally {
            taskanaEngine.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = result.size();
                LOGGER.debug("exit from getDistributionTargets(workbasketId). Returning {} resulting Objects: {} ",
                    numberOfResultObjects, LoggerUtils.listToString(result));
            }
        }
    }

    @Override
    public List<WorkbasketSummary> getDistributionTargets(String workbasketKey, String domain)
        throws NotAuthorizedException, WorkbasketNotFoundException {
        LOGGER.debug("entry to getDistributionTargets(workbasketKey = {}, domain = {})", workbasketKey, domain);
        List<WorkbasketSummary> result = new ArrayList<>();
        try {
            taskanaEngine.openConnection();
            // check that source workbasket exists
            Workbasket workbasket = getWorkbasket(workbasketKey, domain);
            if (!taskanaEngine.isUserInRole(TaskanaRole.ADMIN, TaskanaRole.BUSINESS_ADMIN)) {
                checkAuthorization(workbasket.getId(), WorkbasketPermission.READ);
            }
            List<WorkbasketSummaryImpl> distributionTargets = workbasketMapper
                .findDistributionTargets(workbasket.getId());
            result.addAll(distributionTargets);
            return result;
        } finally {
            taskanaEngine.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = result.size();
                LOGGER.debug("exit from getDistributionTargets(workbasketId). Returning {} resulting Objects: {} ",
                    numberOfResultObjects, LoggerUtils.listToString(result));
            }
        }

    }

    @Override
    public List<WorkbasketSummary> getDistributionSources(String workbasketId)
        throws NotAuthorizedException, WorkbasketNotFoundException {
        LOGGER.debug("entry to getDistributionSources(workbasketId = {})", workbasketId);
        List<WorkbasketSummary> result = new ArrayList<>();
        try {
            taskanaEngine.openConnection();
            // check that source workbasket exists
            getWorkbasket(workbasketId);
            if (!taskanaEngine.isUserInRole(TaskanaRole.ADMIN, TaskanaRole.BUSINESS_ADMIN)) {
                checkAuthorization(workbasketId, WorkbasketPermission.READ);
            }
            List<WorkbasketSummaryImpl> distributionSources = workbasketMapper
                .findDistributionSources(workbasketId);
            result.addAll(distributionSources);
            return result;
        } finally {
            taskanaEngine.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = result.size();
                LOGGER.debug("exit from getDistributionSources(workbasketId). Returning {} resulting Objects: {} ",
                    numberOfResultObjects, LoggerUtils.listToString(result));
            }
        }
    }

    @Override
    public List<WorkbasketSummary> getDistributionSources(String workbasketKey, String domain)
        throws NotAuthorizedException, WorkbasketNotFoundException {
        LOGGER.debug("entry to getDistributionSources(workbasketKey = {}, domain = {})", workbasketKey, domain);
        List<WorkbasketSummary> result = new ArrayList<>();
        try {
            taskanaEngine.openConnection();
            // check that source workbasket exists
            Workbasket workbasket = getWorkbasket(workbasketKey, domain);
            if (!taskanaEngine.isUserInRole(TaskanaRole.ADMIN, TaskanaRole.BUSINESS_ADMIN)) {
                checkAuthorization(workbasket.getId(), WorkbasketPermission.READ);
            }
            List<WorkbasketSummaryImpl> distributionSources = workbasketMapper
                .findDistributionSources(workbasket.getId());
            result.addAll(distributionSources);
            return result;
        } finally {
            taskanaEngine.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = result.size();
                LOGGER.debug("exit from getDistributionSources(workbasketId). Returning {} resulting Objects: {} ",
                    numberOfResultObjects, LoggerUtils.listToString(result));
            }
        }
    }

    @Override
    public void setDistributionTargets(String sourceWorkbasketId, List<String> targetWorkbasketIds)
        throws WorkbasketNotFoundException, NotAuthorizedException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to setDistributionTargets(sourceWorkbasketId = {}, targetWorkazketIds = {})",
                sourceWorkbasketId,
                LoggerUtils.listToString(targetWorkbasketIds));
        }
        taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
        try {
            taskanaEngine.openConnection();
            // check existence of source workbasket
            WorkbasketImpl sourceWorkbasket = (WorkbasketImpl) getWorkbasket(sourceWorkbasketId);
            distributionTargetMapper.deleteAllDistributionTargetsBySourceId(sourceWorkbasketId);

            sourceWorkbasket.setModified(Instant.now());
            workbasketMapper.update(sourceWorkbasket);

            if (targetWorkbasketIds != null) {
                for (String targetId : targetWorkbasketIds) {
                    // check for existence of target workbasket
                    getWorkbasket(targetId);
                    distributionTargetMapper.insert(sourceWorkbasketId, targetId);
                    LOGGER.debug(
                        "Method setDistributionTargets() created distributiontarget for source '{}' and target {}",
                        sourceWorkbasketId, targetId);
                }
            }
        } finally {
            taskanaEngine.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("setDistributionTargets set {} distribution targets to source workbasket {} ",
                    targetWorkbasketIds == null ? 0 : targetWorkbasketIds.size(), sourceWorkbasketId);
            }
        }

    }

    @Override
    public void addDistributionTarget(String sourceWorkbasketId, String targetWorkbasketId)
        throws NotAuthorizedException, WorkbasketNotFoundException {
        LOGGER.debug("entry to addDistributionTarget(sourceWorkbasketId = {}, targetWorkbasketId = {})",
            sourceWorkbasketId, targetWorkbasketId);
        taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
        try {
            taskanaEngine.openConnection();
            // check existence of source workbasket
            WorkbasketImpl sourceWorkbasket = (WorkbasketImpl) getWorkbasket(sourceWorkbasketId);
            // check existence of target workbasket
            getWorkbasket(targetWorkbasketId);
            // check whether the target is already set as target
            int numOfDistTargets = distributionTargetMapper.getNumberOfDistributionTargets(sourceWorkbasketId,
                targetWorkbasketId);
            if (numOfDistTargets > 0) {
                LOGGER.debug(
                    "addDistributionTarget detected that the specified distribution target exists already. Doing nothing...");
            } else {
                distributionTargetMapper.insert(sourceWorkbasketId, targetWorkbasketId);
                LOGGER.debug("addDistributionTarget inserted distribution target sourceId = {}, targetId = {}",
                    sourceWorkbasketId, targetWorkbasketId);
                sourceWorkbasket.setModified(Instant.now());
                workbasketMapper.update(sourceWorkbasket);
            }

        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from addDistributionTarget");
        }

    }

    @Override
    public void removeDistributionTarget(String sourceWorkbasketId, String targetWorkbasketId)
        throws NotAuthorizedException {
        LOGGER.debug("entry to removeDistributionTarget(sourceWorkbasketId = {}, targetWorkbasketId = {})",
            sourceWorkbasketId, targetWorkbasketId);
        taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
        try {
            taskanaEngine.openConnection();
            // don't check existence of source / target workbasket to enable cleanup even if the db is corrupted
            // check whether the target is set as target
            int numberOfDistTargets = distributionTargetMapper.getNumberOfDistributionTargets(sourceWorkbasketId,
                targetWorkbasketId);
            if (numberOfDistTargets > 0) {
                distributionTargetMapper.delete(sourceWorkbasketId, targetWorkbasketId);
                LOGGER.debug("removeDistributionTarget deleted distribution target sourceId = {}, targetId = {}",
                    sourceWorkbasketId, targetWorkbasketId);

                try {
                    WorkbasketImpl sourceWorkbasket = (WorkbasketImpl) getWorkbasket(sourceWorkbasketId);
                    sourceWorkbasket.setModified(Instant.now());
                    workbasketMapper.update(sourceWorkbasket);
                } catch (WorkbasketNotFoundException e) {
                    LOGGER.debug(
                        "removeDistributionTarget found that the source workbasket {} doesn't exist. Ignoring the request... ",
                        sourceWorkbasketId);
                }

            } else {
                LOGGER.debug(
                    "removeDistributionTarget detected that the specified distribution target doesn't exist. Doing nothing...");
            }
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from addDistributionTarget");
        }
    }

    @Override
    public void deleteWorkbasket(String workbasketId)
        throws NotAuthorizedException, WorkbasketNotFoundException, WorkbasketInUseException, InvalidArgumentException {
        LOGGER.debug("entry to deleteWorkbasket(workbasketId = {})", workbasketId);
        taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
        try {
            taskanaEngine.openConnection();
            if (workbasketId == null || workbasketId.isEmpty()) {
                throw new InvalidArgumentException("The WorkbasketId can´t be NULL or EMPTY for deleteWorkbasket()");
            }

            // check if the workbasket does exist and is empty (Task)
            Workbasket wb = this.getWorkbasket(workbasketId);

            long numTasksInWorkbasket = taskanaEngine.getSqlSession()
                .getMapper(TaskMapper.class)
                .countTasksInWorkbasket(workbasketId)
                .longValue();

            if (numTasksInWorkbasket > 0) {
                throw new WorkbasketInUseException(
                    "Workbasket is used on tasks and can´t be deleted. WorkbasketId = \"" + workbasketId
                        + "\" and WorkbasketKey = \"" + wb.getKey() + "\" in domain = \"" + wb.getDomain() + "\"");
            }

            // delete workbasket and sub-tables
            distributionTargetMapper.deleteAllDistributionTargetsBySourceId(wb.getId());
            distributionTargetMapper.deleteAllDistributionTargetsByTargetId(wb.getId());
            workbasketAccessMapper.deleteAllAccessItemsForWorkbasketId(wb.getId());
            workbasketMapper.delete(workbasketId);
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from deleteWorkbasket(workbasketId = {})", workbasketId);
        }
    }

    @Override
    public WorkbasketAccessItemQuery createWorkbasketAccessItemQuery() throws NotAuthorizedException {
        taskanaEngine.checkRoleMembership(TaskanaRole.ADMIN, TaskanaRole.BUSINESS_ADMIN);
        return new WorkbasketAccessItemQueryImpl(this.taskanaEngine);
    }

}
