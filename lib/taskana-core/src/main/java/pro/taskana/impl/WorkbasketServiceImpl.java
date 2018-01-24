package pro.taskana.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskanaEngine;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketQuery;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.model.WorkbasketAccessItem;
import pro.taskana.model.WorkbasketAuthorization;
import pro.taskana.model.mappings.DistributionTargetMapper;
import pro.taskana.model.mappings.WorkbasketAccessMapper;
import pro.taskana.model.mappings.WorkbasketMapper;
import pro.taskana.security.CurrentUserContext;

/**
 * This is the implementation of WorkbasketService.
 */
public class WorkbasketServiceImpl implements WorkbasketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkbasketServiceImpl.class);
    private static final String ID_PREFIX_WORKBASKET = "WBI";
    private static final String ID_PREFIX_WORKBASKET_AUTHORIZATION = "WAI";
    private TaskanaEngine taskanaEngine;
    private TaskanaEngineImpl taskanaEngineImpl;
    private WorkbasketMapper workbasketMapper;
    private DistributionTargetMapper distributionTargetMapper;
    private WorkbasketAccessMapper workbasketAccessMapper;

    public WorkbasketServiceImpl() {
    }

    public WorkbasketServiceImpl(TaskanaEngine taskanaEngine, WorkbasketMapper workbasketMapper,
        DistributionTargetMapper distributionTargetMapper, WorkbasketAccessMapper workbasketAccessMapper) {
        this.taskanaEngine = taskanaEngine;
        this.taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
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
            taskanaEngineImpl.openConnection();
            result = workbasketMapper.findById(workbasketId);
            if (result == null) {
                LOGGER.error(
                    "Method getWorkbasket() didn't find workbasket with ID {}. Throwing WorkbasketNotFoundException",
                    workbasketId);
                throw new WorkbasketNotFoundException(workbasketId);
            }
            this.checkAuthorization(result.getKey(), WorkbasketAuthorization.READ);
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getWorkbasket(workbasketId). Returning result {} ", result);
        }
    }

    @Override
    public Workbasket getWorkbasketByKey(String workbasketKey)
        throws WorkbasketNotFoundException, NotAuthorizedException {
        LOGGER.debug("entry to getWorkbasketByKey(workbasketKey = {})", workbasketKey);
        Workbasket result = null;
        try {
            taskanaEngineImpl.openConnection();
            result = workbasketMapper.findByKey(workbasketKey);
            if (result == null) {
                LOGGER.error(
                    "Method getWorkbasketByKey() didn't find workbasket with key {}. Throwing WorkbasketNotFoundException",
                    workbasketKey);
                throw new WorkbasketNotFoundException(workbasketKey);
            }
            this.checkAuthorization(workbasketKey, WorkbasketAuthorization.READ);
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
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
            taskanaEngineImpl.openConnection();
            // use a set to avoid duplicates
            Set<WorkbasketSummary> workbaskets = new HashSet<>();
            for (String accessId : CurrentUserContext.getAccessIds()) {
                workbaskets.addAll(workbasketMapper.findByPermission(permissions, accessId));
            }
            result = new ArrayList<WorkbasketSummary>();
            result.addAll(workbaskets);
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = result == null ? 0 : result.size();
                LOGGER.debug("exit from getWorkbaskets(permissions). Returning {} resulting Objects: {} ",
                    numberOfResultObjects, LoggerUtils.listToString(result));
            }
        }
    }

    @Override
    public List<WorkbasketSummary> getWorkbaskets() {
        LOGGER.debug("entry to getWorkbaskets()");
        List<WorkbasketSummary> workbaskets = new ArrayList<>();
        try {
            taskanaEngineImpl.openConnection();
            List<WorkbasketSummaryImpl> workbasketImpls = workbasketMapper.findAll();
            for (WorkbasketSummaryImpl workbasketSummaryImpl : workbasketImpls) {
                workbaskets.add(workbasketSummaryImpl);
            }
            return workbaskets;
        } finally {
            taskanaEngineImpl.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = workbaskets == null ? 0 : workbaskets.size();
                LOGGER.debug("exit from getWorkbaskets(). Returning {} resulting Objects: {} ", numberOfResultObjects,
                    LoggerUtils.listToString(workbaskets));
            }
        }
    }

    @Override
    public Workbasket createWorkbasket(Workbasket newWorkbasket)
        throws InvalidWorkbasketException, WorkbasketNotFoundException, NotAuthorizedException {
        LOGGER.debug("entry to createtWorkbasket(workbasket)", newWorkbasket);
        Workbasket result = null;
        WorkbasketImpl workbasket = (WorkbasketImpl) newWorkbasket;
        try {
            taskanaEngineImpl.openConnection();
            Instant now = Instant.now();
            workbasket.setCreated(now);
            workbasket.setModified(now);
            if (workbasket.getId() == null || workbasket.getId().isEmpty()) {
                workbasket.setId(IdGenerator.generateWithPrefix(ID_PREFIX_WORKBASKET));
            }
            validateWorkbasket(workbasket);

            workbasketMapper.insert(workbasket);
            LOGGER.debug("Method createWorkbasket() created Workbasket '{}'", workbasket);
            if (workbasket.getDistributionTargets() != null) {
                for (WorkbasketSummary distributionTarget : workbasket.getDistributionTargets()) {
                    // validate that all distribution targets exist
                    this.getWorkbasket(distributionTarget.getId());
                    distributionTargetMapper.insert(workbasket.getId(), distributionTarget.getId());
                    LOGGER.debug("Method createWorkbasket() created distributiontarget for source '{}' and target {}",
                        workbasket.getId(), distributionTarget.getId());
                }
            }
            result = workbasketMapper.findById(workbasket.getId());
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from createWorkbasket(workbasket). Returning result {} ", result);
        }
    }

    @Override
    public Workbasket updateWorkbasket(Workbasket workbasketToUpdate)
        throws NotAuthorizedException, WorkbasketNotFoundException, InvalidWorkbasketException {
        LOGGER.debug("entry to updateWorkbasket(workbasket)", workbasketToUpdate);

        Workbasket result = null;
        WorkbasketImpl workbasket = (WorkbasketImpl) workbasketToUpdate;
        try {
            taskanaEngineImpl.openConnection();
            workbasket.setModified(Instant.now());
            workbasketMapper.update(workbasket);
            LOGGER.debug("Method updateWorkbasket() updated workbasket '{}'", workbasket.getId());
            List<String> oldDistributionTargets = distributionTargetMapper.findBySourceId(workbasket.getId());
            List<WorkbasketSummary> newDistributionTargets = workbasket.getDistributionTargets();
            for (WorkbasketSummary distributionTarget : newDistributionTargets) {
                if (!oldDistributionTargets.contains(distributionTarget.getId())) {
                    // check that old distribution target exists
                    getWorkbasket(distributionTarget.getId());
                    distributionTargetMapper.insert(workbasket.getId(), distributionTarget.getId());
                    LOGGER.debug("Method updateWorkbasket() created distributionTarget for '{}' and '{}'",
                        workbasket.getId(), distributionTarget.getId());
                } else {
                    oldDistributionTargets.remove(distributionTarget.getId());
                }
            }
            distributionTargetMapper.deleteMultiple(workbasket.getId(), oldDistributionTargets);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.debug(
                    "Method updateWorkbasket() deleted distributionTargets for '{}' and old distribution targets {}",
                    workbasket.getId(), LoggerUtils.listToString(oldDistributionTargets));
            }
            result = workbasketMapper.findById(workbasket.getId());
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from updateWorkbasket(). Returning result {} ", result);
        }
    }

    @Override
    public WorkbasketAccessItem createWorkbasketAuthorization(WorkbasketAccessItem workbasketAccessItem) {
        LOGGER.debug("entry to createWorkbasketAuthorization(workbasketAccessItem = {})", workbasketAccessItem);
        try {
            taskanaEngineImpl.openConnection();
            workbasketAccessItem.setId(IdGenerator.generateWithPrefix(ID_PREFIX_WORKBASKET_AUTHORIZATION));
            workbasketAccessMapper.insert(workbasketAccessItem);
            LOGGER.debug("Method createWorkbasketAuthorization() created workbaskteAccessItem {}",
                workbasketAccessItem);
            return workbasketAccessItem;
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from createWorkbasketAuthorization(workbasketAccessItem). Returning result {}",
                workbasketAccessItem);
        }
    }

    @Override
    public WorkbasketAccessItem getWorkbasketAuthorization(String id) {
        LOGGER.debug("entry to getWorkbasketAuthorization(id = {})", id);
        WorkbasketAccessItem result = null;
        try {
            taskanaEngineImpl.openConnection();
            result = workbasketAccessMapper.findById(id);
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getWorkbasketAuthorization(id). Returning result {}", result);
        }
    }

    @Override
    public void deleteWorkbasketAuthorization(String id) {
        LOGGER.debug("entry to deleteWorkbasketAuthorization(id = {})", id);
        try {
            taskanaEngineImpl.openConnection();
            workbasketAccessMapper.delete(id);
            LOGGER.debug("Method deleteWorkbasketAuthorization() deleted workbasketAccessItem wit Id {}", id);
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from deleteWorkbasketAuthorization(id).");
        }
    }

    @Override
    public List<WorkbasketAccessItem> getAllAuthorizations() {
        LOGGER.debug("entry to getAllAuthorizations()");
        List<WorkbasketAccessItem> result = null;
        try {
            taskanaEngineImpl.openConnection();
            result = workbasketAccessMapper.findAll();
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = result == null ? 0 : result.size();
                LOGGER.debug("exit from getAllAuthorizations(). Returning {} resulting Objects: {} ",
                    numberOfResultObjects, LoggerUtils.listToString(result));
            }
        }
    }

    @Override
    public void checkAuthorization(String workbasketKey, WorkbasketAuthorization workbasketAuthorization)
        throws NotAuthorizedException {
        LOGGER.debug("entry to checkAuthorization(workbasketId = {}, workbasketAuthorization = {})", workbasketKey,
            workbasketAuthorization);
        boolean isAuthorized = false;
        try {
            taskanaEngineImpl.openConnection();
            // Skip permission check is security is not enabled
            if (!taskanaEngine.getConfiguration().isSecurityEnabled()) {
                LOGGER.debug("Skipping permissions check since security is disabled.");
                isAuthorized = true;
                return;
            }

            List<String> accessIds = CurrentUserContext.getAccessIds();
            LOGGER.debug("checkAuthorization: Verifying that {} has the permission {} on workbasket {}",
                CurrentUserContext.getUserid(), workbasketAuthorization.name(), workbasketKey);

            List<WorkbasketAccessItem> accessItems = workbasketAccessMapper
                .findByWorkbasketAndAccessIdAndAuthorization(workbasketKey, accessIds, workbasketAuthorization.name());

            if (accessItems.size() <= 0) {
                throw new NotAuthorizedException("Not authorized. Authorization '" + workbasketAuthorization.name()
                    + "' on workbasket '" + workbasketKey + "' is needed.");
            }

            isAuthorized = true;

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from checkAuthorization(). User is authorized = {}.", isAuthorized);
        }
    }

    @Override
    public WorkbasketAccessItem updateWorkbasketAuthorization(WorkbasketAccessItem workbasketAccessItem) {
        LOGGER.debug("entry to updateWorkbasketAuthorization(workbasketAccessItem = {}", workbasketAccessItem);
        try {
            taskanaEngineImpl.openConnection();
            workbasketAccessMapper.update(workbasketAccessItem);
            LOGGER.debug("Method updateWorkbasketAuthorization() updated workbasketAccessItem {}",
                workbasketAccessItem);
            return workbasketAccessItem;
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from updateWorkbasketAuthorization(workbasketAccessItem). Returning {}",
                workbasketAccessItem);
        }
    }

    @Override
    public List<WorkbasketAccessItem> getWorkbasketAuthorizations(String workbasketKey) {
        LOGGER.debug("entry to getWorkbasketAuthorizations(workbasketId = {})", workbasketKey);
        List<WorkbasketAccessItem> result = null;
        try {
            taskanaEngineImpl.openConnection();
            result = workbasketAccessMapper.findByWorkbasketKey(workbasketKey);
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = result == null ? 0 : result.size();
                LOGGER.debug("exit from getWorkbasketAuthorizations(workbasketId). Returning {} resulting Objects: {} ",
                    numberOfResultObjects, LoggerUtils.listToString(result));
            }
        }
    }

    @Override
    public List<WorkbasketAuthorization> getPermissionsForWorkbasket(String workbasketKey) {
        List<WorkbasketAuthorization> permissions = new ArrayList<>();
        WorkbasketAccessItem wbAcc = workbasketAccessMapper.findByWorkbasketAndAccessId(workbasketKey, CurrentUserContext.getAccessIds());
        this.addWorkbasketAccessItemValuesToPermissionSet(wbAcc, permissions);
        return permissions;
    }

    @Override
    public WorkbasketQuery createWorkbasketQuery() {
        return new WorkbasketQueryImpl(taskanaEngine, workbasketAccessMapper);
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

    private void addWorkbasketAccessItemValuesToPermissionSet(WorkbasketAccessItem workbasketAccessItem, List<WorkbasketAuthorization> permissions) {
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
    }

    @Override
    public Workbasket newWorkbasket() {
        return new WorkbasketImpl();
    }
}
