package pro.taskana.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskSummary;
import pro.taskana.TaskanaEngine;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketAccessItemQuery;
import pro.taskana.WorkbasketQuery;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.SystemException;
import pro.taskana.exceptions.WorkbasketInUseException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.mappings.DistributionTargetMapper;
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

    public WorkbasketServiceImpl() {
    }

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
                LOGGER.error(
                    "Method getWorkbasket() didn't find workbasket with ID {}. Throwing WorkbasketNotFoundException",
                    workbasketId);
                throw new WorkbasketNotFoundException(workbasketId);
            }
            this.checkAuthorization(workbasketId, WorkbasketAuthorization.READ);
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
                LOGGER.error(
                    "Method getWorkbasketByKey() didn't find workbasket with key {}. Throwing WorkbasketNotFoundException",
                    workbasketKey);
                throw new WorkbasketNotFoundException(workbasketKey);
            }
            this.checkAuthorization(workbasketKey, domain, WorkbasketAuthorization.READ);
            return result;
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from getWorkbasket(workbasketId). Returning result {} ", result);
        }
    }

    @Override
    public List<WorkbasketSummary> getWorkbaskets(List<WorkbasketAuthorization> permissions) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to getWorkbaskets(permissions = {})", LoggerUtils.listToString(permissions));
        }
        List<WorkbasketSummary> result = null;
        try {
            taskanaEngine.openConnection();
            // use a set to avoid duplicates
            Set<WorkbasketSummary> workbaskets = new HashSet<>();
            for (String accessId : CurrentUserContext.getAccessIds()) {
                workbaskets.addAll(workbasketMapper.findByPermission(permissions, accessId));
            }
            result = new ArrayList<>();
            result.addAll(workbaskets);
            return result;
        } finally {
            taskanaEngine.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = result == null ? 0 : result.size();
                LOGGER.debug("exit from getWorkbaskets(permissions). Returning {} resulting Objects: {} ",
                    numberOfResultObjects, LoggerUtils.listToString(result));
            }
        }
    }

    @Override
    public Workbasket createWorkbasket(Workbasket newWorkbasket)
        throws InvalidWorkbasketException, NotAuthorizedException {
        LOGGER.debug("entry to createtWorkbasket(workbasket)", newWorkbasket);
        taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
        Workbasket result = null;
        WorkbasketImpl workbasket = (WorkbasketImpl) newWorkbasket;
        try {
            taskanaEngine.openConnection();
            Instant now = Instant.now();
            workbasket.setCreated(now);
            workbasket.setModified(now);
            if (workbasket.getId() == null || workbasket.getId().isEmpty()) {
                workbasket.setId(IdGenerator.generateWithPrefix(ID_PREFIX_WORKBASKET));
            }
            validateWorkbasket(workbasket);

            workbasketMapper.insert(workbasket);
            LOGGER.debug("Method createWorkbasket() created Workbasket '{}'", workbasket);
            result = workbasketMapper.findById(workbasket.getId());
            return result;
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from createWorkbasket(workbasket). Returning result {} ", result);
        }
    }

    @Override
    public Workbasket updateWorkbasket(Workbasket workbasketToUpdate)
        throws NotAuthorizedException, WorkbasketNotFoundException, InvalidWorkbasketException {
        LOGGER.debug("entry to updateWorkbasket(workbasket)", workbasketToUpdate);
        taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);

        Workbasket result = null;
        WorkbasketImpl workbasket = (WorkbasketImpl) workbasketToUpdate;
        try {
            taskanaEngine.openConnection();
            workbasket.setModified(Instant.now());
            workbasketMapper.update(workbasket);
            LOGGER.debug("Method updateWorkbasket() updated workbasket '{}'", workbasket.getId());
            result = workbasketMapper.findById(workbasket.getId());
            return result;
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from updateWorkbasket(). Returning result {} ", result);
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
    public WorkbasketAccessItem createWorkbasketAuthorization(WorkbasketAccessItem workbasketAccessItem)
        throws InvalidArgumentException, NotAuthorizedException {
        LOGGER.debug("entry to createWorkbasketAuthorization(workbasketAccessItem = {})", workbasketAccessItem);
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
            workbasketAccessMapper.insert(accessItem);
            LOGGER.debug("Method createWorkbasketAuthorization() created workbaskteAccessItem {}",
                accessItem);
            return accessItem;
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from createWorkbasketAuthorization(workbasketAccessItem). Returning result {}",
                accessItem);
        }
    }

    @Override
    public void setWorkbasketAuthorizations(String workbasketId, List<WorkbasketAccessItem> wbAccessItems)
        throws InvalidArgumentException {
        List<WorkbasketAccessItemImpl> newItems = new ArrayList<>();
        try {
            LOGGER.debug("entry to setWorkbasketAuthorizations(workbasketAccessItems = {})", wbAccessItems.toString());
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

                // delete all current ones
                workbasketAccessMapper.deleteAllAccessItemsForWorkbasketId(workbasketId);

                // add all
                newItems.stream().forEach(item -> workbasketAccessMapper.insert(item));
            }
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from setWorkbasketAuthorizations(workbasketAccessItems = {})", wbAccessItems.toString());
        }
    }

    @Override
    public void deleteWorkbasketAuthorization(String accessItemId) throws NotAuthorizedException {
        LOGGER.debug("entry to deleteWorkbasketAuthorization(id = {})", accessItemId);
        taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
        try {
            taskanaEngine.openConnection();
            workbasketAccessMapper.delete(accessItemId);
            LOGGER.debug("Method deleteWorkbasketAuthorization() deleted workbasketAccessItem wit Id {}", accessItemId);
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from deleteWorkbasketAuthorization(id).");
        }
    }

    @Override
    public void deleteWorkbasketAuthorizationForAccessId(String accessId) {
        LOGGER.debug("entry to deleteWorkbasketAuthorizationByAccessId(accessId = {})", accessId);
        try {
            taskanaEngine.openConnection();
            workbasketAccessMapper.deleteAccessItemsForAccessId(accessId);
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from deleteWorkbasketAuthorizationByAccessId(accessId={}).", accessId);
        }
    }

    @Override
    public void checkAuthorization(String workbasketId,
        WorkbasketAuthorization workbasketAuthorization) throws NotAuthorizedException {
        checkAuthorization(null, null, workbasketId, workbasketAuthorization);
    }

    @Override
    public void checkAuthorization(String workbasketKey, String domain,
        WorkbasketAuthorization workbasketAuthorization)
        throws NotAuthorizedException {
        checkAuthorization(workbasketKey, domain, null, workbasketAuthorization);
    }

    @Override
    public WorkbasketAccessItem updateWorkbasketAuthorization(WorkbasketAccessItem workbasketAccessItem)
        throws InvalidArgumentException, NotAuthorizedException {
        LOGGER.debug("entry to updateWorkbasketAuthorization(workbasketAccessItem = {}", workbasketAccessItem);
        taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);
        WorkbasketAccessItemImpl accessItem = (WorkbasketAccessItemImpl) workbasketAccessItem;
        try {
            taskanaEngine.openConnection();
            WorkbasketAccessItem originalItem = workbasketAccessMapper.findById(accessItem.getId());

            if ((originalItem.getAccessId() != null && !originalItem.getAccessId().equals(accessItem.getAccessId()))
                || (originalItem.getWorkbasketId() != null
                    && !originalItem.getWorkbasketId().equals(accessItem.getWorkbasketId()))) {
                throw new InvalidArgumentException(
                    "AccessId and WorkbasketId must not be changed in updateWorkbasketAuthorization calls");
            }

            workbasketAccessMapper.update(accessItem);
            LOGGER.debug("Method updateWorkbasketAuthorization() updated workbasketAccessItem {}",
                accessItem);
            return accessItem;
        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from updateWorkbasketAuthorization(workbasketAccessItem). Returning {}",
                accessItem);
        }
    }

    @Override
    public List<WorkbasketAccessItem> getWorkbasketAuthorizations(String workbasketId) {
        LOGGER.debug("entry to getWorkbasketAuthorizations(workbasketId = {})", workbasketId);
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
                LOGGER.debug("exit from getWorkbasketAuthorizations(workbasketId). Returning {} resulting Objects: {} ",
                    numberOfResultObjects, LoggerUtils.listToString(result));
            }
        }
    }

    @Override
    public List<WorkbasketAuthorization> getPermissionsForWorkbasket(String workbasketId) {
        List<WorkbasketAuthorization> permissions = new ArrayList<>();
        WorkbasketAccessItem wbAcc = workbasketAccessMapper.findByWorkbasketAndAccessId(workbasketId,
            CurrentUserContext.getAccessIds());
        this.addWorkbasketAccessItemValuesToPermissionSet(wbAcc, permissions);
        return permissions;
    }

    @Override
    public WorkbasketQuery createWorkbasketQuery() {
        return new WorkbasketQueryImpl(taskanaEngine);
    }

    private void validateWorkbasket(Workbasket workbasket) throws InvalidWorkbasketException {
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
    }

    private void addWorkbasketAccessItemValuesToPermissionSet(WorkbasketAccessItem workbasketAccessItem,
        List<WorkbasketAuthorization> permissions) {
        if (workbasketAccessItem.isPermOpen()) {
            permissions.add(WorkbasketAuthorization.OPEN);
        }
        if (workbasketAccessItem.isPermRead()) {
            permissions.add(WorkbasketAuthorization.READ);
        }
        if (workbasketAccessItem.isPermAppend()) {
            permissions.add(WorkbasketAuthorization.APPEND);
        }
        if (workbasketAccessItem.isPermTransfer()) {
            permissions.add(WorkbasketAuthorization.TRANSFER);
        }
        if (workbasketAccessItem.isPermDistribute()) {
            permissions.add(WorkbasketAuthorization.DISTRIBUTE);
        }
        if (workbasketAccessItem.isPermCustom1()) {
            permissions.add(WorkbasketAuthorization.CUSTOM_1);
        }
        if (workbasketAccessItem.isPermCustom2()) {
            permissions.add(WorkbasketAuthorization.CUSTOM_2);
        }
        if (workbasketAccessItem.isPermCustom3()) {
            permissions.add(WorkbasketAuthorization.CUSTOM_3);
        }
        if (workbasketAccessItem.isPermCustom4()) {
            permissions.add(WorkbasketAuthorization.CUSTOM_4);
        }
        if (workbasketAccessItem.isPermCustom5()) {
            permissions.add(WorkbasketAuthorization.CUSTOM_5);
        }
        if (workbasketAccessItem.isPermCustom6()) {
            permissions.add(WorkbasketAuthorization.CUSTOM_6);
        }
        if (workbasketAccessItem.isPermCustom7()) {
            permissions.add(WorkbasketAuthorization.CUSTOM_7);
        }
        if (workbasketAccessItem.isPermCustom8()) {
            permissions.add(WorkbasketAuthorization.CUSTOM_8);
        }
        if (workbasketAccessItem.isPermCustom9()) {
            permissions.add(WorkbasketAuthorization.CUSTOM_9);
        }
        if (workbasketAccessItem.isPermCustom10()) {
            permissions.add(WorkbasketAuthorization.CUSTOM_10);
        }
        if (workbasketAccessItem.isPermCustom11()) {
            permissions.add(WorkbasketAuthorization.CUSTOM_11);
        }
        if (workbasketAccessItem.isPermCustom12()) {
            permissions.add(WorkbasketAuthorization.CUSTOM_12);
        }
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
            checkAuthorization(workbasketId, WorkbasketAuthorization.READ);
            List<WorkbasketSummaryImpl> distributionTargets = workbasketMapper
                .findByDistributionTargets(workbasketId);
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
            checkAuthorization(workbasket.getId(), WorkbasketAuthorization.READ);
            List<WorkbasketSummaryImpl> distributionTargets = workbasketMapper
                .findByDistributionTargets(workbasket.getId());
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
            checkAuthorization(workbasketId, WorkbasketAuthorization.READ);
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
            checkAuthorization(workbasket.getId(), WorkbasketAuthorization.READ);
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
            checkAuthorization(sourceWorkbasketId, WorkbasketAuthorization.READ);
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
            // check esistence of target workbasket
            getWorkbasket(targetWorkbasketId);
            checkAuthorization(sourceWorkbasketId, WorkbasketAuthorization.READ);
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
            checkAuthorization(sourceWorkbasketId, WorkbasketAuthorization.READ);
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
            List<TaskSummary> taskUsages = taskanaEngine.getTaskService()
                .createTaskQuery()
                .workbasketIdIn(wb.getId())
                .list();
            if (taskUsages == null || taskUsages.size() > 0) {
                throw new WorkbasketInUseException(
                    "Workbasket is used on tasks and can´t be deleted. WorkbasketId=" + workbasketId);
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

    private void checkAuthorization(String workbasketKey, String domain, String workbasketId,
        WorkbasketAuthorization workbasketAuthorization)
        throws NotAuthorizedException {
        LOGGER.debug("entry to checkAuthorization(workbasketId = {}, workbasketAuthorization = {})", workbasketKey,
            workbasketAuthorization);
        if (workbasketAuthorization == null) {
            throw new SystemException("checkAuthorization was called with an invalid parameter combination");
        }
        if (taskanaEngine.isUserInRole(TaskanaRole.ADMIN)) {
            LOGGER.debug("Skipping permissions check since user is in role ADMIN.");
            return;
        }

        boolean isAuthorized = false;
        try {
            taskanaEngine.openConnection();
            // Skip permission check is security is not enabled
            if (!taskanaEngine.getConfiguration().isSecurityEnabled()) {
                LOGGER.debug("Skipping permissions check since security is disabled.");
                isAuthorized = true;
                return;
            }

            List<String> accessIds = CurrentUserContext.getAccessIds();
            LOGGER.debug("checkAuthorization: Verifying that {} has the permission {} on workbasket {}",
                CurrentUserContext.getUserid(),
                workbasketAuthorization.name(), workbasketKey);

            List<WorkbasketAccessItemImpl> accessItems;

            if (workbasketKey != null) {
                accessItems = workbasketAccessMapper
                    .findByWorkbasketAccessByWorkbasketKeyDomainAndAuthorization(workbasketKey, domain, accessIds,
                        workbasketAuthorization.name());
            } else if (workbasketId != null) {
                accessItems = workbasketAccessMapper
                    .findByWorkbasketAndAccessIdAndAuthorizationsById(workbasketId, accessIds,
                        workbasketAuthorization.name());
            } else {
                throw new SystemException(
                    "checkAuthorizationImpl was called with both workbasketKey and workbasketId set to null");
            }

            if (accessItems.isEmpty()) {
                throw new NotAuthorizedException("Not authorized. Authorization '" + workbasketAuthorization.name()
                    + "' on workbasket '" + workbasketKey + "' is needed.");
            }

            isAuthorized = true;

        } finally {
            taskanaEngine.returnConnection();
            LOGGER.debug("exit from checkAuthorization(). User is authorized = {}.", isAuthorized);
        }
    }

}
