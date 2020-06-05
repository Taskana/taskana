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

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.rest.ldap.LdapClient;
import pro.taskana.common.rest.models.AccessIdRepresentationModel;

/**
 * Controller for access id validation.
 *
 * @author bbr
 */
@RestController
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class AccessIdController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccessIdController.class);

  LdapClient ldapClient;

  @Autowired
  public AccessIdController(LdapClient ldapClient) {
    this.ldapClient = ldapClient;
  }

  @GetMapping(path = Mapping.URL_ACCESSID)
  public ResponseEntity<List<AccessIdRepresentationModel>> validateAccessIds(
      @RequestParam("search-for") String searchFor) throws InvalidArgumentException {

    LOGGER.debug("Entry to validateAccessIds(search-for= {})", searchFor);

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
      @RequestParam("access-id") String accessId) throws InvalidArgumentException {
    LOGGER.debug("Entry to getGroupsByAccessId(access-id= {})", accessId);
    if (!validateAccessId(accessId)) {
      throw new InvalidArgumentException("The accessId is invalid");
    }
    List<AccessIdRepresentationModel> accessIdUsers;
    ResponseEntity<List<AccessIdRepresentationModel>> response;
    accessIdUsers = ldapClient.searchUsersAndGroups(accessId);
    accessIdUsers.addAll(ldapClient.searchGroupsofUsersIsMember(accessId));
    response = ResponseEntity.ok(accessIdUsers);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getGroupsByAccessId(), returning {}", response);
    }

    return response;
  }

  private boolean validateAccessId(String accessId) throws InvalidArgumentException {
    return ldapClient.searchUsersAndGroups(accessId).size() == 1;
  }
}
