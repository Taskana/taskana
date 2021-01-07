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

/** Controller for Access Id validation. */
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

  /**
   * This endpoint searches a provided access Id in the configured ldap.
   *
   * @title Search for Access Id (users and groups)
   * @param searchFor the Access Id which should be searched for.
   * @return a list of all found Access Ids
   * @throws InvalidArgumentException if the provided search for Access Id is shorter than the
   *     configured one.
   * @throws NotAuthorizedException if the current user is not ADMIN or BUSINESS_ADMIN.
   */
  @GetMapping(path = RestEndpoints.URL_ACCESS_ID)
  public ResponseEntity<List<AccessIdRepresentationModel>> searchUsersAndGroups(
      @RequestParam("search-for") String searchFor)
      throws InvalidArgumentException, NotAuthorizedException {

    LOGGER.debug("Entry to validateAccessIds(search-for= {})", searchFor);

    taskanaEngine.checkRoleMembership(TaskanaRole.ADMIN, TaskanaRole.BUSINESS_ADMIN);

    List<AccessIdRepresentationModel> accessIdUsers = ldapClient.searchUsersAndGroups(searchFor);
    ResponseEntity<List<AccessIdRepresentationModel>> response = ResponseEntity.ok(accessIdUsers);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from validateAccessIds(), returning {}", response);
    }

    return response;
  }

  /**
   * This endpoint retrieves all groups a given Access Id belongs to.
   *
   * @title Get groups for Access Id
   * @param accessId the Access Id whose groups should be determined.
   * @return a list of the group Access Ids the requested Access Id belongs to
   * @throws InvalidArgumentException if the requested Access Id does not exist or is not unique.
   * @throws NotAuthorizedException if the current user is not ADMIN or BUSINESS_ADMIN.
   */
  @GetMapping(path = RestEndpoints.URL_ACCESS_ID_GROUPS)
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
