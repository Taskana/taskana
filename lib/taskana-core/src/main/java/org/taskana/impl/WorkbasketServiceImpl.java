package org.taskana.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.taskana.TaskanaEngine;
import org.taskana.WorkbasketService;
import org.taskana.exceptions.NotAuthorizedException;
import org.taskana.exceptions.WorkbasketNotFoundException;
import org.taskana.model.Workbasket;
import org.taskana.model.WorkbasketAccessItem;
import org.taskana.model.WorkbasketAuthorization;
import org.taskana.model.mappings.DistributionTargetMapper;
import org.taskana.model.mappings.WorkbasketAccessMapper;
import org.taskana.model.mappings.WorkbasketMapper;
import org.taskana.security.CurrentUserContext;

public class WorkbasketServiceImpl implements WorkbasketService {

	private static final Logger logger = LoggerFactory.getLogger(WorkbasketServiceImpl.class);

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
	public List<Workbasket> getWorkbaskets(List<String> permissions) {
		List<Workbasket> workbaskets = workbasketMapper.findByPermission(permissions, CurrentUserContext.getUserid());
		return workbaskets;
	}
	
	@Override
	public List<Workbasket> getWorkbaskets() {
		List<Workbasket> workbaskets = workbasketMapper.findAll();
		return workbaskets;
	}

	@Override
	public Workbasket createWorkbasket(Workbasket workbasket) {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		workbasket.setCreated(now);
		workbasket.setModified(now);
		if (workbasket.getId() == null || workbasket.getId().isEmpty()) {
			workbasket.setId(UUID.randomUUID().toString());
		}
		workbasketMapper.insert(workbasket);
		logger.debug("Workbasket '{}' created", workbasket.getId());
		if (workbasket.getDistributionTargets() != null) {
			for (Workbasket distributionTarget : workbasket.getDistributionTargets()) {
				if (workbasketMapper.findById(distributionTarget.getId()) == null) {
					distributionTarget.setCreated(now);
					distributionTarget.setModified(now);
					workbasketMapper.insert(distributionTarget);
					logger.debug("Workbasket '{}' created", distributionTarget.getId());
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
					logger.debug("Workbasket '{}' created", distributionTarget.getId());
				}
				distributionTargetMapper.insert(workbasket.getId(), distributionTarget.getId());
			} else {
				oldDistributionTargets.remove(distributionTarget.getId());
			}
		}
		distributionTargetMapper.deleteMultiple(workbasket.getId(), oldDistributionTargets);
		logger.debug("Workbasket '{}' updated", workbasket.getId());
		return workbasketMapper.findById(workbasket.getId());
	}

	@Override
	public WorkbasketAccessItem createWorkbasketAuthorization(WorkbasketAccessItem workbasketAccessItem) {
		workbasketAccessItem.setId(UUID.randomUUID().toString());
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
	public void checkPermission(String workbasketId, WorkbasketAuthorization workbasketAuthorization)
			throws NotAuthorizedException {

		// Skip permission check is security is not enabled
		if (!taskanaEngine.getConfiguration().isSecurityEnabled()) {
			logger.debug("Skipping permissions check since security is disabled.");
			return;
		}

		String userId = CurrentUserContext.getUserid();
		logger.debug("Verifying that {} has the permission {} on workbasket {}", userId, workbasketAuthorization.name(),
				workbasketId);

		List<WorkbasketAccessItem> accessItems = workbasketAccessMapper
				.findByWorkbasketAndUserAndAuthorization(workbasketId, userId, workbasketAuthorization.name());

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