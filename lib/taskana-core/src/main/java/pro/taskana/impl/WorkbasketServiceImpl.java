package pro.taskana.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.taskana.TaskanaEngine;
import pro.taskana.WorkbasketService;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.util.IdGenerator;
import pro.taskana.model.Workbasket;
import pro.taskana.model.WorkbasketAccessItem;
import pro.taskana.model.WorkbasketAuthorization;
import pro.taskana.model.mappings.DistributionTargetMapper;
import pro.taskana.model.mappings.WorkbasketAccessMapper;
import pro.taskana.model.mappings.WorkbasketMapper;
import pro.taskana.security.CurrentUserContext;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This is the implementation of WorkbasketService.
 */
public class WorkbasketServiceImpl implements WorkbasketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkbasketServiceImpl.class);

    private static final String ID_PREFIX_WORKBASKET = "WBI";
    private static final String ID_PREFIX_WORKBASKET_AUTHORIZATION = "WAI";

    private TaskanaEngine taskanaEngine;

    private WorkbasketMapper workbasketMapper;
    private DistributionTargetMapper distributionTargetMapper;
    private WorkbasketAccessMapper workbasketAccessMapper;

    public WorkbasketServiceImpl() {
    }

    public WorkbasketServiceImpl(TaskanaEngine taskanaEngine, WorkbasketMapper workbasketMapper,
            DistributionTargetMapper distributionTargetMapper, WorkbasketAccessMapper workbasketAccessMapper) {
        this.taskanaEngine = taskanaEngine;
        this.workbasketMapper = workbasketMapper;
        this.distributionTargetMapper = distributionTargetMapper;
        this.workbasketAccessMapper = workbasketAccessMapper;
    }

    @Override
    public Workbasket getWorkbasket(String workbasketId) throws WorkbasketNotFoundException {
        Workbasket workbasket = workbasketMapper.findById(workbasketId);
        if (workbasket == null) {
            throw new WorkbasketNotFoundException(workbasketId);
        }
        return workbasket;
    }

    @Override
    public List<Workbasket> getWorkbaskets(List<WorkbasketAuthorization> permissions) {
        //use a set to avoid duplicates
        Set<Workbasket> workbaskets = new HashSet<>();
        for (String accessId : CurrentUserContext.getAccessIds()) {
            workbaskets.addAll(workbasketMapper.findByPermission(permissions, accessId));
        }
        List<Workbasket> workbasketList = new ArrayList<Workbasket>();
        workbasketList.addAll(workbaskets);
        return workbasketList;
    }

    @Override
    public List<Workbasket> getWorkbaskets() {
        return workbasketMapper.findAll();
    }

    @Override
    public Workbasket createWorkbasket(Workbasket workbasket) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        workbasket.setCreated(now);
        workbasket.setModified(now);
        if (workbasket.getId() == null || workbasket.getId().isEmpty()) {
            workbasket.setId(IdGenerator.generateWithPrefix(ID_PREFIX_WORKBASKET));
        }
        workbasketMapper.insert(workbasket);
        LOGGER.debug("Workbasket '{}' created", workbasket.getId());
        if (workbasket.getDistributionTargets() != null) {
            for (Workbasket distributionTarget : workbasket.getDistributionTargets()) {
                if (workbasketMapper.findById(distributionTarget.getId()) == null) {
                    distributionTarget.setCreated(now);
                    distributionTarget.setModified(now);
                    workbasketMapper.insert(distributionTarget);
                    LOGGER.debug("Workbasket '{}' created", distributionTarget.getId());
                }
                distributionTargetMapper.insert(workbasket.getId(), distributionTarget.getId());
            }
        }
        return workbasketMapper.findById(workbasket.getId());
    }

    @Override
    public Workbasket updateWorkbasket(Workbasket workbasket) throws NotAuthorizedException {
        workbasket.setModified(new Timestamp(System.currentTimeMillis()));
        workbasketMapper.update(workbasket);
        List<String> oldDistributionTargets = distributionTargetMapper.findBySourceId(workbasket.getId());
        List<Workbasket> distributionTargets = workbasket.getDistributionTargets();
        for (Workbasket distributionTarget : distributionTargets) {
            if (!oldDistributionTargets.contains(distributionTarget.getId())) {
                if (workbasketMapper.findById(distributionTarget.getId()) == null) {
                    workbasketMapper.insert(distributionTarget);
                    LOGGER.debug("Workbasket '{}' created", distributionTarget.getId());
                }
                distributionTargetMapper.insert(workbasket.getId(), distributionTarget.getId());
            } else {
                oldDistributionTargets.remove(distributionTarget.getId());
            }
        }
        distributionTargetMapper.deleteMultiple(workbasket.getId(), oldDistributionTargets);
        LOGGER.debug("Workbasket '{}' updated", workbasket.getId());
        return workbasketMapper.findById(workbasket.getId());
    }

    @Override
    public WorkbasketAccessItem createWorkbasketAuthorization(WorkbasketAccessItem workbasketAccessItem) {
        workbasketAccessItem.setId(IdGenerator.generateWithPrefix(ID_PREFIX_WORKBASKET_AUTHORIZATION));
        workbasketAccessMapper.insert(workbasketAccessItem);
        return workbasketAccessItem;
    }

    @Override
    public WorkbasketAccessItem getWorkbasketAuthorization(String id) {
        return workbasketAccessMapper.findById(id);
    }

    @Override
    public void deleteWorkbasketAuthorization(String id) {
        workbasketAccessMapper.delete(id);
    }

    @Override
    public List<WorkbasketAccessItem> getAllAuthorizations() {
        return workbasketAccessMapper.findAll();
    }

    @Override
    public void checkAuthorization(String workbasketId, WorkbasketAuthorization workbasketAuthorization)
            throws NotAuthorizedException {

        // Skip permission check if security is not enabled
        if (!taskanaEngine.getConfiguration().isSecurityEnabled()) {
            LOGGER.debug("Skipping permissions check since security is disabled.");
            return;
        }

        List<String> accessIds = CurrentUserContext.getAccessIds();
        LOGGER.debug("Verifying that {} has the permission {} on workbasket {}",
                CurrentUserContext.getUserid(), workbasketAuthorization.name(), workbasketId);

        List<WorkbasketAccessItem> accessItems = workbasketAccessMapper
                .findByWorkbasketAndAccessIdAndAuthorizations(workbasketId, accessIds, workbasketAuthorization.name());

        if (accessItems.size() <= 0) {
            throw new NotAuthorizedException("Not authorized. Authorization '" + workbasketAuthorization.name()
                    + "' on workbasket '" + workbasketId + "' is needed.");

        }
    }

    @Override
    public WorkbasketAccessItem updateWorkbasketAuthorization(WorkbasketAccessItem workbasketAccessItem) {
        workbasketAccessMapper.update(workbasketAccessItem);
        return workbasketAccessItem;
    }

    @Override
    public List<WorkbasketAccessItem> getWorkbasketAuthorizations(String workbasketId) {
        return workbasketAccessMapper.findByWorkbasketId(workbasketId);
    }
}
