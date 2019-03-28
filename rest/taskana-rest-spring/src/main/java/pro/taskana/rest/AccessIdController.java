package pro.taskana.rest;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.ldap.LdapCache;
import pro.taskana.ldap.LdapClient;
import pro.taskana.rest.resource.AccessIdResource;

/**
 * Controller for access id validation.
 *
 * @author bbr
 */
@RestController
@EnableHypermediaSupport(type = HypermediaType.HAL)
@RequestMapping(path = "/v1/access-ids", produces = "application/hal+json")
public class AccessIdController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessIdController.class);

    @Autowired
    LdapClient ldapClient;

    private static LdapCache ldapCache;

    @GetMapping
    public ResponseEntity<List<AccessIdResource>> validateAccessIds(
        @RequestParam("search-for") String searchFor) throws InvalidArgumentException {
        LOGGER.debug("Entry to validateAccessIds(search-for= {})", searchFor);
        if (searchFor.length() < ldapClient.getMinSearchForLength()) {
            throw new InvalidArgumentException(
                "searchFor string '" + searchFor + "' is too short. Minimum searchFor length = "
                    + ldapClient.getMinSearchForLength());
        }
        ResponseEntity<List<AccessIdResource>> response;
        if (ldapClient.useLdap()) {
            List<AccessIdResource> accessIdUsers = ldapClient.searchUsersAndGroups(searchFor);
            response = new ResponseEntity<>(accessIdUsers, HttpStatus.OK);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Exit from validateAccessIds(), returning {}", response);
            }

            return response;
        } else if (ldapCache != null) {
            response = new ResponseEntity<>(
                ldapCache.findMatchingAccessId(searchFor, ldapClient.getMaxNumberOfReturnedAccessIds()),
                HttpStatus.OK);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Exit from validateAccessIds(), returning {}", response);
            }

            return response;
        } else {
            response = new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
            LOGGER.debug("Exit from validateAccessIds(), returning {}", response);
            return response;
        }
    }

    @GetMapping(path = "/groups")
    public ResponseEntity<List<AccessIdResource>> getGroupsByAccessId(
        @RequestParam("access-id") String accessId) throws InvalidArgumentException {
        LOGGER.debug("Entry to getGroupsByAccessId(access-id= {})", accessId);
        if (ldapClient.useLdap() || ldapCache != null) {
            if (!validateAccessId(accessId)) {
                throw new InvalidArgumentException("The accessId is invalid");
            }
        }
        List<AccessIdResource> accessIdUsers;
        ResponseEntity<List<AccessIdResource>> response;
        if (ldapClient.useLdap()) {
            accessIdUsers = ldapClient.searchUsersAndGroups(accessId);
            accessIdUsers.addAll(ldapClient.searchGroupsofUsersIsMember(accessId));
            response = new ResponseEntity<>(accessIdUsers, HttpStatus.OK);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Exit from getGroupsByAccessId(), returning {}", response);
            }

            return response;
        } else if (ldapCache != null) {
            accessIdUsers = ldapCache.findGroupsOfUser(accessId, ldapClient.getMaxNumberOfReturnedAccessIds());
            response = new ResponseEntity<>(accessIdUsers, HttpStatus.OK);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Exit from getGroupsByAccessId(), returning {}", response);
            }

            return response;
        } else {
            response = new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
            LOGGER.debug("Exit from getGroupsByAccessId(), returning {}", response);
            return response;
        }
    }

    public static void setLdapCache(LdapCache cache) {
        ldapCache = cache;
    }

    private boolean validateAccessId(String accessId) throws InvalidArgumentException {
        return (ldapClient.useLdap() && ldapClient.searchUsersAndGroups(accessId).size() == 1) || (!ldapClient.useLdap()
            && ldapCache.validateAccessId(accessId).size() == 1);
    }

}
