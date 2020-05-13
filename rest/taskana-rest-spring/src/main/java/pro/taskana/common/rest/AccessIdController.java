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
import pro.taskana.common.rest.models.AccessIdRepresentationModel;
import pro.taskana.ldap.LdapCache;
import pro.taskana.ldap.LdapClient;

/**
 * Controller for access id validation.
 *
 * @author bbr
 */
@RestController
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class AccessIdController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccessIdController.class);
  private static final String EXIT_FROM_VALIDATE_ACCESS_IDS
      = "Exit from validateAccessIds(), returning {}";
  private static final String EXIT_FROM_GET_GROUP_BY_ACCESS_ID
      = "Exit from getGroupsByAccessId(), returning {}";
  private static LdapCache ldapCache;

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
    if (ldapClient.useLdap()) {
      List<AccessIdRepresentationModel> accessIdUsers = ldapClient.searchUsersAndGroups(searchFor);
      response = ResponseEntity.ok(accessIdUsers);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(EXIT_FROM_VALIDATE_ACCESS_IDS, response);
      }

      return response;
    } else if (ldapCache != null) {
      response =
          ResponseEntity.ok(
              ldapCache.findMatchingAccessId(
                  searchFor, ldapClient.getMaxNumberOfReturnedAccessIds()));
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(EXIT_FROM_VALIDATE_ACCESS_IDS, response);
      }

      return response;
    } else {
      response = ResponseEntity.notFound().build();
      LOGGER.debug(EXIT_FROM_VALIDATE_ACCESS_IDS, response);
      return response;
    }
  }

  @GetMapping(path = Mapping.URL_ACCESSID_GROUPS)
  public ResponseEntity<List<AccessIdRepresentationModel>> getGroupsByAccessId(
      @RequestParam("access-id") String accessId) throws InvalidArgumentException {
    LOGGER.debug("Entry to getGroupsByAccessId(access-id= {})", accessId);
    if ((ldapClient.useLdap() || ldapCache != null) && (!validateAccessId(accessId))) {
      throw new InvalidArgumentException("The accessId is invalid");
    }
    List<AccessIdRepresentationModel> accessIdUsers;
    ResponseEntity<List<AccessIdRepresentationModel>> response;
    if (ldapClient.useLdap()) {
      accessIdUsers = ldapClient.searchUsersAndGroups(accessId);
      accessIdUsers.addAll(ldapClient.searchGroupsofUsersIsMember(accessId));
      response = ResponseEntity.ok(accessIdUsers);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(EXIT_FROM_GET_GROUP_BY_ACCESS_ID, response);
      }

      return response;
    } else if (ldapCache != null) {
      accessIdUsers =
          ldapCache.findGroupsOfUser(accessId, ldapClient.getMaxNumberOfReturnedAccessIds());
      response = ResponseEntity.ok(accessIdUsers);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(EXIT_FROM_GET_GROUP_BY_ACCESS_ID, response);
      }

      return response;
    } else {
      response = ResponseEntity.notFound().build();
      LOGGER.debug(EXIT_FROM_GET_GROUP_BY_ACCESS_ID, response);
      return response;
    }
  }

  public static void setLdapCache(LdapCache cache) {
    ldapCache = cache;
  }

  private boolean validateAccessId(String accessId) throws InvalidArgumentException {
    return (ldapClient.useLdap() && ldapClient.searchUsersAndGroups(accessId).size() == 1)
        || (!ldapClient.useLdap() && ldapCache.validateAccessId(accessId).size() == 1);
  }
}
