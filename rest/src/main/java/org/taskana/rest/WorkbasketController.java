package org.taskana.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.taskana.WorkbasketService;
import org.taskana.exceptions.NotAuthorizedException;
import org.taskana.exceptions.WorkbasketNotFoundException;
import org.taskana.model.Workbasket;
import org.taskana.model.WorkbasketAccessItem;

@RestController
@RequestMapping(path = "/v1/workbaskets", produces = { MediaType.APPLICATION_JSON_VALUE })
public class WorkbasketController {

	@Autowired
	private WorkbasketService workbasketService;

	@GetMapping
	public List<Workbasket> getWorkbaskets(@RequestParam MultiValueMap<String, String> params) {
		if (params.containsKey("requiredPermission")) {
			List<String> permissions = new ArrayList<>();
			params.get("requiredPermission").stream().forEach(item -> {
				permissions.addAll(Arrays.asList(item.split(",")));
			});
			return workbasketService.getWorkbaskets(permissions);
		} else {
			return workbasketService.getWorkbaskets();
		}
		
	}

	@RequestMapping(value = "/{workbasketid}")
	public ResponseEntity<Workbasket> getWorkbasketById(@PathVariable(value = "workbasketid") String workbasketId) {
		try {
			Workbasket workbasket = workbasketService.getWorkbasket(workbasketId);
			return new ResponseEntity<>(workbasket, HttpStatus.OK);
		} catch (WorkbasketNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> createWorkbasket(@RequestBody Workbasket workbasket) {
		Workbasket createdWorkbasket = workbasketService.createWorkbasket(workbasket);
		return new ResponseEntity<>(createdWorkbasket, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{workbasketid}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateWorkbasket(@PathVariable(value = "workbasketid") String workbasketId,
			@RequestBody Workbasket workbasket) {
		try {
			Workbasket updatedWorkbasket = workbasketService.updateWorkbasket(workbasket);
			return new ResponseEntity<>(updatedWorkbasket, HttpStatus.OK);
		} catch (NotAuthorizedException e) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
	}

	@RequestMapping(value = "/{workbasketid}/authorizations", method = RequestMethod.GET)
	public List<WorkbasketAccessItem> getWorkbasketAuthorizations(
			@PathVariable(value = "workbasketid") String workbasketId) {
		return workbasketService.getWorkbasketAuthorizations(workbasketId);
	}

	@RequestMapping(value = "/authorizations", method = RequestMethod.POST)
	public WorkbasketAccessItem createWorkbasketAuthorization(@RequestBody WorkbasketAccessItem workbasketAccessItem) {
		return workbasketService.createWorkbasketAuthorization(workbasketAccessItem);
	}

	@RequestMapping(value = "/authorizations/{authid}", method = RequestMethod.PUT)
	public WorkbasketAccessItem updateWorkbasketAuthorization(@PathVariable(value = "authid") String authId,
			@RequestBody WorkbasketAccessItem workbasketAccessItem) {
		return workbasketService.updateWorkbasketAuthorization(workbasketAccessItem);
	}

	@RequestMapping(value = "/authorizations/{authid}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteWorkbasketAuthorization(@PathVariable(value = "authid") String authId) {
		workbasketService.deleteWorkbasketAuthorization(authId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
