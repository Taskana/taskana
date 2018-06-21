package pro.taskana.rest;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    LdapClient ldapClient;

    private static LdapCache ldapCache;

    @GetMapping
    public ResponseEntity<List<AccessIdResource>> validateAccessIds(
        @RequestParam String searchFor) throws InvalidArgumentException {
        if (searchFor.length() < ldapClient.getMinSearchForLength()) {
            throw new InvalidArgumentException("searchFor string '" + searchFor + "' is too short. Minimum searchFor length = "
                + ldapClient.getMinSearchForLength());
        }
        if (ldapClient.useLdap()) {
            return new ResponseEntity<>(ldapClient.searchUsersAndGroups(searchFor), HttpStatus.OK);
        } else if (ldapCache != null) {
            return new ResponseEntity<>(
                ldapCache.findMatchingAccessId(searchFor, ldapClient.getMaxNumberOfReturnedAccessIds()),
                HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }
    }

    public static void setLdapCache(LdapCache cache) {
        ldapCache = cache;
    }

}
