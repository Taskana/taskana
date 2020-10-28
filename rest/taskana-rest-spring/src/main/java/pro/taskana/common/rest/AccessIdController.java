package pro.taskana.common.rest;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.rest.ldap.LdapClient;
import pro.taskana.common.rest.models.AccessIdRepresentationModel;

/** Controller for access id validation. */
@RestController
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class AccessIdController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccessIdController.class);

  private final LdapClient ldapClient;
  private final TaskanaEngine taskanaEngine;

  @Autowired
  public AccessIdController(LdapClient ldapClient, TaskanaEngine taskanaEngine) {
    this.ldapClient = ldapClient;
    this.taskanaEngine = taskanaEngine;
  }

  @GetMapping(path = Mapping.URL_ACCESSID)
  public ResponseEntity<List<AccessIdRepresentationModel>> validateAccessIds(
      @RequestParam("search-for") String searchFor)
      throws InvalidArgumentException, NotAuthorizedException {

    LOGGER.debug("Entry to validateAccessIds(search-for= {})", searchFor);

    taskanaEngine.checkRoleMembership(TaskanaRole.ADMIN, TaskanaRole.BUSINESS_ADMIN);

    if (searchFor.length() < ldapClient.getMinSearchForLength()) {
      throw new InvalidArgumentException(
          "searchFor string '"
              + searchFor
              + "' is too short. Minimum searchFor length = "
              + ldapClient.getMinSearchForLength());
    }
    ResponseEntity<List<AccessIdRepresentationModel>> response;
    List<AccessIdRepresentationModel> accessIdUsers = ldapClient.searchUsersAndGroups(searchFor);
    response = ResponseEntity.ok(accessIdUsers);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from validateAccessIds(), returning {}", response);
    }

    return response;
  }

  @GetMapping(path = Mapping.URL_ACCESSID_GROUPS)
  public ResponseEntity<List<AccessIdRepresentationModel>> getGroupsByAccessId(
      @RequestParam("access-id") String accessId)
      throws InvalidArgumentException, NotAuthorizedException {

    LOGGER.debug("Entry to getGroupsByAccessId(access-id= {})", accessId);

    taskanaEngine.checkRoleMembership(TaskanaRole.ADMIN, TaskanaRole.BUSINESS_ADMIN);

    if (!validateAccessId(accessId)) {
      throw new InvalidArgumentException("The accessId is invalid");
    }

    List<AccessIdRepresentationModel> accessIds =
        ldapClient.searchGroupsAccessIdIsMemberOf(accessId);
    ResponseEntity<List<AccessIdRepresentationModel>> response = ResponseEntity.ok(accessIds);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getGroupsByAccessId(), returning {}", response);
    }
    return response;
  }

  private boolean validateAccessId(String accessId) throws InvalidArgumentException {
    return ldapClient.searchUsersAndGroups(accessId).size() == 1;
  }
}
